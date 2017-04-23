package nz.co.senanque.addressbook;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.addressbook.instances.Person;
import nz.co.senanque.addressbook.instances.TreeSpecies;
import nz.co.senanque.permissionmanager.PermissionManager;
import nz.co.senanque.permissionmanager.PermissionManagerImpl;
import nz.co.senanque.vaadin.Hints;
import nz.co.senanque.vaadin.HintsImpl;
import nz.co.senanque.vaadin.MaduraSessionManager;
import nz.co.senanque.vaadin.permissionmanager.PermissionResolverLoginImpl;
import nz.co.senanque.vaadin.tableeditor.EditorWindowImpl;
import nz.co.senanque.vaadin.tableeditor.TableEditorLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.vaadin.data.Container;
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
import com.vaadin.ui.themes.ValoTheme;

/**
 * Main application class.
 * 
 * @author Roger Parkinson
 *
 */
@Theme("addressbooktheme")
@Title("Madura Address Book")
@Widgetset("com.vaadin.DefaultWidgetSet")
@SpringUI
public class AddressBookUI extends UI  {
	
	private static Logger m_logger = LoggerFactory.getLogger(AddressBookUI.class);

	@Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired private PersonView m_personView;
	@Autowired private TreeView m_treeView;

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
    		"nz.co.senanque.vaadin.tableeditor"})// madura-tableeditor
    @PropertySource("classpath:config.properties")
    public static class MyConfiguration {
    	
    	@Autowired @Qualifier("personContainer") 
    	private Container.Filterable personContainer;
    	@Autowired @Qualifier("treeContainer") 
    	private Container.Filterable treeContainer;

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
    	public TableEditorLayout<Person> getPersonTableLayout() {
    		TableEditorLayout<Person> ret = new TableEditorLayout<Person>("people", Person.class);
    		ret.setColumns(new String[]{"name","email","address","gender","startDate","amount"});
    		ret.setEditorWindow(new EditorWindowImpl<Person>("person",ValoTheme.BUTTON_PRIMARY));
    		ret.setContainer(personContainer);
    		return ret;
    	}
    	@Bean(name="treeTableLayout")
    	@UIScope
    	public TableEditorLayout<TreeSpecies> getTreeTableLayout() {
    		TableEditorLayout<TreeSpecies> ret = new TableEditorLayout<TreeSpecies>("people", TreeSpecies.class);
    		ret.setColumns(new String[]{"name"});
    		ret.setEditorWindow(new EditorWindowImpl<TreeSpecies>("person",ValoTheme.BUTTON_PRIMARY));
    		ret.setContainer(treeContainer);
    		return ret;
    	}
    	@Bean(name="hints")
    	@UIScope
    	public Hints getHints() {
    		return new HintsImpl();
    	}
    	@Bean
    	@UIScope
    	public PermissionManager getPermissionManager() {
    		PermissionManagerImpl ret =  new PermissionManagerImpl();
    		ret.setPermissionResolver(new PermissionResolverLoginImpl());
    		return ret;
    	}
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	
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
        tab1.addComponent(m_personView);
        tabsheet.addTab(tab1, messageSourceAccessor.getMessage("people"));

        // Create the second tab
        VerticalLayout tab2 = new VerticalLayout();
        tab2.addComponent(m_treeView);
        tabsheet.addTab(tab2, messageSourceAccessor.getMessage("tree"));

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
    @WebServlet(urlPatterns = "/*", name = "AddressBookUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = AddressBookUI.class, productionMode = false)
    public static class AddressBookUIServlet extends SpringVaadinServlet {
    }

	public PersonView getPersonView() {
		return m_personView;
	}
	public void setPersonView(PersonView defaultView) {
		m_personView = defaultView;
	}

}
