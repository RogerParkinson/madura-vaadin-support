package nz.co.senanque.login;

public class AccessTokenRequestBuilder extends RequestBuilder {

	public static String CLIENT_SECRET = "client_secret";

	public AccessTokenRequestBuilder(String uri) {
		super(uri);
	}
	public RequestBuilder setClientId(String clientId) {
		parameters.put(CLIENT_SECRET, clientId);
		return this;
	}


}
