package nz.co.senanque.login;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

//@Component
public class LoginLocaleResolver implements LocaleResolver {

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		String locale = request.getParameter("locale");
		if (locale != null) {
			return new Locale(locale);
		}
		return null;
	}

	@Override
	public void setLocale(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		response.setLocale(locale);
	}

}
