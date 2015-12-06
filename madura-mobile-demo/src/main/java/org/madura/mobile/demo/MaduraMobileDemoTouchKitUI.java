package org.madura.mobile.demo;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import nz.co.senanque.vaadin.Hints;
import nz.co.senanque.vaadin.HintsImpl;
import nz.co.senanque.vaadin.MaduraSessionManager;
import nz.co.senanque.vaadin.RequestWrapper;
import nz.co.senanque.vaadin.SpringAwareTouchKitServlet;
import nz.co.senanque.vaadin.TouchkitHintsImpl;

import org.madura.mobile.demo.ui.MenuView;
import org.madura.mobile.demo.ui.PizzaView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.MessageSourceAccessor;
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
@Theme("mytouchkit")
@SpringUI(path="mobile")
public class MaduraMobileDemoTouchKitUI extends UI {

	private static Logger m_logger = LoggerFactory.getLogger(MaduraMobileDemoTouchKitUI.class);
	
	@Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired private PizzaView m_pizzaView;

	@WebServlet(name = "MyUIServlet", urlPatterns = "/*", asyncSupported = true)
    public static class MyUIServlet extends SpringAwareTouchKitServlet {

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
    		"nz.co.senanque.vaadin",			// madura-vaadin
    		"nz.co.senanque.validationengine",	// madura-objects
    		"nz.co.senanque.rules"})			// madura-rules
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
    	@Bean(name="hints")
    	@UIScope
    	public Hints getHints() {
            if (RequestWrapper.isMobile()) {
                return new TouchkitHintsImpl();
            } else {
                return new HintsImpl();
            }
    	}
    }
    @Override
    protected void init(VaadinRequest vaadinRequest) {

    	MessageSourceAccessor messageSourceAccessor= new MessageSourceAccessor(m_maduraSessionManager.getMessageSource());
    	final String logout = messageSourceAccessor.getMessage("Logout");

    	final TabBarView tabBarView = new TabBarView();
        final NavigationManager navigationManager = new NavigationManager();
        navigationManager.setCaption("Tab 1");
        navigationManager.setCurrentComponent(new MenuView());
        Tab tab;
        tab = tabBarView.addTab(navigationManager);
        tab.setIcon(FontAwesome.BOOK);
        tab = tabBarView.addTab(m_pizzaView, "Tab 2");
        tab.setIcon(FontAwesome.AMBULANCE);
        tab = tabBarView.addTab(new Label("Tab 3"), "Tab 3");
        tab.setIcon(FontAwesome.DOWNLOAD);
        setContent(tabBarView);

    }
	public PizzaView getPizzaView() {
		return m_pizzaView;
	}
	public void setPizzaView(PizzaView pizzaView) {
		m_pizzaView = pizzaView;
	}
}

