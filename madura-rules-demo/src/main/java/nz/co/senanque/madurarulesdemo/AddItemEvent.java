package nz.co.senanque.madurarulesdemo;

import java.util.EventObject;

import nz.co.senanque.pizzaorder.instances.Pizza;

import com.vaadin.ui.Button.ClickListener;

/**
 * @author Roger Parkinson
 *
 */
public class AddItemEvent extends EventObject {
	
	private final Pizza m_pizza;

	public AddItemEvent(ClickListener clickListener, Pizza pizza) {
		super(clickListener);
		m_pizza = pizza;
	}

	public Pizza getPizza() {
		return m_pizza;
	}

}
