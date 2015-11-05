/**
 * 
 */
package nz.co.senanque.madurarulesdemo;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Customer;
import nz.co.senanque.vaadin.FieldButtonPainter;
import nz.co.senanque.vaadin.MaduraForm;
import nz.co.senanque.vaadin.SimpleButtonPainter;
import nz.co.senanque.vaadin.SubmitButtonPainter;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.vaadin.directed.OneFieldWindowFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Roger Parkinson
 *
 */
@UIScope
@Component
public class CustomerView extends VerticalLayout {
    @Autowired private MaduraSessionManager m_maduraSessionManager;
    @Autowired private OneFieldWindowFactory m_oneFieldWindowFactory;
    private Customer m_customer = null;
    private MaduraForm customerForm;

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

        customerForm = new MaduraForm(m_maduraSessionManager);
        customerForm.setWidth("30%");
        customerForm.setFieldList(new String[]{"name","email"});
        verticalLayout.addComponent(customerForm);

		HorizontalLayout actions = new HorizontalLayout();
		Button cancel = customerForm.createButton("button.cancel", new SimpleButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.cancel"),
						messageSourceAccessor.getMessage("message.noop"),
						Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		Button submit = customerForm.createButton("button.submit", new SubmitButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.submit"),
						messageSourceAccessor.getMessage("message.noop"),
		                  Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		submit.setClickShortcut(KeyCode.ENTER );
		submit.addStyleName(ValoTheme.BUTTON_PRIMARY);
		Button bmi = customerForm.createButton("button.bmi", new FieldButtonPainter("dynamic","ADMIN",m_maduraSessionManager), new ClickListener(){
			// TODO: doesn't use the button.bmi here, it uses the dynamic label instead

			@Override
			public void buttonClick(ClickEvent event) {
				m_oneFieldWindowFactory.createWindow(m_customer, "bmi",ValoTheme.BUTTON_PRIMARY);				
			}});
		actions.addComponent(cancel);
		actions.addComponent(submit);
		actions.addComponent(bmi);
		customerForm.setFooter(actions);
    }
    /* 
     * This is where we establish the actual customer object. 
     * We just get it from the UI object and assume to knows how to supply it(non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    public void enter(ViewChangeEvent event) {
    	MyUI ui = MyUI.getCurrent();
    	if (m_customer == null) {
    		m_customer = ui.getCustomer();
        	customerForm.setItemDataSource(new BeanItem<Customer>(m_customer));
    	}
    }
	public OneFieldWindowFactory getOneFieldWindowFactory() {
		return m_oneFieldWindowFactory;
	}
	public void setOneFieldWindowFactory(OneFieldWindowFactory oneFieldWindowFactory) {
		m_oneFieldWindowFactory = oneFieldWindowFactory;
	}
}
