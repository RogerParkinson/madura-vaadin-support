package nz.co.senanque.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author Roger Parkinson
 *
 */
@Component
@PropertySource("classpath:application.properties")
public class LoginParams {

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
	@Value("${oauth.server.config.keyStoreName}")
	private String keyStoreName;
	@Value("${oauth.server.config.keyStorePassword}")
	private String keyStorePassword;
	@Value("${oauth.server.config.keyPairName}")
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
	public String getKeyStoreName() {
		return keyStoreName;
	}
	public void setKeyStoreName(String keyStoreName) {
		this.keyStoreName = keyStoreName;
	}
	public String getKeyStorePassword() {
		return keyStorePassword;
	}
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}
	public String getKeyPairName() {
		return keyPairName;
	}
	public void setKeyPairName(String keyPairName) {
		this.keyPairName = keyPairName;
	}
}
