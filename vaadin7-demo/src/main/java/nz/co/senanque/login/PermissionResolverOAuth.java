package nz.co.senanque.login;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nz.co.senanque.permissionmanager.PermissionResolver;
import nz.co.senanque.permissionmanager.PermissionResolverDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * This gets the username and permissions from Spring Security. We make few assumptions about the underlying
 * Spring Security customisations, but we assume the Principal is a UserDetails object, that
 * the granted authorities from it map to Madura permissions, and that the user name in it is valid.
 * 
 * @author Roger Parkinson
 *
 */
public class PermissionResolverOAuth implements PermissionResolver {

	private static Logger log = LoggerFactory.getLogger(PermissionResolverOAuth.class);

	@Override
	public PermissionResolverDTO unpackPermissions() {
		String userName = "No user";
		Set<String> currentPermissions = new HashSet<String>();
		SecurityContext sc = SecurityContextHolder.getContext();
		Authentication authentication = sc.getAuthentication(); // null
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserDetails userDetails = null;
		if (principal instanceof UserDetails) {
		    userDetails = (UserDetails) principal;
		    userName = userDetails.getUsername();
		    Collection<? extends GrantedAuthority>  authorities = userDetails.getAuthorities();
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
