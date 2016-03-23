/**
 * 
 */
package nz.co.senanque.madurarulesdemo;

import java.util.Map;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Customer;
import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.MaduraSessionManager;
import nz.co.senanque.vaadin.directed.OneFieldWindowFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Shows a MaduraFieldGroup binding against predefined fields and used to create buttons.
 * This is all done before the data source is established, but you do have to know what
 * fields are needed on the form at coding time which is not always the case.
 * 
 * @author Roger Parkinson
 *
 */
@UIScope
@Component
public class CustomerView5 extends VerticalLayout {
    @Autowired private MaduraSessionManager m_maduraSessionManager;
    @Autowired private OneFieldWindowFactory m_oneFieldWindowFactory;
	private Layout panel;
    private Customer m_customer;
    private FormLayout customerForm;
    private MaduraFieldGroup fieldGroup;

    /*
     * Defines the form, buttons and their connections to Madura
     * At this point we don't have an actual data object, so all this gets done once
     * and will not need redoing if we add or change a data object.
     */
    @PostConstruct
    void init() {
    	
        panel = new VerticalLayout();
        addComponent(panel);
    }
    /* 
     * This is where we establish the actual customer object. 
     * We just get it from the UI object and assume to knows how to supply it(non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    public void load(Customer customer) {
        final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_maduraSessionManager.getMessageSource());

		// Clean the panel of any previous fields
		panel.removeAllComponents();
		// bind the object to the Madura session
		m_maduraSessionManager.getValidationSession().bind(customer);
    	BeanItem<Customer> beanItem = new BeanItem<Customer>(customer);

    	// make a new layout and add to the panel
    	customerForm = new FormLayout();
    	panel.addComponent(customerForm);
    	
    	fieldGroup = m_maduraSessionManager.createMaduraFieldGroup("CustomerView4");
    	HorizontalLayout actions = createActions(messageSourceAccessor);
    	fieldGroup.buildAndBind(customerForm,
    			new String[]{"name","email","gender","status"},
    			beanItem);

        m_customer = customer;
		customerForm.addComponent(actions);
    }
    private HorizontalLayout createActions(final MessageSourceAccessor messageSourceAccessor) {
		HorizontalLayout actions = new HorizontalLayout();
		Button cancel = fieldGroup.createButton("button.cancel", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.cancel"),
						messageSourceAccessor.getMessage("message.noop"),
						Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		Button submit = fieldGroup.createSubmitButton("button.submit", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.submit"),
						messageSourceAccessor.getMessage("message.noop"),
		                  Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		submit.setClickShortcut(KeyCode.ENTER );
		submit.addStyleName(ValoTheme.BUTTON_PRIMARY);
		Button bmi = fieldGroup.createFieldButton("button.bmi", "dynamic","ADMIN", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				m_oneFieldWindowFactory.createWindow(m_customer, "bmi",ValoTheme.BUTTON_PRIMARY);				
			}});
		actions.addComponent(cancel);
		actions.addComponent(submit);
		actions.addComponent(bmi);
    	return actions;
    }
	public OneFieldWindowFactory getOneFieldWindowFactory() {
		return m_oneFieldWindowFactory;
	}
	public void setOneFieldWindowFactory(OneFieldWindowFactory oneFieldWindowFactory) {
		m_oneFieldWindowFactory = oneFieldWindowFactory;
	}
}
