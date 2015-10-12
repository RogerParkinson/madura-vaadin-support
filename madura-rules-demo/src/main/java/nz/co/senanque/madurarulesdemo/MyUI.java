package nz.co.senanque.madurarulesdemo;

import java.util.Set;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.addressbook.instances.Person;
import nz.co.senanque.login.AuthenticationDelegate;
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
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
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
    	
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        PersonForm personForm = new PersonForm(m_maduraSessionManager);
        personForm.setCaption(new MessageSourceAccessor(m_maduraSessionManager.getMessageSource()).getMessage("login.title"));
        personForm.setWidth("30%");
        layout.addComponent(personForm);
        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                layout.addComponent(new Label("Thank you for clicking#1"));
            }
        });
        layout.addComponent(button);
        
        Person m_person = new Person();
    	m_maduraSessionManager.getValidationSession().bind(m_person);
    	personForm.setItemDataSource(new BeanItem<Person>(m_person));

    }

//    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
//    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
//    public static class MyUIServlet extends VaadinServlet {
//    }
}
