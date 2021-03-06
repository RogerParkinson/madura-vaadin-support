/**
 * 
 */
package nz.co.senanque.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nz.co.senanque.login.users.UserRepository;
import nz.co.senanque.login.users.UserRepositoryImpl;
import nz.co.senanque.permissionmanager.PermissionManager;
import nz.co.senanque.resourceloader.MessageResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Spring bean that is fetched from the Spring context explicitly by the filter rather than injected.
 * This doesn't make any difference to the bean itself, of course. It optionally injects an {link nz.co.senanque.login.AuthenticationDelegate}
 * and if there is none it uses itself instead. But the {link nz.co.senanque.login.AuthenticationDelegate} it implements
 * is demo-ware so you should provide your own implementation for production.
 * <p>
 * To test if the current request has been authenticated the HttpSession is examined for an attribute {link nz.co.senanque.login.AuthenticationDelegate.USERNAME}
 * containing a valid string.
 * <p>
 * There are facilities to ensure image files etc bypass the authentication mechanism, as well as a mechanism to serve up some files
 * from the classpath, ie those needed for the login pages.
 * 
 * @author Roger Parkinson
 *
 */
@Component("requestValidator")
@MessageResource("messages")
public class RequestValidatorImpl implements AuthenticationDelegate, MessageSourceAware, RequestValidator {
	
	private static Logger m_logger = LoggerFactory.getLogger(RequestValidatorImpl.class);

	private String m_loginPage = LOGIN_URL;
	private String[] m_ignoreURLs;
	private String[] m_flags = new String[]{"English","French"};
	private String[] m_langs = new String[]{"en","fr"};
    @Value("${nz.co.senanque.login.RequestValidatorImpl.defaultLogin:}")
    private transient String m_defaultLogin;
	@Autowired(required=false) @Qualifier("applicationVersion") private String m_applicationVersion;
    
    private String[] m_imageExtensions = new String[]{".ico",".gif",".png",".jpg",".jpeg",".css",};
	
	@Autowired(required=false) private AuthenticationDelegate m_authenticationDelegate;

	private MessageSource m_messageSource;

	@Autowired(required=false) private UserRepository m_users;

    @Value("${nz.co.senanque.login.mobilePathPrefix:mobile}")
	private String m_mobilePathPrefix;
	
