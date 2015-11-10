/**
 * 
 */
package nz.co.senanque.madurarulesdemo;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Order;
import nz.co.senanque.pizzaorder.instances.Pizza;
import nz.co.senanque.vaadin.FieldFactory;
import nz.co.senanque.vaadin.LabelProperty;
import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.MaduraForm;
import nz.co.senanque.vaadin.MaduraPropertyWrapper;
import nz.co.senanque.vaadin.PropertiesSource;
import nz.co.senanque.vaadin.SimpleButtonPainter;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.vaadin.format.FormattingTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Roger Parkinson
 *
 */
@UIScope
@Component
public class OrderView extends VerticalLayout {
	
	private static Logger m_logger = LoggerFactory.getLogger(OrderView.class);
    public static final String VIEW_NAME = "order";
    @Autowired private MaduraSessionManager m_maduraSessionManager;
    @Autowired private PizzaWindow m_pizzaWindow;
//    @Autowired FieldFactory m_fieldFactory;
	@Autowired private MyEventRouter m_eventRouter;
	MaduraFieldGroup fieldGroup;

    private Order m_order = null;
    private FormLayout orderForm;
    private Button m_addItem;
    private FormattingTable m_itemsTable;
	public static final String[] NATURAL_COL_ORDER = new String[] {"description","-amount" };
	public static final String[] ENGLISH_COL_ORDER = new String[] {"Description","Amount" };
	private Label orderDateLabel;
	private Label orderStatusLabel;
	private Label orderAmountLabel;
	private TextField orderAmountText;

    /*
     * Defines the form, buttons and their connections to Madura
     * At this point we don't have an actual data object, so all this gets done once
     * and will not need redoing if we add or change a data object.
     */
    @PostConstruct
    void init() {

//    	final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_maduraSessionManager.getMessageSource());

    	final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        addComponent(verticalLayout);
        
        orderForm = new FormLayout();
        
        final HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setMargin(true);

		orderStatusLabel = new Label();
		horizontalLayout.addComponent(orderStatusLabel);
		m_maduraSessionManager.register(orderStatusLabel);
		orderStatusLabel.setImmediate(true);
		
		orderAmountLabel = new Label();
		horizontalLayout.addComponent(orderAmountLabel);
		m_maduraSessionManager.register(orderAmountLabel);
		
		fieldGroup = new MaduraFieldGroup(m_maduraSessionManager);
		fieldGroup.setReadOnly(true);
		
		verticalLayout.addComponent(orderForm);
		orderForm.addComponent(horizontalLayout);
	
		HorizontalLayout actions = new HorizontalLayout();
		m_addItem = fieldGroup.createButton("button.addItem", new SimpleButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Pizza pizza = new Pizza();
				pizza.setId(System.currentTimeMillis());
				m_pizzaWindow.load(pizza);
				
			}});
		actions.addComponent(m_addItem);
		verticalLayout.addComponent(actions);
		getEventRouter().addListener(AddItemEvent.class, this, "addItem");
		m_itemsTable = new FormattingTable();
		m_itemsTable.setImmediate(false);
		m_itemsTable.setWidth("500px");
		m_itemsTable.setHeight("240px");
		Container indexed = new BeanItemContainer<Pizza>(Pizza.class, new ArrayList<Pizza>());
		m_itemsTable.setContainerDataSource(indexed,NATURAL_COL_ORDER,ENGLISH_COL_ORDER,m_maduraSessionManager.getMessageSource());
		verticalLayout.addComponent(m_itemsTable);
		m_itemsTable.addItemClickListener(new ItemClickListener(){

			@Override
			public void itemClick(ItemClickEvent event) {
				m_pizzaWindow.load((Pizza)event.getItemId());
			}});
    }
    public void addItem(AddItemEvent o) {
    	if (!m_order.getPizzas().contains(o.getPizza())) {
    		m_order.getPizzas().add(o.getPizza());
    	}
		Item item = m_itemsTable.addItem(o.getPizza());
		m_logger.debug("item {}",item); // null if already exists.
		
    	m_itemsTable.refreshRowCache();
    	markAsDirtyRecursive();
    }
    /* 
     * This is where we establish the actual order object. 
     * We just get it from the UI object and assume to knows how to supply it(non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    public void enter(ViewChangeEvent event) {
    	MyUI ui = MyUI.getCurrent();
    	if (m_order == null) {
    		m_order = ui.getOrder();
    		fieldGroup.setItemDataSource(new BeanItem<Order>(m_order));
//    		m_maduraSessionManager.bind(m_addItem);
    		m_maduraSessionManager.bind(orderStatusLabel, new LabelProperty<Object>(m_maduraSessionManager.getMaduraPropertyWrapper(m_order,"orderStatus")));
    		m_maduraSessionManager.bind(orderAmountLabel, new LabelProperty<Object>(m_maduraSessionManager.getMaduraPropertyWrapper(m_order,"amount")));
    	}
    }
	public PizzaWindow getPizzaWindow() {
		return m_pizzaWindow;
	}
	public void setPizzaWindow(PizzaWindow pizzaWindow) {
		m_pizzaWindow = pizzaWindow;
	}
	public MyEventRouter getEventRouter() {
		return m_eventRouter;
	}
	public void setEventRouter(MyEventRouter eventRouter) {
		m_eventRouter = eventRouter;
	}
}
