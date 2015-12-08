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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import nz.co.senanque.vaadin.application.MaduraConverterFactory;
import nz.co.senanque.vaadin.application.PropertyNotFoundException;
import nz.co.senanque.vaadin.permissionmanager.PermissionManager;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.LocaleAwareExceptionFactory;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.SetterListener;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * 
 * All of the Madura session management is done here. You register fields (or whole forms) with the manager.
 * Then you bind specific instances of the object graph to these fields.
 * There is one of these per Madura Session
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
@Component("maduraSessionManager")
@UIScope
public class MaduraSessionManager implements Serializable, MessageSourceAware, InitializingBean
{
	private static final long serialVersionUID = -1L;
	private static Logger logger = LoggerFactory.getLogger(MaduraSessionManager.class);
	private Map<Integer,AbstractComponent> m_fields = new HashMap<>();
	private Map<Integer,Label> m_labels = new HashMap<>();
    @Autowired(required=false) private transient ValidationEngine m_validationEngine;
    private transient ValidationSession m_validationSession;
    @Autowired private transient FieldFactory m_formFieldFactory;
    @Autowired transient PermissionManager m_permissionManager;
    @Autowired transient MaduraConverterFactory m_maduraConverterFactory;
    @Autowired private transient Hints m_hints;
    @Autowired private transient LocaleAwareExceptionFactory m_localeAwareExceptionFactory;
	private transient MessageSource m_messageSource;
	
	// Used for internal testing, should be left false.
    @Value("${nz.co.senanque.vaadin.application.MaduraSessionManager.suppressUpdates:false}")
    private transient boolean m_suppressUpdates;
    
	@PostConstruct
	public void init() {
		VaadinSession session = VaadinSession.getCurrent();
		session.setConverterFactory(getMaduraConverterFactory());
	}
    private class MenuItemWrapper extends AbstractComponent {
    	
    	private static final long serialVersionUID = -1L;
		private final MenuItemPainter m_menuItemPainter;

		private MenuItemWrapper(MenuItem menuItem, MenuItemPainter menuItemPainter)
		{
			m_menuItemPainter = menuItemPainter;
			setData(menuItem);
		}

		public MenuItemPainter getMenuItemPainter() {
			return m_menuItemPainter;
		}
    }

	private void registerWidget(AbstractComponent field) {
		int key = System.identityHashCode(field);
		if (m_fields.get(key) == null) {
			m_fields.put(key,field);
//			logger.debug("MaduraSessionManager {} Registering field {} {}",getValidationEngine().getIdentifier(),key,field.getCaption());
		}
	}
	private void registerWidget(MenuItem field, MenuItemPainter bp) {
		Collection<AbstractComponent> fields = getFields();
		for (AbstractComponent foundField: fields)
		{
			if (foundField instanceof MenuItemWrapper)
			{
				MenuItemWrapper menuItemWrapper = (MenuItemWrapper)foundField;
				if (menuItemWrapper.getMenuItemPainter() == bp)
				{
					// we found this item already in the list so reuse it
					// rather than create a new entry. This saves us having to deregister and
					// recreate whenever the menu reconfigures.
					menuItemWrapper.setData(field);
					return;
				}
			}
		}
		registerWidget(new MenuItemWrapper(field, bp));
	}
	public void register(Label field) {
		int key = System.identityHashCode(field);
		if (m_labels.get(key) == null) {
			m_labels.put(key,field);
		}
	}
	public void deregister(AbstractComponent field) {
		m_fields.remove(System.identityHashCode(field));
		m_labels.remove(System.identityHashCode(field));
	}

