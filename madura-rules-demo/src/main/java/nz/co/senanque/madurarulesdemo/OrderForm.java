package nz.co.senanque.madurarulesdemo;

import nz.co.senanque.vaadin.MaduraForm;
import nz.co.senanque.vaadin.application.MaduraSessionManager;

import com.vaadin.ui.VerticalLayout;

public class OrderForm extends MaduraForm {

	public OrderForm(MaduraSessionManager maduraSessionManager) {
		super(new VerticalLayout(),maduraSessionManager);
		setFieldList(new String[]{"date","amount"});
	}

}
