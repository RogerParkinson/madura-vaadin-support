package org.madura.mobile.demo;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class MaduraMobileDemoUIProvider extends UIProvider {

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {

        boolean mobileUserAgent = event.getRequest().getHeader("user-agent")
                .toLowerCase().contains("mobile");
        boolean mobileParameter = event.getRequest().getParameter("mobile") != null;

        if (overrideMobileUA() || mobileUserAgent || mobileParameter) {
            return MaduraMobileDemoTouchKitUI.class;
        } else {
            return MaduraMobileDemoFallbackUI.class;
        }
    }

    private boolean overrideMobileUA() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null && session.getAttribute("mobile") != null;
    }
}
