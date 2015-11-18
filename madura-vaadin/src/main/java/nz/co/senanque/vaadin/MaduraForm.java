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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.validationengine.SetterListener;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * Extend this abstract class to implement a specific form.
 * Site specific form will include buttons (and their action code) and
 * specify what it is to be mapped to etc.
 * 
 * @author Roger Parkinson
 *
 */
@Deprecated
public class MaduraForm extends Form implements PropertiesSource {
	
    private static final long serialVersionUID = 1L;
    Logger logger = LoggerFactory.getLogger(MaduraForm.class);
	List<String> m_errors = new ArrayList<String>();
	List<Button> m_myButtons = new ArrayList<Button>();
	private List<MaduraPropertyWrapper> m_itemDataSourceProperties = new ArrayList<MaduraPropertyWrapper>();
	private List<String> m_fieldList;
	private final AbstractLayout m_layout;
	private FieldFactory m_maduraFieldFactory;
    private MaduraSessionManager m_maduraSessionManager;
	
    public MaduraForm(AbstractLayout layout,MaduraSessionManager maduraSessionManager)
    {
    	m_layout = layout;
    	m_maduraSessionManager = maduraSessionManager;
    	m_maduraFieldFactory = maduraSessionManager.getFieldFactory();
    	this.setFormFieldFactory(m_maduraFieldFactory);
    	setLayout(m_layout);
    	setLocale(LocaleContextHolder.getLocale());
        setBuffered(false);
        setImmediate(true);
    }
    public MaduraForm(MaduraSessionManager maduraSessionManager)
    {
        this(new VerticalLayout(),maduraSessionManager);
    }
	public void setFooter(HorizontalLayout layout) {
		HorizontalLayout l = new HorizontalLayout();
		l.addComponent(layout);
		l.setComponentAlignment(layout, Alignment.MIDDLE_CENTER);
		super.setFooter(l);
	}

    public List<MaduraPropertyWrapper> getItemDataSourceProperties()
	{
		return m_itemDataSourceProperties;
	}
    @SuppressWarnings("rawtypes")
	public void setItemDataSource(Item dataSource) {

        MaduraSessionManager maduraSessionManager = getMaduraSessionManager();
        if (dataSource instanceof BeanItem) 
        {
            Object source = ((BeanItem)dataSource).getBean();
            if (source instanceof ValidationObject)
            {
        		m_maduraSessionManager.getValidationSession().bind((ValidationObject)source);
                List<String> allFields = maduraSessionManager.getFieldList((ValidationObject)source,dataSource);
                List<String> fields = allFields;
                if (getFieldList() != null)
                {
                    fields = getFieldList();
                }
                super.setItemDataSource(dataSource, fields);
                getFooter().setVisible(true);
                List<MaduraPropertyWrapper> properties = new ArrayList<MaduraPropertyWrapper>();
                for (String fieldName: allFields)
                {
                    Property p = getItemProperty(fieldName);
                    if (p == null)
                    {
                    	p = maduraSessionManager.getMaduraPropertyWrapper((ValidationObject)source, fieldName);
                    }
                    if (p instanceof MaduraPropertyWrapper)
                    {
                        properties.add((MaduraPropertyWrapper)p);
                    }
                }
                m_itemDataSourceProperties = properties;
                for (Button button : m_myButtons)
                {
                    ButtonProperty buttonProperty = (ButtonProperty)button.getData();
                    buttonProperty.getPainter().setPropertiesSource(this);
                    buttonProperty.getPainter().paint(button);
                    maduraSessionManager.register(button, buttonProperty.getPainter());
                    MaduraPropertyWrapper wrapper = buttonProperty.getPainter().getProperty();
                    if (wrapper != null) {
                    	final Button finalButton = button;
                    	m_maduraSessionManager.getValidationSession().addListener(wrapper.getOwner(),wrapper.getName(), new SetterListener(){

                			@Override
                			public void run(ValidationObject object, String name,
                					Object newValue, ValidationSession session) {
                				finalButton.markAsDirty();
                			}});
                    }
                }
            }
            else
            {
                // some other kind of bean item
                super.setItemDataSource(dataSource,getFieldList());
                getFooter().setVisible(false);
            }
        }
        else
        {
            // Some other kind of item
            super.setItemDataSource(dataSource,getFieldList());
            getFooter().setVisible(false);
        }
    }
    public Button createButton(String name,
            ButtonPainter painter, ClickListener listener)
    {
       return createButton(name,painter,listener,null);
    }
    public Button createButton(String name, ButtonPainter painter)
    {
       return createButton(name,painter,null,null);
    }
    protected Button createButton(String name,
            ButtonPainter painter, ClickListener listener, Object data)
    {
       Button ret = m_maduraFieldFactory.createButton(name, listener, painter);
       painter.setPropertiesSource(this);
       m_myButtons.add(ret);
       return ret;
    }

	public void addExtraField(Item item, Object id)
	{
		final Property<?> property = item.getItemProperty(id);
        final Field<?> f = getFormFieldFactory().createField(item, id, this);
        if (f != null) {
            f.setPropertyDataSource(property);
            addField(id, f);
        }
	}
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
        logger.debug(" {} {}",source,variables);

    }
	public void setErrors(List<String> errors) {
		m_errors = errors;
	}
	public List<String> getErrors() {
		return m_errors;
	}
	protected List<String> getFieldList()
	{
		return m_fieldList;
	}
	public void setFieldList(List<String> fieldList) {
		m_fieldList = fieldList;
	}
	public void setFieldList(String[] fieldList) {
		m_fieldList = Arrays.asList(fieldList);
	}
    public void destroy()
    {
        MaduraSessionManager maduraSessionManager = getMaduraSessionManager();
        for (MaduraPropertyWrapper property: m_itemDataSourceProperties)
        {
            AbstractComponent field = (AbstractComponent)getField(property.getName());
            maduraSessionManager.deregister(field);
        }
        for (Button button : m_myButtons)
        {
            maduraSessionManager.deregister(button);
        }
    }
	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}
	public void dumpFields() {
		if (logger.isDebugEnabled()) {
			for (String propertyId: m_fieldList) {
				Field<?> field = getField(propertyId);
				logger.debug("Field {} Enabled: {} ReadOnly: {}",propertyId,field.isEnabled(),field.isReadOnly());
			}
		}
	}
	@Override
	public List<MaduraPropertyWrapper> getProperties() {
		return m_itemDataSourceProperties;
	}
	@Override
	public MaduraPropertyWrapper findProperty(String propertyName) {
		return m_maduraSessionManager.findProperty(propertyName, m_itemDataSourceProperties);
	}
}
