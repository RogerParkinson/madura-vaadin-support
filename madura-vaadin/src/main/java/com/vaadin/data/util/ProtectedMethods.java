package com.vaadin.data.util;


/**
 * Allows us to get to the protected methods on {link com.vaadin.data.util.AbstractProperty}.
 * 
 * @author Roger Parkinson
 *
 */
public class ProtectedMethods {
	
	public static void fireValueChange(AbstractProperty<?> abstractProperty) {
		abstractProperty.fireValueChange();
	}

}
