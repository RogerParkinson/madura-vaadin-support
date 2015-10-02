package nz.co.senanque.addressbook;

import java.util.Set;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.addressbook.instances.Person;
import nz.co.senanque.login.RequestValidator;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.vaadin.tableeditor.EditorWindow;
import nz.co.senanque.vaadin.tableeditor.EditorWindowImpl;
import nz.co.senanque.vaadin.tableeditor.TableEditorLayout;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
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
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 *
 */
@Theme("addressbooktheme")
@Widgetset("nz.co.senanque.addressbook.AddressBookWidgetset")
@SpringUI
public class AddressBookUI extends UI implements MessageSourceAware  {
	
	private static Logger m_logger = LoggerFactory.getLogger(AddressBookUI.class);

	@Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired private TableEditorLayout<?> m_tableEditorLayout;

	private MessageSource m_messageSource;

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
    		"nz.co.senanque.vaadin.tableeditor"})
    @PropertySource("classpath:config.properties")
    public static class MyConfiguration {
    	
    	public MyConfiguration() {
    		m_logger.info("MyConfiguration"); // this gets called at application startup, not session startup so this is an app bean.
    	}
    	
    	// needed for @PropertySource
    	@Bean
    	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
    		return new PropertySourcesPlaceholderConfigurer();
    	}

    	@Bean(name="personTableLayout")
    	@UIScope
    	public TableEditorLayout<Person> getTableEditorLayout() {
    		TableEditorLayout<Person> ret = new TableEditorLayout<Person>("people");
    		ret.setColumns(new String[]{"name","address","email"});
    		return ret;
    	}
    	@Bean(name="editorWindow")
    	@UIScope
    	public EditorWindow<Person> getEditorWIndow() {
    		EditorWindowImpl<Person> ret = new EditorWindowImpl<Person>("person");
    		return ret;
    	}
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	
    	// Initialise the permission manager using data from the login
    	// This assumes madura-login handled the login. Other authentication mechanisms will need different code
    	// but they should all populate the permission manager.
    	String currentUser = (String)vaadinRequest.getWrappedSession().getAttribute(RequestValidator.USERNAME);
    	@SuppressWarnings("unchecked")
		Set<String> currentPermissions = (Set<String>)vaadinRequest.getWrappedSession().getAttribute(RequestValidator.PERMISSIONS);
    	m_maduraSessionManager.getPermissionManager().setPermissionsList(currentPermissions);
    	m_maduraSessionManager.getPermissionManager().setCurrentUser(currentUser);

    	final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        layout.addComponent(m_tableEditorLayout);
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
        Button button = new Button(messageSourceAccessor.getMessage("logout"));
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                logout();
            }
        });
        layout.addComponent(button);
        
    }
    private void logout() {
    	VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        this.close();
        String contextPath = VaadinService.getCurrentRequest().getContextPath();
        getUI().getPage().setLocation(contextPath);
    }

    @WebServlet(urlPatterns = "/*", name = "AddressBookUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = AddressBookUI.class, productionMode = false)
    public static class AddressBookUIServlet extends SpringVaadinServlet {
    }

	@Override
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
		
	}

}
