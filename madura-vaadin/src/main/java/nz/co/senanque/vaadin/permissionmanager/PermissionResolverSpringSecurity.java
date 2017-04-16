package nz.co.senanque.vaadin.permissionmanager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * This gets the username and permissions from Spring Security. We make few assumptions about the underlying
 * Spring Security customisations, but we assume the Principal is a UserDetails object, that
 * that the granted authorities from it map to Madura permissions, and that the user name in it is valid.
 * 
 * The dependency on Spring Security is scope=provided because this is optional. The class needs to
 * be configured as a Spring Bean to be active.
 * 
 * @author Roger Parkinson
 *
 */
public class PermissionResolverSpringSecurity implements PermissionResolver {

	private static Logger log = LoggerFactory.getLogger(PermissionResolverSpringSecurity.class);

	@Override
	public PermissionResolverDTO unpackPermissions() {
		String userName = "No user";
		Set<String> currentPermissions = new HashSet<String>();
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