	/**
	 * Check all the other fields to ensure they are still valid
	 * this includes any buttons that were registered because they
	 * get disabled if there are errors on the relevant form or
	 * if the required-ness is incomplete.
	 * 
	 * @param field
	 */
	public void updateOtherFields(AbstractComponent field) {
		
		if (m_suppressUpdates) {
			return;
		}
		PermissionManager permissionmanager = getPermissionManager();
		Collection<AbstractComponent> fields = getFields();
		Collection<Label> labels = getLabels();
		for (Label fieldx: labels)
		{
		    Property<?> p = fieldx.getPropertyDataSource();
			if (p != null && p instanceof LabelProperty<?>)
			{
				fieldx.markAsDirty();
			}
		}
		for (AbstractComponent fieldx: fields)
		{
			if (fieldx.equals(field)) continue;
			if ((fieldx instanceof Button) && !(fieldx instanceof CheckBox))
			{
				ButtonProperty p = (ButtonProperty)fieldx.getData();
				if (p != null && p instanceof ButtonProperty)
				{
					p.getPainter().paint((Button)fieldx);
					fieldx.markAsDirty();
				}
				continue;
			}
			if (fieldx instanceof MenuItemWrapper)
			{
				MenuItemPainter menuItemPainter = ((MenuItemWrapper)fieldx).getMenuItemPainter();
			    MenuItem menuItem = (MenuItem)fieldx.getData();
				if (menuItemPainter != null)
				{
					menuItemPainter.paint(menuItem);
					fieldx.markAsDirty();
				}
				continue;
			}
			if (fieldx instanceof AbstractField<?>)
			{
				AbstractField<?> abstractField = (AbstractField<?>)fieldx;
				MaduraPropertyWrapper property=null;
			try {
				property = (MaduraPropertyWrapper)abstractField.getPropertyDataSource();
			} catch (Exception e) {
				// ignore
				property = null;
			}
			if (property != null)
			{
				if (logger.isDebugEnabled())
				{
					logger.debug("evaluating field: {}",property.getName());
					if (abstractField.isEnabled()!=property.isEnabled())
					{
						logger.debug("Enabled: {} {}",abstractField.isEnabled(),property.isEnabled());
					}
					if (abstractField.isReadOnly()!=property.isReadOnly())
					{
						logger.debug("ReadOnly: {} {}",abstractField.isReadOnly(),property.isReadOnly());
					}
					if (abstractField.isRequired()!=property.isRequired())
					{
						logger.debug("Required: {} {}",abstractField.isRequired(),property.isRequired());
					}
					if (abstractField.isVisible()!=property.isVisible())
					{
						logger.debug("Visible: {} {}",abstractField.isVisible(),property.isVisible());
					}
				}
				boolean parentReadOnly = getParentReadOnly(abstractField);
				abstractField.setEnabled(parentReadOnly?false:property.isEnabled());
				abstractField.setReadOnly(parentReadOnly?true:property.isReadOnly());
				abstractField.setRequired(property.isRequired());
				abstractField.setVisible(property.isVisible());
				// Permissions trump rules
		        if (!permissionmanager.hasPermission(property.getReadPermission()))
		        {
		        	abstractField.setVisible(false);
		        }
		        if (!permissionmanager.hasPermission(property.getWritePermission()))
		        {
		        	abstractField.setEnabled(false);
		        }
				if (abstractField instanceof AbstractSelect)
				{
					AbstractSelect select = (AbstractSelect)abstractField;
					List<ChoiceBase> availableList = new ArrayList<ChoiceBase>();
					for (ChoiceBase v:property.getAvailableValues())
					{
						availableList.add(v);
					}
	                logger.debug("{} availableList {}",property.getName(), availableList);
					Collection<?> itemIds = select.getItemIds();
					List<Object> killList = new ArrayList<Object>();
					for (Object itemId:itemIds)
					{
						if (availableList.contains(itemId)) continue;
						killList.add(itemId);
					}
					for (Object kill: killList)
					{
						select.removeItem(kill);
					}
					for (ChoiceBase cb: availableList)
					{
						select.addItem(cb);
					}
					logger.debug("Select {} value \"{}\", updated to {}",new Object[]{property.getName(), select.getValue(), select.getItemIds()});
				}
			}
			abstractField.markAsDirty();
			}
		}
	}
	
//	public void updatePermissions() {
//		updatePermissions(VaadinService.getCurrentRequest());
//	}
//
//	public void updatePermissions(VaadinRequest vaadinRequest) {
//    	String currentUser = (String)vaadinRequest.getWrappedSession().getAttribute(AuthenticationDelegate.USERNAME);
//    	@SuppressWarnings("unchecked")
//		Set<String> currentPermissions = (Set<String>)vaadinRequest.getWrappedSession().getAttribute(AuthenticationDelegate.PERMISSIONS);
//    	getPermissionManager().setPermissionsList(currentPermissions);
//    	getPermissionManager().setCurrentUser(currentUser);
//	}

