/**
 * 
 */
package nz.co.senanque.login;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nz.co.senanque.permissionmanager.PermissionManager;
import nz.co.senanque.resourceloader.MessageResource;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.state.DefaultStateKeyGenerator;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

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
@PropertySource("classpath:application.properties")
@MessageResource("messages")
public class RequestValidatorImpl implements RequestValidator {
	
	private static Logger m_logger = LoggerFactory.getLogger(RequestValidatorImpl.class);
	public static final String ERROR_ATTRIBUTE = "nz.co.senanque.login.RequestValidator.ERROR";

	@Value("${oauth2.client.registration.wso2.authorizationUri}")
	private String authzEndpoint;
	@Value("${oauth2.client.registration.wso2.accessToken}")
	private String accessTokenEndpoint;
	@Value("${oauth2.client.registration.wso2.client-id}")
	private String clientId;
	@Value("${oauth2.client.registration.wso2.client-secret}")
	private String clientSecret;
	@Value("${oauth2.client.registration.wso2.callback}")
	private String callback;
	@Value("${oauth2.client.registration.wso2.grantType}")
	private String authzGrantType;
	@Value("${oauth2.client.registration.wso2.scope}")
	private String scope;

	private String[] m_ignoreURLs;
	@Autowired(required=false) @Qualifier("applicationVersion") private String m_applicationVersion;
	
	private OAuth2RestOperations restTemplate;
	
	private DefaultStateKeyGenerator stateKeyGenerator = new DefaultStateKeyGenerator();

    
    private String[] m_imageExtensions = new String[]{".ico",".gif",".png",".jpg",".jpeg",".css",};
	
	@PostConstruct
	public void init() {
		if (m_ignoreURLs == null) {
			m_ignoreURLs = new String[]{"/login"};
		}
		restTemplate = oAuthRestTemplate();
	}
	
	//@Bean
	public OAuth2RestOperations oAuthRestTemplate() {
		AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
	    resourceDetails.setId("1");
	    resourceDetails.setClientId(clientId);
	    resourceDetails.setClientSecret(clientSecret);
	    resourceDetails.setAccessTokenUri(accessTokenEndpoint);
	    resourceDetails.setUserAuthorizationUri(authzEndpoint);

	    OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext());

