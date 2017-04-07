package nz.co.senanque.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

@WebServlet("/login")
public class LoginController extends HttpServlet {

	// supply the login form
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String url = StringUtils.isEmpty(req.getContextPath())?"":req.getContextPath();
		resp.setContentType("text/html; charset=UTF-8");
		req.getRequestDispatcher("/login.jsp").forward(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String url = StringUtils.isEmpty(req.getContextPath())?"":req.getContextPath();
		resp.setContentType("text/html; charset=UTF-8");
		req.getRequestDispatcher("/login.jsp").forward(req, resp);
	}

}
