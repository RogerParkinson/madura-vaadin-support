/**
 * 
 */
package nz.co.senanque.vaadindemo;

import java.util.Map;

import javax.annotation.PostConstruct;

import nz.co.senanque.addressbook.instances.Person;
import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.MaduraSessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Shows the use of {@link nz.co.senanque.vaadin.MaduraFieldGroupImpl} to create buttons and fields.
 * Notice that the buttons can be created before a data source it bound to the field group, but fields
 * are done afterwards.
 * 
 * @author Roger Parkinson
 *
 */
@UIScope
@SpringComponent
public class DefaultView extends VerticalLayout {

	@Autowired private MaduraSessionManager m_maduraSessionManager;
    private final VerticalLayout panel = new VerticalLayout();
	private String[] m_fields = new String[]{"name","email","address","gender","startDate","amountDouble","amountFloat","booleanValue","longValue","integerValue","decimalValue","mydecimalValue"};

    /*
     * Defines the form, buttons and their connections to Madura
     * At this point we don't have an actual data object, so all this gets done once
     * and will not need redoing if we add or change a data object.
     */
    @PostConstruct
    void init() {
    	
        addComponent(panel);
    }
    /* 
     * This is where we establish the actual person object. 
     * We just get it from the UI object and assume to knows how to supply it(non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    public void load(Person person) {
        final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_maduraSessionManager.getMessageSource());

		// Clean the panel of any previous fields
		panel.removeAllComponents();
    	BeanItem<Person> beanItem = new BeanItem<Person>(person);

    	// make a new layout and add to the panel
    	Layout form = new FormLayout();
    	panel.addComponent(form);
    	
    	MaduraFieldGroup fieldGroup = m_maduraSessionManager.createMaduraFieldGroup();
    	Layout actions = createActions(messageSourceAccessor, fieldGroup);
    	Map<String,Field<?>> fields = fieldGroup.buildAndBind(
    			m_fields,
    			beanItem);

    	for (Field<?> f: fields.values()) {
    		form.addComponent(f);
    	}
        form.addComponent(actions);
    }
    private Layout createActions(final MessageSourceAccessor messageSourceAccessor,MaduraFieldGroup fieldGroup) {
		HorizontalLayout actions = new HorizontalLayout();
		Button cancel = fieldGroup.createButton("button.cancel", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.cancel"),
						messageSourceAccessor.getMessage("message.noop"),
						Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		Button submit = fieldGroup.createSubmitButton("button.submit", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.submit"),
						messageSourceAccessor.getMessage("message.noop"),
		                  Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		submit.setClickShortcut( KeyCode.ENTER ) ;
		submit.addStyleName( ValoTheme.BUTTON_PRIMARY ) ;
		actions.addComponent(cancel);
		actions.addComponent(submit);
        actions.setMargin(true);
        actions.setSpacing(true);
        return actions;
    }
}
