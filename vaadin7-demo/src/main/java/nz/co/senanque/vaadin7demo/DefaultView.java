/**
 * 
 */
package nz.co.senanque.vaadin7demo;

import javax.annotation.PostConstruct;

import nz.co.senanque.permissionmanager.PermissionManager;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Roger Parkinson
 *
 */
@SpringView(name = DefaultView.VIEW_NAME)
public class DefaultView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";
    @Autowired PermissionManager permissionManager;

    @PostConstruct
    void init() {
        addComponent(new Label("This is the default view"));
    }

    public void enter(ViewChangeEvent event) {
        // the view is constructed in the init() method()
    	permissionManager.getCurrentUser();
    }
}
