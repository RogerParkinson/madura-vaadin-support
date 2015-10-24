package nz.co.senanque.vaadin.application;

/**
 * @author Roger Parkinson
 *
 */
public class PropertyNotFoundException extends RuntimeException {

	public PropertyNotFoundException(String propertyName) {
		super(propertyName);
	}

}
