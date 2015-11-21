package nz.co.senanque.vaadin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.validationengine.SetterListener;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.util.ReflectTools;

/**
 * Use this instead of the Vaadin {link com.vaadin.data.fieldgroup.FieldGroup} to manage integration with a Madura session.
 * 
 * @author Roger Parkinson
 *
 */
public class MaduraFieldGroup extends FieldGroup implements PropertiesSource {
	
	private final MaduraSessionManager m_maduraSessionManager;
	private final FieldFactory m_fieldFactory;
	private Hints m_hints;
	private MessageSource m_messageSource;
//	private List<String> m_fieldList;
	private List<Button> m_myButtons = new ArrayList<Button>();
	private List<MaduraPropertyWrapper> m_properties = new ArrayList<>();
//	private Collection<Object> m_propertyIds = new ArrayList<>();
	private Map<Label,String> m_labels = new HashMap<>();
	private List<MenuItem> m_menuItems = new ArrayList<>();

    /**
     * Constructor insists on a {link nz.co.senanque.vaadin.application.MaduraSessionManager}.
     * It does not need a current session or a current object to instantiate this, so it can be used as a Spring bean.
     * 
     * @param maduraSessionManager
     */
    public MaduraFieldGroup(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
		m_fieldFactory = maduraSessionManager.getFieldFactory();
		m_messageSource = maduraSessionManager.getMessageSource();
		m_hints = maduraSessionManager.getHints();
	}

//	/**
//	 * Tell the class what fields are interesting. Invalid names will be ignored, missing names will also be ignored.
//	 * @param fieldList
//	 */
//	public void setFieldList(List<String> fieldList) {
//		m_fieldList = fieldList;
//	}
//	/**
//	 * Tell the class what fields are interesting. Invalid names will be ignored, missing names will also be ignored.
//	 * @param fieldList
//	 */
//	public void setFieldList(String[] fieldList) {
//		m_fieldList = Arrays.asList(fieldList);
//	}
//	protected List<String> getFieldList()
//	{
//		return m_fieldList;
//	}
	/**
	 * Tells the Madura session manager to connect this {link com.vaadin.ui.Label) to the given propertyId.
	 * This can be done before there is a data source.
	 * @param field
	 * @param propertyId
	 */
	public void bind(Label label, String propertyId) {
		m_maduraSessionManager.register(label);
		m_labels.put(label,propertyId);
		if (getItemDataSource() == null) {
			return;
		}
		configureLabel(label);
	}
	/**
	 * Establish a data source. The data source must be a {@code BeanItem<ValidationObject>} or we throw an exception.
	 * This establishes the Madura binding for the fields. 
	 * @see com.vaadin.data.fieldgroup.FieldGroup#setItemDataSource(com.vaadin.data.Item)
	 */
	@SuppressWarnings("unchecked")
	public void setItemDataSource(Item itemDataSource) {
    	if (!(itemDataSource instanceof BeanItem && ((BeanItem<?>) itemDataSource).getBean() instanceof ValidationObject)) {
    		throw new RuntimeException("Use BeanItem<ValidationObject> only");
    	}
    	// make sure the data source is bound to Madura
		m_maduraSessionManager.getValidationSession().bind(((BeanItem<ValidationObject>)itemDataSource).getBean());
		// the super call will only bind fields
    	super.setItemDataSource(itemDataSource);
    	// this will configure labels, menuitems, and buttons
    	configure((BeanItem<ValidationObject>) itemDataSource);
	}
    
    /**
     * Establish the Madura binding for the fields.
     * 
     * @param dataSource
     */
    private void configure(BeanItem<ValidationObject> dataSource) {
    	if (m_maduraSessionManager == null) {
    		return; // too early
    	}
    	if (dataSource == null) {
    		throw new RuntimeException("No data source set");
    	}
//    	ValidationObject validationObject = dataSource.getBean();
//        List<String> allFields = m_maduraSessionManager.getFieldList(validationObject,dataSource);
//        List<String> fields = allFields;
//		if (getFieldList() != null) {
//			fields = getFieldList();
//		}
		for (Label label : m_labels.keySet()) {
			configureLabel(label);
		}
		for (MenuItem menuItem : m_menuItems) {
			configureMenuItem(menuItem);
		}
		for (Button button : m_myButtons) {
			configureButton(button);
		}
    }
    
