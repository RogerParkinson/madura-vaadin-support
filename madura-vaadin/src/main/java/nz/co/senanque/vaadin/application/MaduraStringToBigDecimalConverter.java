package nz.co.senanque.vaadin.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.converter.StringToBigDecimalConverter;

/**
 * @author Roger Parkinson
 *
 */
public class MaduraStringToBigDecimalConverter extends StringToBigDecimalConverter implements MaduraNumericConverter {

	Logger logger = LoggerFactory.getLogger(MaduraStringToBigDecimalConverter.class);
	
	private int fractionDigits = 0;

	protected Number convertToNumber(String value,
            Class<? extends Number> targetType, Locale locale) {
		Number n = super.convertToNumber(value, targetType, locale);
		if (n == null) {
			return null;
		}
		BigDecimal ret = new BigDecimal(n.doubleValue());
		ret = ret.divide(new BigDecimal("1"),fractionDigits, RoundingMode.HALF_UP);
		return ret;
	}
    public String convertToPresentation(BigDecimal value,
            Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        if (value == null) {
            value = new BigDecimal(0);
        }

        return getFormat(locale).format(value);
    }
    protected NumberFormat getFormat(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }

        NumberFormat ret = NumberFormat.getNumberInstance(locale);
        ret.setMaximumFractionDigits(fractionDigits);
        ret.setMinimumFractionDigits(fractionDigits);
        return ret;
    }

	public int getFractionDigits() {
		return fractionDigits;
	}

	public void setFractionDigits(int fractionDigits) {
		this.fractionDigits = fractionDigits;
		logger.debug("fraction digits: {}",fractionDigits);
	}
}
