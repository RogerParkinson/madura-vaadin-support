/**
 * 
 */
package nz.co.senanque.addressbook.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nz.co.senanque.addressbook.instances.Person;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainerEditor;
import com.vaadin.addon.jpacontainer.provider.MutableLocalEntityProvider;

/**
 * This is a Spring Configuration class that defines the beans needed for
 * the database containers. They get injected into the table layouts.
 * 
 * @author Roger Parkinson
 *
 */
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
public class ConfigContainer {

	@PersistenceContext
	private EntityManager entityManager;

	@Bean(name="personContainer")
	public JPAContainerEditor<Person> getPersonContainer() throws Exception {
		JPAContainerEditor<Person> container = new JPAContainerEditor<Person>(Person.class, entityManager);
		EntityProvider<Person> entityProvider = new MutableLocalEntityProvider<Person>(Person.class, entityManager); 
		container.setEntityProvider(entityProvider);
		return container;
	}
}
