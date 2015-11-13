package nz.co.senanque.vaadin;

import java.util.Collection;
import java.util.List;

/**
 * Implemented by the painters.
 * 
 * @author Roger Parkinson
 *
 */
public interface PropertiesSource {
	
	public abstract List<MaduraPropertyWrapper> getProperties();
	public abstract boolean isReadOnly();
	public abstract MaduraPropertyWrapper findProperty(String propertyName);
	public abstract Collection<?> getItemPropertyIds();
}
