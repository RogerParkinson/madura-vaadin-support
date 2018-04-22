package nz.co.senanque.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author Roger Parkinson
 *
 */
@Component
@PropertySource("classpath:oauth.properties")
public class LoginParams {

	@Value("${oauth2.client.authorizationUri}")
	private String authzEndpoint;
	@Value("${oauth2.client.accessToken}")
	private String accessTokenEndpoint;
	@Value("${oauth2.client.client-id}")
	private String clientId;
	@Value("${oauth2.client.client-secret}")
	private String clientSecret;
	@Value("${oauth2.client.callback}")
	private String callback;
	@Value("${oauth2.client.grantType}")
	private String authzGrantType;
	@Value("${oauth2.client.scope}")
	private String scope;
	@Value("${oauth2.client.keyPairName}")
	private String keyPairName;

	public String getAuthzEndpoint() {
		return authzEndpoint;
	}
	public void setAuthzEndpoint(String authzEndpoint) {
		this.authzEndpoint = authzEndpoint;
	}
	public String getAccessTokenEndpoint() {
		return accessTokenEndpoint;
	}
	public void setAccessTokenEndpoint(String accessTokenEndpoint) {
		this.accessTokenEndpoint = accessTokenEndpoint;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getAuthzGrantType() {
		return authzGrantType;
	}
	public void setAuthzGrantType(String authzGrantType) {
		this.authzGrantType = authzGrantType;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getKeyPairName() {
		return keyPairName;
	}
	public void setKeyPairName(String keyPairName) {
		this.keyPairName = keyPairName;
	}
}
