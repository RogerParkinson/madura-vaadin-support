/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.vaadin.tableeditor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainerEditor;
import com.vaadin.addon.jpacontainer.provider.MutableLocalEntityProvider;

/**
 * Creates a {@link com.vaadin.addon.jpacontainer.JPAContainerEditor}. It assumes there is only one
 * entity manager and uses that. For more complex cases you should clone this class.
 * 
 * Configure it like this:
 *  
 *   &lt;bean id="personContainer" class="com.vaadin.addon.jpacontainer.JPAContainerEditorFactory"&gt;
 *   	&lt;property name="type" value="nz.co.senanque.addressbook.instances.Person"/&gt;
 *   &lt;/bean&gt;
 * 
 * @author Roger Parkinson
 *
 */
//@Component
public class JPAContainerEditorFactory<T>  implements FactoryBean<JPAContainerEditor<T>>{

	@PersistenceContext
	private EntityManager entityManager;
	@Value("${nz.co.senanque.vaadin.tableeditor.JPAContainerEditorFactory.type}")
	private Class<T> m_type;

	public JPAContainerEditor<T> getObject() throws Exception {
		JPAContainerEditor<T> container = new JPAContainerEditor<T>(m_type, entityManager);
		EntityProvider<T> entityProvider = new MutableLocalEntityProvider<T>(m_type, entityManager); 
		container.setEntityProvider(entityProvider);
		return container;
	}

	@SuppressWarnings("rawtypes")
	public Class<JPAContainerEditor> getObjectType() {
		return JPAContainerEditor.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public Class<T> getType() {
		return m_type;
	}

	public void setType(Class<T> type) {
		m_type = type;
	}
}
