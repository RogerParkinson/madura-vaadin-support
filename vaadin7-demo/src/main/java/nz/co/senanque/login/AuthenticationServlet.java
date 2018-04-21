/**
 * 
 */
package nz.co.senanque.login;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Roger Parkinson
 *
 */
@WebServlet("/login")
public class AuthenticationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger m_logger = LoggerFactory
			.getLogger(AuthenticationServlet.class);
	private String loginURL="/app";
	private RequestValidator m_validator;

	public AuthenticationServlet() {
		m_logger.debug("");
	}

	public void init(ServletConfig config) throws ServletException {
		ServletContext sc = config.getServletContext();
		WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(sc);
		m_validator = applicationContext.getBean(RequestValidator.class);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI().replace("login", "app");
		try {
			m_validator.authenticate(req);
		} catch (Exception e) {
			e.printStackTrace();
			uri = req.getRequestURI().replace("app", "error");
		}
		m_logger.debug("redirecting to {}",uri);
		resp.sendRedirect(uri);
	}

	// Check the password
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			m_validator.authenticate(req);
		} catch (Exception e) {
			e.printStackTrace();
		}
		m_logger.debug("redirecting to {}",loginURL);
		resp.sendRedirect(loginURL);
		
	}

}
