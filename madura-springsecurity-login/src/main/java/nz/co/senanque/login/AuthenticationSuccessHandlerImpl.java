package nz.co.senanque.login;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AuthenticationSuccessHandlerImpl extends SimpleUrlAuthenticationSuccessHandler {

	private static Logger m_logger = LoggerFactory
			.getLogger(AuthenticationSuccessHandlerImpl.class);
    @Value("${nz.co.senanque.login.mobilePathPrefix:mobile}")
	private String m_mobilePathPrefix;
    @Value("${nz.co.senanque.login.targetURL:/app}")
	private String m_targetURL;
    
    @PostConstruct
    public void init() {
    	setDefaultTargetUrl(m_targetURL);
    }

//	@Override
//	public void onAuthenticationSuccess(HttpServletRequest request,
//			HttpServletResponse response, Authentication authentication)
//			throws IOException, ServletException {
//		String url = StringUtils.isEmpty(request.getContextPath())?"":request.getContextPath();
//		String pathPrefix = getPrefix(request);
//		response.setContentType("text/html; charset=UTF-8");
//		response.sendRedirect(url+"/app"+pathPrefix);
//	}

//	private String getPrefix(HttpServletRequest request) {
//		String userAgent = request.getHeader("user-agent").toLowerCase();
//		m_logger.debug("user-agent: {}",userAgent);
//        boolean mobileUserAgent = userAgent.contains("mobile");//||userAgent.contains("tablet");
//        if (mobileUserAgent) {
//        	return getDefaultTargetUrl()+"/"+m_mobilePathPrefix;
//        }
//        return getDefaultTargetUrl();
//	}
	protected String determineTargetUrl(HttpServletRequest request,
			HttpServletResponse response) {
		
		String userAgent = request.getHeader("user-agent").toLowerCase();
		m_logger.debug("user-agent: {}",userAgent);
        boolean mobileUserAgent = userAgent.contains("mobile");//||userAgent.contains("tablet");
        if (mobileUserAgent) {
        	return getDefaultTargetUrl()+"/"+m_mobilePathPrefix;
        }
        return getDefaultTargetUrl();
	}
}
