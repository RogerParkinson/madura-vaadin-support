package nz.co.senanque.vaadin.permissionmanager;

import java.util.Set;

import nz.co.senanque.permissionmanager.PermissionManager;
import nz.co.senanque.permissionmanager.PermissionResolver;
import nz.co.senanque.permissionmanager.PermissionResolverDTO;

import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;

/**
 * @author Roger Parkinson
 *
 */
public class PermissionResolverLoginImpl implements PermissionResolver {

	@Override
	public PermissionResolverDTO unpackPermissions() {
		WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
    	String currentUser = (String)session.getAttribute(PermissionManager.USERNAME);
    	@SuppressWarnings("unchecked")
		Set<String> currentPermissions = (Set<String>)session.getAttribute(PermissionManager.PERMISSIONS);
    	return new PermissionResolverDTO(currentPermissions,currentUser);
	}

}
