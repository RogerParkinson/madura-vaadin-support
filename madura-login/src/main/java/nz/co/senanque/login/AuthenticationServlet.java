/**
 * 
 */
package nz.co.senanque.login;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Roger Parkinson
 *
 */
@WebServlet("/auth/login")
public class AuthenticationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger m_logger = LoggerFactory
			.getLogger(AuthenticationServlet.class);
	private String loginURL;
	private RequestValidator m_validator;
	private String m_mobilePathPrefix;

	public AuthenticationServlet() {
		m_logger.debug("");
	}

	public void init(ServletConfig config) throws ServletException {
		ServletContext sc = config.getServletContext();
		WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(sc);
		m_validator = applicationContext.getBean(RequestValidator.class);
		loginURL = sc.getContextPath()
				+ m_validator.getLoginPage();
		m_mobilePathPrefix = m_validator.getMobilePathPrefix();
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		m_logger.debug("redirecting to {}",loginURL);
		resp.sendRedirect(loginURL);
	}

	// Check the password
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String url = StringUtils.isEmpty(req.getContextPath())?"/":req.getContextPath();
		try {
			String pathPrefix = getPrefix(req);
			m_validator.setErrorAttribute(req, null);
			m_validator.authenticate(req);
			m_logger.debug("redirecting to {}",url+pathPrefix);
			resp.sendRedirect(url+pathPrefix); // on to application
		} catch (LocaleChangeException e) {
			m_validator.setLocale(req, e.getLocale());
			resp.setLocale(new Locale(e.getLocale()));
			m_logger.debug("redirecting to {}",url);
			resp.sendRedirect(url); // back to login page
		} catch (Exception e) {
			m_logger.error(e.getMessage(),e);
			m_validator.setErrorAttribute(req, e.getLocalizedMessage());
			m_logger.debug("redirecting to {}",url);
			resp.sendRedirect(url); // back to login page
		}
	}
	private String getPrefix(HttpServletRequest req) {
        boolean mobileUserAgent = req.getHeader("user-agent")
                .toLowerCase().contains("mobile");
        if (mobileUserAgent) {
        	return "/"+m_mobilePathPrefix;
        }
        return "/";
	}

}
