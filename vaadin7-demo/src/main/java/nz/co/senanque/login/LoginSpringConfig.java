package nz.co.senanque.login;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.context.support.ServletContextAttributeExporter;

@Configuration
public class LoginSpringConfig {
	
	@Autowired(required=false) @Qualifier("applicationVersion") private String m_applicationVersion;
	@Autowired LocaleSelectBuilder localeSelectBuilder;

	@Bean
	public ServletContextAttributeExporter getServletContextAttributeExporter() {
		ResourceBundleMessageSource labels = new ResourceBundleMessageSource();
		labels.setBasename("login");
		ServletContextAttributeExporter ret = new ServletContextAttributeExporter();
		Map<String,Object> attributes = new HashMap<>();
		attributes.put("applicationVersion", (m_applicationVersion==null)?"no version":m_applicationVersion);
		attributes.put("localeSelectBuilder", localeSelectBuilder);
		attributes.put("labels", labels);
		ret.setAttributes(attributes);
		return ret;
	}
	
}
