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

	Set<String> authenticate(ServletContext servletContext, String user, String password) throws IOException, LoginException;

}
