package nz.co.senanque.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Roger Parkinson
 *
 */
public class RequestBuilder {

	private final String uri;

	public static String CLIENT_ID = "client_id";
	public static String REDIRECT_URI = "redirect_uri";
	public static String RESPONSE_TYPE = "response_type";

	protected Map<String, String> parameters = new HashMap<>();
	protected HttpHeaders headers = new HttpHeaders();
	private HttpMethod httpMethod = HttpMethod.GET;

	public RequestBuilder(String uri) {
		this.uri = uri;
	}

	public RequestBuilder setRedirectURI(String redirectURI) {
		parameters.put(REDIRECT_URI, redirectURI);
		return this;
	}

	public HttpHeaders getHeaders(String username, String password) {
		return createHeaders(username, password);
	}
	private HttpHeaders createHeaders(String username, String password) {
		return new HttpHeaders() {
			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset
						.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
	}

	public RequestBuilder setCode(String code) {
		parameters.put(RESPONSE_TYPE, code);
		return this;
	}

	public String getFullUri() {
		StringBuilder ret = new StringBuilder(uri);
		if (httpMethod.equals(HttpMethod.GET)) {
			int i = 0;
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				if (i++ == 0) {
					ret.append("?");
				} else {
					ret.append("&");
				}
				ret.append(entry.getKey());
				ret.append("=");
				String value = entry.getValue();
				try {
					value = URLEncoder.encode(value, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				ret.append(value);
			}
		}
		return ret.toString();
	}

	public MultiValueMap<String, String> getForm() {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			form.set(entry.getKey(), entry.getValue());
		}
		return form;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

}
