package nz.co.senanque.vaadin.application;

import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import nz.co.senanque.vaadin.converter.StringToChoiceBase;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

/**
 * @author Roger Parkinson
 *
 */
@Component("maduraConverterFactory")
public class MaduraConverterFactory extends DefaultConverterFactory {

	private final static Logger log = Logger
			.getLogger(MaduraConverterFactory.class.getName());

	protected <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> findConverter(
			Class<PRESENTATION> presentationType, Class<MODEL> modelType) {

		// check the Vaadin-supplied converters first
		Converter<PRESENTATION, MODEL> ret = super.findConverter(
				presentationType, modelType);
		if (ret == null) {
			return ret;
		}
		if (modelType == ChoiceBase.class) {
			return (Converter<PRESENTATION, MODEL>) new StringToChoiceBase();
		}

		return null;

	}

}
