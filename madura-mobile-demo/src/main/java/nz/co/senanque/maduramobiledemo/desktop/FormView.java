package nz.co.senanque.maduramobiledemo.desktop;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Customer;
import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.MaduraSessionManager;
import nz.co.senanque.validationengine.ValidationObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@UIScope
@Component("desktop-form")
public class FormView extends FormLayout {

	@Autowired private MaduraSessionManager m_maduraSessionManager;
	String[] m_fields = new String[]{"name","email","gender"};

    public FormView() {
    }
	@PostConstruct
	public void init() {
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_maduraSessionManager.getMessageSource());
        setCaption(messageSourceAccessor.getMessage("Form"));
    	MaduraFieldGroup maduraFieldGroup = m_maduraSessionManager.createMaduraFieldGroup();
        final VerticalLayout content = new VerticalLayout();

        final Button submitButton = maduraFieldGroup.createSubmitButton("button.submit", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Notification.show("Thanks!");
            }
        });
        submitButton.setClickShortcut(KeyCode.ENTER );
        submitButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

        final Customer customer = new Customer();
		maduraFieldGroup.buildAndBind(content,Arrays.asList(m_fields),new BeanItem<ValidationObject>(customer));
        addComponent(content);
        addComponent(submitButton);
    }
	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}

	public void setMaduraSessionManager(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
	}

}
