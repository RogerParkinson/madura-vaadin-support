package nz.co.senanque.vaadin;

import java.util.List;

/**
 * @author Roger Parkinson
 *
 */
public class AboutInfo {

	private String m_applicationVersion;
	private List<String> m_versions;

	public void setApplicationVersion(String applicationVersion) {
		m_applicationVersion = applicationVersion;
		
	}

	public void setVersions(List<String> versions) {
		m_versions = versions;
		
	}

	public String getApplicationVersion() {
		return m_applicationVersion;
	}

	public List<String> getVersions() {
		return m_versions;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder(m_applicationVersion);
		sb.append("<br/>");
		for (String s: m_versions) {
			sb.append("<br/>");
			sb.append(s);
		}
		return sb.toString();
	}

}
