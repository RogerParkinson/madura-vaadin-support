package nz.co.senanque.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.DelegatingFilterProxy;

@WebFilter(urlPatterns = "/*", filterName = "springSecurityFilterChain")
public class AuthenticationFilter extends DelegatingFilterProxy {

	private static Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	public static String LOCALE = "nz.co.senanque.login.AuthenticationFilter.locale";
	private String[] m_imageExtensions = new String[]{".ico",".gif",".png",".jpg",".jpeg",".css",};


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String requestLocaleString = request.getParameter("locale");
		String url = ((HttpServletRequest)request).getRequestURI();

		log.info("url: {}",url);

		HttpSession session = ((HttpServletRequest)request).getSession(true);
		if (session != null) {
			String sessionLocaleString = (String)session.getAttribute(LOCALE);
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
//			if (url.contains("login-resources/")) {
//				if (write((HttpServletRequest)request, this.getServletContext(),(HttpServletResponse)response)) {
//					return;
//				}
//			}

		}
		super.doFilter(request, response, filterChain);
	}
	public boolean write(HttpServletRequest req,ServletContext servletContext,
			HttpServletResponse httpServletResponse) throws IOException {
		
		String contextPath = servletContext.getContextPath();
		String uri = req.getRequestURI();
		uri = StringUtils.delete(uri, contextPath);
		if (uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		if (uri.endsWith("css")) {
			String css = getLoginCSS(uri, servletContext);
			httpServletResponse.setContentType("text/css; charset=UTF-8");
			httpServletResponse.getOutputStream().print(css);
			return true;
		}
		try {
			for (String ext: m_imageExtensions) {
				if (uri.endsWith(ext)) {
					httpServletResponse.setContentType("image/*");
					InputStream is = getStream(uri, servletContext);
					OutputStream out = httpServletResponse.getOutputStream();
					pipe(is,out);
					return true;
				}
			}
			
		} catch (Exception e) {
			log.warn("{}",e.toString());
			return false;
		}
		if (log.isDebugEnabled()) {
			log.debug("ContentType: {}",httpServletResponse.getContentType());
			log.debug("status: {}",httpServletResponse.getStatus());
			Enumeration<String> nameEnum = req.getHeaderNames();
			while (nameEnum.hasMoreElements()) {
				String name = nameEnum.nextElement();
				log.debug("header name: {} value: {}",name,req.getHeader(name));
			}
			nameEnum = req.getAttributeNames();
			while (nameEnum.hasMoreElements()) {
				String name = nameEnum.nextElement();
				log.debug("Attribute name: {} value: {}",name,req.getAttribute(name));
			}
			nameEnum = req.getParameterNames();
			while (nameEnum.hasMoreElements()) {
				String name = nameEnum.nextElement();
				log.debug("Parameter name: {} value: {}",name,req.getParameter(name));
			}
		}
		log.debug("Context path: {} uri: {} content-type: {}",contextPath,uri, req.getContentType());
		return true;
	}
	
	private void pipe(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len = in.read(buffer);
		while (len != -1) {
		    out.write(buffer, 0, len);
		    len = in.read(buffer);
		}
	}
	protected String getLoginCSS(String fileName, ServletContext servletContext) throws IOException {
		String s = getFile(fileName,servletContext);
		String contextPath = servletContext.getContextPath();
		return s.replace("~CONTEXTPATH", contextPath);
	}
	
	private String getFile(String fileName,ServletContext servletContext) throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStream is = getStream(fileName,servletContext);
		while (true) {
			byte[] bytes = new byte[1000];
			int i = 0;
			try {
				i = is.read(bytes);
			} catch (IOException e) {
				throw new IOException("Failed to open "+fileName,e);
			}
			if (i == -1) {
				break;
			}
			sb.append(new String(bytes, 0, i));
		}
		return sb.toString();
	}

	private InputStream getStream(String fileName,ServletContext servletContext) throws IOException {
		InputStream is = servletContext.getResourceAsStream("/WEB-INF/"+fileName);
		if (is == null) {
			is = this.getClass().getResourceAsStream(fileName);
		}
		if (is == null) {
			throw new IOException("failed to find "+fileName);
		}
		return is;
	}
	
}
