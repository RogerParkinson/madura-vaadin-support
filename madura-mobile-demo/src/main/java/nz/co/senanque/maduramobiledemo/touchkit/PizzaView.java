package nz.co.senanque.maduramobiledemo.touchkit;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Pizza;
import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.MaduraSessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@UIScope
@Component("mobile-pizza")
public class PizzaView extends NavigationView {

	private MaduraFieldGroup m_maduraFieldGroup;
	@Autowired private MaduraSessionManager m_maduraSessionManager;
    @PropertyId("base")
    private NativeSelect base = new NativeSelect();
    @PropertyId("topping")
    private NativeSelect topping = new NativeSelect();
    @PropertyId("size")
    private NativeSelect size = new NativeSelect();
    @PropertyId("amount")
    private TextField amount = new TextField();
    @PropertyId("testing")
    private TextField testing = new TextField();
    @PropertyId("description")
    private Label descr = new Label();

    public PizzaView() {
    }
	@PostConstruct
	public void init() {

        final VerticalComponentGroup formLayout = new VerticalComponentGroup();
        
        final Pizza pizza = new Pizza();

		getMaduraSessionManager().getValidationSession().bind(pizza);
    	BeanItem<Pizza> beanItem = new BeanItem<Pizza>(pizza);
    	m_maduraFieldGroup = m_maduraSessionManager.createMaduraFieldGroup();
    	m_maduraFieldGroup.setItemDataSource(beanItem);
    	VaadinSession session = VaadinSession.getCurrent();
    	m_maduraFieldGroup.buildAndBindMemberFields(this); // This discovers the fields on this object and binds them

    	// Now we have to add the fields to the panel
		formLayout.addComponent(base);
		formLayout.addComponent(topping);
		formLayout.addComponent(size);
		formLayout.addComponent(amount);
		formLayout.addComponent(testing);
		formLayout.addComponent(descr);

		Button OK = m_maduraFieldGroup.createSubmitButton("button.OK", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("Thanks!");
			}});
        OK.setClickShortcut(KeyCode.ENTER );
        OK.addStyleName(ValoTheme.BUTTON_PRIMARY);

        setContent(new CssLayout(formLayout, OK));
    }

	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}

	public void setMaduraSessionManager(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
	}

}
