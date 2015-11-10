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

import java.util.Date;
import java.util.List;

import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;


/**
 * The field factory uses the the injected hints to decide what widget to use to represent the
 * property. It also is responsible for adding the appropriate listeners to the field
 * 
 * @author Roger Parkinson
 *
 */
@SuppressWarnings("unchecked")
@SpringComponent("fieldFactory") // need the expanded name because there is a class called Component somewhere
@UIScope
public final class FieldFactory extends DefaultFieldFactory {
	
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(FieldFactory.class);
	
    @Autowired private Hints m_hints;
    private MaduraSessionManager m_maduraSessionManager;

    public FieldFactory()
	{
	}
	
    public Hints getHints() {
		return m_hints;
	}
	public void setHints(Hints hints) {
		m_hints = hints;
	}

	public Field<?> createField(Item item, Object propertyId,
            Component uiContext) {

	    logger.debug("creating field for {}",propertyId);
	    boolean readOnly = false;
	    if (uiContext instanceof MaduraForm) {
	    	readOnly = ((MaduraForm)uiContext).isReadOnly();
	    }
	    com.vaadin.data.Property<?> property = item.getItemProperty(propertyId);
        Field<?> ret;
        if (property instanceof MaduraPropertyWrapper)
        {
            MaduraPropertyWrapper maduraProperty = (MaduraPropertyWrapper)property;
            ret = createFieldByPropertyType(maduraProperty);
//            logger.debug("created field {} class {} value {}",new Object[]{propertyId,ret.getClass().getName(),ret.getValue()});
        }
        else if (item instanceof BeanItem) {
        	MaduraPropertyWrapper maduraProperty = getMaduraSessionManager().getMaduraPropertyWrapper((ValidationObject)((BeanItem<?>)item).getBean(), propertyId.toString());
        	if (maduraProperty == null) {
        		logger.warn("property {} is not mapped",propertyId);
        		return null;
        	} else {
            ret = createFieldByPropertyType(maduraProperty);
//            logger.debug("created field {} class {} value {}",new Object[]{propertyId,ret.getClass().getName(),ret.getValue()});
        	}
        }
        else {
        	// this probably never gets called
            Field<?> field = super.createField(item, propertyId, uiContext);
	        field.setWidth(getHints().getWidth());
	        field.setBuffered(false);
	        field.setPropertyDataSource(property);
	        ret = field;
        }
        if (readOnly) {
        	ret.setReadOnly(true);
        	ret.setCaption("");
        	ret.setStyleName("readOnly");
        	ret.setEnabled(false);
        }
        return ret;
    }
    public Field<?> createFieldByPropertyType(MaduraPropertyWrapper property) {
    	return createFieldByPropertyType(property,getHints());
    }
    public Field<?> createFieldByPropertyType(MaduraPropertyWrapper property, Hints hints) {
        // Null typed properties can not be edited
    	Class<?> type = property.getDataType();
        if (type == null) {
            return null;
        }

        // Item field: we never create item fields
        if (Item.class.isAssignableFrom(type)) {
            return null;
        }
        
        if (List.class.isAssignableFrom(type)) {
            return null;
        }
        
        AbstractField<?> ret = null;

        // Date field
        if (Date.class.isAssignableFrom(type)) {
        	ret = hints.getDateField(property);
        }

        // Boolean field
        if (Boolean.class.isAssignableFrom(type) || type == boolean.class) {
            ret = hints.getBooleanField(property);
        }

        // List field
        int size = 0;
        List<ChoiceBase> choices = property.getAvailableValues();
        if (choices != null)
        {
            size = choices.size();
        }
        if (List.class.isAssignableFrom(type) || size > 0) {
        	ret = hints.getSelectField(property);
        	ret.setImmediate(true);
        	ret.setBuffered(false);
        }

        if (ret == null)
        {
        	ret = hints.getTextField(property);
        }
        ret.setInvalidCommitted(true);
    	getMaduraSessionManager().bind(getParentForm(ret), ret, property);
        return ret;
    }
    public Button createButton(String name, 
            ClickListener listener) {
        return createButton(name,listener,new SimpleButtonPainter(m_maduraSessionManager),getHints());
    }
    public Button createButton(String name,
            ClickListener listener, ButtonPainter painter) {
        return createButton(name,listener,painter,getHints());
    }
    public Button createButton(String name,
            ClickListener listener, ButtonPainter painter, Hints hints) {
		Button ret = hints.getButtonField(name, painter.getMessageSource());
		if (listener != null) {
			ret.addClickListener(listener);
		}
		if (painter != null)
		{
		    getMaduraSessionManager().register(ret, painter);
		}
		return ret;
	}
	private MaduraForm getParentForm(AbstractField<?> field)
	{
		Component parent = field.getParent();
		while (parent != null)
		{
			parent = parent.getParent();
			if (parent instanceof MaduraForm)
			{
				break;
			}
		}
		return (MaduraForm)parent;
	}
	
	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}

	public void setMaduraSessionManager(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
	}

}
