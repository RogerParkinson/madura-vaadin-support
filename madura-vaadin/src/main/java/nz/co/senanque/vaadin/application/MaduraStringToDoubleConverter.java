package nz.co.senanque.vaadin.application;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToDoubleConverter;

/**
 * @author Roger Parkinson
 *
 */
public class MaduraStringToDoubleConverter extends StringToDoubleConverter  implements MaduraNumericConverter {
	
	private int fractionDigits = 0;

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
	}
}
