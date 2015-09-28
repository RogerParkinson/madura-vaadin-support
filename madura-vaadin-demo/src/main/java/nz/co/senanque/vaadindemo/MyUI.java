package nz.co.senanque.vaadindemo;

import java.util.Set;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.addressbook.instances.Person;
import nz.co.senanque.login.RequestValidator;
import nz.co.senanque.vaadin.application.MaduraSessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.ContextLoaderListener;

import ch.qos.logback.ext.spring.web.LogbackConfigListener;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Theme("valo")
@Widgetset("nz.co.senanque.vaadindemo.MyAppWidgetset")
@SpringUI
public class MyUI extends UI implements MessageSourceAware {

	private static final long serialVersionUID = 1L;
	private static Logger m_logger = LoggerFactory.getLogger(MyUI.class);
	
	@Autowired private SpringViewProvider viewProvider;
	private transient MessageSourceAccessor m_messageSourceAccessor;
	@Autowired private MaduraSessionManager m_maduraSessionManager;

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
    		"nz.co.senanque.validationengine"})
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
    }
    @Override
    protected void init(VaadinRequest vaadinRequest) { // called at session start
    	
    	// Initialise the permission manager using data from the login
    	// This assumes madura-login handled the login. Other authentication mechanisms will need different code
    	// but they should all populate the permission manager.
    	String currentUser = (String)vaadinRequest.getWrappedSession().getAttribute(RequestValidator.USERNAME);
    	@SuppressWarnings("unchecked")
		Set<String> currentPermissions = (Set<String>)vaadinRequest.getWrappedSession().getAttribute(RequestValidator.PERMISSIONS);
    	m_maduraSessionManager.getPermissionManager().setPermissionsList(currentPermissions);
    	m_maduraSessionManager.getPermissionManager().setCurrentUser(currentUser);

//        final VerticalLayout root = new VerticalLayout();
//        root.setSizeFull();
//        root.setMargin(true);
//        root.setSpacing(true);
//        setContent(root);
//
//        final CssLayout navigationBar = new CssLayout();
//        navigationBar.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
//        navigationBar.addComponent(createNavigationButton("View Scoped View",
//                ViewScopedView.VIEW_NAME));
//		root.addComponent(navigationBar);
//		Button logout = new Button("Logout");
//		logout.addClickListener(new Button.ClickListener() {
//			@Override
//			public void buttonClick(ClickEvent event) {
//				logout();
//			}
//		});
//		navigationBar.addComponent(logout);
//
//        final Panel viewContainer = new Panel();
//        viewContainer.setSizeFull();
//        root.addComponent(viewContainer);
//        root.setExpandRatio(viewContainer, 1.0f);
//
//        Navigator navigator = new Navigator(this, viewContainer);
//        navigator.addProvider(viewProvider);
    	
    	final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        // create a dummy object to map things to
        Person person = new Person();
    	m_maduraSessionManager.getValidationSession().bind(person);

    	// create a form to display the object and add it to the UI
    	PersonForm personForm = new PersonForm(m_maduraSessionManager);
    	personForm.setFieldList(new String[]{"id","name","email","address"});
    	layout.addComponent(personForm);
    	
    	// Map the person object onto the person form
    	personForm.setItemDataSource(new BeanItem<Person>(person));

        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                layout.addComponent(new Label("Thank you for clicking"));
            }
        });
        layout.addComponent(button);
        Button logout = new Button("Logout");
        logout.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                logout();
            }
        });
        layout.addComponent(logout);

    }
    private Button createNavigationButton(String caption, final String viewName) {
        Button button = new Button(caption);
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        // If you didn't choose Java 8 when creating the project, convert this to an anonymous listener class
        button.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				getUI().getNavigator().navigateTo(viewName);
			}
		});
        return button;
    }
    private void logout() {
    	VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        this.close();
        String contextPath = VaadinService.getCurrentRequest().getContextPath();
        getUI().getPage().setLocation(contextPath);
    }
	@Override
	public void setMessageSource(MessageSource messageSource) {
		m_messageSourceAccessor = new MessageSourceAccessor(messageSource);
		
	}
}
