/**
 * 
 */
package nz.co.senanque.vaadindemo;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
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

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		m_beanFactory = (DefaultListableBeanFactory)beanFactory;
	}
	@PostConstruct
	public void init() {
		StringBuilder sb = new StringBuilder("\n");
		for (String s: m_beanFactory.getBeanDefinitionNames()) {
			sb.append(s).append(" ");
			BeanDefinition bd = m_beanFactory.getBeanDefinition(s);
			sb.append(bd.getBeanClassName()).append(" ");
			sb.append("\"").append(bd.getScope()).append("\"").append("\n");
		}
		m_logger.info("app beans: {}",sb);
	}
	
	public String toString() {
		return "hello from session bean";
	}

}
