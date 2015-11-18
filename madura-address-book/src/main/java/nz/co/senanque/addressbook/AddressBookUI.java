package nz.co.senanque.addressbook;

import java.util.Set;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.addressbook.instances.Person;
import nz.co.senanque.login.AuthenticationDelegate;
import nz.co.senanque.vaadin.Hints;
import nz.co.senanque.vaadin.HintsImpl;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.vaadin.tableeditor.EditorWindow;
import nz.co.senanque.vaadin.tableeditor.EditorWindowImpl;
import nz.co.senanque.vaadin.tableeditor.TableEditorLayout;

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
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Main application class.
 * 
 * @author Roger Parkinson
 *
 */
@Theme("addressbooktheme")
@Title("Madura Address Book")
@Widgetset("nz.co.senanque.addressbook.AddressBookWidgetset")
@SpringUI
public class AddressBookUI extends UI  {
	
	private static Logger m_logger = LoggerFactory.getLogger(AddressBookUI.class);

	@Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired private TableEditorLayout<?> m_tableEditorLayout;
	@Autowired private DefaultView m_defaultView;

	@WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    	// This causes the applicationContext.xml context file to be loaded
    	// per session.
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
    		TableEditorLayout<Person> ret = new TableEditorLayout<Person>("people", Person.class);
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
    		EditorWindowImpl<Person> ret = new EditorWindowImpl<Person>("person",ValoTheme.BUTTON_PRIMARY);
    		return ret;
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
    	final String logout = messageSourceAccessor.getMessage("logout");
    	
    	final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.setSpacing(true);
        setContent(root);
        
        TabSheet tabsheet = new TabSheet();
        root.addComponent(tabsheet);
        // Create the first tab
        VerticalLayout tab1 = new VerticalLayout();
        tab1.addComponent(m_defaultView);
        tabsheet.addTab(tab1, messageSourceAccessor.getMessage("people"));

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
    	m_maduraSessionManager.close();
    	VaadinService.getCurrentRequest().getWrappedSession().invalidate();
    	getUI().close();
        String contextPath = VaadinService.getCurrentRequest().getContextPath();
        getUI().getPage().setLocation(contextPath);
    }
    @WebServlet(urlPatterns = "/*", name = "AddressBookUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = AddressBookUI.class, productionMode = false)
    public static class AddressBookUIServlet extends SpringVaadinServlet {
    }

	public DefaultView getDefaultView() {
		return m_defaultView;
	}
	public void setDefaultView(DefaultView defaultView) {
		m_defaultView = defaultView;
	}

}
