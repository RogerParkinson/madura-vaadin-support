package nz.co.senanque.login;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

/**
 * Describes the request validator.
 * 
 * @author Roger Parkinson
 *
 */
public interface RequestValidator {

	public abstract boolean isURLIgnored(HttpServletRequest req);
	public abstract void authenticate(HttpServletRequest req) throws OAuthSystemException, OAuthProblemException;
	public abstract AuthorizationCodeRequestBuilder getAuthorizationCodeRequestBuilder(String state);

	
}