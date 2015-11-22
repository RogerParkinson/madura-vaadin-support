package nz.co.senanque.madurarulesdemo;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Pizza;
import nz.co.senanque.vaadin.MaduraForm;
import nz.co.senanque.vaadin.MaduraSessionManager;
import nz.co.senanque.validationengine.ValidationObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This is a popup window that uses the deprecated MaduraForm to configure the pizza.
 * 
 * @author Roger Parkinson
 *
 */
@org.springframework.stereotype.Component
@UIScope
public class PizzaWindow extends Window {

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
		Button OK = m_maduraForm.createSubmitButton("button.OK", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				MyUI.getCurrent().getEventRouter().fireEvent(new AddItemEvent(this,pizza));
				close();
			}});
        OK.setClickShortcut(KeyCode.ENTER );
        OK.addStyleName(ValoTheme.BUTTON_PRIMARY);
		actions.addComponent(OK);
		Button cancel = m_maduraForm.createButton("button.cancel", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				getMaduraSessionManager().getValidationSession().unbind(pizza);
				close();
			}});
		actions.addComponent(cancel);
		m_maduraForm.setFooter(actions);

    	if (getParent() == null) {
    		UI.getCurrent().addWindow(this);
        	this.center();
        }
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
