/**
 * 
 */
package nz.co.senanque.login.users;

import java.util.Set;

/**
 * Object to represent a user and password
 * Demo ware, not for production.
 * 
 * @author Roger Parkinson
 *
 */
public class User {
	
	private final String m_name;
	private final String m_password;
	private final Set<String> m_permissions;

	public User(String name, String password, Set<String> permissions) {
		m_name = name;
		m_password = password;
		m_permissions = permissions;
	}
	
	public Set<String> test(String name, String password) {
		if (m_name.equals(name) && m_password.equals(password)) {
			return m_permissions;
		}
		return null;
	}

}
