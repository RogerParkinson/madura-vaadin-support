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

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * Describes the various hints that can be applied to new widgets as they are created
 * 
 * @author Roger Parkinson
 *
 */
public interface Hints {

	public enum SelectType
	{
		STANDARD, RADIO
	}
	public String getWidth();
	public SelectType getSelectType();
	public void setCommonProperties(AbstractField<?> ret, MaduraPropertyWrapper property, final MessageSource messageSource);
	public void setCommonProperties(final MenuItem ret, final MaduraPropertyWrapper property, final MessageSource messageSource);
	public boolean isEnabled(boolean active);
	public boolean isVisible(boolean active);
	public AbstractField<?> getDateField(MaduraPropertyWrapper property);
	public AbstractField<?> getBooleanField(MaduraPropertyWrapper property);
	public AbstractField<?> getSelectField(MaduraPropertyWrapper property);
	public AbstractField<?> getTextField(MaduraPropertyWrapper property);
	public Button getButtonField(String name, MessageSource messageSource);
//	public MessageSource getMessageSource();
	public void setCommonProperties(Button field,
			MaduraPropertyWrapper property, MessageSource m_messageSource);

}
