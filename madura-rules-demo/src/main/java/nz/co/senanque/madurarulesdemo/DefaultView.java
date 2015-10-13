/**
 * 
 */
package nz.co.senanque.madurarulesdemo;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Customer;
import nz.co.senanque.vaadin.SimpleButtonPainter;
import nz.co.senanque.vaadin.SubmitButtonPainter;
import nz.co.senanque.vaadin.application.MaduraSessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Roger Parkinson
 *
 */
@SpringView(name = DefaultView.VIEW_NAME)
public class DefaultView extends VerticalLayout implements View, MessageSourceAware {
    public static final String VIEW_NAME = "";
    @Autowired private MaduraSessionManager m_maduraSessionManager;
    private Customer m_customer = null;
    private CustomerForm customerForm;
	private MessageSource m_messageSource;

    /*
     * Defines the form, buttons and their connections to Madura
     * At this point we don't have an actual data object, so all this gets done once
     * and will not need redoing if we add or change a data object.
     */
    @PostConstruct
    void init() {
    	
    	final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);

        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        addComponent(verticalLayout);

        customerForm = new CustomerForm(m_maduraSessionManager);
        customerForm.setCaption(messageSourceAccessor.getMessage("login.title"));
        customerForm.setWidth("30%");
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
		Button logout = customerForm.createButton("button.logout", new SimpleButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				logout();
				
			}});
		actions.addComponent(cancel);
		actions.addComponent(submit);
		actions.addComponent(logout);
		customerForm.setFooter(actions);
		
		Component instructions = getInstructions(messageSourceAccessor);
		verticalLayout.addComponent(instructions);
		instructions.setWidth("30%");
		verticalLayout.setComponentAlignment(instructions, Alignment.MIDDLE_LEFT);

    }
    private VerticalLayout getInstructions(MessageSourceAccessor messageSourceAccessor) {
		VerticalLayout panel = new VerticalLayout();
		TextArea textArea = new TextArea();
		textArea.setWidth("100%");
		textArea.setHeight("100%");
		textArea.setValue(messageSourceAccessor.getMessage("demo.instructions"));
		textArea.setReadOnly(true);
        panel.addComponent(textArea);
        panel.setComponentAlignment(textArea, Alignment.MIDDLE_CENTER);
        return panel;
    }
    private void logout() {
    	VaadinService.getCurrentRequest().getWrappedSession().invalidate();
    	getUI().close();
        String contextPath = VaadinService.getCurrentRequest().getContextPath();
        getUI().getPage().setLocation(contextPath);
    }
    /* 
     * This is where we establish the actual person object. 
     * We just get it from the UI object and assume to knows how to supply it(non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
    	MyUI ui = (MyUI)UI.getCurrent();
    	if (m_customer == null) {
    		m_customer = ui.getCustomer();
        	m_maduraSessionManager.getValidationSession().bind(m_customer);
        	customerForm.setItemDataSource(new BeanItem<Customer>(m_customer));
// The following happens automatically in a MaduraForm.setItemDataSource
//        	List<MaduraPropertyWrapper> properties = personForm.getItemDataSourceProperties();
//    		m_maduraSessionManager.bind(submit, properties);
//    		m_maduraSessionManager.bind(cancel, properties);
    	}
    }
	@Override
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}
}
