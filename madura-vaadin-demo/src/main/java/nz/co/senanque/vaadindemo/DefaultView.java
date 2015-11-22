/**
 * 
 */
package nz.co.senanque.vaadindemo;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import nz.co.senanque.addressbook.instances.Person;
import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.MaduraFieldGroupImpl;
import nz.co.senanque.vaadin.MaduraSessionManager;
import nz.co.senanque.validationengine.ValidationObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
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
public class DefaultView extends VerticalLayout implements MessageSourceAware {

	@Autowired private MaduraSessionManager m_maduraSessionManager;
    private final Layout m_panel = new VerticalLayout();
    protected MaduraFieldGroup m_maduraFieldGroup;
	private MessageSource m_messageSource;
	private String[] fields = new String[]{"name","email","address","gender","startDate","amount"};

    /*
     * Defines the form, buttons and their connections to Madura
     * At this point we don't have an actual data object, so all this gets done once
     * and will not need redoing if we add or change a data object.
     */
    @PostConstruct
    void init() {
    	
    	final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
		m_maduraFieldGroup = m_maduraSessionManager.createMaduraFieldGroup();

        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        addComponent(verticalLayout);

        m_panel.setWidth("30%");
        verticalLayout.addComponent(m_panel);

		HorizontalLayout actions = new HorizontalLayout();
		Button cancel = m_maduraFieldGroup.createButton("button.cancel", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show(messageSourceAccessor.getMessage("message.clicked.cancel"),
						messageSourceAccessor.getMessage("message.noop"),
						Notification.Type.HUMANIZED_MESSAGE);
				
			}});
		Button submit = m_maduraFieldGroup.createSubmitButton("button.submit", new ClickListener(){

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
		verticalLayout.addComponent(actions);
		Component instructions = getInstructions(messageSourceAccessor);
		verticalLayout.addComponent(instructions);
		instructions.setWidth("30%");
		verticalLayout.setComponentAlignment(instructions, Alignment.MIDDLE_LEFT);

    }
    private VerticalLayout getInstructions(MessageSourceAccessor messageSourceAccessor) {
		VerticalLayout panel = new VerticalLayout();
		TextArea textArea = new TextArea();
		textArea.setWidth("100%");
		textArea.setHeight("100%");
		textArea.setValue(messageSourceAccessor.getMessage("demo.instructions"));
		textArea.setReadOnly(true);
        panel.addComponent(textArea);
        panel.setComponentAlignment(textArea, Alignment.MIDDLE_CENTER);
        return panel;
    }
    /* 
     * This is where we establish the actual person object. 
     * We just get it from the UI object and assume to knows how to supply it(non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    public void load(Person person) {
		m_maduraFieldGroup.buildAndBind(m_panel,Arrays.asList(fields),new BeanItem<ValidationObject>(person));
    }
	@Override
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}
}
