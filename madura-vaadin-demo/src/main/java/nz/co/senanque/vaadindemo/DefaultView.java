/**
 * 
 */
package nz.co.senanque.vaadindemo;

import java.util.List;

import javax.annotation.PostConstruct;

import nz.co.senanque.addressbook.instances.Person;
import nz.co.senanque.vaadin.MaduraPropertyWrapper;
import nz.co.senanque.vaadin.SimpleButtonPainter;
import nz.co.senanque.vaadin.SubmitButtonPainter;
import nz.co.senanque.vaadin.application.MaduraSessionManager;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Roger Parkinson
 *
 */
@SpringView(name = DefaultView.VIEW_NAME)
public class DefaultView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "";
    @Autowired private MaduraSessionManager m_maduraSessionManager;
    private Person m_person = null;
    private Button submit;
    private Button cancel;
    private PersonForm personForm;

    /*
     * Defines the form, buttons and their connections to Madura
     * At this point we don't have an actual data object, so all this gets done once
     * and will not need redoing if we add or change a data object.
     */
    @PostConstruct
    void init() {

    	// create a form to display the object and add it to the UI
    	personForm = new PersonForm(m_maduraSessionManager);
    	addComponent(personForm);
		HorizontalLayout actions = new HorizontalLayout();
		cancel = personForm.createButton("Cancel", new SimpleButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Pressed Cancel",
		                  "...which doesn't actually do anything in this demo",
		                  Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		submit = personForm.createButton("Submit", new SubmitButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Pressed Submit",
		                  "...which doesn't actually do anything in this demo",
		                  Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		actions.addComponent(cancel);
		actions.addComponent(submit);
		addComponent(actions);
		
		VerticalLayout panel = new VerticalLayout();
		TextArea textArea = new TextArea();
		textArea.setWidth("50%");
		textArea.setValue(
				"The Submit button is inactive until the form is completed, and without errors\n"+
				"'complete' means all the required fields have valid values. Name and Email are required\n"+
				"but address is not, also Email needs an '@' in it to be valid\n"+
				"Try different values in the different fields and note how the Submit button changes");
		textArea.setReadOnly(true);
        panel.addComponent(textArea);
        panel.setComponentAlignment(textArea, Alignment.BOTTOM_RIGHT);
        addComponent(panel);
    }
    /* 
     * This is where we establish the actual person object. 
     * We just get it from the UI object and assume to knows how to supply it(non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
    	MyUI ui = (MyUI)UI.getCurrent();
    	if (m_person == null) {
    		m_person = ui.getPerson();
        	m_maduraSessionManager.getValidationSession().bind(m_person);
        	personForm.setItemDataSource(new BeanItem<Person>(m_person));
// The following happens automatically in a MaduraForm.setItemDataSource
//        	List<MaduraPropertyWrapper> properties = personForm.getItemDataSourceProperties();
//    		m_maduraSessionManager.bind(submit, properties);
//    		m_maduraSessionManager.bind(cancel, properties);
    	}
    }
}
