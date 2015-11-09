package com.vaadin.data.util;

import com.vaadin.ui.AbstractField;

/**
 * @author Roger Parkinson
 *
 */
public class ProtectedMethods {
	
	public static void fireValueChange(AbstractProperty<?> abstractProperty) {
		abstractProperty.fireValueChange();
	}

}
