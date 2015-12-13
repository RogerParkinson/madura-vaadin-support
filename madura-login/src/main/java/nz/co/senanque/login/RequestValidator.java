package nz.co.senanque.login;

import java.io.IOException;
import java.util.Set;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Describes the request validator.
 * 
 * @author Roger Parkinson
 *
 */
public interface RequestValidator {

	public abstract boolean isURLIgnored(HttpServletRequest req);

	public abstract void authenticate(HttpServletRequest req)
			throws IOException, LoginException;

	public abstract void setErrorAttribute(HttpServletRequest req, String error);

	public abstract String getErrorAttribute(HttpServletRequest req);

	public abstract boolean write(HttpServletRequest req,
			ServletContext servletContext,
			HttpServletResponse httpServletResponse) throws IOException;

	public abstract Set<String> authenticate(ServletContext servletContext,
			String user, String password) throws IOException, LoginException;

	public abstract String getLoginPage();

	public abstract void setLocale(HttpServletRequest req, String locale);

	public abstract String getMobilePathPrefix();
	
}