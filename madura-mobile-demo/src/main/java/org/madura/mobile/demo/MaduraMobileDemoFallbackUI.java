package org.madura.mobile.demo;

import nz.co.senanque.vaadin.MaduraSessionManager;

import org.madura.mobile.demo.ui.FormView2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is served for browsers that don't support TouchKit.
 */
@SuppressWarnings("serial")
@Theme("mytheme")
@SpringUI
public class MaduraMobileDemoFallbackUI extends UI {

	@Autowired private MaduraSessionManager m_maduraSessionManager;

	@Override
    protected void init(VaadinRequest vaadinRequest) {

//		// Initialise the permission manager using data from the login
//    	// This assumes madura-login handled the login. Other authentication mechanisms will need different code
//    	// but they should all populate the permission manager.
//    	String currentUser = (String)vaadinRequest.getWrappedSession().getAttribute(AuthenticationDelegate.USERNAME);
//    	@SuppressWarnings("unchecked")
//		Set<String> currentPermissions = (Set<String>)vaadinRequest.getWrappedSession().getAttribute(AuthenticationDelegate.PERMISSIONS);
//    	m_maduraSessionManager.getPermissionManager().setPermissionsList(currentPermissions);
//    	m_maduraSessionManager.getPermissionManager().setCurrentUser(currentUser);
//    	this.getSession().setConverterFactory(m_maduraSessionManager.getMaduraConverterFactory());
    	
    	MessageSourceAccessor messageSourceAccessor= new MessageSourceAccessor(m_maduraSessionManager.getMessageSource());
    	final String logout = messageSourceAccessor.getMessage("Logout");

        final VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.setSpacing(true);
        setContent(root);

        TabSheet tabsheet = new TabSheet();
        root.addComponent(tabsheet);

        // Create the first tab
        VerticalLayout tab1 = new VerticalLayout();
        tab1.addComponent(new FormView2());
        tabsheet.addTab(tab1, messageSourceAccessor.getMessage("Customer"));

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

}