	private boolean getParentReadOnly(AbstractComponent abstractField) {
		AbstractComponent parent = (AbstractComponent)abstractField.getParent();
		if (parent == null) {
			return false;
		}
		if (parent instanceof PropertiesSource) {
			return ((PropertiesSource)parent).isReadOnly();
		}
		return getParentReadOnly(parent);
	}
	public Collection<AbstractComponent> getFields() {
		if (logger.isDebugEnabled()) {
			logger.debug("fetching registered fields for MaduraSessionManager {}",
					getValidationEngine().getIdentifier());
			for (Entry<Integer,AbstractComponent> entry: m_fields.entrySet()) {
				logger.debug("{}={}",entry.getKey(),entry.getValue().getCaption());
			}
			logger.debug("--------");
		}
		return Collections.unmodifiableCollection(m_fields.values());
	}
	private Collection<Label> getLabels() {
		return Collections.unmodifiableCollection(m_labels.values());
	}
	public Hints getHints() {
		return m_hints;
	}

	public void setHints(Hints hints) {
		m_hints = hints;
	}

	public List<MaduraPropertyWrapper> getFieldList(ValidationObject validationObject)
    {
        List<MaduraPropertyWrapper> ret = new ArrayList<MaduraPropertyWrapper>();
        ObjectMetadata objectMetadata = validationObject.getMetadata();
        Collection<PropertyMetadata> propertyMetadata = objectMetadata.getAllPropertyMetadata();
		for (PropertyMetadata property: propertyMetadata)
		{
            Method getter = property.getGetMethod();
            Method setter = property.getSetMethod();
            FieldMetadata fieldMetadata = objectMetadata.getFieldMetadata(property.getName());
            ret.add(new MaduraPropertyWrapper(fieldMetadata,
                    validationObject, setter, getter, getHints(),m_messageSource));
        }
        return ret;
    }
    public List<String> getFieldList(ValidationObject validationObject, Item dataSource)
    {
        List<String> ret = new ArrayList<String>();
        ObjectMetadata objectMetadata = validationObject.getMetadata();
        Collection<PropertyMetadata> propertyMetadata = objectMetadata.getAllPropertyMetadata();
		for (PropertyMetadata property: propertyMetadata)
		{
			String fieldName = property.getName();
            Method getter = property.getGetMethod();
            Method setter = property.getSetMethod();
            FieldMetadata fieldMetadata;
            try {
            	fieldMetadata = objectMetadata.getFieldMetadata(property.getName());
                dataSource.removeItemProperty(fieldName);
                dataSource.addItemProperty(fieldName, new MaduraPropertyWrapper(fieldMetadata,validationObject,setter, getter, getHints(),m_messageSource));
            }
            catch (NullPointerException e)
            {
                logger.info("property {} not bound to Madura Objects",fieldName);
            }
            ret.add(fieldName);
        }
        return ret;
    }
    public MaduraPropertyWrapper getMaduraPropertyWrapper(FieldMetadata fieldMetadata)
    {
    	ProxyField pf = fieldMetadata.getValidationSession().getProxyField(fieldMetadata);
    	return getMaduraPropertyWrapper((ValidationObject)pf.getProxyObject().getObject(),pf.getFieldName());
    }
    public MaduraPropertyWrapper getMaduraPropertyWrapper(ValidationObject validationObject,
            String propertyName)
    {
        Hints hints = getHints();
        ObjectMetadata objectMetadata = validationObject.getMetadata();
        
        Collection<PropertyMetadata> propertyMetadata = objectMetadata.getAllPropertyMetadata();
		for (PropertyMetadata property: propertyMetadata)
		{
			String fieldName = property.getName();
			if (!fieldName.equals(propertyName)) {
				continue;
			}
            Method getter = property.getGetMethod();
            Method setter = property.getSetMethod();
            FieldMetadata fieldMetadata;
            try
            {
                fieldMetadata = objectMetadata.getFieldMetadata(fieldName);
                return new MaduraPropertyWrapper(fieldMetadata, validationObject, setter,
                        getter, hints,m_messageSource);
            }
            catch (NullPointerException e)
            {
                return new MaduraPropertyWrapper(propertyName, validationObject, setter,
                        getter, hints,m_messageSource);
            }
        }
        throw new PropertyNotFoundException(propertyName);
    }
    
    public MaduraFieldGroup createMaduraFieldGroup() {
    	return new MaduraFieldGroupImpl(this);
    }

