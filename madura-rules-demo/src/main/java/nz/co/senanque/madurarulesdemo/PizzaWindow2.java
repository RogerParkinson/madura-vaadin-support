package nz.co.senanque.madurarulesdemo;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Pizza;
import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.application.MaduraSessionManager;

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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
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
public class PizzaWindow2 extends Window {

	private Layout main;
	private Layout panel;
	private MaduraFieldGroup m_maduraFieldGroup;
	@Autowired private MaduraSessionManager m_maduraSessionManager;
    private String m_windowWidth = "400px";
    private String m_windowHeight = "500px";
    private Pizza m_pizza;
    
    @PropertyId("base")
    private ComboBox base = new ComboBox();
    @PropertyId("topping")
    private ComboBox topping = new ComboBox();
    @PropertyId("size")
    private ComboBox size = new ComboBox();
    @PropertyId("amount")
    private TextField amount = new TextField();
    @PropertyId("testing")
    private TextField testing = new TextField();
    @PropertyId("description")
    private TextField description = new TextField();

	public PizzaWindow2() {
	}

	/**
	 * @param caption
	 */
	public PizzaWindow2(String caption) {
		super(caption);
	}

	/**
	 * @param caption
	 * @param content
	 */
	public PizzaWindow2(String caption, Component content) {
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

		FormLayout formLayout = new FormLayout();
		formLayout.addComponent(base);
		formLayout.addComponent(topping);
		formLayout.addComponent(size);
		formLayout.addComponent(amount);
		formLayout.addComponent(testing);
		formLayout.addComponent(description);
    	
    	panel.addComponent(formLayout);
    	
    	m_maduraFieldGroup = new MaduraFieldGroup(getMaduraSessionManager());
    	m_maduraFieldGroup.setItemDataSource(beanItem);
    	m_maduraFieldGroup.buildAndBindMemberFields(this);
    	
		HorizontalLayout actions = new HorizontalLayout();
		Button OK = m_maduraFieldGroup.createSubmitButton("button.OK", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				MyUI.getCurrent().getEventRouter().fireEvent(new AddItemEvent(this,pizza));
				close();
			}});
        OK.setClickShortcut(KeyCode.ENTER );
        OK.addStyleName(ValoTheme.BUTTON_PRIMARY);
		actions.addComponent(OK);
		Button cancel = m_maduraFieldGroup.createButton("button.cancel", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				getMaduraSessionManager().getValidationSession().unbind(pizza);
				close();
			}});
		actions.addComponent(cancel);
		formLayout.addComponent(actions);

    	if (getParent() == null) {
    		UI.getCurrent().addWindow(this);
        	this.center();
        }
	}
    public void close() {
    	m_maduraFieldGroup.unbind(m_pizza);
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
