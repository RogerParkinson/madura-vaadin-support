package nz.co.senanque.madurarulesdemo;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Pizza;
import nz.co.senanque.vaadin.MaduraForm;
import nz.co.senanque.vaadin.SimpleButtonPainter;
import nz.co.senanque.vaadin.SubmitButtonPainter;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.validationengine.ValidationObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * @author Roger Parkinson
 *
 */
@org.springframework.stereotype.Component
@UIScope
public class PizzaWindow extends Window implements MessageSourceAware {
	private Layout main;
	private Layout panel;
	private MaduraForm m_maduraForm;
	@Autowired private MaduraSessionManager m_maduraSessionManager;
//    private String m_windowWidth = "800px";
//    private String m_windowHeight = "400px";
	private MessageSourceAccessor m_messageSourceAccessor;

	public PizzaWindow() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param caption
	 */
	public PizzaWindow(String caption) {
		super(caption);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param caption
	 * @param content
	 */
	public PizzaWindow(String caption, Component content) {
		super(caption, content);
		// TODO Auto-generated constructor stub
	}
	@PostConstruct
	public void init() {
        main = new VerticalLayout();
        setContent(main);
        setModal(true);
//        main.setStyleName(Panel.STYLE_LIGHT);
//        this.setWidth(getWindowWidth());
//        this.setHeight(getWindowHeight());
        
        panel = new VerticalLayout();
//        main.setMargin(true);
        main.addComponent(panel);
        HorizontalLayout actions = new HorizontalLayout();
        Button OK = new Button();
        actions.addComponent(OK);
        Button cancel = new Button();
        actions.addComponent(cancel);
        
        setCaption(m_messageSourceAccessor.getMessage("launch.wizard", "Launch Wizard"));
	}
	
	public void load(final Pizza pizza) {
		panel.removeAllComponents();
		getMaduraSessionManager().getValidationSession().bind(pizza);
    	BeanItem<Pizza> beanItem = new BeanItem<Pizza>(pizza);

    	m_maduraForm = new MaduraForm(getMaduraSessionManager());
    	String[] fieldList = new String[]{"base","topping","size","amount","testing"};
    	m_maduraForm.setFieldList(fieldList);
    	m_maduraForm.setItemDataSource(beanItem);
    	panel.addComponent(m_maduraForm);
		HorizontalLayout actions = new HorizontalLayout();
		Button OK = m_maduraForm.createButton("button.OK", new SimpleButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				getMaduraSessionManager().getValidationSession().unbind(pizza);
				// TODO: add pizza to order
				close();
			}});
		actions.addComponent(OK);
		Button cancel = m_maduraForm.createButton("button.cancel", new SubmitButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				getMaduraSessionManager().getValidationSession().unbind(pizza);
				close();
			}});
		actions.addComponent(cancel);
		m_maduraForm.setFooter(actions);

//		panel.addComponent(getInitialLayout());
//    	panel.requestRepaint();
    	if (getParent() == null) {
    		UI.getCurrent().addWindow(this);
        	this.center();
        }
	}
    public void close() {
    	getMaduraSessionManager().getValidationSession().unbind((ValidationObject) m_maduraForm.getData());
    	UI.getCurrent().removeWindow(this);
    }

	@Override
	public void setMessageSource(MessageSource messageSource) {
		m_messageSourceAccessor = new MessageSourceAccessor(messageSource);
	}

	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}

	public void setMaduraSessionManager(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
	}

}
