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
package com.vaadin.addon.jpacontainer;

import java.io.Serializable;

import javax.persistence.EntityManager;

import nz.co.senanque.vaadin.tableeditor.Editor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * This extends the com.vaadin.addon.jpacontainer.JPAContainer to add Spring's Transactional capabilities. Create it as a bean like this:<br>
 *  
 * <pre>
 * &#64;PersistenceContext
 * private EntityManager entityManager;
 *
 * &#64;Bean(name="personContainer")
 * public JPAContainerEditor<Person> getPersonContainer() throws Exception {
 *  JPAContainerEditor<Person> container = new JPAContainerEditor<Person>(Person.class, entityManager);
 *  EntityProvider<Person> entityProvider = new MutableLocalEntityProvider<Person>(Person.class, entityManager); 
 *  container.setEntityProvider(entityProvider);
 *  return container;
 * }
 * </pre>
 * 
 * @author Roger Parkinson
 *
 */
public class JPAContainerEditor<T> extends JPAContainer<T> implements Editor<T>{

	private final EntityManager m_entityManager;

	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(JPAContainerEditor.class);
    protected Class<T> type;
    
    public JPAContainerEditor(Class<T> T, EntityManager entityManager) {
    	super(T);
    	type = T;
    	m_entityManager = entityManager;
    	log.debug("Constructor");
    }

	public T get(Serializable target) {
		log.debug("");
		T entity = (T) m_entityManager.find(type, target);
		return entity;
	}
	
	@Override
	@Transactional
    public Object addItem() throws UnsupportedOperationException {
        try {
            T newInstance = getEntityClass().newInstance();
            Object id = addEntity(newInstance);
            return id;
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        throw new UnsupportedOperationException();
    }

	@Override
	public Object addEntity(T entity) throws UnsupportedOperationException,
			IllegalStateException {
		assert entity != null : "entity must not be null";
		log.debug("");
		m_entityManager.persist(entity);
		m_entityManager.flush();
		Object id = getIdForPojo(entity);
        setFireItemSetChangeOnProviderChange(false); 
        // Prevent the container
        // from firing duplicate
        // events
        try {
            fireContainerItemSetChange(new ItemAddedEvent(id));
        } finally {
            setFireItemSetChangeOnProviderChange(true);
        }
        return id;
	}

	@Transactional
	public void merge(T entity) {
		assert entity != null : "entity must not be null";
		log.debug("");
		m_entityManager.merge(entity);
		m_entityManager.flush();
		Object id = getIdForPojo(entity);

        setFireItemSetChangeOnProviderChange(false); 
        // Prevent the container
        // from firing duplicate
        // events
        try {
            fireContainerItemSetChange(new ItemAddedEvent(id));
        } finally {
            setFireItemSetChangeOnProviderChange(true);
        }
	}

	@Override
	@Transactional
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        assert itemId != null : "itemId must not be null";
		log.debug("");
		T entity = m_entityManager.find(type, itemId);
		m_entityManager.remove(entity);
		m_entityManager.flush();
        setFireItemSetChangeOnProviderChange(false);
        try {
            fireContainerItemSetChange(new ItemRemovedEvent(itemId));
        } finally {
            setFireItemSetChangeOnProviderChange(true);
        }
        return true;
    }

    public Object getIdForPojo(T entity) {
		assert entity != null : "entity must not be null";
        return getEntityClassMetadata().getPropertyValue(entity,
				getEntityClassMetadata().getIdentifierProperty().getName());
        
    }
}
