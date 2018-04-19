package nz.co.senanque.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Roger Parkinson
 *
 */
public class RequestBuilder {
	
	private final String uri;
	
	public static String REDIRECT_URI = "redirect_uri";
	public static String RESPONSE_TYPE = "response_type";
	
	protected Map<String,String> parameters = new HashMap<>();
	protected Map<String,String> headers = new HashMap<>();
	
	public RequestBuilder(String uri) {
		this.uri = uri;
	}

	public RequestBuilder setRedirectURI(String redirectURI) {
		parameters.put(REDIRECT_URI, redirectURI);
		return this;
	}

	public RequestBuilder setCode(String code) {
		parameters.put(RESPONSE_TYPE, code);
		return this;
	}
	public String getFullUri() {
		StringBuilder ret = new StringBuilder(uri);
		int i = 0;
		for (Map.Entry<String, String> entry: parameters.entrySet()) {
			if (i == 0) {
				ret.append("?");
			} else {
				ret.append("&");
			}
			ret.append(entry.getKey());
			ret.append("=");
			try {
				ret.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return ret.toString();
	}


}
