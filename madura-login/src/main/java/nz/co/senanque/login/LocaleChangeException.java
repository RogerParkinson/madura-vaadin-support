package nz.co.senanque.login;

public class LocaleChangeException extends RuntimeException {
	
	private final String m_locale;
	
	public LocaleChangeException(String locale) {
		m_locale = locale;
	}

	public String getLocale() {
		return m_locale;
	}

}
