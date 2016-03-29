package nz.co.senanque.maduramobiledemo.desktop;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Pizza;
import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.MaduraSessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@UIScope
@Component("desktop-pizza")
public class PizzaView extends FormLayout {

	private MaduraFieldGroup m_maduraFieldGroup;
	@Autowired private MaduraSessionManager m_maduraSessionManager;

    public PizzaView() {
    }
	@PostConstruct
	public void init() {

        final VerticalLayout formLayout = new VerticalLayout();
        
        final Pizza pizza = new Pizza();

		getMaduraSessionManager().getValidationSession().bind(pizza);
    	BeanItem<Pizza> beanItem = new BeanItem<Pizza>(pizza);
    	m_maduraFieldGroup = m_maduraSessionManager.createMaduraFieldGroup();
		Button OK = m_maduraFieldGroup.createSubmitButton("button.OK", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Thanks!");
			}});
        OK.setClickShortcut(KeyCode.ENTER );
        OK.addStyleName(ValoTheme.BUTTON_PRIMARY);
    	m_maduraFieldGroup.buildAndBind(formLayout,
    			new String[]{"base","topping","size","amount","testing","description"},
    			beanItem);


        addComponent(formLayout);
        addComponent(OK);
    }

	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}

	public void setMaduraSessionManager(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
	}

}
