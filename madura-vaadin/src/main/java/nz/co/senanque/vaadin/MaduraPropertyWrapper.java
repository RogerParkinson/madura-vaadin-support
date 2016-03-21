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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import nz.co.senanque.vaadin.format.Formatter;
import nz.co.senanque.vaadin.format.FormatterFactory;
import nz.co.senanque.validationengine.ConvertUtils;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.annotations.Email;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;


/**
 * There is one of these for each field that we map.
 * It holds the references to the object's getters, setters and metadata
 * Because it implements com.vaadin.data.Property it can be used as a datasource.
 * 
 * @author Roger Parkinson
 *
 */
@SuppressWarnings("rawtypes")
public class MaduraPropertyWrapper implements com.vaadin.data.Property {
	
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(MaduraPropertyWrapper.class);
	private String m_errorText;
	private final Hints m_hints;
	private final Class<?> m_dataType;
	private Object m_lastFailedValue;
    private final String m_label;
    private final String m_description;
    private final ValidationObject m_owner;
    private final Method m_setter;
    private final Method m_getter;
    private final FieldMetadata m_fieldMetadata;
    private final String m_name;
    private final String m_writePermission;
    private String m_readPermission;
    private boolean m_numeric;
    private Formatter m_propertyFormatter;
    private final transient MessageSource m_messageSource;
	private boolean m_email;
    
    public MaduraPropertyWrapper(FieldMetadata fieldMetadata, ValidationObject owner, Method setter, Method getter, Hints hints, MessageSource messageSource)
    {
        m_name = fieldMetadata.getName();
        m_label = fieldMetadata.getLabelName();
        m_description = fieldMetadata.getDescription();
        m_writePermission = fieldMetadata.getPermission();
        m_readPermission = fieldMetadata.getReadPermission();
        m_hints = hints;
        m_setter = setter;
        m_getter = getter;
        m_owner = owner;
        m_dataType = setter.getParameterTypes()[0];
        m_fieldMetadata = fieldMetadata;
        figureFormattedProperty();
        m_messageSource = messageSource;
        m_email = getter.isAnnotationPresent(Email.class);
    }
    
    public MaduraPropertyWrapper(String name, ValidationObject owner, Method setter, Method getter, Hints hints, MessageSource messageSource)
    {
        m_name = name;
        m_label = name;
        m_writePermission = null;
        m_description = null;
        m_hints = hints;
        m_setter = setter;
        m_getter = getter;
        m_owner = owner;
        m_dataType = setter.getParameterTypes()[0];
        m_fieldMetadata = null;
        figureFormattedProperty();
        m_messageSource = messageSource;
        m_email = getter.isAnnotationPresent(Email.class);
    }
    private void figureFormattedProperty()
    {
        Class<?> type = getDataType();
        if (type == null) {
            return;
        }
        Formatter formatter = FormatterFactory.getFormatter(type);
        if (formatter != null)
        {
            setNumeric(true);
            setFormatter(formatter);
        }
    }
    
