package nz.co.senanque.vaadindemo;

import java.util.Locale;
import java.util.Set;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.addressbook.instances.Person;
import nz.co.senanque.login.AuthenticationDelegate;
import nz.co.senanque.vaadin.Hints;
import nz.co.senanque.vaadin.HintsImpl;
import nz.co.senanque.vaadin.SimpleButtonPainter;
import nz.co.senanque.vaadin.SubmitButtonPainter;
import nz.co.senanque.vaadin.application.MaduraSessionManager;

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
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItem;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
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

@Theme("mytheme")
@Widgetset("nz.co.senanque.vaadindemo.MyAppWidgetset")
@SpringUI
public class MyUI extends UI implements MessageSourceAware {

	private static final long serialVersionUID = 1L;
	private static Logger m_logger = LoggerFactory.getLogger(MyUI.class);
	
	@Autowired private SpringViewProvider viewProvider;
	@Autowired private MaduraSessionManager m_maduraSessionManager;
	private MessageSource m_messageSource;

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
    protected void init(VaadinRequest vaadinRequest) { // called at session start
    	
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
    	final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
    	Person person = new Person();
    	
    	final HorizontalLayout horizontalLayout = new HorizontalLayout();
    	horizontalLayout.setSizeFull();
        setContent(horizontalLayout);
    	
        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        verticalLayout.addStyleName("madura-form");
        horizontalLayout.addComponent(verticalLayout);

        PersonForm personForm = new PersonForm(m_maduraSessionManager);
        personForm.setCaption(messageSourceAccessor.getMessage("login.title"));
        verticalLayout.addComponent(personForm);

		HorizontalLayout actions = new HorizontalLayout();
		Button cancel = personForm.createButton("button.cancel", new SimpleButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.cancel"),
						messageSourceAccessor.getMessage("message.noop"),
						Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		Button submit = personForm.createButton("button.submit", new SubmitButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.submit"),
						messageSourceAccessor.getMessage("message.noop"),
		                  Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		Button logout = personForm.createButton("button.logout", new SimpleButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				logout();
				
			}});
		actions.addComponent(cancel);
		actions.addComponent(submit);
		actions.addComponent(logout);
		verticalLayout.addComponent(actions);
		verticalLayout.setComponentAlignment(actions, Alignment.MIDDLE_CENTER);
		
		Component instructions = getInstructions(messageSourceAccessor);
		horizontalLayout.addComponent(instructions);
		horizontalLayout.setComponentAlignment(instructions, Alignment.MIDDLE_CENTER);

    	m_maduraSessionManager.getValidationSession().bind(person);
    	personForm.setItemDataSource(new BeanItem<Person>(person));
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
        this.close();
        String contextPath = VaadinService.getCurrentRequest().getContextPath();
        getUI().getPage().setLocation(contextPath);
    }
	public Person getPerson() {
        return new Person();
	}
	@Override
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}
}
