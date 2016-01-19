/**
 * 
 */
package nz.co.senanque.vaadin7demo;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;

/**
 * @author Roger Parkinson
 *
 */
@Component
public class SessionBean implements BeanFactoryAware {
	
	private static Logger m_logger = LoggerFactory.getLogger(SessionBean.class);

	@Autowired DefaultListableBeanFactory m_beanFactory;
	
	public SessionBean() {
		m_logger.info("instantiated");
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		m_beanFactory = (DefaultListableBeanFactory)beanFactory;
	}
	@PostConstruct
	public void init() {
		m_logger.info("init");
		for (String s: m_beanFactory.getBeanDefinitionNames()) {
			m_logger.info("{}",s);
		}
		m_logger.info("init-done");
	}
	
	public String toString() {
		return "hello from session bean";
	}

}
