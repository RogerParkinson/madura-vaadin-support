package nz.co.senanque.vaadin.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

/**
 * Provides an injectable way to get the correct {link com.vaadin.data.util.converter.Converter}.
 * Also adds specialised converters to handle decimals.
 * 
 * @author Roger Parkinson
 *
 */
@Component("maduraConverterFactory")
public class MaduraConverterFactory extends DefaultConverterFactory {

	protected <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> findConverter(
			Class<PRESENTATION> presentationType, Class<MODEL> modelType) {

		Converter<PRESENTATION, MODEL> ret = super.findConverter(
				presentationType, modelType);

		return ret;
	}

    protected Converter<String, ?> createStringConverter(Class<?> sourceType) {
        if (Double.class.isAssignableFrom(sourceType) || Double.TYPE == sourceType) {
            return new MaduraStringToDoubleConverter();
        } else if (Float.class.isAssignableFrom(sourceType) || Float.TYPE == sourceType) {
            return new MaduraStringToFloatConverter();
        } else if (BigDecimal.class.isAssignableFrom(sourceType)) {
            return new MaduraStringToBigDecimalConverter();
        } else {
            return super.createStringConverter(sourceType);
        }
    }
}