	@PostConstruct
	public void init() {
		if (m_ignoreURLs == null) {
			m_ignoreURLs = new String[]{LOGIN_URL};
		}
		if (m_authenticationDelegate == null) {
			m_authenticationDelegate = this;
		}
	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.login.RequestValidator#isURLIgnored(javax.servlet.http.HttpServletRequest)
	 */
	public boolean isURLIgnored(HttpServletRequest req) {
		String url = req.getRequestURI();
		HttpSession session = req.getSession(true);
		Object o = session.getAttribute(PermissionManager.USERNAME);
		m_logger.debug("checking url {} current user {} session {}",url,o,session.getId());
		if (o != null) {
			m_logger.debug("true");
			return true; // ignore URLs when we are already authenticated
		}
		if (StringUtils.hasText(m_defaultLogin)) {
			fakeLogin(req);
			return true;
		}
		for (String ext: m_imageExtensions) {
			if (url.endsWith(ext)) {
				return false;
			}
		}
		String context = req.getContextPath();
		for (String ignoreURL: m_ignoreURLs) {
			if (url.startsWith(context+ignoreURL)) {
				return true;
			}
		}
		return false;
	}
	
	private void fakeLogin(HttpServletRequest req) {
		String[] login = StringUtils.split(m_defaultLogin, "/");
		Set<String> permissions;
		try {
			permissions = m_authenticationDelegate.authenticate(req.getServletContext(),login[0],login[1]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		HttpSession session = req.getSession(true);
		session.setAttribute(PermissionManager.PERMISSIONS, permissions);
		session.setAttribute(PermissionManager.USERNAME, login[0]);
		session.setAttribute(LOCALE, "en");
		m_logger.debug("Setting user: {} on session {}",login[0],req.getSession().getId());
		
	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.login.RequestValidator#authenticate(javax.servlet.http.HttpServletRequest)
	 */
	public void authenticate(HttpServletRequest req) throws IOException, LoginException {
		String user = req.getParameter("user");
		String password = req.getParameter("password");
		String locale = req.getParameter("locale");
		if (StringUtils.isEmpty(user) || StringUtils.isEmpty(password)) {
			// assume we just changed the locale
			throw new LocaleChangeException(locale);
		}
		Set<String> permissions = m_authenticationDelegate.authenticate(req.getServletContext(),user,password);
		HttpSession session = req.getSession(true);
		session.setAttribute(PermissionManager.PERMISSIONS, permissions);
		session.setAttribute(PermissionManager.USERNAME, user);
		session.setAttribute(LOCALE, locale);
		m_logger.debug("Setting user: {} on session {}",user,req.getSession().getId());
	}
	/* (non-Javadoc)
	 * @see nz.co.senanque.login.RequestValidator#setErrorAttribute(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	public void setErrorAttribute(HttpServletRequest req, String error) {
		req.getSession().setAttribute(ERROR_ATTRIBUTE, error);
		m_logger.debug("Setting error: {} on session {}",error,req.getSession().getId());
	}
	public void setLocale(HttpServletRequest req, String locale) {
		req.getSession().setAttribute(LOCALE, locale);
		m_logger.debug("Setting locale: {} on session {}",locale,req.getSession().getId());
	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.login.RequestValidator#getErrorAttribute(javax.servlet.http.HttpServletRequest)
	 */
	public String getErrorAttribute(HttpServletRequest req) {
		m_logger.debug("Getting error: {} on session {}",
				(String)req.getSession().getAttribute(ERROR_ATTRIBUTE),
				req.getSession().getId());
		return (String)req.getSession().getAttribute(ERROR_ATTRIBUTE);
	}

	public String getLocale(HttpServletRequest req) {
		m_logger.debug("Getting locale: {} on session {}",
				(String)req.getSession().getAttribute(LOCALE),
				req.getSession().getId());
		return (String)req.getSession().getAttribute(LOCALE);
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

	/* (non-Javadoc)
	 * @see nz.co.senanque.login.RequestValidator#write(javax.servlet.http.HttpServletRequest, javax.servlet.ServletContext, javax.servlet.http.HttpServletResponse)
	 */
	public boolean write(HttpServletRequest req,ServletContext servletContext,
			HttpServletResponse httpServletResponse) throws IOException {
		
		String contextPath = servletContext.getContextPath();
		String uri = req.getRequestURI();
		uri = StringUtils.delete(uri, contextPath);
		if (uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		if (uri.endsWith("css")) {
			String css = getLoginCSS(uri, servletContext);
			httpServletResponse.setContentType("text/css; charset=UTF-8");
			httpServletResponse.getOutputStream().print(css);
			return true;
		}
		try {
			for (String ext: m_imageExtensions) {
				if (uri.endsWith(ext)) {
					httpServletResponse.setContentType("image/*");
					InputStream is = getStream(uri, servletContext);
					OutputStream out = httpServletResponse.getOutputStream();
					pipe(is,out);
					return true;
				}
			}
			
		} catch (Exception e) {
			m_logger.warn("{}",e.toString());
			return false;
		}
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("ContentType: {}",httpServletResponse.getContentType());
			m_logger.debug("status: {}",httpServletResponse.getStatus());
			Enumeration<String> nameEnum = req.getHeaderNames();
			while (nameEnum.hasMoreElements()) {
				String name = nameEnum.nextElement();
				m_logger.debug("header name: {} value: {}",name,req.getHeader(name));
			}
			nameEnum = req.getAttributeNames();
			while (nameEnum.hasMoreElements()) {
				String name = nameEnum.nextElement();
				m_logger.debug("Attribute name: {} value: {}",name,req.getAttribute(name));
			}
			nameEnum = req.getParameterNames();
			while (nameEnum.hasMoreElements()) {
				String name = nameEnum.nextElement();
				m_logger.debug("Parameter name: {} value: {}",name,req.getParameter(name));
			}
		}
		m_logger.debug("Context path: {} uri: {} content-type: {}",contextPath,uri, req.getContentType());
		String login = getLoginHTML(getErrorAttribute(req),getLocale(req), servletContext);
		httpServletResponse.setContentType("text/html; charset=UTF-8");
		httpServletResponse.getOutputStream().print(login);
		return true;
	}
	
	private void pipe(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len = in.read(buffer);
		while (len != -1) {
		    out.write(buffer, 0, len);
		    len = in.read(buffer);
		}
	}
	
	protected String getLoginHTML(String error, String locale, ServletContext servletContext) throws IOException {
		
		m_logger.debug("Current locale {}",locale);
		Locale useLocale = Locale.getDefault();
		if (locale != null) {
			Locale.setDefault(new Locale(locale));
			useLocale = new Locale(locale);
		}
		String flags = getLocales(useLocale);
		m_logger.debug("Current locale {}",useLocale);
		String s = getFile("login.html",servletContext).replace("~FLAGS",flags);
		final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
		String t = messageSourceAccessor.getMessage("login.title",useLocale);
		m_logger.debug("login.title={}",t);
		t = messageSourceAccessor.getMessage("login.name",useLocale);
		m_logger.debug("login.name={}",t);
		String contextPath = servletContext.getContextPath();
		String c = s.replace("~CONTEXTPATH", contextPath);
		c = c.replace("~TITLE", messageSourceAccessor.getMessage("login.title","Welcome to Madura",useLocale));
		c = c.replace("~NAME", messageSourceAccessor.getMessage("login.name","User",useLocale));
		c = c.replace("~PASSWORD", messageSourceAccessor.getMessage("login.password","Password",useLocale));
		c = c.replace("~LOGIN", messageSourceAccessor.getMessage("login.login","Login",useLocale));
		c = c.replace("~HELP", messageSourceAccessor.getMessage("login.help","Help",useLocale));
		c = c.replace("~ERROR", (error==null?"":error));
		c = c.replace("~LOCALE", (locale==null?"":locale.toString()));
		c = c.replace("~VERSION", (m_applicationVersion==null)?"":m_applicationVersion);
		m_logger.debug("\n{}",c);
		return c;
	}
	
	private String getLocales(Locale currentLocale) {
		String currentFlag ="";
		for (int i=0;i<m_langs.length;i++) {
			if (currentLocale.getLanguage().equals(new Locale(m_langs[i]).getLanguage())) {
				currentFlag = m_flags[i];
				}
			
		}
			 
		StringBuilder ret = new StringBuilder("<select name=\"locale\" class=\"icon-menu\" onchange=\"this.form.submit()\" ");
		ret.append("style=\"background-image:url(~CONTEXTPATH/flags/~CURRENTFLAG.png);\">".replace("~CURRENTFLAG", currentFlag));
		for (int i=0;i<m_langs.length;i++) {
			if (currentLocale.getLanguage().equals(new Locale(m_langs[i]).getLanguage())) {
				ret.append("<option value=\"~LANG\" selected style=\"background-image:url(~CONTEXTPATH/flags/~FLAG.png)\">~FLAG</option>"
						.replace("~FLAG", m_flags[i]).replace("~LANG", m_langs[i]));
			} else {
				ret.append("<option value=\"~LANG\" style=\"background-image:url(~CONTEXTPATH/flags/~FLAG.png)\">~FLAG</option>"
						.replace("~FLAG", m_flags[i]).replace("~LANG", m_langs[i]));
			}
		}
		ret.append("</select>");
		return ret.toString();
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

	/* (non-Javadoc)
	 * @see nz.co.senanque.login.RequestValidator#authenticate(javax.servlet.ServletContext, java.lang.String, java.lang.String)
	 */
	public Set<String> authenticate(ServletContext servletContext, String user, String password) throws IOException, LoginException {
		if (m_users == null) {
			m_users = new UserRepositoryImpl(servletContext);
		}
		return m_users.authenticate(servletContext, user, password);
	}


	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}

	public UserRepository getUsers() {
		return m_users;
	}

	public void setUsers(UserRepository users) {
		m_users = users;
	}

	public String getDefaultLogin() {
		return m_defaultLogin;
	}

	public void setDefaultLogin(String defaultLogin) {
		m_defaultLogin = defaultLogin;
	}

	@Override
	public String getMobilePathPrefix() {
		return m_mobilePathPrefix;
	}

	public void setMobilePathPrefix(String mobilePathPrefix) {
		m_mobilePathPrefix = mobilePathPrefix;
	}

}
