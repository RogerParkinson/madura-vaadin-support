/**
 * 
 */
package nz.co.senanque.madurarulesdemo;

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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
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
public class CustomerView2 extends VerticalLayout {
    @Autowired private MaduraSessionManager m_maduraSessionManager;
    @Autowired private OneFieldWindowFactory m_oneFieldWindowFactory;
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
    	
    	final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_maduraSessionManager.getMessageSource());

        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        addComponent(verticalLayout);

        customerForm = new FormLayout();
        customerForm.setWidth("30%");
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        ComboBox genderField = new ComboBox();
        customerForm.addComponent(nameField);
        customerForm.addComponent(emailField);
        customerForm.addComponent(genderField);
      
        fieldGroup = m_maduraSessionManager.createMaduraFieldGroup();
        fieldGroup.bind(nameField, "name");
        fieldGroup.bind(emailField, "email");
        fieldGroup.bind(genderField, "gender");
        verticalLayout.addComponent(customerForm);

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
		customerForm.addComponent(actions);
    }
    /* 
     * This is where we establish the actual customer object. 
     * We just get it from the UI object and assume to knows how to supply it(non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    public void load(Customer customer) {
		m_customer = customer;
		fieldGroup.setItemDataSource(new BeanItem<Customer>(m_customer));
    }
	public OneFieldWindowFactory getOneFieldWindowFactory() {
		return m_oneFieldWindowFactory;
	}
	public void setOneFieldWindowFactory(OneFieldWindowFactory oneFieldWindowFactory) {
		m_oneFieldWindowFactory = oneFieldWindowFactory;
	}
}
