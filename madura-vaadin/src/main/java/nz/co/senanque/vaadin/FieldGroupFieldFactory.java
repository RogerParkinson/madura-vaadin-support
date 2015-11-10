package nz.co.senanque.vaadin;

import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup.BindException;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Field;

/**
 * @author Roger Parkinson
 *
 */
@SpringComponent("fieldGroupFieldFactory") // need the expanded name because there is a class called Component somewhere
@UIScope
public class FieldGroupFieldFactory extends DefaultFieldGroupFieldFactory {
	
//	public void bind(Field<?> field, Object propertyId) throws BindException {
//		
//	}

}
