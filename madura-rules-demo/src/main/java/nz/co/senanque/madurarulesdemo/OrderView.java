/**
 * 
 */
package nz.co.senanque.madurarulesdemo;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Customer;
import nz.co.senanque.pizzaorder.instances.Order;
import nz.co.senanque.vaadin.MaduraForm;
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
@SpringView(name = OrderView.VIEW_NAME)
public class OrderView extends VerticalLayout implements View, MessageSourceAware {
    public static final String VIEW_NAME = "";
    @Autowired private MaduraSessionManager m_maduraSessionManager;
    @Autowired private PizzaWindow m_pizzaWindow;
    private Order m_order = null;
    private MaduraForm orderForm;
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

        orderForm = new MaduraForm(m_maduraSessionManager);
        orderForm.setCaption(messageSourceAccessor.getMessage("login.title"));
        orderForm.setWidth("30%");
        orderForm.setFieldList(new String[]{"name","email","address","gender","startDate","amount"});
        verticalLayout.addComponent(orderForm);

		HorizontalLayout actions = new HorizontalLayout();
		Button cancel = orderForm.createButton("button.cancel", new SimpleButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.cancel"),
						messageSourceAccessor.getMessage("message.noop"),
						Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		Button submit = orderForm.createButton("button.submit", new SubmitButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.submit"),
						messageSourceAccessor.getMessage("message.noop"),
		                  Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		actions.addComponent(cancel);
		actions.addComponent(submit);
		orderForm.setFooter(actions);
		
    }
    /* 
     * This is where we establish the actual customer object. 
     * We just get it from the UI object and assume to knows how to supply it(non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
    	MyUI ui = (MyUI)UI.getCurrent();
    	ui.reviewNavigationButtons(VIEW_NAME);
    	if (m_order == null) {
    		m_order = ui.getOrder();
        	orderForm.setItemDataSource(new BeanItem<Order>(m_order));
    	}
    }
	@Override
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}
	public PizzaWindow getPizzaWindow() {
		return m_pizzaWindow;
	}
	public void setPizzaWindow(PizzaWindow pizzaWindow) {
		m_pizzaWindow = pizzaWindow;
	}
}