    /**
     * Create (if necessary) and bind fields.
     * @see com.vaadin.data.fieldgroup.FieldGroup#buildAndBindMemberFields(java.lang.Object, boolean)
     */
    protected void buildAndBindMemberFields(Object objectWithMemberFields,
            boolean buildFields) throws BindException {
    	super.buildAndBindMemberFields(objectWithMemberFields,
                buildFields);
    	processLabels(objectWithMemberFields, buildFields);
    }
    
    /**
     * We get here when there is a bind() call and the data source has been set.
     * @see com.vaadin.data.fieldgroup.FieldGroup#configureField(com.vaadin.ui.Field)
     */
    protected void configureField(Field<?> field) {
    	super.configureField(field);
    	ValidationObject source = getDataSource();
    	MaduraPropertyWrapper p = getMaduraPropertyWrapper(source,getPropertyId(field),true);
    	AbstractField<?> f = (AbstractField<?>)field;
    	m_maduraSessionManager.bind(f, p);
    }
    
    private ValidationObject getDataSource() {
    	BeanItem<ValidationObject> dataSource = (BeanItem<ValidationObject>)getItemDataSource();
    	ValidationObject source = dataSource.getBean();
    	return source;
    }
    
    protected void configureLabel(Label label) {
    	ValidationObject source = getDataSource();
    	final LabelProperty<?> property = new LabelProperty(getMaduraPropertyWrapper(source,m_labels.get(label),true));
    	label.setPropertyDataSource(property);
    	m_maduraSessionManager.setPermissions(property.getProperty(), label);
        MaduraPropertyWrapper wrapper = property.getProperty();
        if (wrapper != null) {
            m_maduraSessionManager.getValidationSession().addListener(wrapper.getOwner(),wrapper.getName(), new SetterListener(){

    			@Override
    			public void run(ValidationObject object, String name,
    					Object newValue, ValidationSession session) {
    				com.vaadin.data.util.ProtectedMethods.fireValueChange(property);
    			}});
        }
    	
    }
    
    protected void configureMenuItem(MenuItem menuItem) {
    	Command command = menuItem.getCommand();
    	if (command instanceof CommandExt)
    	{
    		CommandExt commandExt = (CommandExt)command;
    		MenuItemPainter painter = commandExt.getPainter();
    		painter.paint(menuItem);
    	}
    }
        
