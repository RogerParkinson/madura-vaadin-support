package nz.co.senanque.addressbook;

import java.util.Locale;
import java.util.Set;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.addressbook.instances.Person;
import nz.co.senanque.login.AuthenticationDelegate;
import nz.co.senanque.login.RequestValidator;
import nz.co.senanque.vaadin.Hints;
import nz.co.senanque.vaadin.HintsImpl;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.vaadin.tableeditor.EditorWindow;
import nz.co.senanque.vaadin.tableeditor.EditorWindowImpl;
import nz.co.senanque.vaadin.tableeditor.TableEditorLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.ContextLoaderListener;

import ch.qos.logback.ext.spring.web.LogbackConfigListener;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 *
 */
@Theme("addressbooktheme")
@Widgetset("nz.co.senanque.addressbook.AddressBookWidgetset")
@SpringUI
public class AddressBookUI extends UI  {
	
	private static Logger m_logger = LoggerFactory.getLogger(AddressBookUI.class);

	@Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired private TableEditorLayout<?> m_tableEditorLayout;
	@Autowired private SpringViewProvider viewProvider;

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
    		ret.setColumns(new String[]{"name","email","address","gender","startDate","amount"});
    		return ret;
    	}
    	@Bean(name="hints")
    	@UIScope
    	public Hints getHints() {
    		return new HintsImpl();
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
    	String currentUser = (String)vaadinRequest.getWrappedSession().getAttribute(AuthenticationDelegate.USERNAME);
    	String localeString = (String)vaadinRequest.getWrappedSession().getAttribute(AuthenticationDelegate.LOCALE);
    	@SuppressWarnings("unchecked")
		Set<String> currentPermissions = (Set<String>)vaadinRequest.getWrappedSession().getAttribute(AuthenticationDelegate.PERMISSIONS);
    	m_maduraSessionManager.getPermissionManager().setPermissionsList(currentPermissions);
    	m_maduraSessionManager.getPermissionManager().setCurrentUser(currentUser);
    	this.getSession().setConverterFactory(m_maduraSessionManager.getMaduraConverterFactory());
    	
    	Locale locale = new Locale(localeString);
    	this.setLocale( locale );
    	this.getSession().setLocale( locale );
    	LocaleContextHolder.setLocale(locale);

    	final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.setSpacing(true);
        setContent(root);

        final Panel viewContainer = new Panel();
        viewContainer.setSizeFull();
        root.addComponent(viewContainer);
        root.setExpandRatio(viewContainer, 1.0f);

        Navigator navigator = new Navigator(this, viewContainer);
        navigator.addProvider(viewProvider);
        
    }
    @WebServlet(urlPatterns = "/*", name = "AddressBookUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = AddressBookUI.class, productionMode = false)
    public static class AddressBookUIServlet extends SpringVaadinServlet {
    }

}
