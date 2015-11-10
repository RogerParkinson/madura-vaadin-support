package nz.co.senanque.vaadin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.validationengine.SetterListener;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;

import org.springframework.context.MessageSource;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;

/**
 * @author Roger Parkinson
 *
 */
public class MaduraFieldGroup extends FieldGroup implements PropertiesSource {
	
	private final MaduraSessionManager m_maduraSessionManager;
	private final FieldGroupFieldFactory m_fieldFactory;
	private Hints m_hints;
	private MessageSource m_messageSource;
	private List<String> m_fieldList;
	private List<Button> m_myButtons = new ArrayList<Button>();
	private List<MaduraPropertyWrapper> m_properties = new ArrayList<MaduraPropertyWrapper>();
	private Collection<Object> m_propertyIds = new ArrayList<>();
	
	public MaduraFieldGroup(MaduraSessionManager maduraSessionManager, BeanItem<ValidationObject> itemDataSource) {
		super(itemDataSource);
		m_maduraSessionManager = maduraSessionManager;
		m_fieldFactory = maduraSessionManager.getFieldGroupFieldFactory();
		m_messageSource = maduraSessionManager.getMessageSource();
		m_hints = maduraSessionManager.getHints();
		loadProperties(itemDataSource);
	}
	
    public MaduraFieldGroup(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
		m_fieldFactory = maduraSessionManager.getFieldGroupFieldFactory();
		m_messageSource = maduraSessionManager.getMessageSource();
		m_hints = maduraSessionManager.getHints();
	}

	public void setFieldList(List<String> fieldList) {
		m_fieldList = fieldList;
	}
	public void setFieldList(String[] fieldList) {
		m_fieldList = Arrays.asList(fieldList);
	}
	protected List<String> getFieldList()
	{
		return m_fieldList;
	}
	public void setItemDataSource(Item itemDataSource) {
    	if (!(itemDataSource instanceof BeanItem && ((BeanItem) itemDataSource).getBean() instanceof ValidationObject)) {
    		throw new RuntimeException("Use BeanItem<ValidationObject> only");
    	}
    	super.setItemDataSource(itemDataSource);
    	loadProperties((BeanItem<ValidationObject>) itemDataSource);
	}
    
    private void loadProperties(BeanItem<ValidationObject> dataSource) {
    	if (m_maduraSessionManager == null) {
    		return; // too early
    	}
        List<String> allFields = m_maduraSessionManager.getFieldList(dataSource.getBean(),dataSource);
        List<String> fields = allFields;
        if (getFieldList() != null)
        {
            fields = getFieldList();
        }
        m_properties = new ArrayList<MaduraPropertyWrapper>();
        for (String fieldName: fields)
        {
            Property<?> p = getItemProperty(fieldName);
            if (p == null)
            {
            	p = m_maduraSessionManager.getMaduraPropertyWrapper(dataSource.getBean(), fieldName);
            }
            if (p instanceof MaduraPropertyWrapper)
            {
            	m_properties.add((MaduraPropertyWrapper)p);
            	m_propertyIds.add(fieldName);
            }
        }
        for (Button button : m_myButtons)
        {
            ButtonProperty buttonProperty = (ButtonProperty)button.getData();
            button.setCaption(buttonProperty.getCaption());
            buttonProperty.getPainter().setPropertiesSource(this);
            buttonProperty.getPainter().paint(button);
//            m_maduraSessionManager.register(button, buttonProperty.getPainter());
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

    public void bind(Field<?> field, Object propertyId) throws BindException {
    	super.bind(field, propertyId);
    	Item dataSource = getItemDataSource();
        if (dataSource instanceof BeanItem) 
        {
            Object source = ((BeanItem)dataSource).getBean();
            if (source instanceof ValidationObject)
            {
            	MaduraPropertyWrapper p = m_maduraSessionManager.getMaduraPropertyWrapper((ValidationObject)source, propertyId.toString());
            	AbstractField<?> f = (AbstractField<?>)field;
            	m_maduraSessionManager.bind(null, f, p);
            }
        }
    }

    public void unbind(Field<?> field) throws BindException {
    	
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

	public Button createButton(String name, ButtonPainter painter, ClickListener listener) {
		Button ret = m_hints.getButtonField(name, painter.getMessageSource());
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

	@Override
	public List<MaduraPropertyWrapper> getProperties() {
		return m_properties;
	}
	public MaduraPropertyWrapper findProperty(String propertyName) {
		return m_maduraSessionManager.findProperty(propertyName, getProperties());
	}

	@Override
	public Collection<?> getItemPropertyIds() {
		return m_propertyIds;
	}

}
