/**
 * 
 */
package nz.co.senanque.vaadin7demo;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Roger Parkinson
 *
 */
@SpringView(name = ViewScopedView.VIEW_NAME)
public class ViewScopedView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "view";
    @Value("${nz.co.senanque.vaadin7demo.ViewScopedView.identifier:not-set}")
    private transient String m_identifier;

    @PostConstruct
    void init() {
        setMargin(true);
        setSpacing(true);
        addComponent(new Label("This is a view scoped view"));
        addComponent(new Label(m_identifier));
    }

    public void enter(ViewChangeEvent event) {
        // the view is constructed in the init() method()
    	//permissionFactory.getCurrentPermissions();

    }
}