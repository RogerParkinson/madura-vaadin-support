package nz.co.senanque.login;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.DelegatingFilterProxy;

@WebFilter(urlPatterns = "/*", filterName = "springSecurityFilterChain")
public class AuthenticationFilter extends DelegatingFilterProxy {

	private static Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	public static String LOCALE = "nz.co.senanque.login.AuthenticationFilter.locale";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String requestLocaleString = request.getParameter("locale");
		String url = ((HttpServletRequest)request).getRequestURI();

		log.info("username: {} password: {} locale: {}",username,password,requestLocaleString);

		HttpSession session = ((HttpServletRequest)request).getSession(true);
		if (session != null) {
			String sessionLocaleString = (String)session.getAttribute(LOCALE);
			Locale contextLocale = LocaleContextHolder.getLocale();
			if (requestLocaleString != null && !requestLocaleString.equals(sessionLocaleString)) {
				LocaleContextHolder.setLocale(new Locale(requestLocaleString));
				session.setAttribute(LOCALE, requestLocaleString);
				Locale.setDefault(new Locale(requestLocaleString));
				log.debug("Set locale to {}",requestLocaleString);
				if (url.endsWith("login") && (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))) {
					request.getRequestDispatcher("/login.jsp").forward(request, response);
					return;
				}
			}
		}
		super.doFilter(request, response, filterChain);
	}
	
}
