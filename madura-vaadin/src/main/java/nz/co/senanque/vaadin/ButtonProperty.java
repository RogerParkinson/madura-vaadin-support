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
package nz.co.senanque.vaadin;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.Property;


/**
 * 
 * This acts as a data source for a button.
 * It includes the raw caption (which is translated to the current locale)
 * It also holds the button painter.
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class ButtonProperty implements Property<Boolean>, MessageSourceAware {

    private static final long serialVersionUID = 1L;
    private final ButtonPainter m_painter;
	private String m_rawCaption;
	private transient MessageSource m_messageSource;

	public ButtonPainter getPainter() {
		return m_painter;
	}

	public ButtonProperty(ButtonPainter painter, String rawCaption, MessageSource messageSource) {
		m_painter = painter;
		m_rawCaption = rawCaption;
		m_messageSource = messageSource;
	}

	public Class<Boolean> getType() {
		// TODO Auto-generated method stub
		return Boolean.class;
	}

	public Boolean getValue() {
		// TODO Auto-generated method stub
		return Boolean.TRUE;
	}

	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setReadOnly(boolean newStatus) {
		// TODO Auto-generated method stub
		
	}

	public String getCaption() {
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
        String ret = messageSourceAccessor.getMessage(
        		m_rawCaption, new Object[]{}, 
        		m_rawCaption);
        return ret;
	}
	public void setMessageSource(MessageSource messageSource) {
        m_messageSource = messageSource;
	}

	@Override
	public void setValue(Boolean newValue)
			throws com.vaadin.data.Property.ReadOnlyException {
		// TODO Auto-generated method stub
		
	}
	public String toString() {
		return m_rawCaption;
	}

}
