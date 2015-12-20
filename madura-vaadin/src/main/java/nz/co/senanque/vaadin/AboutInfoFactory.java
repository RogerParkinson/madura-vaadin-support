package nz.co.senanque.vaadin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import nz.co.senanque.version.Version;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author Roger Parkinson
 *
 */
@Component("aboutInfo")
public class AboutInfoFactory implements FactoryBean<AboutInfo>, BeanFactoryAware {

	@Autowired(required=false) @Qualifier("applicationVersion") private String m_applicationVersion;
	private DefaultListableBeanFactory m_beanFactory;
	private AboutInfo m_aboutInfo = new AboutInfo();

	@Override
	public AboutInfo getObject() throws Exception {
		return m_aboutInfo;
	}

	@Override
	public Class<?> getObjectType() {
		return AboutInfo.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		m_beanFactory = (DefaultListableBeanFactory)arg0;
	}
	@PostConstruct
	public void init() {
		m_aboutInfo.setApplicationVersion(m_applicationVersion);
		List<String> versions = new ArrayList<>();
		Map<String, Version> versionMap = m_beanFactory.getBeansOfType(nz.co.senanque.version.Version.class);
		for (Map.Entry<String, Version> entry: versionMap.entrySet()) {
			String v = entry.getKey()+": ";
			String v1 = entry.getValue().getVersion();
			if (v1 != null) {
				v=v+v1;
			}
			versions.add(v);
		}
		versions.add("VaadinVersion: "+com.vaadin.shared.Version.getFullVersion());
		m_aboutInfo.setVersions(versions);
	}

}
