package nz.co.senanque.permissionmanager;

/**
 * Implementations of this pull authentication and authorization data from the current request and load it into the
 * permission manager.
 * 
 * @author Roger Parkinson
 *
 */
public interface PermissionResolver {
	
	public PermissionResolverDTO unpackPermissions();

}
