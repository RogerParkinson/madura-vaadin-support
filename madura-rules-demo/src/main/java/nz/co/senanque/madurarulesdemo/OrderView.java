/**
 * 
 */
package nz.co.senanque.madurarulesdemo;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import nz.co.senanque.pizzaorder.instances.Order;
import nz.co.senanque.pizzaorder.instances.Pizza;
import nz.co.senanque.vaadin.MaduraForm;
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
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
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
    private Order m_order = null;
    private MaduraForm orderForm;
    private FormattingTable m_itemsTable;
	public static final String[] NATURAL_COL_ORDER = new String[] {"description","-amount" };
	public static final String[] ENGLISH_COL_ORDER = new String[] {"Description","Amount" };

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

        orderForm = new MaduraForm(m_maduraSessionManager);
//        orderForm.setCaption(messageSourceAccessor.getMessage("login.title"));
        orderForm.setWidth("30%");
        orderForm.setFieldList(new String[]{"orderStatus","date","amount"});
        verticalLayout.addComponent(orderForm);

		HorizontalLayout actions = new HorizontalLayout();
		Button add = orderForm.createButton("button.addItem", new SimpleButtonPainter(m_maduraSessionManager), new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				Pizza pizza = new Pizza();
				pizza.setId(System.currentTimeMillis());
				m_pizzaWindow.load(pizza);
				
			}});
		MyUI.getCurrent().getEventRouter().addListener(AddItemEvent.class, this, "addItem");
		actions.addComponent(add);
		orderForm.setFooter(actions);
		m_itemsTable = new FormattingTable();
		m_itemsTable.setImmediate(false);
		m_itemsTable.setWidth("500px");
		m_itemsTable.setHeight("240px");
		Container indexed = new BeanItemContainer<Pizza>(Pizza.class, new ArrayList<Pizza>());
		m_itemsTable.setContainerDataSource(indexed,NATURAL_COL_ORDER,ENGLISH_COL_ORDER,m_maduraSessionManager.getMessageSource());
		verticalLayout.addComponent(m_itemsTable);
    }
    public void addItem(AddItemEvent o) {
    	if (!m_order.getPizzas().contains(o)) {
    		m_order.getPizzas().add(o.getPizza());
    	}
    	Container indexed = m_itemsTable.getContainerDataSource();
		BeanItem<Pizza> beanItem = new BeanItem<Pizza>(o.getPizza());
		if (!indexed.containsId(beanItem)) {
			Item item = m_itemsTable.addItem(o.getPizza());
			m_logger.debug("item {}",item);
		}
    	
    	m_itemsTable.refreshRowCache();
    	m_itemsTable.markAsDirtyRecursive();
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
    	}
    }
	public PizzaWindow getPizzaWindow() {
		return m_pizzaWindow;
	}
	public void setPizzaWindow(PizzaWindow pizzaWindow) {
		m_pizzaWindow = pizzaWindow;
	}
}
