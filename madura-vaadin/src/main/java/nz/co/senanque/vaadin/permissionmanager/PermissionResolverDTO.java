package nz.co.senanque.vaadin.permissionmanager;

import java.util.Set;

public class PermissionResolverDTO {
	
	private final Set<String> m_currentPermissions;
	private final String m_userName;

	public PermissionResolverDTO(Set<String> currentPermissions, String userName) {
		m_currentPermissions = currentPermissions;
		m_userName = userName;
	}

	public Set<String> getCurrentPermissions() {
		return m_currentPermissions;
	}

	public String getUserName() {
		return m_userName;
	}

}
