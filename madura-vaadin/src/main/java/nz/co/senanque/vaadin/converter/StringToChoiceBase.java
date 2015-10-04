package nz.co.senanque.vaadin.converter;

import java.util.Locale;

import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import com.vaadin.data.util.converter.Converter;

/**
 * @author Roger Parkinson
 *
 */
public class StringToChoiceBase implements Converter<String, ChoiceBase> {

	@Override
	public ChoiceBase convertToModel(String value,
			Class<? extends ChoiceBase> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String convertToPresentation(ChoiceBase value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value.toString();
	}

	@Override
	public Class<ChoiceBase> getModelType() {
		return ChoiceBase.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
