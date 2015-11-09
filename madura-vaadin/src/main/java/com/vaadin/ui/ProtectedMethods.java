package com.vaadin.ui;

import com.vaadin.ui.AbstractField;

/**
 * @author Roger Parkinson
 *
 */
public class ProtectedMethods {
	
	public static void fireValueChange(AbstractField<?> abstractProperty) {
		abstractProperty.fireValueChange(false);
	}

}
