/**
 * 
 */
package nz.co.senanque.login;

import java.io.IOException;
import java.util.Locale;

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
import org.springframework.context.i18n.LocaleContextHolder;
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
	private String loginURL;
	private RequestValidator m_validator;

	protected void initFilterBean() throws ServletException {
		loginURL = getServletContext().getContextPath()+AuthenticationDelegate.LOGIN_URL;
		m_logger.debug("{}",loginURL);
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		if (httpServletRequest.getHeader("User-Agent") == null) {
//			chain.doFilter(request, response);
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
		HttpSession session = ((HttpServletRequest)request).getSession(true);
		if (session != null) {
			String localeString = (String)session.getAttribute(AuthenticationDelegate.LOCALE);
			if (localeString != null && !localeString.equals(LocaleContextHolder.getLocale())) {
				LocaleContextHolder.setLocale(new Locale(localeString));
				m_logger.debug("Set locale to {}",localeString);
			}
		}
		if (!m_validator.isURLIgnored(httpServletRequest)) {
			if (m_validator.write(httpServletRequest, this.getServletContext(),httpServletResponse)) {
				return;
			}
		}
		chain.doFilter(request, response);
	}

	public void destroy() {
		
	}

}
