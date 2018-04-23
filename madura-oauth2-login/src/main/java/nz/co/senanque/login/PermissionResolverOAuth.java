package nz.co.senanque.login;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import nz.co.senanque.permissionmanager.PermissionResolver;
import nz.co.senanque.permissionmanager.PermissionResolverDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This gets the username and permissions from Spring Security. We make few assumptions about the underlying
 * Spring Security customisations, but we assume the Principal is a String containing the user name, that
 * the granted authorities from it map to Madura permissions, and that the user name is valid.
 * 
 * @author Roger Parkinson
 *
 */
public class PermissionResolverOAuth implements PermissionResolver {

	private static Logger log = LoggerFactory.getLogger(PermissionResolverOAuth.class);
	
	@Autowired private JwtTokenStore tokenStore;

	@PostConstruct
	public void init() {
		log.debug("initialized permission resolver oauth");
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		OAuth2AccessToken oauth2AccessToken = (OAuth2AccessToken)requestAttributes.getRequest().getSession().getAttribute(OAuth2Constants.ACCESS_TOKEN);
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		OAuth2Authentication authentication = tokenStore.readAuthentication(oauth2AccessToken);
		securityContext.setAuthentication(authentication);
		authentication.setAuthenticated(true);
		SecurityContextHolder.setContext(securityContext);
		log.debug("configuring permission manager");
	}

	@Override
	public PermissionResolverDTO unpackPermissions() {
		String userName = "No user";
		Set<String> currentPermissions = new HashSet<String>();
		SecurityContext sc = SecurityContextHolder.getContext();
		Authentication authentication = sc.getAuthentication(); // null
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof String) {
		    userName = (String)principal;
		    Collection<? extends GrantedAuthority>  authorities = authentication.getAuthorities();
		    for (GrantedAuthority ga: authorities) {
		    	// Madura can use this as a permission, though by default it is actually a ROLE.
		    	// The underlying implementation of Spring Security can be customised to implement
		    	// permissions.
		    	String authority = ga.getAuthority();
		    	log.debug("authority {}",authority);
		    	currentPermissions.add(authority);
		    }
		}
		return new PermissionResolverDTO(currentPermissions,userName);
	}
}