    protected void configureButton(Button button) {
        ButtonProperty buttonProperty = (ButtonProperty)button.getData();
        button.setCaption(buttonProperty.getCaption());
        ButtonPainter painter = buttonProperty.getPainter();
        if (painter.getPropertyName() != null) {
        	ValidationObject source = getDataSource();
        	this.getMaduraPropertyWrapper(source, painter.getPropertyName(), true);
        }
        painter.setPropertiesSource(this);
        painter.paint(button);
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
        
    /** 
     * Scan the class for {link com.vaadin.ui.Label} fields, as opposed for input fields that the
     * Vaadin code already scans for, and bind them to the Madura session.
     * @param objectWithMemberFields
     * @param buildFields
     */
    private void processLabels(Object objectWithMemberFields, boolean buildFields) {
    	Class<?> objectClass = objectWithMemberFields.getClass();
        for (java.lang.reflect.Field memberField : getFieldsInDeclareOrder(objectClass)) {

            if (!Label.class.isAssignableFrom(memberField.getType())) {
                // Process next field
                continue;
            }

            PropertyId propertyIdAnnotation = memberField
                    .getAnnotation(PropertyId.class);

            Object propertyId = null;
            if (propertyIdAnnotation != null) {
                // @PropertyId(propertyId) always overrides property id
                propertyId = propertyIdAnnotation.value();
            } else {
                try {
                    propertyId = findPropertyId(memberField);
                } catch (SearchException e) {
                    // Property id was not found, skip this field
                    continue;
                }
                if (propertyId == null) {
                    // Property id was not found, skip this field
                    continue;
                }
            }
            Label label;
            try {
                // Get the field from the object
                label = (Label) ReflectTools.getJavaFieldValue(
                        objectWithMemberFields, memberField, Label.class);
            } catch (Exception e) {
                // If we cannot determine the value, just skip the field and try
                // the next one
                continue;
            }
            if (label == null && buildFields) {

                // Create the component (Field)
                label = new Label();

                // Store it in the field
                try {
                    ReflectTools.setJavaFieldValue(objectWithMemberFields,
                            memberField, label);
                } catch (IllegalArgumentException e) {
                    throw new BindException("Could not assign value to field '"
                            + memberField.getName() + "'", e);
                } catch (IllegalAccessException e) {
                    throw new BindException("Could not assign value to field '"
                            + memberField.getName() + "'", e);
                } catch (InvocationTargetException e) {
                    throw new BindException("Could not assign value to field '"
                            + memberField.getName() + "'", e);
                }
            }
            if (label != null) {
                // Bind it to the property id
//            	ValidationObject validationObject = (ValidationObject)((BeanItem<ValidationObject>)getItemDataSource()).getBean();
            	bind(label,propertyId.toString());
            }
        }    	
    }
    /**
     *  We don't trust this call to work right so this override just throws an exception
     * @see com.vaadin.data.fieldgroup.FieldGroup#build(java.lang.String, java.lang.Class, java.lang.Class)
     */
    protected <T extends Field> T build(String caption, Class<?> dataType,
            Class<T> fieldType) throws BindException {
    	throw new RuntimeException("Using the buildxxx methods is not supported by Madura");
    }
    
    /**
     * Uses the field factory to create a field using the property type. This requires a data source already established.
     * 
     * @see com.vaadin.data.fieldgroup.FieldGroup#buildAndBind(java.lang.Object)
     */
    public Field<?> buildAndBind(Object propertyId) throws BindException {
    	if (getItemDataSource()==null) {
    		throw new BindException("No data source established, cannot build and bind "+propertyId);
    	}
    	ValidationObject validationObject = getDataSource();
    	MaduraPropertyWrapper maduraPropertyWrapper = getMaduraPropertyWrapper(validationObject,propertyId,true);
    	final Field<?> field = m_fieldFactory.createFieldByPropertyType(maduraPropertyWrapper);
    	return field;
    }
    
    private MaduraPropertyWrapper getMaduraPropertyWrapper(ValidationObject validationObject, Object propertyId, boolean create) {
    	for (MaduraPropertyWrapper maduraPropertyWrapper: m_properties) {
    		if (maduraPropertyWrapper.getName().equals(propertyId)) {
    	    	return maduraPropertyWrapper;
    		}
    	}
    	if (create) {
    		MaduraPropertyWrapper maduraPropertyWrapper = m_maduraSessionManager.getMaduraPropertyWrapper(validationObject, propertyId.toString());
    		m_properties.add(maduraPropertyWrapper);
    		return maduraPropertyWrapper;
    	}
    	throw new RuntimeException("No such property defined: "+propertyId);
    }
    
    public void unbind(ValidationObject validationObject) {
    	getMaduraSessionManager().getValidationSession().unbind(validationObject);
    }

	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}

	public Hints getHints() {
		return m_hints;
	}

	public void setHints(Hints hints) {
		m_hints = hints;
	}
	
	/**
	 * Create a button that only enables when all the required fields are completed without error.
	 * @param name
	 * @param listener
	 * @return button
	 */
	public Button createSubmitButton(String name, ClickListener listener) {
		return createButton(name,new SubmitButtonPainter(m_maduraSessionManager),listener);
	}

	/**
	 * Create a simple button.
	 * @param name
	 * @param listener
	 * @return button
	 */
	public Button createButton(String name, ClickListener listener) {
		return createButton(name,new SimpleButtonPainter(m_maduraSessionManager),listener);
	}

	/**
	 * Create a button that enables and disables depending on the value of the boolean propertyId.
	 * @param name
	 * @param propertyId
	 * @param listener
	 * @return button
	 */
	public Button createFieldButton(String name, String propertyId, ClickListener listener) {
		return createButton(name,new FieldButtonPainter(propertyId, m_maduraSessionManager),listener);
	}

	/**
	 * Create a button that only enables when all the required fields are completed without error.
	 * @param name
	 * @param permission
	 * @param listener
	 * @return button
	 */
	public Button createSubmitButton(String name, String permission, ClickListener listener) {
		return createButton(name,new SubmitButtonPainter(permission,m_maduraSessionManager),listener);
	}

