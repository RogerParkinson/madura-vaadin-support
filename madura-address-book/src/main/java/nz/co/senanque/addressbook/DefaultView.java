/**
 * 
 */
package nz.co.senanque.addressbook;

import javax.annotation.PostConstruct;

import nz.co.senanque.vaadin.MaduraSessionManager;
import nz.co.senanque.vaadin.tableeditor.TableEditorLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Roger Parkinson
 *
 */
@UIScope
@Component
public class DefaultView extends VerticalLayout {

	@Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired private TableEditorLayout<?> m_tableEditorLayout;

    @PostConstruct
    void init() {
    	
    	final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        addComponent(layout);

        layout.addComponent(m_tableEditorLayout);
    }
}
