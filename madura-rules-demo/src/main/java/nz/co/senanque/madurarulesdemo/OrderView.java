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
import nz.co.senanque.vaadin.MaduraForm;
import nz.co.senanque.vaadin.MaduraPropertyWrapper;
import nz.co.senanque.vaadin.SimpleButtonPainter;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.vaadin.format.FormattingTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Roger Parkinson
 *
 */
@UIScope
@SpringView(name = OrderView.VIEW_NAME)
public class OrderView extends VerticalLayout implements View {
	
	private static Logger m_logger = LoggerFactory.getLogger(OrderView.class);
    public static final String VIEW_NAME = "order";
    @Autowired private MaduraSessionManager m_maduraSessionManager;
    @Autowired private PizzaWindow m_pizzaWindow;
    @Autowired FieldFactory m_fieldFactory;
	@Autowired private MyEventRouter m_eventRouter;

    private Order m_order = null;
    private MaduraForm orderForm;
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

    	final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        verticalLayout.setSpacing(true);
        addComponent(verticalLayout);
        setImmediate(true); // no effect
////
        orderForm = new MaduraForm(new HorizontalLayout(),m_maduraSessionManager);
//        orderForm.setWidth("30%");
        orderForm.setFieldList(new String[]{"orderStatus","date","amount"});
//        orderForm.setReadOnly(true);
        verticalLayout.addComponent(orderForm);
        verticalLayout.setImmediate(true);// no effect
///
		HorizontalLayout orderStatus = new HorizontalLayout();
		orderStatus.setImmediate(true);

		orderDateLabel = new Label();
		orderStatus.addComponent(orderDateLabel);
		m_maduraSessionManager.register(orderDateLabel);
		
		orderStatusLabel = new Label();
		orderStatus.addComponent(orderStatusLabel);
		m_maduraSessionManager.register(orderStatusLabel);
		
		orderAmountLabel = new Label();
		orderStatus.addComponent(orderAmountLabel);
		m_maduraSessionManager.register(orderAmountLabel);
		orderAmountLabel.setImmediate(true);// no effect
		
		orderAmountText = new TextField();
		orderStatus.addComponent(orderAmountText);
		m_maduraSessionManager.register(orderAmountText);
		orderAmountText.setImmediate(true);// no effect
		orderAmountText.setBuffered(false);// no effect
		
		verticalLayout.addComponent(orderStatus);
				
		HorizontalLayout actions = new HorizontalLayout();
		m_addItem = m_fieldFactory.createButton("button.addItem", new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Pizza pizza = new Pizza();
				pizza.setId(System.currentTimeMillis());
				m_pizzaWindow.load(pizza);
				
			}}, new SimpleButtonPainter(m_maduraSessionManager));
		actions.addComponent(m_addItem);
		verticalLayout.addComponent(actions);
//		Button add = orderForm.createButton("button.addItem", new SimpleButtonPainter(m_maduraSessionManager), new ClickListener(){
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				Pizza pizza = new Pizza();
//				pizza.setId(System.currentTimeMillis());
//				m_pizzaWindow.load(pizza);
//				
//			}});
		getEventRouter().addListener(AddItemEvent.class, this, "addItem");
//		actions.addComponent(add);
//		orderForm.setFooter(actions);
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
    	orderAmountLabel.markAsDirty(); // no effect
    	orderAmountText.markAsDirty(); // no effect
    }
    /* 
     * This is where we establish the actual customer object. 
     * We just get it from the UI object and assume to knows how to supply it(non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
    	MyUI ui = MyUI.getCurrent();
    	ui.reviewNavigationButtons(VIEW_NAME);
    	if (m_order == null) {
    		m_order = ui.getOrder();
        	orderForm.setItemDataSource(new BeanItem<Order>(m_order));
    		List<MaduraPropertyWrapper> properties = m_maduraSessionManager.getFieldList(m_order);
    		m_maduraSessionManager.bind(m_addItem, properties);
    		m_maduraSessionManager.bind(orderDateLabel, new LabelProperty(m_maduraSessionManager.getMaduraPropertyWrapper(m_order,"date")));
    		m_maduraSessionManager.bind(orderStatusLabel, new LabelProperty(m_maduraSessionManager.getMaduraPropertyWrapper(m_order,"orderStatus")));
    		m_maduraSessionManager.bind(orderAmountLabel, new LabelProperty(m_maduraSessionManager.getMaduraPropertyWrapper(m_order,"amount")));
    		m_maduraSessionManager.bind(orderAmountText, "amount", properties);
    	}
    }
	public PizzaWindow getPizzaWindow() {
		return m_pizzaWindow;
	}
	public void setPizzaWindow(PizzaWindow pizzaWindow) {
		m_pizzaWindow = pizzaWindow;
	}
	public FieldFactory getFieldFactory() {
		return m_fieldFactory;
	}
	public void setFieldFactory(FieldFactory fieldFactory) {
		m_fieldFactory = fieldFactory;
	}
	public MyEventRouter getEventRouter() {
		return m_eventRouter;
	}
	public void setEventRouter(MyEventRouter eventRouter) {
		m_eventRouter = eventRouter;
	}
}
