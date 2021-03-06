/**
 * 
 */
package nz.co.senanque.login;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nz.co.senanque.permissionmanager.PermissionManager;
import nz.co.senanque.resourceloader.MessageResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseExtractor;

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
public class RequestValidatorImpl implements RequestValidator {
	
	private static Logger m_logger = LoggerFactory.getLogger(RequestValidatorImpl.class);
	public static final String ERROR_ATTRIBUTE = "nz.co.senanque.login.RequestValidator.ERROR";

	private String[] m_ignoreURLs;
	@Autowired LoginParams loginParams;
	@Autowired private RestTemplateFactory restTemplateFactory;
	    
    private String[] m_imageExtensions = new String[]{".ico",".gif",".png",".jpg",".jpeg",".css",};
	
	@PostConstruct
	public void init() {
		if (m_ignoreURLs == null) {
			m_ignoreURLs = new String[]{"/login"};
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
	
	public String[] getIgnoreURLs() {
		return m_ignoreURLs;
	}

	public void setIgnoreURLs(String[] ignoreURLs) {
		m_ignoreURLs = ignoreURLs;
	}

	@Override
	public void authenticate(HttpServletRequest request) {
		
        HttpSession session = request.getSession(true);
		String code = request.getParameter(OAuth2Constants.CODE);
		String state = request.getParameter(OAuth2Constants.STATE);
		Object preservedState = session.getAttribute(OAuth2Constants.PRESERVED_STATE);
		if (preservedState != null && !preservedState.equals(state)) {
			throw new RuntimeException("Invalid state");
		}
		
		AccessTokenRequestBuilder accessTokenRequestBuilder = new AccessTokenRequestBuilder(loginParams.getAccessTokenEndpoint());
		accessTokenRequestBuilder.setClientId(loginParams.getClientId());
		accessTokenRequestBuilder.setRedirectURI(loginParams.getCallback());
		accessTokenRequestBuilder.setCode(code);
		accessTokenRequestBuilder.setHttpMethod(HttpMethod.POST);
		MultiValueMap<String, String> form = accessTokenRequestBuilder.getForm();

		final ResponseExtractor<OAuth2AccessToken> delegate = restTemplateFactory.getResponseExtractor();
		ResponseExtractor<OAuth2AccessToken> responseExtractor = new ResponseExtractor<OAuth2AccessToken>() {
			@Override
			public OAuth2AccessToken extractData(ClientHttpResponse response) throws IOException {
				return delegate.extractData(response);
			}
		};
		OAuth2AccessToken oauth2AccessToken = restTemplateFactory.getRestTemplate().execute(accessTokenRequestBuilder.getFullUri(),accessTokenRequestBuilder.getHttpMethod(),
				restTemplateFactory.getRequestCallback(form,accessTokenRequestBuilder.getHeaders(loginParams.getClientId(), loginParams.getClientSecret())),
				responseExtractor, form.toSingleValueMap());
		session.setAttribute(OAuth2Constants.ACCESS_TOKEN, oauth2AccessToken);
	}

	@Override
	public AuthorizationCodeRequestBuilder getAuthorizationCodeRequestBuilder(String state) {
		AuthorizationCodeRequestBuilder ret = new AuthorizationCodeRequestBuilder(loginParams.getAuthzEndpoint());
		ret.setClientId(loginParams.getClientId());
		ret.setRedirectURI(loginParams.getCallback());
		ret.setState(state);
		return ret;
	}
	

}
