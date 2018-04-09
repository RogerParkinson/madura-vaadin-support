package nz.co.senanque.login;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.stereotype.Component;

/**
 * @author Roger Parkinson
 * 
 * Adapted from an example at {@link https://github.com/danpersa/first-spring-mvc/blob/master/src/main/java/ro/danix/first/security/MyUserDetailService.java}
 * 
 * It uses an injected properties file (user.properties) to hold the list of users and permissions.
 *
 */
@Component
public class SimpleUserDetailsService implements UserDetailsService {
	
	private static Logger log = LoggerFactory.getLogger(SimpleUserDetailsService.class);

    @Autowired @Qualifier("users")
    private Properties userProperties;
    
    /* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
        String userPropsValue = userProperties.getProperty(username);
        if (userPropsValue == null) {
            throw new UsernameNotFoundException(username
                    + "User does not exist");
        }

        UserAttributeEditor configAttribEd = new UserAttributeEditor();
        configAttribEd.setAsText(userPropsValue);

        UserAttribute userAttributes = (UserAttribute) configAttribEd.getValue();

        return new User(username, userAttributes.getPassword(), userAttributes.isEnabled(), true, true, true, userAttributes.getAuthorities());
    }
    
    public void setUserProperties(Properties userProperties) {
        this.userProperties = userProperties;
    }

    public Properties getUserProperties() {
        return userProperties;
}

}
