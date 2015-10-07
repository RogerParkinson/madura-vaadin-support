package nz.co.senanque.login.users;

import java.io.IOException;
import java.util.Set;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletContext;

public interface UserRepository {

	public abstract Set<String> authenticate(ServletContext servletContext,
			String user, String password) throws IOException, LoginException;

}