    public void bind(final PropertiesSource form, final AbstractField<?> field,
            ValidationObject validationObject, String propertyName)
    {
        MaduraPropertyWrapper property = getMaduraPropertyWrapper(validationObject, propertyName);
        if (property == null)
        {
        	throw getLocaleAwareExceptionFactory().getRuntimeException("property.not.found", new Object[]
                    { validationObject.getClass().getName()});
        }
        bind(form, field, property);
    }
    public void bind(final AbstractField<?> field,
            ValidationObject validationObject, String propertyName) {
    	bind(null,field,validationObject,propertyName);
    }

    public void bind(final AbstractField<?> field,
            MaduraPropertyWrapper property)
    {
    	bind(null,field,property);
    }
    public void bind(final PropertiesSource form1, final AbstractField<?> field,
            MaduraPropertyWrapper property)
        {
        field.setPropertyDataSource(property);
        Hints hints = getHints();
        
        hints.setCommonProperties(field, property,m_messageSource);
        setPermissions(property, field);
        registerWidget(field);
		getValidationSession().addListener(property.getOwner(),property.getName(), new SetterListener(){

			@Override
			public void run(ValidationObject object, String name,
					Object newValue, ValidationSession session) {
				com.vaadin.ui.ProtectedMethods.fireValueChange(field);
				
			}});
        field.addValueChangeListener(new MaduraPropertyWrapper.ValueChangeListener()
        {

			private static final long serialVersionUID = -1L;

			public void valueChange(ValueChangeEvent event)
            {
                Property<?> p = field.getPropertyDataSource();
				logger.debug("Changed value ");
                if (p instanceof MaduraPropertyWrapper)
                {
                    MaduraPropertyWrapper property = (MaduraPropertyWrapper) p;
                    if (property.getErrorText() != null)
                    {
                        field.setComponentError(new UserError(property
                                .getErrorText()));
                    }
                    else
                    {
                        field.setComponentError(null);
                    }
                }
                updateOtherFields(field);
            }
        });
    }
    public void register(final AbstractComponent field)
    {
        if (field instanceof Button)
        {
            throw new RuntimeException("Attempted to register a button without a Button Painter");
        }
        registerWidget(field);
        if (field instanceof AbstractField<?>) {
        	final AbstractField<?> abstractField = (AbstractField<?>)field;
        	abstractField.addValueChangeListener(new MaduraPropertyWrapper.ValueChangeListener()
	        {
	
	            private static final long serialVersionUID = -1L;
	
				public void valueChange(ValueChangeEvent event)
	            {
					logger.debug("Changed value ");
	                Property<?> p = abstractField.getPropertyDataSource();
	                if (p instanceof MaduraPropertyWrapper)
	                {
	                    MaduraPropertyWrapper property = (MaduraPropertyWrapper) p;
	                    if (property.getErrorText() != null)
	                    {
	                        field.setComponentError(new UserError(property
	                                .getErrorText()));
	                    }
	                    else
	                    {
	                        field.setComponentError(null);
	                    }
	                }
	                updateOtherFields(abstractField);
	            }
	        });
        }
    }

    public void register(final Button field, ButtonPainter painter)
    {
        MaduraPropertyWrapper property = painter.getProperty();        
        ButtonProperty bp = new ButtonProperty(painter,field.getCaption(),m_messageSource);
        field.setData(bp);
        if (property != null)
        {
            Hints hints = getHints();
            hints.setCommonProperties(field, property,m_messageSource);
            setPermissions(property, field);
        }
        registerWidget(field);
    }
    
    public void register(final MenuItem menuItem)
    {
    	Command command = menuItem.getCommand();
    	if (command instanceof CommandExt)
    	{
    		CommandExt commandExt = (CommandExt)command;
    		MenuItemPainter painter = commandExt.getPainter();
            MaduraPropertyWrapper property = painter.getProperty();
            if (property != null)
            {
                Hints hints = getHints();
                hints.setCommonProperties(menuItem, property,m_messageSource);
                setPermissions(property, menuItem);
            }
            registerWidget(menuItem,painter);
    	}
    }
    