	/**
	 * Create a simple button.
	 * @param name
	 * @param permission
	 * @param listener
	 * @return button
	 */
	public Button createButton(String name, String permission, ClickListener listener) {
		return createButton(name,new SimpleButtonPainter(permission, m_maduraSessionManager),listener);
	}

	/**
	 * Create a button that enables and disables depending on the value of the boolean propertyId.
	 * @param name
	 * @param permission
	 * @param propertyId
	 * @param listener
	 * @return
	 */
	public Button createFieldButton(String name, String propertyId, String permission, ClickListener listener) {
		return createButton(name,new FieldButtonPainter(propertyId, permission, m_maduraSessionManager),listener);
	}

	public Button createButton(String name, ButtonPainter painter, ClickListener listener) {
		Button ret = m_hints.getButtonField(name, m_messageSource);
		if (listener != null) {
			ret.addClickListener(listener);
		}
		if (painter != null)
		{
		    getMaduraSessionManager().register(ret, painter);
		    painter.setPropertiesSource(this);
		}
		m_myButtons.add(ret);
		return ret;
	}
	
	/**
	 * Creates an extended command for use with a {@link com.vaadin.ui.MenuBar.MenuItem}.
	 * @param listener
	 * @return commandExt
	 */
	public CommandExt createMenuItemCommand(final ClickListener listener) {
		return createMenuItemCommandExt(new SimpleButtonPainter(m_maduraSessionManager),listener);
	}
	/**
	 * Creates an extended command for use with a {@link com.vaadin.ui.MenuBar.MenuItem}.
	 * The MenuItem will disable until all the fields are clean.
	 * @param listener
	 * @return commandExt
	 */
	public CommandExt createMenuItemCommandSubmit(final ClickListener listener) {
		return createMenuItemCommandExt(new SubmitButtonPainter(m_maduraSessionManager),listener);
	}
	/**
	 * Creates an extended command for use with a {@link com.vaadin.ui.MenuBar.MenuItem}.
	 * If the current user does not have the given permission it remains disabled
	 * @param permission
	 * @param listener
	 * @return commandExt
	 */
	public CommandExt createMenuItemCommand(final String permission, final ClickListener listener) {
		return createMenuItemCommandExt(new SimpleButtonPainter(permission,m_maduraSessionManager),listener);
	}
	/**
	 * Creates an extended command for use with a {@link com.vaadin.ui.MenuBar.MenuItem}.
	 * The MenuItem will disable until all the fields are clean.
	 * If the current user does not have the given permission it remains disabled
	 * @param permission
	 * @param listener
	 * @return commandExt
	 */
	public CommandExt createMenuItemCommandSubmit(final String permission, final ClickListener listener) {
		return createMenuItemCommandExt(new SubmitButtonPainter(permission,m_maduraSessionManager),listener);
	}
	public CommandExt createMenuItemCommandExt(final MenuItemPainter painter,
			final ClickListener listener) {
		final MaduraFieldGroup me = this;
		CommandExt ret = new CommandExt() {
			MenuItemPainter m_menuItemPainter = painter;

			public void menuSelected(MenuItem selectedItem) {
				listener.buttonClick(null);
			}

			public MenuItemPainter getPainter() {
				m_menuItemPainter.setPropertiesSource(me);
				return m_menuItemPainter;
			}

			public MaduraSessionManager getMaduraSessionManager() {
				return m_maduraSessionManager;
			}
		};
		return ret;
	}
	/**
	 * Tells the madura session about this {@link com.vaadin.ui.MenuBar.MenuItem}.
	 * @param field
	 */
	public void bind(MenuItem field) {
		Command command = field.getCommand();
		if (command != null && command instanceof CommandExt) {
			MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
			field.setText(messageSourceAccessor.getMessage(field.getText(), field.getText()));
			m_maduraSessionManager.register(field);
			m_menuItems.add(field);
		} else {
			throw new RuntimeException("Menu item command is not a CommandExt");
		}
		if (getItemDataSource() == null) {
			return;
		}
		configureMenuItem(field);
	}

	@Override
	public List<MaduraPropertyWrapper> getProperties() {
		return m_properties;
	}
	public MaduraPropertyWrapper findProperty(String propertyName) {
		return m_maduraSessionManager.findProperty(propertyName, getProperties());
	}
}
