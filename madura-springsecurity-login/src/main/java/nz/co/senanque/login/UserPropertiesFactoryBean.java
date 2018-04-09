package nz.co.senanque.login;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Roger Parkinson
 *
 */
@Component("users")
public class UserPropertiesFactoryBean implements FactoryBean<Properties> {
	
	private static Logger log = LoggerFactory.getLogger(UserPropertiesFactoryBean.class);

	@Autowired @Qualifier("default-users")
    private Properties defaultUserProperties;

	@Override
	public Properties getObject() throws Exception {
    	Resource resource = new ClassPathResource("users.properties");
    	if (resource.exists()) {
    		try {
				Properties properties = new Properties();
				properties.load(resource.getInputStream());
				return properties;
			} catch (IOException e) {
				log.error("Failed to read user.properties, falling back to default-user.properties");
			}
    	}
		return defaultUserProperties;
	}

	@Override
	public Class<?> getObjectType() {
		return Properties.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
