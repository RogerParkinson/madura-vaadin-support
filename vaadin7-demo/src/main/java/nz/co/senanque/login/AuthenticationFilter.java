/**
 * 
 */
package nz.co.senanque.login;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * This filter intercepts the servlet requests. If it is not an explicitly ignored request type then
 * test and redirect if necessary.
 * 
 * @author Roger Parkinson
 *
 */
@WebFilter(urlPatterns = "/*", filterName = "0AuthenticationFilter")
public class AuthenticationFilter extends GenericFilterBean {
	
	private static Logger m_logger = LoggerFactory.getLogger(AuthenticationFilter.class);
	private RequestValidator m_validator;
	private RandomValueStringGenerator generator = new RandomValueStringGenerator();

	protected void initFilterBean() throws ServletException {
//		m_logger.debug("{}",authzEndpoint);
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		if (httpServletRequest.getHeader("User-Agent") == null) {
			return;
		}
		m_logger.debug("User-Agent {}",httpServletRequest.getHeader("User-Agent"));
		m_logger.debug("ContentType {}",httpServletRequest.getContentType());
		m_logger.debug("method {}",httpServletRequest.getMethod());
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		if (m_validator == null) {
			WebApplicationContext applicationContext = WebApplicationContextUtils
		            .getWebApplicationContext(this.getServletContext());
			m_validator = applicationContext.getBean(RequestValidator.class);
		}
		String uri = ((HttpServletRequest)request).getRequestURI();
		if (!uri.endsWith("login")) {
			HttpSession session = ((HttpServletRequest)request).getSession(true);
			Object accessToken = session.getAttribute(OAuth2Constants.ACCESS_TOKEN);
			m_logger.debug("accessToken {}",accessToken);
			if (!m_validator.isURLIgnored(httpServletRequest) && accessToken == null) {
				String state = generator.generate();
				session.setAttribute(OAuth2Constants.PRESERVED_STATE, state);
				AuthorizationCodeRequestBuilder authzRequest = m_validator.getAuthorizationCodeRequestBuilder(state);
			    httpServletResponse.sendRedirect(authzRequest.getFullUri());
			    return;
			}
		}
		chain.doFilter(request, response);
	}

	public void destroy() {
		
	}

}
