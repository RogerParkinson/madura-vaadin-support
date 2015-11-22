package nz.co.senanque.vaadin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.util.ReflectTools;

/**
 * Use this instead of the Vaadin {link com.vaadin.data.fieldgroup.FieldGroup} to manage integration with a Madura session.
 * 
 * @author Roger Parkinson
 *
 */
public class MaduraFieldGroupImpl extends FieldGroup implements PropertiesSource, MaduraFieldGroup {
	
	private final MaduraSessionManager m_maduraSessionManager;
	private final FieldFactory m_fieldFactory;
	private Hints m_hints;
	private MessageSource m_messageSource;
	private List<Button> m_myButtons = new ArrayList<Button>();
	private List<MaduraPropertyWrapper> m_properties = new ArrayList<>();
	private Map<Label,String> m_labels = new HashMap<>();
	private List<MenuItem> m_menuItems = new ArrayList<>();

    /**
     * Constructor insists on a {link nz.co.senanque.vaadin.application.MaduraSessionManager}.
     * It does not need a current session or a current object to instantiate this. Use the method
     * on MaduraSessionManager to create this.
     * 
     * @param maduraSessionManager
     */
    protected MaduraFieldGroupImpl(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
		m_fieldFactory = maduraSessionManager.getFieldFactory();
		m_messageSource = maduraSessionManager.getMessageSource();
		m_hints = maduraSessionManager.getHints();
	}

	/**
	 * Tells the Madura session manager to connect this {link com.vaadin.ui.Label) to the given propertyId.
	 * This can be done before there is a data source.
	 * @param field
	 * @param propertyId
	 */
	public void bind(Label label, Object propertyId) {
		m_maduraSessionManager.register(label);
		m_labels.put(label,propertyId.toString());
		if (getItemDataSource() == null) {
			return;
		}
		configureLabel(label);
	}
	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#setItemDataSource(com.vaadin.data.Item)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setItemDataSource(Item itemDataSource) {
    	if (!(itemDataSource instanceof BeanItem && ((BeanItem<?>) itemDataSource).getBean() instanceof ValidationObject)) {
    		throw new RuntimeException("Use BeanItem<ValidationObject> only");
    	}
    	// make sure the data source is bound to Madura
		m_maduraSessionManager.getValidationSession().bind(((BeanItem<ValidationObject>)itemDataSource).getBean());
		// the super call will only bind fields
    	super.setItemDataSource(itemDataSource);
    	m_properties.clear();
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
    	if (dataSource == null) {
    		return null;
    	}
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
     * Using this in inevitably wrong
     * 
     * @see com.vaadin.data.fieldgroup.FieldGroup#buildAndBind(java.lang.Object)
     */
    public Field<?> buildAndBind(Object propertyId) throws BindException {
    	throw new RuntimeException("use buildAndBind(Layout panel, String[] fields, BeanItem<ValidationObject> itemDataSource) instead");
    }
    
	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#buildAndBind(com.vaadin.ui.Layout, java.util.List, com.vaadin.data.util.BeanItem)
	 */
	@Override
	public void buildAndBind(Layout panel, List<String> fields, BeanItem<ValidationObject> itemDataSource) {
		m_maduraSessionManager.getValidationSession().bind(((BeanItem<ValidationObject>)itemDataSource).getBean());
		// the super call will only bind fields
    	super.setItemDataSource(itemDataSource);
    	m_properties.clear();
		panel.removeAllComponents();
		for (String propertyId : fields) {
	    	ValidationObject validationObject = getDataSource();
	    	MaduraPropertyWrapper maduraPropertyWrapper = getMaduraPropertyWrapper(validationObject,propertyId,true);
	    	final Field<?> field = m_fieldFactory.createFieldByPropertyType(maduraPropertyWrapper);
			panel.addComponent(field);
		}
		configure(itemDataSource);
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
    
    /* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#unbind()
	 */
    @Override
	public void unbind() {
    	m_properties.clear();
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
	
	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#createSubmitButton(java.lang.String, com.vaadin.ui.Button.ClickListener)
	 */
	@Override
	public Button createSubmitButton(String name, ClickListener listener) {
		return createButton(name,new SubmitButtonPainter(m_maduraSessionManager),listener);
	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#createButton(java.lang.String, com.vaadin.ui.Button.ClickListener)
	 */
	@Override
	public Button createButton(String name, ClickListener listener) {
		return createButton(name,new SimpleButtonPainter(m_maduraSessionManager),listener);
	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#createFieldButton(java.lang.String, java.lang.String, com.vaadin.ui.Button.ClickListener)
	 */
	@Override
	public Button createFieldButton(String name, String propertyId, ClickListener listener) {
		return createButton(name,new FieldButtonPainter(propertyId, m_maduraSessionManager),listener);
	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#createSubmitButton(java.lang.String, java.lang.String, com.vaadin.ui.Button.ClickListener)
	 */
	@Override
	public Button createSubmitButton(String name, String permission, ClickListener listener) {
		return createButton(name,new SubmitButtonPainter(permission,m_maduraSessionManager),listener);
	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#createButton(java.lang.String, java.lang.String, com.vaadin.ui.Button.ClickListener)
	 */
	@Override
	public Button createButton(String name, String permission, ClickListener listener) {
		return createButton(name,new SimpleButtonPainter(permission, m_maduraSessionManager),listener);
	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#createFieldButton(java.lang.String, java.lang.String, java.lang.String, com.vaadin.ui.Button.ClickListener)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#createMenuItemCommand(com.vaadin.ui.Button.ClickListener)
	 */
	@Override
	public CommandExt createMenuItemCommand(final ClickListener listener) {
		return createMenuItemCommandExt(new SimpleButtonPainter(m_maduraSessionManager),listener);
	}
	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#createMenuItemCommandSubmit(com.vaadin.ui.Button.ClickListener)
	 */
	@Override
	public CommandExt createMenuItemCommandSubmit(final ClickListener listener) {
		return createMenuItemCommandExt(new SubmitButtonPainter(m_maduraSessionManager),listener);
	}
	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#createMenuItemCommand(java.lang.String, com.vaadin.ui.Button.ClickListener)
	 */
	@Override
	public CommandExt createMenuItemCommand(final String permission, final ClickListener listener) {
		return createMenuItemCommandExt(new SimpleButtonPainter(permission,m_maduraSessionManager),listener);
	}
	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#createMenuItemCommandSubmit(java.lang.String, com.vaadin.ui.Button.ClickListener)
	 */
	@Override
	public CommandExt createMenuItemCommandSubmit(final String permission, final ClickListener listener) {
		return createMenuItemCommandExt(new SubmitButtonPainter(permission,m_maduraSessionManager),listener);
	}
	public CommandExt createMenuItemCommandExt(final MenuItemPainter painter,
			final ClickListener listener) {
		final MaduraFieldGroupImpl me = this;
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
	/* (non-Javadoc)
	 * @see nz.co.senanque.vaadin.MaduraFieldGroup#bind(com.vaadin.ui.MenuBar.MenuItem)
	 */
	@Override
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
