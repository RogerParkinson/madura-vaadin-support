package nz.co.senanque.login;

import javax.servlet.annotation.WebFilter;

import org.springframework.web.filter.DelegatingFilterProxy;

@WebFilter(urlPatterns = "/*", filterName = "springSecurityFilterChain")
public class AuthenticationFilter extends DelegatingFilterProxy {

}
