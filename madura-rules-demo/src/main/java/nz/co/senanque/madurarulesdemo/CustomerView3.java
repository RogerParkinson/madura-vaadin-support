/**
 * 
 */
package nz.co.senanque.madurarulesdemo;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Customer;
import nz.co.senanque.vaadin.CommandExt;
import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.vaadin.directed.OneFieldWindowFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import com.vaadin.data.util.BeanItem;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * This is much the same as {@link nz.co.senanque.madurarulesdemo.CustomerView2} except we use
 * a menu bar instead buttons. The menu bar dynamically enables and disables the submit option in the same way
 * the submit button was enabled and disabled.
 * 
 * @author Roger Parkinson
 *
 */
@UIScope
@Component
public class CustomerView3 extends VerticalLayout {
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
      
        HorizontalLayout headingButtonslLayout = new HorizontalLayout();
		headingButtonslLayout.setStyleName("heading-buttons");
		headingButtonslLayout.setImmediate(false);
		headingButtonslLayout.setWidth("100.0%");
		headingButtonslLayout.setHeight("26px");
		headingButtonslLayout.setMargin(false);
		
		MenuBar menuBar = new MenuBar();
		menuBar.setImmediate(false);
		menuBar.setWidth("-1px");
		menuBar.setHeight("-1px");
		headingButtonslLayout.addComponent(menuBar);
		customerForm.addComponent(headingButtonslLayout, 0);

        fieldGroup = new MaduraFieldGroup(m_maduraSessionManager);
        fieldGroup.bind(nameField, "name");
        fieldGroup.bind(emailField, "email");
        fieldGroup.bind(genderField, "gender");
        verticalLayout.addComponent(customerForm);
        
        final MenuBar.MenuItem file = menuBar.addItem(messageSourceAccessor.getMessage("File"), null);

        CommandExt command = fieldGroup.createMenuItemCommand(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.cancel"),
						messageSourceAccessor.getMessage("message.noop"),
						Notification.Type.HUMANIZED_MESSAGE);
				
			}});
        MenuItem menuItemCancel = file.addItem("button.cancel", command);
        fieldGroup.bind(menuItemCancel);

        command = fieldGroup.createMenuItemCommandSubmit(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.cancel"),
						messageSourceAccessor.getMessage("message.noop"),
						Notification.Type.HUMANIZED_MESSAGE);
				
			}});
        MenuItem menuItemSubmit = file.addItem("button.submit", command);
        fieldGroup.bind(menuItemSubmit);

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
