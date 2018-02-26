package nz.co.senanque.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class LocaleSelectBuilder {
	
    private List<Locale> supportedLocales = new ArrayList<>();

    public String getLocaleSelect() {
    	String currentLanguage = getLanguageTag(Locale.getDefault());
    	StringBuilder sb = new StringBuilder();
    	for (Locale locale: supportedLocales) {
    		String language = getLanguageTag(locale);
    		sb.append("<option value=\"");
    		sb.append(language);
    		sb.append("\" style=\"background-image:url(login-resources/flags/");
    		sb.append(language);
    		sb.append(".png);\"");
    		if (currentLanguage.equals(language)) {
    			sb.append(" selected ");
    		}
    		sb.append(" >");
    		sb.append(language);
    		sb.append("</option>");
    	}
    	return sb.toString();
	}
    public String getLocale() {
    	String language = getLanguageTag(Locale.getDefault());
    	return "background-image:url(login-resources/flags/"+language+".png);";
    }
    @PostConstruct
    public void init() {
		ResourceBundleMessageSource labels = new ResourceBundleMessageSource();
		labels.setBasename("login");
		String locales = labels.getMessage("login.locales", null, Locale.getDefault());
		for (String s: StringUtils.tokenizeToStringArray(locales, "|")) {
			supportedLocales.add(new Locale(s));
		}
    }
    
    private String getLanguageTag(Locale locale) {
    	return locale.getLanguage();
    }
    

}
