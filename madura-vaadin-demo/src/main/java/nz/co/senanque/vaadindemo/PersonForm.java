package nz.co.senanque.vaadindemo;

import nz.co.senanque.vaadin.MaduraForm;
import nz.co.senanque.vaadin.application.MaduraSessionManager;

import com.vaadin.ui.VerticalLayout;

public class PersonForm extends MaduraForm {

	public PersonForm(MaduraSessionManager maduraSessionManager) {
		super(new VerticalLayout(),maduraSessionManager);
		setFieldList(new String[]{"id","name","email","address"});
	}

}
