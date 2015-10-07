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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * @author Roger Parkinson
 *
 */
@WebFilter(urlPatterns = "/*", filterName = "0AuthenticationFilter")
public class AuthenticationFilter extends GenericFilterBean {
	
	private static Logger m_logger = LoggerFactory.getLogger(AuthenticationFilter.class);
	private String loginURL;
	private RequestValidator m_validator;

	protected void initFilterBean() throws ServletException {
		loginURL = getServletContext().getContextPath()+"/auth/login";
		m_logger.debug("{}",loginURL);
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		if (m_validator == null) {
			WebApplicationContext applicationContext = WebApplicationContextUtils
		            .getWebApplicationContext(this.getServletContext());
			m_validator = applicationContext.getBean(RequestValidator.class);
		}
		if (!m_validator.isURLIgnored(httpServletRequest)) {
			m_validator.write(httpServletRequest, this.getServletContext(),httpServletResponse);
			return;
		}
		chain.doFilter(request, response);
	}

	public void destroy() {
		
	}

}
