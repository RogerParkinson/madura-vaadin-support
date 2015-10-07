/**
 * 
 */
package nz.co.senanque.login;

import java.io.IOException;
import java.util.Set;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletContext;

/**
 * Optional delegation for authentication
 * 
 * @author Roger Parkinson
 *
 */
public interface AuthenticationDelegate {

	public static final String ERROR_ATTRIBUTE = "nz.co.senanque.login.RequestValidator.ERROR";
	public static final String LOGIN_URL = "/auth/login";
	public static final String PERMISSIONS = "nz.co.senanque.login.RequestValidator.AUTHENTICATED";
	public static final String USERNAME = "nz.co.senanque.login.RequestValidator.USERNAME";
	public static final String LOCALE = "nz.co.senanque.login.RequestValidator.LOCALE";

	Set<String> authenticate(ServletContext servletContext, String user, String password) throws IOException, LoginException;

}
