package org.madura.mobile.demo;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.vaadin.Hints;
import nz.co.senanque.vaadin.HintsImpl;
import nz.co.senanque.vaadin.SpringAwareTouchKitServlet;

import org.madura.mobile.demo.ui.MenuView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.ContextLoaderListener;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;

/**
 * The UI's "main" class
 */
@SuppressWarnings("serial")
@Widgetset("org.madura.mobile.demo.gwt.MaduraMobileDemoWidgetSet")
@Theme("touchkit")
//// Cache static application files so as the application can be started
//// and run even when the network is down.
//@CacheManifestEnabled
//// Switch to the OfflineMode client UI when the server is unreachable
//@OfflineModeEnabled
//// Make the server retain UI state whenever the browser reloads the app
//@PreserveOnRefresh
@SpringUI
public class MaduraMobileDemoTouchKitUI extends UI {

	private static Logger m_logger = LoggerFactory.getLogger(MaduraMobileDemoTouchKitUI.class);
	
	@WebServlet(name = "MyUIServlet", urlPatterns = "/*", asyncSupported = true)
    public static class MyUIServlet extends SpringAwareTouchKitServlet {

		private static final long serialVersionUID = 1L;
    }

    @WebListener
    public static class MyContextLoaderListener extends ContextLoaderListener {
    	// This causes the applicationContext.xml context file to be loaded
    	// per session.
    }
//    private final MaduraMobileDemoPersistToServerRpc serverRpc = new MaduraMobileDemoPersistToServerRpc() {
//        @Override
//        public void persistToServer() {
//            // TODO this method is called from client side to store offline data
//        }
//    };

    @Configuration
    @EnableVaadin
//    @ComponentScan(basePackages = {
//    		"nz.co.senanque.vaadin",			// madura-vaadin
//    		"nz.co.senanque.validationengine",	// madura-objects
//    		"nz.co.senanque.rules"})			// madura-rules
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
    protected void init(VaadinRequest request) {
        final TabBarView tabBarView = new TabBarView();
        final NavigationManager navigationManager = new NavigationManager();
        navigationManager.setCaption("Tab 1");
        navigationManager.setCurrentComponent(new MenuView());
        Tab tab;
        tab = tabBarView.addTab(navigationManager);
        tab.setIcon(FontAwesome.BOOK);
        tab = tabBarView.addTab(new Label("Tab 2"), "Tab 2");
        tab.setIcon(FontAwesome.AMBULANCE);
        tab = tabBarView.addTab(new Label("Tab 3"), "Tab 3");
        tab.setIcon(FontAwesome.DOWNLOAD);
        setContent(tabBarView);

//        // Use of the OfflineMode connector is optional.
//        OfflineMode offlineMode = new OfflineMode();
//        offlineMode.extend(this);
//        // Maintain the session when the browser app closes.
//        offlineMode.setPersistentSessionCookie(true);
//        // Define the timeout in secs to wait when a server request is sent
//        // before falling back to offline mode.
//        offlineMode.setOfflineModeTimeout(15);
    }
}

