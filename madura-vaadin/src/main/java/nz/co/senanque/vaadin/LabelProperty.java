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

import com.vaadin.data.Property;


/**
 * This is the data source we use for a label.
 * It allows the caption to be easily fetched from the business objects
 * 
 * @author Roger Parkinson
 *
 */
public class LabelProperty implements com.vaadin.data.Property {
	
	private static final long serialVersionUID = -1355267850336182862L;
	private final MaduraPropertyWrapper m_property;

	public MaduraPropertyWrapper getProperty() {
		return m_property;
	}

	public LabelProperty(MaduraPropertyWrapper property) {
		m_property = property;
	}

	public Object getValue() {
        String ret = String.valueOf(m_property.getValue());
        return ret;
	}

	public void setValue(Object newValue) throws Property.ReadOnlyException {
		// TODO Auto-generated method stub
		
	}

	public Class<?> getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isReadOnly() {
		return true;
	}

	public void setReadOnly(boolean newStatus) {
	}

	public String getCaption() {
        String ret = String.valueOf(m_property.getValue());
        return ret;
	}
	public String toString()
	{
		return getCaption();
	}

}
