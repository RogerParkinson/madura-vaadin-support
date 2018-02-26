/**
 * 
 */
package nz.co.senanque.login;

import java.io.IOException;
import java.util.Set;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletContext;

/**
 * Delegation for authentication. By default the {link nz.co.senanque.login.RequestValidatorImpl} class
 * is the implementation for this, but that is a very simple implementation which is suitable for demos.
 * To implement your own delegate just create a Spring bean with this interface.    
 * 
 * @author Roger Parkinson
 *
 */
public interface AuthenticationDelegate {

	public static final String ERROR_ATTRIBUTE = "nz.co.senanque.login.RequestValidator.ERROR";
	public static final String LOGIN_URL = "/auth/login";
	public static final String LOCALE = "nz.co.senanque.login.RequestValidator.LOCALE";

	Set<String> authenticate(ServletContext servletContext, String user, String password) throws IOException, LoginException;

}
