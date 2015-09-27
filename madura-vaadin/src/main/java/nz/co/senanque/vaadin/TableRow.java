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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

/**
 * Provides various ways of selecting a list of fields from a model
 * to be used in a display. You can specify hints to be used in the creation
 * of the controls (the creation happens later) and different selection property
 * mechanisms. 
 * 
 * The constructors all assume the selected properties are members
 * of the same model, but the add-hoc property methods make no assumptions about
 * the property path, so they can belong to any model.
 * 
 * Similarly the constructors also assume the hints apply to all of the selected
 * properties, while the ad-hoc property methods allow you to pass different hints
 * for each property.
 * 
 * Hints always default to the application hints.
 * 
 * @author Roger Parkinson
 *
 */
public final class TableRow extends PropertysetItem {

	private static final long serialVersionUID = -5054943072632832496L;
	private String m_id;
	private final ValidationObject m_validationObject;
	
    private final MaduraSessionManager m_maduraSessionManager;
	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}
	public String getId() {
		return m_id;
	}
	public void setId(String id) {
		m_id = id;
	}
	public TableRow(ValidationObject validationObject,MaduraSessionManager maduraSessionManager)
	{
		this(validationObject,null,maduraSessionManager);
		m_id = String.valueOf(System.identityHashCode(validationObject));
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TableRow(ValidationObject validationObject, String[] fields, MaduraSessionManager maduraSessionManager)
	{
		m_maduraSessionManager = maduraSessionManager;
	    m_validationObject = validationObject;
		List<String> fieldsList=null;
		if (fields != null)
		{
			fieldsList = Arrays.asList(fields);
		}
        ObjectMetadata objectMetadata = validationObject.getMetadata();
        Collection<PropertyMetadata> propertyMetadata = objectMetadata.getAllPropertyMetadata();
		for (PropertyMetadata property: propertyMetadata)
		{
            FieldMetadata fieldMetadata = objectMetadata.getFieldMetadata(property.getName().toUpperCase());
            Method getter = property.getGetMethod();
            Method setter = property.getSetMethod();
            if (getter == null || setter == null) {
                try
                {
                    Object value = getter.invoke(validationObject, new Object[]{});
                    if (value != null)
                    {
                        addItemProperty(property,new ObjectProperty(value,value.getClass(),true));
                    }
                    else
                    {
                        addItemProperty(property,new ObjectProperty("",String.class,true));
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                continue;
            } 
		    // This give null pointer because object is not bound
		    // needs to make up a different impl of com.vaadin.data.Property
			if (fields != null)
			{
				if (fieldsList.contains(property.getName()))
				{
					addItemProperty(property, new MaduraPropertyWrapper(fieldMetadata,validationObject,setter, getter, getMaduraSessionManager().getHints(), getMaduraSessionManager().getMessageSource()));
				}
			}
			else
			{
				addItemProperty(property, new MaduraPropertyWrapper(fieldMetadata,validationObject,setter, getter, getMaduraSessionManager().getHints(), getMaduraSessionManager().getMessageSource()));
			}
		}
	}
    public void addProperty(ValidationObject validationObject, PropertyDescriptor propertyDescriptor)
    {
        addProperty(validationObject, propertyDescriptor,getMaduraSessionManager().getHints());
    }
	public void addProperty(ValidationObject validationObject, PropertyDescriptor propertyDescriptor, Hints hints)
	{
        Method getter = propertyDescriptor.getReadMethod();
        Method setter = propertyDescriptor.getWriteMethod();
        String property = propertyDescriptor.getName();
        FieldMetadata fieldMetadata = validationObject.getMetadata().getFieldMetadata(property.toLowerCase());
		addItemProperty(property, new MaduraPropertyWrapper(fieldMetadata,validationObject,setter, getter, hints, getMaduraSessionManager().getMessageSource()));
	}
	public boolean canISubmitYet() {
		for (Object propertyId: getItemPropertyIds())
		{
		    com.vaadin.data.Property property = getItemProperty(propertyId);
			if (property instanceof MaduraPropertyWrapper)
			{
				MaduraPropertyWrapper maduraProperty = (MaduraPropertyWrapper)property;
				if (maduraProperty.isRequired() && maduraProperty.getValue() == null)
				{
					return false;
				}
			}
		}
		return true;
	}
    public ValidationObject getValidationObject()
    {
        return m_validationObject;
    }
}
