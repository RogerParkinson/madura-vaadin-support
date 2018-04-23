package nz.co.senanque.login;

import javax.servlet.http.HttpServletRequest;

/**
 * Describes the request validator.
 * 
 * @author Roger Parkinson
 *
 */
public interface RequestValidator {

	public abstract boolean isURLIgnored(HttpServletRequest req);
	public abstract void authenticate(HttpServletRequest req) ;
	public abstract AuthorizationCodeRequestBuilder getAuthorizationCodeRequestBuilder(String state);

	
}