	    return restTemplate;
	}
	
	public void extablishToken() {
		BaseOAuth2ProtectedResourceDetails b = new BaseOAuth2ProtectedResourceDetails();
		OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(b);
		OAuth2AccessToken accessToken = restTemplate.getAccessToken();
		
		Map map = restTemplate.getForEntity(accessTokenEndpoint, Map.class).getBody();
		map.toString();

	}

	public OAuthPKCEAuthenticationRequestBuilder getRedirectToWso2() {
	    OAuthPKCEAuthenticationRequestBuilder oAuthPKCEAuthenticationRequestBuilder = new OAuthPKCEAuthenticationRequestBuilder(authzEndpoint);
	    oAuthPKCEAuthenticationRequestBuilder
	            .setClientId(clientId)
	            .setRedirectURI(callback)
	            .setResponseType(authzGrantType)
	            .setScope(scope);
	    return oAuthPKCEAuthenticationRequestBuilder;
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
	public void setErrorAttribute(HttpServletRequest req, String error) {
		req.getSession().setAttribute(ERROR_ATTRIBUTE, error);
		m_logger.debug("Setting error: {} on session {}",error,req.getSession().getId());
	}
	
	public String[] getIgnoreURLs() {
		return m_ignoreURLs;
	}

	public void setIgnoreURLs(String[] ignoreURLs) {
		m_ignoreURLs = ignoreURLs;
	}

	@Override
	public void authenticate(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException {
		
        HttpSession session = request.getSession(true);
		String code = request.getParameter(OAuth2Constants.CODE);
		AuthorizationCodeAccessTokenProvider accessTokenProvider = new AuthorizationCodeAccessTokenProvider();
		AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
		details.setAccessTokenUri(accessTokenEndpoint);
		details.setClientId(clientId);
		details.setClientSecret(clientSecret);
		details.setScope(Arrays.asList(new String[]{"default"}));
		details.setId("1");
//		details.setCode(code);

		AccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest();
		accessTokenRequest.setStateKey(stateKeyGenerator.generateKey(details));
		accessTokenRequest.add(OAuth2Constants.CODE, code);
		AuthorizationCodeAccessTokenProvider t = new AuthorizationCodeAccessTokenProvider();
		OAuth2AccessToken accessToken = t.obtainAccessToken(details, accessTokenRequest);
//		BaseOAuth2ProtectedResourceDetails b = new BaseOAuth2ProtectedResourceDetails();
//		b.setAccessTokenUri(accessTokenEndpoint);
//		b.setClientId(clientId);
//		b.setClientSecret(clientSecret);
//		b.setCode(code);
//		OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(b);
//		OAuth2AccessToken accessToken = restTemplate.getAccessToken();
//		
//		Map map = restTemplate.getForEntity(accessTokenEndpoint, Map.class).getBody();
//		map.toString();
		
		
//        OAuthTokenPKCERequestBuilder oAuthTokenPKCERequestBuilder = new OAuthTokenPKCERequestBuilder(accessTokenEndpoint);
//
//        OAuthClientRequest accessRequest = oAuthTokenPKCERequestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE)
//                .setClientId(clientId)
//                .setClientSecret(clientSecret)
//                .setRedirectURI(callback)
//                .setCode(code)
//                .buildBodyMessage();
//
//        //create OAuth client that uses custom http client under the hood
//        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
////
//        OAuthClientResponse oAuthResponse = oAuthClient.accessToken(accessRequest);
//        String accessToken = oAuthResponse.getParam(OAuth2Constants.ACCESS_TOKEN);
//        session.setAttribute(OAuth2Constants.ACCESS_TOKEN, accessToken);

        
//        HttpSession session = request.getSession(true);
//		String code = request.getParameter(OAuth2Constants.CODE);
//		//String code = (String) session.getAttribute(OAuth2Constants.CODE);
//		TokenRequestBuilder tokenRequestBuilder = new OAuthClientRequest.TokenRequestBuilder(accessTokenEndpoint);
//
//        OAuthClientRequest accessRequest = tokenRequestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE)
//                .setClientId(clientId)
//                .setClientSecret(clientSecret)
//                .setRedirectURI(accessTokenEndpoint)
//                .setCode(code)
//                .buildBodyMessage();
//        accessRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        accessRequest.addHeader("Cache-Control", "no-cache");
//        accessRequest.addHeader("Pragma", "no-cache");
//        accessRequest.addHeader("User-Agent", "Java/1.8.0_60");
//        accessRequest.addHeader("Host", "localhost");
//        accessRequest.addHeader("Connection", "keep-alive");
//        //create OAuth client that uses custom http client under the hood
//        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
//
//        OAuthClientResponse oAuthResponse = oAuthClient.accessToken(accessRequest);
//        String accessToken = oAuthResponse.getParam(OAuth2Constants.ACCESS_TOKEN);
//        session.setAttribute(OAuth2Constants.ACCESS_TOKEN, accessToken);
//
//        String idToken = oAuthResponse.getParam("id_token");
//        if (idToken != null) {
//            session.setAttribute("id_token", idToken);
//        }

//		OAuth2RestOperations restTemplate = oAuthRestTemplate();
//		restTemplate.getOAuth2ClientContext();
//		
//		Map map = restTemplate.getForEntity(accessTokenEndpoint, Map.class).getBody();
//		map.toString();
		
	}

	@Override
	public String buildRedirectRequest() {
		AuthorizationCodeRequestBuilder details = new AuthorizationCodeRequestBuilder(authzEndpoint);
		details.setClientId(clientId);
		details.setRedirectURI(callback);
		return details.getFullUri();
	}

}
