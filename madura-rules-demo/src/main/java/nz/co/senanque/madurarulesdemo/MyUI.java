package nz.co.senanque.madurarulesdemo;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.pizzaorder.instances.Customer;
import nz.co.senanque.pizzaorder.instances.Order;
import nz.co.senanque.vaadin.Hints;
import nz.co.senanque.vaadin.HintsImpl;
import nz.co.senanque.vaadin.MaduraSessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.ContextLoaderListener;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.EventRouter;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Main UI class.
 * 
 * @author Roger Parkinson
 *
 */
@Theme("mytheme")
@Title("Madura Rules Demo")
@Widgetset("com.vaadin.DefaultWidgetSet")
@SpringUI
public class MyUI extends UI {

	private static Logger m_logger = LoggerFactory.getLogger(MyUI.class);
	
	@Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired private CustomerView m_customerView;
	@Autowired private CustomerView2 m_customerView2;
	@Autowired private CustomerView3 m_customerView3;
	@Autowired private OrderView m_orderView;
	@Autowired private MyEventRouter m_eventRouter;
	
    @WebServlet(name = "MyUIServlet", urlPatterns = "/*", asyncSupported = true)
    public static class MyUIServlet extends SpringVaadinServlet {

		private static final long serialVersionUID = 1L;
    }

    @WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    	// This causes the applicationContext.xml context file to be loaded
    	// per session.
    }

    @Configuration
    @EnableVaadin
    @ComponentScan(basePackages = {
    		"nz.co.senanque.vaadin",			// madura-vaadin
    		"nz.co.senanque.validationengine",	// madura-objects
    		"nz.co.senanque.rules"})			// madura-rules
    @PropertySource("classpath:config.properties")
    public static class MyConfiguration {
    	
    	public MyConfiguration() {
    		m_logger.info("MyConfiguration"); // this gets called at application startup, not session startup so this is an app bean.
    	}
// Must not instantiate this here. MyUI is a session bean, but it is already instantiated before we get here
//    	@Bean
//    	public MyUI getMyUI() {
//    		return new MyUI();
//    	}

    	// needed for @PropertySource
    	@Bean
    	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
    		return new PropertySourcesPlaceholderConfigurer();
    	}
    	@Bean(name="hints")
    	@UIScope
    	public Hints getHints() {
    		return new HintsImpl();
    	}
    }
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	
    	MessageSourceAccessor messageSourceAccessor= new MessageSourceAccessor(m_maduraSessionManager.getMessageSource());
    	final String logout = messageSourceAccessor.getMessage("Logout");
    	
        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.setSpacing(true);
        setContent(root);

        TabSheet tabsheet = new TabSheet();
        root.addComponent(tabsheet);

        // Create the first tab
        VerticalLayout tab1 = new VerticalLayout();
        tab1.addComponent(m_customerView);
        m_customerView.load(new Customer());
        tabsheet.addTab(tab1, messageSourceAccessor.getMessage("Customer"));

        // This tab gets its caption from the component caption
        VerticalLayout tab3 = new VerticalLayout();
        tab3.addComponent(m_customerView2);
        m_customerView2.load(new Customer());
        tabsheet.addTab(tab3,"C2");

        // This tab gets its caption from the component caption
        VerticalLayout tab4 = new VerticalLayout();
        tab4.addComponent(m_customerView3);
        m_customerView3.load(new Customer());
        tabsheet.addTab(tab4,"C3");

        // This tab gets its caption from the component caption
        VerticalLayout tabOrder = new VerticalLayout();
        tabOrder.addComponent(m_orderView);
        m_orderView.load(new Order());
        tabsheet.addTab(tabOrder,messageSourceAccessor.getMessage("Order"));

        VerticalLayout tabLogout = new VerticalLayout();
        tabsheet.addTab(tabLogout,logout);

        tabsheet.addSelectedTabChangeListener(new SelectedTabChangeListener(){

			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet tabSheet = event.getTabSheet();
				Component c = tabSheet.getSelectedTab();
				String caption = tabSheet.getTab(c).getCaption();
				if (caption.equals(logout)) {
					m_maduraSessionManager.logout(getUI());
				}
			}});

    }

	public EventRouter getEventRouter() {
		return m_eventRouter;
	}
	
	public static MyUI getCurrent() {
		return (MyUI)UI.getCurrent();
	}
	public CustomerView getCustomerView() {
		return m_customerView;
	}
	public void setCustomerView(CustomerView customerView) {
		m_customerView = customerView;
	}
	public OrderView getOrderView() {
		return m_orderView;
	}
	public void setOrderView(OrderView orderView) {
		m_orderView = orderView;
	}
	public void setEventRouter(MyEventRouter eventRouter) {
		m_eventRouter = eventRouter;
	}
	public CustomerView2 getCustomerView2() {
		return m_customerView2;
	}
	public void setCustomerView2(CustomerView2 customerView2) {
		m_customerView2 = customerView2;
	}

}
