package nz.co.senanque.vaadin.converter;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.co.senanque.vaadin.MaduraPropertyWrapper;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import com.vaadin.data.util.converter.Converter;

/**
 * @author Roger Parkinson
 *
 */
public class StringToChoiceBase implements Converter<Object, Object> {
	
//    private static final Logger logger = LoggerFactory.getLogger(StringToChoiceBase.class);

	private final MaduraPropertyWrapper m_property;

	public StringToChoiceBase(MaduraPropertyWrapper property) {
		m_property = property;
	}

	@Override
	public Object convertToModel(Object value,
			Class<? extends Object> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null) {
//			logger.debug("value={} type={} targetType={}",value,null,targetType);
			return null;
		}
//		logger.debug("value={} type={} targetType={}",value,value.getClass(),targetType);
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
//			logger.debug("value={} type={} targetType={}",value,null,targetType);
			return null;
		}
//		logger.debug("value={} type={} targetType={}",value,value.getClass(),targetType);
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