	public String toString()
	{
	    Object o = getValue();
		return String.valueOf(o==null?"":o);
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.Property#getType()
	 */
	public Class<?> getType() {
		if (m_dataType == String.class) {
			return String.class;
		}
		if (m_dataType == Float.TYPE) {
			return Float.class;
		}
		if (m_dataType == Double.TYPE) {
			return Double.class;
		}
		if (m_dataType == Boolean.TYPE) {
			return Boolean.class;
		}
		if (m_dataType == Integer.TYPE) {
			return Integer.class;
		}
		if (m_dataType == Long.TYPE) {
			return Long.class;
		}
		if (m_dataType == Date.class) {
			return Date.class;
		}
		if (m_dataType == BigDecimal.class) {
			return BigDecimal.class;
		}
		return String.class;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.Property#getValue()
	 */
	public Object getValue() {
		//logger.debug("getting current value for {}",m_propertyPath);
		if (m_lastFailedValue != null)
		{
			return m_lastFailedValue;
		}
		try
        {
            Object ret = m_getter.invoke(m_owner, new Object[]{});
            List<ChoiceBase> choiceBaseList = getAvailableValues();
            if (ret != null && ret.getClass().isEnum())
            {
                // Field is an enum, convert it to string
                ret = ret.toString();
            }
            if (ret != null && choiceBaseList != null && choiceBaseList.size() > 0)
            {
                // This field has a choiceList so we have to return a ChoiceBase as the value
                for (ChoiceBase v: getAvailableValues())
                {
                    if (v.getKey().equals(ret))
                    {
                        ret = v;
                        break;
                    }
                }
            }
            if (m_propertyFormatter != null)
            {
//                ret = m_propertyFormatter.format(ret);
            }
//            logger.debug("getValue() {} {}",getFullName(),ret); 
            return ret;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.Property#isReadOnly()
	 */
	public boolean isReadOnly() {
		if (m_fieldMetadata == null) {
			if (m_getter.isAnnotationPresent(Id.class)) {
				return true;
			} else {
				return false;
			}
		}
	    return m_fieldMetadata.isReadOnly();
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.Property#setValue(java.lang.Object)
	 */
	public void setValue(Object newValue) {
	    if (isReadOnly())
	    {
	        return;
	    }
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
        try {
            Class<?> clazz = this.getDataType();
            Object converted = newValue;
            if (m_propertyFormatter != null)
            {
                try
                {
//                    converted =  m_propertyFormatter.parse(String.valueOf(newValue));
                }
                catch (Exception e)
                {
                    String message = messageSourceAccessor.getMessage("nz.co.senanque.validationengine.numericparse", new Object[]{ this.m_label, String.valueOf(newValue) });
                    throw new ValidationException(message);
                }
            }
            else
            {
            	if (getAvailableValues() != null) {
            		for (ChoiceBase cb: getAvailableValues()) {
            			if (cb.toString().equals(converted)) {
            				converted = cb.getKey();
            				break;
            			}
            		}
            	}
                if (clazz != String.class)
                {
                    try
                    {
                        if (clazz.isEnum())
                        {
                            if (newValue == null) {
                            	converted = null;
                            } else {
                            	Method fromValueMethod = clazz.getMethod("fromValue", String.class);
                                converted = fromValueMethod.invoke(null,new Object[]{String.valueOf(newValue)});
                            }
                        }
                        else
                        {
                            converted = ConvertUtils.convertToObject(clazz, newValue, messageSourceAccessor);
                        }
                    }
                    catch (Exception e)
                    {
                        throw e;
                    }
                }
            }
			m_setter.invoke(m_owner, new Object[]{converted});
			setErrorText(null);
			m_lastFailedValue = null;
		}
        catch (InvocationTargetException e)
        {
        	logger.warn("error",e);
            Throwable target = e.getTargetException();
            if (target != null)
            {
                if (target instanceof ValidationException)
                {
                    setErrorText(target.getLocalizedMessage());
                    m_lastFailedValue = newValue;
                }
                else
                {
                    throw new RuntimeException(target);
                }
            }
            else
            {
                throw new RuntimeException(e);
            }
        }
        catch (ValidationException e)
        {
        	logger.warn("error",e);
            setErrorText(e.getLocalizedMessage());
            m_lastFailedValue = newValue;
        }
        catch (Exception e)
        {
        	logger.warn("error",e);
            throw new RuntimeException(e);
        }
	}

	public List<ChoiceBase> getAvailableValues() {
	    if (m_fieldMetadata == null)
	    {
	        return new ArrayList<ChoiceBase>();
	    }
	    return m_fieldMetadata.getChoiceList();
	}

	public boolean isActive() {
        if (m_fieldMetadata == null)
        {
            return true;
        }
		return m_fieldMetadata.isActive();
	}

	public boolean isRequired() {
        if (m_fieldMetadata == null)
        {
            return false;
        }
	    return m_fieldMetadata.isRequired();
	}
	
    public boolean isEnabled() {
        return m_hints.isEnabled(isActive());
    }

    public boolean isVisible() {
        return m_hints.isVisible(isActive());
    }

    public String getErrorText()
    {
        return m_errorText;
    }

    private void setErrorText(String errorText)
    {
        m_errorText = errorText;
    }

    public void setReadOnly(boolean newStatus)
    {
        // Vaadin requires this but we have no need for it.
    }

    public Hints getHints()
    {
        return m_hints;
    }

    public Class<?> getDataType()
    {
        return m_dataType;
    }

    public String getLabel()
    {
        return m_label;
    }

    public String getDescription()
    {
        return m_description;
    }

    public String getName()
    {
        return m_name;
    }

    public String getWritePermission()
    {
        return m_writePermission;
    }
    public String getReadPermission()
    {
        return m_readPermission;
    }

    public void setNumeric(boolean b)
    {
        m_numeric = b;
        
    }

    public boolean isNumeric()
    {
        return m_numeric;
    }

    public boolean isEmail()
    {
        return m_email;
    }

    public void setFormatter(Formatter formatter)
    {
        m_propertyFormatter = formatter;
        
    }

    public boolean isSecret()
    {
        if (m_fieldMetadata == null)
        {
            return false;
        }
        return m_fieldMetadata.isSecret();
    }

	public FieldMetadata getFieldMetadata() {
		return m_fieldMetadata;
	}

	public int getMaxLength() {
		return m_fieldMetadata.getMaxLength();
	}

	public MessageSource getMessageSource() {
		return m_messageSource;
	}
	public String getFullName() {
		if (logger.isDebugEnabled()) {
			return m_owner.getClass().getSimpleName()+"(id="+System.identityHashCode(m_owner)+")"+getName();
		}
		return "";
	}

	public ValidationObject getOwner() {
		return m_owner;
	}
}
