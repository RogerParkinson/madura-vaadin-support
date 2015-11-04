package nz.co.senanque.madurarulesdemo;

import java.util.List;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Pizza;
import nz.co.senanque.vaadin.MaduraForm;
import nz.co.senanque.vaadin.MaduraPropertyWrapper;
import nz.co.senanque.vaadin.SimpleButtonPainter;
import nz.co.senanque.vaadin.SubmitButtonPainter;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.validationengine.ValidationObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.AttributeExaminer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Roger Parkinson
 *
 */
@org.springframework.stereotype.Component
@UIScope
public class PizzaWindow extends Window {

	private static Logger logger = LoggerFactory.getLogger(PizzaWindow.class);

	private Layout main;
	private Layout panel;
	private MaduraForm m_maduraForm;
	@Autowired private MaduraSessionManager m_maduraSessionManager;
    private String m_windowWidth = "300px";
    private String m_windowHeight = "550px";

	public PizzaWindow() {
	}

	/**
	 * @param caption
	 */
	public PizzaWindow(String caption) {
		super(caption);
	}

	/**
	 * @param caption
	 * @param content
	 */
	public PizzaWindow(String caption, Component content) {
		super(caption, content);
	}
	@PostConstruct
	public void init() {
        main = new VerticalLayout();
        setContent(main);
        setModal(true);
        this.setWidth(getWindowWidth());
        this.setHeight(getWindowHeight());
        
        panel = new VerticalLayout();
        main.addComponent(panel);
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_maduraSessionManager.getMessageSource());
        setCaption(messageSourceAccessor.getMessage("pizza", "Pizza"));
	}
	
	public void load(final Pizza pizza) {
		panel.removeAllComponents();
		getMaduraSessionManager().getValidationSession().bind(pizza);
    	BeanItem<Pizza> beanItem = new BeanItem<Pizza>(pizza);

    	m_maduraForm = new MaduraForm(getMaduraSessionManager());
    	String[] fieldList = new String[]{"base","topping","size","amount","testing","description"};
    	m_maduraForm.setFieldList(fieldList);
    	m_maduraForm.setItemDataSource(beanItem);
    	panel.addComponent(m_maduraForm);
    	
		HorizontalLayout actions = new HorizontalLayout();
		Button OK = m_maduraForm.createButton("button.OK", new SubmitButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				MyUI.getCurrent().getEventRouter().fireEvent(new AddItemEvent(this,pizza));
				close();
			}});
        OK.setClickShortcut(KeyCode.ENTER );
        OK.addStyleName(ValoTheme.BUTTON_PRIMARY);
		actions.addComponent(OK);
		Button cancel = m_maduraForm.createButton("button.cancel", new SimpleButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				getMaduraSessionManager().getValidationSession().unbind(pizza);
				close();
			}});
		actions.addComponent(cancel);
		m_maduraForm.setFooter(actions);

		List<MaduraPropertyWrapper> properties = m_maduraForm.getItemDataSourceProperties();
		m_maduraSessionManager.bind(OK, properties);
		m_maduraSessionManager.bind(cancel, properties);
		m_maduraForm.markAsDirty();
//		m_maduraForm.setImmediate(false);
    	if (getParent() == null) {
    		UI.getCurrent().addWindow(this);
        	this.center();
        }
		if (logger.isDebugEnabled()) {
	    	ComboBox genderField = (ComboBox)m_maduraForm.getField("size");
			logger.debug("size: MultiSelect {} NullSelectionAllowed {} Immediate {} Buffered {}",
					genderField.isMultiSelect(),
					genderField.isNullSelectionAllowed(),
					genderField.isImmediate(),
					genderField.isBuffered()
					);
			logger.debug("{}",dump(genderField));
		}

	}
	private String dump(AbstractComponent ac) {
		if (ac == null) {
			return null;
		}
		StringBuilder ret = new StringBuilder();
		try {
			ret.append(AttributeExaminer.getAttributes(ac));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String r = dump((AbstractComponent)ac.getParent());
		if (r != null) {
			ret.append(r);
		}
		return ret.toString();
	}
    public void close() {
    	getMaduraSessionManager().getValidationSession().unbind((ValidationObject) m_maduraForm.getData());
    	UI.getCurrent().removeWindow(this);
    }

	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}

	public void setMaduraSessionManager(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
	}

	public String getWindowWidth() {
		return m_windowWidth;
	}

	public String getWindowHeight() {
		return m_windowHeight;
	}

}
