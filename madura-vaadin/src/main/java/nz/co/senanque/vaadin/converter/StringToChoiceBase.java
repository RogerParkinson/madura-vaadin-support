package nz.co.senanque.vaadin.converter;

import java.util.Locale;

import nz.co.senanque.vaadin.MaduraPropertyWrapper;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import com.vaadin.data.util.converter.Converter;

/**
 * Allows Vaadin to convert between a ChoiceBase and a String.
 * @author Roger Parkinson
 *
 */
public class StringToChoiceBase implements Converter<Object, Object> {
	
	private final MaduraPropertyWrapper m_property;

	public StringToChoiceBase(MaduraPropertyWrapper property) {
		m_property = property;
	}

	@Override
	public Object convertToModel(Object value,
			Class<? extends Object> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null) {
			return null;
		}
		if (targetType.equals(String.class)) {
			return value.toString();
		}
			
		if (value instanceof ChoiceBase) {
			return value.toString();
		}
		if (value instanceof String ) {
			return getChoiceBase((String)value);
		}
		return null;
	}

	@Override
	public Object convertToPresentation(Object value,
			Class<? extends Object> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null) {
			return null;
		}
		if (targetType.equals(String.class)) {
			return value.toString();
		}
		if (value instanceof ChoiceBase) {
			return value.toString();
		}
		if (value instanceof String) {
			return getChoiceBase((String)value);
		}
		return null;
	}
	
	private ChoiceBase getChoiceBase(String key) {
		for (ChoiceBase v: m_property.getAvailableValues())
        {
        	if (v.toString().equals(key)) {
        		return v;
        	}
        }
		return null;
	}

	@Override
	public Class<Object> getModelType() {
		return Object.class;
	}

	@Override
	public Class<Object> getPresentationType() {
		return Object.class;
	}


}
