/**
 * 
 */
package nz.co.senanque.madurarulesdemo;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Order;
import nz.co.senanque.pizzaorder.instances.Pizza;
import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.MaduraSessionManager;
import nz.co.senanque.vaadin.format.FormattingTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

/**
 * This show the use of the MaduraFieldGroup when binding to labels rather than to fields.
 * There is also a table variant to display the order entries. This is a 
 * {@link nz.co.senanque.vaadin.format.FormattingTable.FormattingTable()} which
 * handles some I18n issues and right justifies numerics etc.
 * 
 * Two buttons raise popup windows to get new order items.
 * 
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
	@Autowired private MyEventRouter m_eventRouter;
	private MaduraFieldGroup fieldGroup;

    private Order m_order = null;
    private Layout orderForm;
    private FormattingTable m_itemsTable;
	public static final String[] NATURAL_COL_ORDER = new String[] {"description","-amount" };
	public static final String[] ENGLISH_COL_ORDER = new String[] {"Description","Amount" };
	private Label orderStatusLabel;
	private Label orderAmountLabel;

    /*
     * Defines the form, buttons and their connections to Madura
     * At this point we don't have an actual data object, so all this gets done once
     * and will not need redoing if we add or change a data object.
     */
    @PostConstruct
    void init() {

		fieldGroup = m_maduraSessionManager.createMaduraFieldGroup();
		fieldGroup.setReadOnly(true);

		final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        addComponent(verticalLayout);
        
        orderForm = new VerticalLayout();
        
        final HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setMargin(true);

		orderStatusLabel = new Label();
		horizontalLayout.addComponent(orderStatusLabel);
		fieldGroup.bind(orderStatusLabel,"orderStatus");
		
		orderAmountLabel = new Label();
		horizontalLayout.addComponent(orderAmountLabel);
		fieldGroup.bind(orderAmountLabel,"amount");
		
		verticalLayout.addComponent(orderForm);
		orderForm.addComponent(horizontalLayout);
	
		HorizontalLayout actions = new HorizontalLayout();
		Button addItem = fieldGroup.createButton("button.addItem", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Pizza pizza = new Pizza();
				pizza.setId(System.currentTimeMillis());
				m_pizzaWindow.load(pizza);
				
			}});
		actions.addComponent(addItem);
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
    public void load(Order order) {
		m_order = order;
		fieldGroup.setItemDataSource(new BeanItem<Order>(m_order));
//    	m_maduraSessionManager.bind(orderStatusLabel, new LabelProperty<Object>(m_maduraSessionManager.getMaduraPropertyWrapper(m_order,"orderStatus")));
//    	m_maduraSessionManager.bind(orderAmountLabel, new LabelProperty<Object>(m_maduraSessionManager.getMaduraPropertyWrapper(m_order,"amount")));
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
