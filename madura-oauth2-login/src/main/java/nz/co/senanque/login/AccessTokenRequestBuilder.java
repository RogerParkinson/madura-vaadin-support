package nz.co.senanque.login;



public class AccessTokenRequestBuilder extends RequestBuilder {

	public static String CLIENT_SECRET = "client_secret";
	public static String GRANT_TYPE = "grant_type";
	public static String CODE = "code";

	public AccessTokenRequestBuilder(String uri) {
		super(uri);
		parameters.put(GRANT_TYPE, "authorization_code");
	}
	public AccessTokenRequestBuilder setClientId(String clientId) {
		parameters.put(CLIENT_ID, clientId);
		return this;
	}
	public AccessTokenRequestBuilder setClientSecret(String clientSecret) {
		parameters.put(CLIENT_SECRET, clientSecret);
		return this;
	}
	public AccessTokenRequestBuilder setCode(String code) {
		parameters.put(CODE, code);
		return this;
	}


}
