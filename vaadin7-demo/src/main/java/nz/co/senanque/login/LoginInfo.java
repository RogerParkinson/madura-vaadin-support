package nz.co.senanque.login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class LoginInfo {
	
    @Value("${nz.co.senanque.login.LoginInfo.title:title}")
    private transient String title;

    @Value("${nz.co.senanque.login.LoginInfo.logo:logo}")
    private transient String logo;

    @Value("${nz.co.senanque.login.LoginInfo.help:help}")
    private transient String help;

    @Value("${nz.co.senanque.login.LoginInfo.locales:locales}")
    private transient String locales;
    
    private List<Locale> supportedLocales = new ArrayList<>();

	@Autowired(required=false) @Qualifier("applicationVersion") private String m_applicationVersion;

	public String getApplicationVersion() {
		return (m_applicationVersion==null)?"no version info":m_applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		m_applicationVersion = applicationVersion;
	}

	public String getLogo() {
		return logo;
	}

	public String getHelp() {
		return help;
	}

	public List<Locale> getSupportedLocales() {
		return Collections.unmodifiableList(supportedLocales);
	}

	public String getTitle() {
		return title;
	}
	@PostConstruct
	public void init() {
		for (String s: StringUtils.tokenizeToStringArray(locales, "|")) {
			supportedLocales.add(new Locale(s));
		}
	}
	


}