    public void bind (final Button button)
    {
        ButtonProperty buttonProperty = (ButtonProperty)button.getData();
        button.setCaption(buttonProperty.getCaption());
        buttonProperty.getPainter().paint(button);  
        MaduraPropertyWrapper wrapper = buttonProperty.getPainter().getProperty();
        if (wrapper != null) {
        	getValidationSession().addListener(wrapper.getOwner(),wrapper.getName(), new SetterListener(){

    			@Override
    			public void run(ValidationObject object, String name,
    					Object newValue, ValidationSession session) {
    				button.markAsDirty();
    			}});
        }
    }

    public void bind (final MenuItem menuItem)
    {
    	Command command = menuItem.getCommand();
    	if (command instanceof CommandExt)
    	{
    		CommandExt commandExt = (CommandExt)command;
    		MenuItemPainter painter = commandExt.getPainter();
    		painter.paint(menuItem);
    	}
    }

    public void setPermissions(MaduraPropertyWrapper property, AbstractComponent field)
    {
		PermissionManager permissionmanager = getPermissionManager();
        if (!permissionmanager.hasPermission(
                property.getWritePermission()))
        {
            field.setReadOnly(true);
        }
        if (!permissionmanager.hasPermission(
                property.getReadPermission()))
        {
            field.setVisible(false);
        }
    }

    public void setPermissions(MaduraPropertyWrapper property, MenuItem field)
    {
		PermissionManager permissionmanager = getPermissionManager();
        if (!permissionmanager.hasPermission(
                property.getWritePermission()))
        {
            field.setEnabled(false);
        }
        if (!permissionmanager.hasPermission(
                property.getReadPermission()))
        {
            field.setVisible(false);
        }
    }

    public MaduraPropertyWrapper findProperty(String propertyName, List<MaduraPropertyWrapper> properties)
    {
        for (MaduraPropertyWrapper property: properties)
        {
            if (property.getName().equals(propertyName))
            {
                return property;
            }
        }
        String message = org.slf4j.helpers.MessageFormatter.arrayFormat("Property named '{}' not found in list", new Object[]{propertyName}).getMessage();
    	throw new RuntimeException(message);
    }
    
    @PreDestroy
    public void close()
    {
        if (m_validationSession == null)
        {
        	return;
        }
        m_validationSession.close();
        m_validationSession = null;
        m_labels.clear();
        m_fields.clear();
    }
    public ValidationEngine getValidationEngine()
    {
        return m_validationEngine;
    }
    public void setValidationEngine(ValidationEngine validationEngine)
    {
        m_validationEngine = validationEngine;
    }
    public ValidationSession getValidationSession()
    {
        if (m_validationSession == null)
        {
            m_validationSession = getValidationEngine().createSession(LocaleContextHolder.getLocale());
        }
        return m_validationSession;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.vaadin.viewmanager.ViewManager#getFieldFactory()
     */
    public FieldFactory getFieldFactory() {
        return m_formFieldFactory;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.vaadin.viewmanager.ViewManager#getFormFieldFactory()
     */
    public FieldFactory getFormFieldFactory() {
        return m_formFieldFactory;
    }
    public void setFormFieldFactory(FieldFactory formFieldFactory) {
        m_formFieldFactory = formFieldFactory;
    }
	public void setMessageSource(MessageSource messageSource) {
        m_messageSource = messageSource;
	}
	public PermissionManager getPermissionManager() {
		return m_permissionManager;
	}
	public void setPermissionManager(PermissionManager permissionManager) {
		m_permissionManager = permissionManager;
	}
	public MessageSource getMessageSource() {
		return m_messageSource;
	}
	public void afterPropertiesSet() throws Exception {
		m_formFieldFactory.setMaduraSessionManager(this);
	}
	public MaduraConverterFactory getMaduraConverterFactory() {
		return m_maduraConverterFactory;
	}
	public void setMaduraConverterFactory(
			MaduraConverterFactory maduraConverterFactory) {
		m_maduraConverterFactory = maduraConverterFactory;
	}
	public boolean isSuppressUpdates() {
		return m_suppressUpdates;
	}
	public void setSuppressUpdates(boolean suppressUpdates) {
		m_suppressUpdates = suppressUpdates;
	}
	public LocaleAwareExceptionFactory getLocaleAwareExceptionFactory() {
		return m_localeAwareExceptionFactory;
	}
	public void setLocaleAwareExceptionFactory(
			LocaleAwareExceptionFactory localeAwareExceptionFactory) {
		m_localeAwareExceptionFactory = localeAwareExceptionFactory;
	}
}
