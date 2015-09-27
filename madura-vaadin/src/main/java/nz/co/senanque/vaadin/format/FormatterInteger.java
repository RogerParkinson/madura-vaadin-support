/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.vaadin.format;

import java.text.NumberFormat;
import java.text.ParsePosition;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 
 * Used to ensure numbers are formatted okay
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class FormatterInteger implements Formatter
{
    private NumberFormat nf = NumberFormat.getInstance(LocaleContextHolder.getLocale());
    /* (non-Javadoc)
     * @see nz.co.senanque.vaadin.format.Formatter#format(java.lang.Object)
     */
    public String format(Object value)
    {
        if (value == null)
        {
            return "";
        }
        return nf.format(value);
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.vaadin.format.Formatter#parse(java.lang.String)
     */
    public Object parse(String formattedValue) throws Exception
    {
		ParsePosition parsePosition = new ParsePosition(0);
        Object o = nf.parseObject(formattedValue);
        if (parsePosition.getIndex() != formattedValue.length())
        {
        	throw new RuntimeException("Invalid number "+formattedValue);
        }
        return o; 
    }

}
