package nz.co.senanque.login;

import java.util.List;

public class AuthorizationCodeRequestBuilder extends RequestBuilder {

	public static String SCOPE = "scope";
	public static String STATE = "state";

	public AuthorizationCodeRequestBuilder(String uri) {
		super(uri);
		parameters.put(RESPONSE_TYPE, "code");
//		parameters.put(SCOPE, "default");
	}
	public AuthorizationCodeRequestBuilder setClientId(String clientId) {
		parameters.put(CLIENT_ID, clientId);
		return this;
	}
	public AuthorizationCodeRequestBuilder setScope(String scope) {
		parameters.put(SCOPE, scope);
		return this;
	}
	public AuthorizationCodeRequestBuilder setState(String state) {
		parameters.put(STATE, state);
		return this;
	}
	public AuthorizationCodeRequestBuilder setScope(List<String> scopes) {
		StringBuilder value = new StringBuilder();
		for (String v: scopes) {
			value.append(v);
			value.append(" ");
		}
		value.deleteCharAt(value.length()-1);
		parameters.put(SCOPE, value.toString());
		return this;
		
	}

}
