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

import java.math.BigDecimal;

/**
 * 
 * Figure out what numeric formatter applies for this numeric datatype
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class FormatterFactory
{
    public static Formatter getFormatter(Class<?> type)
    {
        if (type == Double.TYPE || type == Double.class)
        {
            return new FormatterDouble();
        }
        if (type == Long.TYPE || type == Long.class)
        {
            return new FormatterLong();
        }
        if (type == Integer.TYPE || type == Integer.class)
        {
            return new FormatterInteger();
        }
        if (type == BigDecimal.class)
        {
            return new FormatterBigDecimal();
        }
        return null;
    }
}
