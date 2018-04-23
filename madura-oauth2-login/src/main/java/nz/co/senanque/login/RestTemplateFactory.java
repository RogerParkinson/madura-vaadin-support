package nz.co.senanque.login;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.http.converter.FormOAuth2AccessTokenMessageConverter;
import org.springframework.security.oauth2.http.converter.FormOAuth2ExceptionHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;


/**
 * @author Roger Parkinson
 * 
 * Mostly adapted from org.springframework.security.oauth2.client.token.OAuth2AccessTokenSupport
 *
 */
@Component
public class RestTemplateFactory {

	private static Logger m_logger = LoggerFactory.getLogger(RestTemplateFactory.class);

	private List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
	private RestOperations restTemplate;
	private ResponseErrorHandler responseErrorHandler = new AccessTokenErrorHandler();
	private List<HttpMessageConverter<?>> messageConverters;
	private static final FormHttpMessageConverter FORM_MESSAGE_CONVERTER = new FormHttpMessageConverter();

	private ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
		@Override
		protected void prepareConnection(HttpURLConnection connection, String httpMethod)
				throws IOException {
			super.prepareConnection(connection, httpMethod);
			connection.setInstanceFollowRedirects(false);
			connection.setUseCaches(false);
		}
	};

	protected ResponseErrorHandler getResponseErrorHandler() {
		return responseErrorHandler;
	}
	public RestOperations getRestTemplate() {
		if (restTemplate == null) {
			synchronized (this) {
				if (restTemplate == null) {
					RestTemplate restTemplate = new RestTemplate();
					restTemplate.setErrorHandler(getResponseErrorHandler());
					restTemplate.setRequestFactory(requestFactory);
					restTemplate.setInterceptors(interceptors);
					this.restTemplate = restTemplate;
				}
			}
		}
		if (messageConverters == null) {
			setMessageConverters(new RestTemplate().getMessageConverters());
			this.messageConverters.add(0,new FormOAuth2AccessTokenMessageConverter());
			this.messageConverters.add(1,new FormOAuth2ExceptionHttpMessageConverter());
			MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
		    List<MediaType> mediaTypes = new ArrayList<MediaType>();
		    mediaTypes.add(new MediaType("application", "x-javascript"));
		    jackson.setSupportedMediaTypes(mediaTypes);
		    this.messageConverters.add(jackson);			for (HttpMessageConverter<?> m: this.messageConverters) {
				m_logger.debug("{} {}",m,m.getSupportedMediaTypes());
			}
		}
		return restTemplate;
	}
	public RequestCallback getRequestCallback(
			MultiValueMap<String, String> form, HttpHeaders headers) {
		return new OAuth2AuthTokenCallback(form, headers);
	}

	/**
	 * Request callback implementation that writes the given object to the request stream.
	 */
	private class OAuth2AuthTokenCallback implements RequestCallback {

		private final MultiValueMap<String, String> form;

		private final HttpHeaders headers;

		private OAuth2AuthTokenCallback(MultiValueMap<String, String> form, HttpHeaders headers) {
			this.form = form;
			this.headers = headers;
		}

		public void doWithRequest(ClientHttpRequest request) throws IOException {
			request.getHeaders().putAll(this.headers);
			request.getHeaders().setAccept(
					Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED));
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Encoding and sending form: " + form);
			}
			FORM_MESSAGE_CONVERTER.write(this.form, MediaType.APPLICATION_FORM_URLENCODED, request);
		}
	}
	public ResponseExtractor<OAuth2AccessToken> getResponseExtractor() {
		getRestTemplate(); // force initialization
		return new HttpMessageConverterExtractor<OAuth2AccessToken>(OAuth2AccessToken.class, this.messageConverters);
	}
	private void setMessageConverters(
			List<HttpMessageConverter<?>> messageConverters2) {
		this.messageConverters = messageConverters2;
	}
	private class AccessTokenErrorHandler extends DefaultResponseErrorHandler {

		@SuppressWarnings("unchecked")
		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			for (HttpMessageConverter<?> converter : messageConverters) {
				if (converter.canRead(OAuth2Exception.class, response.getHeaders().getContentType())) {
					OAuth2Exception ex;
					try {
						ex = ((HttpMessageConverter<OAuth2Exception>) converter).read(OAuth2Exception.class, response);
					}
					catch (Exception e) {
						// ignore
						continue;
					}
					throw ex;
				}
			}
			super.handleError(response);
		}
	}
}
