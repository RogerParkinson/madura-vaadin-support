/**
 * 
 */
package nz.co.senanque.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nz.co.senanque.login.users.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class RequestValidator implements AuthenticationDelegate, MessageSourceAware {
	
	private static Logger m_logger = LoggerFactory.getLogger(RequestValidator.class);

	
	private String m_loginPage = LOGIN_URL;
	private String[] m_ignoreURLs;
	
	@Autowired(required=false) private AuthenticationDelegate m_authenticationDelegate;
	private ArrayList<User> m_users;

	private MessageSource m_messageSource;
	
	@PostConstruct
	public void init() {
		if (m_ignoreURLs == null) {
			m_ignoreURLs = new String[]{LOGIN_URL};
		}
		if (m_authenticationDelegate == null) {
			m_authenticationDelegate = this;
		}
	}

	public boolean isURLIgnored(HttpServletRequest req) {
		String url = req.getRequestURI();
		HttpSession session = req.getSession(true);
		Object o = session.getAttribute(USERNAME);
		m_logger.debug("checking url {} current user {} session {}",url,o,session.getId());
		if (o != null) {
			m_logger.debug("true");
			return true; // ignore URLs when we are already authenticated
		}
		if (url.endsWith(".css")) {
			m_logger.debug("false");
			return false;
		}
		if (url.endsWith(".ico")) {
			m_logger.debug("false");
			return false;
		}
		if (url.endsWith(".gif")) {
			m_logger.debug("false");
			return false;
		}
		if (url.endsWith(".png")) {
			m_logger.debug("false");
			return false;
		}
		if (url.endsWith(".jpg")) {
			m_logger.debug("false");
			return false;
		}
		if (url.endsWith(".jpeg")) {
			m_logger.debug("false");
			return false;
		}
		String context = req.getContextPath();
		for (String ignoreURL: m_ignoreURLs) {
			if (url.startsWith(context+ignoreURL)) {
				m_logger.debug("true");
				return true;
			}
		}
		m_logger.debug("false");
		return false;
	}

	public void authenticate(HttpServletRequest req) throws IOException, LoginException {
		String user = req.getParameter("user");
		String password = req.getParameter("password");
		String locale = req.getParameter("locale");
		Set<String> permissions = m_authenticationDelegate.authenticate(req.getServletContext(),user,password);
		HttpSession session = req.getSession(true);
		session.setAttribute(PERMISSIONS, permissions);
		session.setAttribute(USERNAME, user);
		session.setAttribute(LOCALE, locale);
		m_logger.debug("Setting user: {} on session {}",user,req.getSession().getId());
	}
	public void setErrorAttribute(HttpServletRequest req, String error) {
		req.getSession().setAttribute(ERROR_ATTRIBUTE, error);
		m_logger.debug("Setting error: {} on session {}",error,req.getSession().getId());
	}

	public String getErrorAttribute(HttpServletRequest req) {
		m_logger.debug("Getting error: {} on session {}",
				(String)req.getSession().getAttribute(ERROR_ATTRIBUTE),
				req.getSession().getId());
		return (String)req.getSession().getAttribute(ERROR_ATTRIBUTE);
	}

	public String[] getIgnoreURLs() {
		return m_ignoreURLs;
	}

	public void setIgnoreURLs(String[] ignoreURLs) {
		m_ignoreURLs = ignoreURLs;
	}

	public void setLoginPage(String loginPage) {
		m_loginPage = loginPage;
	}

	public String getLoginPage() {
		return m_loginPage;
	}

	public void write(HttpServletRequest req,ServletContext servletContext,
			HttpServletResponse httpServletResponse) throws IOException {
		
		String contextPath = servletContext.getContextPath();
		String uri = req.getRequestURI();
		uri = StringUtils.delete(uri, contextPath);
		if (uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		if (uri.endsWith("css")) {
			String css = getLoginCSS(uri, servletContext);
			httpServletResponse.getOutputStream().print(css);
			return;
		}
		if (uri.endsWith("gif")) {
			InputStream is = getStream(uri, servletContext);
			OutputStream out = httpServletResponse.getOutputStream();
			pipe(is,out);
			return;
		}
		if (uri.endsWith("png")) {
			InputStream is = getStream(uri, servletContext);
			OutputStream out = httpServletResponse.getOutputStream();
			pipe(is,out);
			return;
		}
		if (uri.endsWith("jpg")) {
			InputStream is = getStream(uri, servletContext);
			OutputStream out = httpServletResponse.getOutputStream();
			pipe(is,out);
			return;
		}
		if (uri.endsWith("jpeg")) {
			InputStream is = getStream(uri, servletContext);
			OutputStream out = httpServletResponse.getOutputStream();
			pipe(is,out);
			return;
		}
		if (uri.endsWith("ico")) {
			InputStream is = getStream(uri, servletContext);
			OutputStream out = httpServletResponse.getOutputStream();
			pipe(is,out);
			return;
		}
		String login = getLoginHTML(getErrorAttribute(req), servletContext);
		httpServletResponse.getOutputStream().print(login);
	}
	
	private void pipe(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len = in.read(buffer);
		while (len != -1) {
		    out.write(buffer, 0, len);
		    len = in.read(buffer);
		}
	}
	
	protected String getLoginHTML(String error, ServletContext servletContext) throws IOException {
		String s = getFile("login.html",servletContext);
		final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
		String contextPath = servletContext.getContextPath();
		String c = s.replace("~CONTEXTPATH", contextPath);
		c = c.replace("~TITLE", messageSourceAccessor.getMessage("login.title","Welcome to Madura"));
		c = c.replace("~NAME", messageSourceAccessor.getMessage("login.name","User"));
		c = c.replace("~PASSWORD", messageSourceAccessor.getMessage("login.password","Password"));
		c = c.replace("~LOGIN", messageSourceAccessor.getMessage("login.login","Login"));
		c = c.replace("~ERROR", (error==null?"":error));
		return c;
	}
	
	protected String getLoginCSS(String fileName, ServletContext servletContext) throws IOException {
		String s = getFile(fileName,servletContext);
		String contextPath = servletContext.getContextPath();
		return s.replace("~CONTEXTPATH", contextPath);
	}
	
	private String getFile(String fileName,ServletContext servletContext) throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStream is = getStream(fileName,servletContext);
		while (true) {
			byte[] bytes = new byte[1000];
			int i = 0;
			try {
				i = is.read(bytes);
			} catch (IOException e) {
				throw new IOException("Failed to open "+fileName,e);
			}
			if (i == -1) {
				break;
			}
			sb.append(new String(bytes, 0, i));
		}
		return sb.toString();
	}

	private InputStream getStream(String fileName,ServletContext servletContext) throws IOException {
		InputStream is = servletContext.getResourceAsStream("/WEB-INF/"+fileName);
		if (is == null) {
			is = this.getClass().getResourceAsStream(fileName);
		}
		if (is == null) {
			throw new IOException("failed to find "+fileName);
		}
		return is;
	}

	public Set<String> authenticate(ServletContext servletContext, String user, String password) throws IOException, LoginException {
		if (m_users == null) {
			loadUsers(servletContext);
		}
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

	private void loadUsers(ServletContext servletContext) throws IOException {
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

	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}

}
