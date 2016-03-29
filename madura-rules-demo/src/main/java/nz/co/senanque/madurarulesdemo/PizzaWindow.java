package nz.co.senanque.madurarulesdemo;

import java.util.Map;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Pizza;
import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.MaduraSessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This is a popup window that uses the MaduraFieldGroup to configure the pizza.
 * 
 * @author Roger Parkinson
 *
 */
@org.springframework.stereotype.Component
@UIScope
public class PizzaWindow extends Window {

	private Layout main;
	private Layout panel;
	private MaduraFieldGroup fieldgroup;
	@Autowired private MaduraSessionManager m_maduraSessionManager;
    private String m_windowWidth = "400px";
    private String m_windowHeight = "500px";
    
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
	
	/**
	 * Loads an existing (or new/empty) pizza object and binds it to the fields.
	 * 
	 * @param pizza
	 */
	public void load(final Pizza pizza) {
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_maduraSessionManager.getMessageSource());
		// Clean the panel of any previous fields
		panel.removeAllComponents();
		// bind the object to the Madura session
		getMaduraSessionManager().getValidationSession().bind(pizza);
    	BeanItem<Pizza> beanItem = new BeanItem<Pizza>(pizza);

    	// make a new layout and add to the panel
    	FormLayout formLayout = new FormLayout();
    	panel.addComponent(formLayout);
    	
    	fieldgroup = m_maduraSessionManager.createMaduraFieldGroup();
    	HorizontalLayout actions = createActions(messageSourceAccessor,pizza);
    	Map<String,Field<?>> fields = fieldgroup.buildAndBind(
    			new String[]{"base","topping","size","amount","testing","description"},
    			beanItem);
    	
    	// Now we have to add the fields to the panel
		formLayout.addComponent(fields.get("base"));
		formLayout.addComponent(fields.get("topping"));
		formLayout.addComponent(fields.get("size"));
		formLayout.addComponent(fields.get("amount"));
		formLayout.addComponent(fields.get("testing"));
		formLayout.addComponent(fields.get("description"));
		formLayout.addComponent(actions);

    	if (getParent() == null) {
    		UI.getCurrent().addWindow(this);
        	this.center();
        }
	}
    private HorizontalLayout createActions(final MessageSourceAccessor messageSourceAccessor, final Pizza pizza) {
		HorizontalLayout actions = new HorizontalLayout();
		Button OK = fieldgroup.createSubmitButton("button.OK", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				MyUI.getCurrent().getEventRouter().fireEvent(new AddItemEvent(this,pizza));
				close();
			}});
        OK.setClickShortcut(KeyCode.ENTER );
        OK.addStyleName(ValoTheme.BUTTON_PRIMARY);
		actions.addComponent(OK);
		Button cancel = fieldgroup.createButton("button.cancel", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				getMaduraSessionManager().getValidationSession().unbind(pizza);
				close();
			}});
    	return actions;
    }
    public void close() {
    	fieldgroup.unbind();
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
