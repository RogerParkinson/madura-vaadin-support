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
package nz.co.senanque.vaadin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.metadata.ClassMetadata;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.ListSet;

/**
 * This is a simple container used to build demos. There is no DB backing, and it is very light weight.
 * 
 * @author Roger Parkinson
 *
 */
public class TableContainer<T> implements Container,Container.ItemSetChangeNotifier {

	private static final long serialVersionUID = -2991403428354955520L;
	private final ListSet<String> allItems = new ListSet<String>();
    private Map<String,TableRow> mappedItems = new HashMap<String,TableRow>();
    private Map<String,PropertyMetadata> m_properties = new HashMap<String,PropertyMetadata>();
	private final List<ItemSetChangeListener> m_listeners = new ArrayList<ItemSetChangeListener>();
	private final Class<?> m_class;
	public final List<T> m_list;
	public T m_t;
	
	public TableContainer(ValidationSession session, List<T> list, Class<?> clazz,MaduraSessionManager maduraSessionManager)
	{
	    m_list = list;
	    m_class = clazz;
        ClassMetadata classMetadata = session.getValidationEngine().getClassMetadata(m_class);
        for (PropertyMetadata propertyMetadata: classMetadata.getAllFields()) {
			m_properties.put(propertyMetadata.getLabelName(),propertyMetadata);
		}
		for (Object validationObject: list)
		{
			addModelItem(new TableRow((ValidationObject) validationObject,maduraSessionManager));
		}
	}

    @SuppressWarnings("unchecked")
	public void addModelItem(TableRow modelItem)
    {
    	String itemId = modelItem.getId();
        if (allItems.contains(itemId)) {
            return;
        }
        m_list.add((T)modelItem.getValidationObject());
    	allItems.add(itemId);
    	mappedItems.put(itemId, modelItem);
    	final Container me = this;
    	ItemSetChangeEvent event = new ItemSetChangeEvent(){

			private static final long serialVersionUID = -6431691952314985789L;

			public Container getContainer() {
				return me;
			}};
    	for (ItemSetChangeListener component: m_listeners)
    	{
    		component.containerItemSetChange(event);
    	}
    }

	public boolean addContainerProperty(Object propertyId, Class<?> type,
			Object defaultValue) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();	}

	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();	}

	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();	}

	public boolean containsId(Object modelItem) {
		return allItems.contains(modelItem);
	}

	public com.vaadin.data.Property getContainerProperty(Object itemId, Object propertyId) {
		return getItem(itemId).getItemProperty(propertyId);
	}

	public Item getItem(Object itemId) {
		return mappedItems.get(itemId);
	}

	public Collection<String> getItemIds() {
		return new ArrayList<String>(allItems);
	}

	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public boolean removeContainerProperty(Object propertyId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public boolean removeItem(Object itemId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return allItems.size();
	}

	@SuppressWarnings("unchecked")
	public T createItem() {
		try
        {
            return (T)m_class.newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
	}

	public void addListener(ItemSetChangeListener listener) {
		m_listeners.add(listener);	
	}

	public void removeListener(ItemSetChangeListener listener) {
		m_listeners.remove(listener);	
	}

	public String[] getLabels(String[] fieldList) {
		String[] ret = new String[fieldList.length];
		int index = 0;
		for (String fieldName: fieldList)
		{
		    PropertyMetadata propertyMetadata = m_properties.get(fieldName);
		    if (propertyMetadata != null)
		    {
		        ret[index++] = propertyMetadata.getLabelName();
		    }
		    else
		    {
		        ret[index++] = fieldName;
		    }
		}
		return ret;
	}

    public Collection<?> getContainerPropertyIds() {
        return m_properties.keySet();
    }


    public Class<?> getType(Object propertyId)
    {
        return m_properties.get(propertyId).getClazz();
    }

	@Override
	public void addItemSetChangeListener(ItemSetChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeItemSetChangeListener(ItemSetChangeListener listener) {
		// TODO Auto-generated method stub
		
	}
}
