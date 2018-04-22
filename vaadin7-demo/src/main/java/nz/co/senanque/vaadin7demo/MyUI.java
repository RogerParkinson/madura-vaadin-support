package nz.co.senanque.vaadin7demo;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.login.OAuth2Constants;
import nz.co.senanque.login.PermissionResolverOAuth;
import nz.co.senanque.permissionmanager.PermissionManager;
import nz.co.senanque.permissionmanager.PermissionManagerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.vaadin.annotations.Theme;
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
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
@SpringUI
public class MyUI extends UI {

	private static final long serialVersionUID = 1L;
	private static Logger m_logger = LoggerFactory.getLogger(MyUI.class);
	
	@Autowired
    private SpringViewProvider viewProvider;
	
    @WebServlet(name = "MyUIServlet", urlPatterns = {"/app/*", "/VAADIN/*"}, asyncSupported = true)
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
    		"nz.co.senanque.vaadin",
    		"nz.co.senanque.validationengine",
    		"nz.co.senanque.testv7"})
    @PropertySource("classpath:config.properties")
    public static class MyConfiguration {
    	
    	@Autowired private JwtTokenStore tokenStore;
    	
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

		@Bean
    	@UIScope
    	public PermissionManager getPermissionManager() {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
			OAuth2AccessToken oauth2AccessToken = (OAuth2AccessToken)requestAttributes.getRequest().getSession().getAttribute(OAuth2Constants.ACCESS_TOKEN);
			SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
			OAuth2Authentication authentication = tokenStore.readAuthentication(oauth2AccessToken);
			securityContext.setAuthentication(authentication);
			authentication.setAuthenticated(true);
			SecurityContextHolder.setContext(securityContext);

    		PermissionManagerImpl ret =  new PermissionManagerImpl();
    		ret.setPermissionResolver(new PermissionResolverOAuth());
    		return ret;
    	}
    }
    @Override
    protected void init(VaadinRequest vaadinRequest) { // called at session start
    	
        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.setSpacing(true);
        setContent(root);

        final CssLayout navigationBar = new CssLayout();
        navigationBar.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        navigationBar.addComponent(createNavigationButton("View Scoped View",
                ViewScopedView.VIEW_NAME));
        navigationBar.addComponent(createLogoutButton());
        root.addComponent(navigationBar);
        

        final Panel viewContainer = new Panel();
        viewContainer.setSizeFull();
        root.addComponent(viewContainer);
        root.setExpandRatio(viewContainer, 1.0f);

        Navigator navigator = new Navigator(this, viewContainer);
        navigator.addProvider(viewProvider);
    	
    }
    private Button createNavigationButton(String caption, final String viewName) {
        Button button = new Button(caption);
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				getUI().getNavigator().navigateTo(viewName);
			}
		});
        return button;
    }
    private Button createLogoutButton() {
        Button button = new Button("logout");
        button.addStyleName(ValoTheme.BUTTON_SMALL);
        button.addClickListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
		    	VaadinService.getCurrentRequest().getWrappedSession().invalidate();
		    	getUI().close();
		        String contextPath = VaadinService.getCurrentRequest().getContextPath();
		        getUI().getPage().setLocation(contextPath+"/logout");

			}
		});
        return button;
    	
    }
}
