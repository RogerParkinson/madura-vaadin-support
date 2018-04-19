package nz.co.senanque.login;

public class AuthorizationCodeRequestBuilder extends RequestBuilder {

	public static String CLIENT_ID = "client_id";

	public AuthorizationCodeRequestBuilder(String uri) {
		super(uri);
		parameters.put(RESPONSE_TYPE, "authorization_code");
	}
	public RequestBuilder setClientId(String clientId) {
		parameters.put(CLIENT_ID, clientId);
		return this;
	}

}
