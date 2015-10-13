package nz.co.senanque.madurarulesdemo;

import nz.co.senanque.vaadin.MaduraForm;
import nz.co.senanque.vaadin.application.MaduraSessionManager;

import com.vaadin.ui.VerticalLayout;

public class CustomerForm extends MaduraForm {

	public CustomerForm(MaduraSessionManager maduraSessionManager) {
		super(new VerticalLayout(),maduraSessionManager);
		setFieldList(new String[]{"name","email","address","gender","startDate","amount"});
	}

}
