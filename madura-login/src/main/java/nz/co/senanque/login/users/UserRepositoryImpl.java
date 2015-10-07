package nz.co.senanque.login.users;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletContext;

import nz.co.senanque.login.RequestValidatorImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roger Parkinson
 *
 */
public class UserRepositoryImpl implements UserRepository {
	
	private static Logger m_logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

	private ArrayList<User> m_users;

	public UserRepositoryImpl(ServletContext servletContext) throws IOException {
		load(servletContext);
	}
	/* (non-Javadoc)
	 * @see nz.co.senanque.login.users.UserRepository#authenticate(javax.servlet.ServletContext, java.lang.String, java.lang.String)
	 */
	public Set<String> authenticate(ServletContext servletContext, String user, String password) throws IOException, LoginException {
		for (User u: m_users) {
			Set<String> permissions = u.test(user, password);
			if (permissions != null) {
				// load permissions somewhere
				m_logger.debug("Authenticated {} and assigned permissions {}",user,permissions);
				return permissions;
			}
		}
		throw new LoginException("Wrong user/password");
	}
	private void load(ServletContext servletContext) throws IOException {
		m_users = new ArrayList<User>();
		InputStream is = servletContext.getResourceAsStream("/WEB-INF/users.csv");
		if (is == null) {
			is = this.getClass().getResourceAsStream("users.csv");
		}
		if (is == null) {
			throw new IOException("failed to find users.csv");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = reader.readLine();
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line,",");
			String name = st.nextToken();
			String password = st.nextToken();
			Set<String> permissions = new HashSet<String>();
			while (st.hasMoreTokens()) {
				permissions.add(st.nextToken());
			}
			User user = new User(name,password,permissions);
			m_users.add(user);
			line = reader.readLine();
		}
	}

}
