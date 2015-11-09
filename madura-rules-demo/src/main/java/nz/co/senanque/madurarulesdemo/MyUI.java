package nz.co.senanque.madurarulesdemo;

import java.util.Set;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.login.AuthenticationDelegate;
import nz.co.senanque.pizzaorder.instances.Customer;
import nz.co.senanque.pizzaorder.instances.Order;
import nz.co.senanque.vaadin.Hints;
import nz.co.senanque.vaadin.HintsImpl;
import nz.co.senanque.vaadin.application.MaduraSessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.ContextLoaderListener;

import ch.qos.logback.ext.spring.web.LogbackConfigListener;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.EventRouter;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@Title("Madura Rules Demo")
@Widgetset("nz.co.senanque.madurarulesdemo.MyAppWidgetset")
@SpringUI
public class MyUI extends UI {

	private static Logger m_logger = LoggerFactory.getLogger(MyUI.class);
	
	@Autowired private SpringViewProvider viewProvider;
	@Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired private CustomerView m_customerView;
	@Autowired private OrderView m_orderView;
	@Autowired private MyEventRouter m_eventRouter;
	
	private Customer m_customer;
	private Order m_order;

    @WebServlet(name = "MyUIServlet", urlPatterns = "/*", asyncSupported = true)
    public static class MyUIServlet extends SpringVaadinServlet {

		private static final long serialVersionUID = 1L;
    }

    @WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    	// This causes the applicationContext.xml context file to be loaded
    	// per session.
    }

    @WebListener
    public static class MyLogbackConfigListener extends LogbackConfigListener {
    	// Need this to init logback correctly
    }
    
    @Configuration
    @EnableVaadin
    @ComponentScan(basePackages = {
    		"nz.co.senanque.vaadin",
    		"nz.co.senanque.validationengine",
    		"nz.co.senanque.rules"})
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
    	
    	// Initialise the permission manager using data from the login
    	// This assumes madura-login handled the login. Other authentication mechanisms will need different code
    	// but they should all populate the permission manager.
    	String currentUser = (String)vaadinRequest.getWrappedSession().getAttribute(AuthenticationDelegate.USERNAME);
    	@SuppressWarnings("unchecked")
		Set<String> currentPermissions = (Set<String>)vaadinRequest.getWrappedSession().getAttribute(AuthenticationDelegate.PERMISSIONS);
    	m_maduraSessionManager.getPermissionManager().setPermissionsList(currentPermissions);
    	m_maduraSessionManager.getPermissionManager().setCurrentUser(currentUser);
    	this.getSession().setConverterFactory(m_maduraSessionManager.getMaduraConverterFactory());
    	
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
        m_customerView.enter(null);
        tabsheet.addTab(tab1, messageSourceAccessor.getMessage("Customer"));

        // This tab gets its caption from the component caption
        VerticalLayout tab2 = new VerticalLayout();
        tab2.addComponent(m_orderView);
        m_orderView.enter(null);
        tabsheet.addTab(tab2,messageSourceAccessor.getMessage("Order"));

        VerticalLayout tabLogout = new VerticalLayout();
        tabsheet.addTab(tabLogout,logout);

        tabsheet.addSelectedTabChangeListener(new SelectedTabChangeListener(){

			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet tabSheet = event.getTabSheet();
				Component c = tabSheet.getSelectedTab();
				String caption = tabSheet.getTab(c).getCaption();
				if (caption.equals(logout)) {
					logout();
				}
				
			}});

    }
    private void logout() {
    	VaadinService.getCurrentRequest().getWrappedSession().invalidate();
    	getUI().close();
        String contextPath = VaadinService.getCurrentRequest().getContextPath();
        getUI().getPage().setLocation(contextPath);
    }
	public Customer getCustomer() {
		if (m_customer == null) {
			m_customer = new Customer();
        	m_maduraSessionManager.getValidationSession().bind(m_customer);
		}
		return m_customer;
	}
	public Order getOrder() {
		if (m_order == null) {
			m_order = new Order();
        	m_maduraSessionManager.getValidationSession().bind(m_order);
		}
		return m_order;
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

}
