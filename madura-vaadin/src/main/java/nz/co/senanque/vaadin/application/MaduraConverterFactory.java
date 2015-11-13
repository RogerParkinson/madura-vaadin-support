package nz.co.senanque.vaadin.application;

import org.springframework.stereotype.Component;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

/**
 * Provides an injectable way to get the correct {link com.vaadin.data.util.converter.Converter}
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

}
