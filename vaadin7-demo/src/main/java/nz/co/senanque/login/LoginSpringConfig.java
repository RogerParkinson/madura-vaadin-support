package nz.co.senanque.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextAttributeExporter;

@Configuration
public class LoginSpringConfig {
	
//    @Value("${nz.co.senanque.login.LoginInfo.title:title}")
//    private transient String title;
//
//    @Value("${nz.co.senanque.login.LoginInfo.logo:logo}")
//    private transient String logo;
//
//    @Value("${nz.co.senanque.login.LoginInfo.help:help}")
//    private transient String help;

    @Value("${nz.co.senanque.login.LoginInfo.locales:locales}")
    private transient String locales;
    
    private List<Locale> supportedLocales = new ArrayList<>();

	@Autowired(required=false) @Qualifier("applicationVersion") private String m_applicationVersion;

	@Bean
	public ServletContextAttributeExporter getServletContextAttributeExporter() {
		ResourceBundle labels = ResourceBundle.getBundle("login");
		for (String s: StringUtils.tokenizeToStringArray(locales, "|")) {
			supportedLocales.add(new Locale(s));
		}
		ServletContextAttributeExporter ret = new ServletContextAttributeExporter();
		Map<String,Object> attributes = new HashMap<>();
		attributes.put("applicationVersion", (m_applicationVersion==null)?"no version":m_applicationVersion);
//		attributes.put("title", title);
//		attributes.put("logo", logo);
//		attributes.put("help", help);
		attributes.put("supportedLocales", supportedLocales);
		attributes.put("labels", labels);
		ret.setAttributes(attributes);
		return ret;
	}
}
