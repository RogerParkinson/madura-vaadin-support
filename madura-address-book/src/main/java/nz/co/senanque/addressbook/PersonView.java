/**
 * 
 */
package nz.co.senanque.addressbook;

import javax.annotation.PostConstruct;

import nz.co.senanque.addressbook.instances.Person;
import nz.co.senanque.vaadin.MaduraSessionManager;
import nz.co.senanque.vaadin.tableeditor.TableEditorLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Roger Parkinson
 *
 */
@UIScope
@Component
public class PersonView extends VerticalLayout {

	@Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired @Qualifier("personTableLayout") private TableEditorLayout<Person> m_personTableEditorLayout;

    @PostConstruct
    void init() {
    	
    	final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        addComponent(layout);

        layout.addComponent(m_personTableEditorLayout);
    }
}
