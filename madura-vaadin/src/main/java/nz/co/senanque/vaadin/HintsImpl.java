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

import java.io.Serializable;

import nz.co.senanque.vaadin.application.MaduraConverterFactory;
import nz.co.senanque.vaadin.converter.StringToChoiceBase;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;

/**
 * Different hints can be injected using Spring, as long as the new object implements the Hints
 * interface. This object can be injected with variations as well.
 * 
 * Hints are used to influence the FieldFactory so that it generates the right widgets for
 * the fields it looks at.
 * 
 * @author Roger Parkinson
 *
 */
public class HintsImpl implements Hints, Serializable {
	
	private static final long serialVersionUID = -1066631793343374468L;

	Logger logger = LoggerFactory.getLogger(HintsImpl.class);
	
	private String m_width = "200px";
	private boolean m_forceImmediate = true;
	private boolean m_hideInactive = true;
	@Autowired private MaduraConverterFactory m_maduraConverterFactory;
	public HintsImpl()
	{
	    logger.debug("Constructing...");
	}
	public boolean isHideInactive() {
		return m_hideInactive;
	}
	public void setHideInactive(boolean hideInactive) {
		m_hideInactive = hideInactive;
	}
	private SelectType m_selectType = SelectType.STANDARD;

//	private transient MessageSource m_messageSource;

	public SelectType getSelectType() {
		return m_selectType;
	}
	public void setSelectType(SelectType selectType) {
		m_selectType = selectType;
	}
	public String getWidth() {
		return m_width;
	}
	public void setWidth(String width) {
		m_width = width;
	}
	public boolean isForceImmediate() {
		return m_forceImmediate;
	}
	public void setForceImmediate(boolean forceImmediate) {
		m_forceImmediate = forceImmediate;
	}
    public void setCommonProperties(final AbstractField ret, final MaduraPropertyWrapper property, final MessageSource messageSource)
    {
        ret.setWidth(getWidth());
        ret.setBuffered(true);
        ret.setPropertyDataSource(property);
        ret.setCaption(property.getLabel());
        ret.setRequired(property.isRequired());
        if (property.isRequired())
        {
            ret.setInvalidCommitted(true);
        }
        if (property.isReadOnly()) {
        	ret.setReadOnly(true);
        }
        ret.setEnabled(property.isEnabled());
        ret.setVisible(property.isVisible());
        ret.setImmediate(m_forceImmediate);
        ret.setLocale(LocaleContextHolder.getLocale());
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
        ret.setDescription(messageSourceAccessor.getMessage(property.getDescription(), null,property.getDescription()));
        if (property.isNumeric())
        {
            ret.addStyleName("v-textfield-align-right");
        }
        ret.setErrorHandler(new ErrorHandler(){

            private static final long serialVersionUID = -1393935533100204195L;
			@Override
			public void error(ErrorEvent event) {
                Throwable t = event.getThrowable();
                while (t != null)
                {
                    if (t instanceof ValidationException)
                    {
                        ret.setComponentError(new UserError(((ValidationException)t).getMessage()));
                    }
                    t = t.getCause();
                }
				
			}});

    }
    public void setCommonProperties(final MenuItem ret, final MaduraPropertyWrapper property, final MessageSource messageSource)
    {
        ret.setText(property.getLabel());
        ret.setEnabled(property.isEnabled());
        ret.setVisible(property.isVisible());
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
        ret.setDescription(messageSourceAccessor.getMessage(property.getDescription(), null,property.getDescription()));
    }
	@Override
	public void setCommonProperties(final Button ret,
			MaduraPropertyWrapper property, MessageSource messageSource) {
        ret.setWidth(getWidth());
        ret.setData(property);
        ret.setCaption(property.getLabel());
        if (property.isReadOnly()) {
        	ret.setReadOnly(true);
        }
        ret.setEnabled(property.isEnabled());
        ret.setVisible(property.isVisible());
        ret.setImmediate(m_forceImmediate);
        ret.setLocale(LocaleContextHolder.getLocale());
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
        ret.setDescription(messageSourceAccessor.getMessage(property.getDescription(), null,property.getDescription()));
        if (property.isNumeric())
        {
            ret.addStyleName("v-textfield-align-right");
        }
        ret.setErrorHandler(new ErrorHandler(){

            private static final long serialVersionUID = -1393935533100204195L;
			@Override
			public void error(ErrorEvent event) {
                Throwable t = event.getThrowable();
                while (t != null)
                {
                    if (t instanceof ValidationException)
                    {
                        ret.setComponentError(new UserError(((ValidationException)t).getMessage()));
                    }
                    t = t.getCause();
                }
				
			}});

	}
	public boolean isEnabled(boolean active) {
		return active;
	}
	public boolean isVisible(boolean active) {
		if (m_hideInactive)
		{
			return active;
		}
		return true;
	}
	public AbstractField getDateField(MaduraPropertyWrapper property) {
        final DateField df = new DateField();
        df.setResolution(DateField.RESOLUTION_DAY);
        df.setStyleName("calendar");
        return df;
	}
	public AbstractField getBooleanField(MaduraPropertyWrapper property) {
		return new CheckBox();
	}
	public AbstractField getSelectField(MaduraPropertyWrapper property) {
        AbstractSelect select = null;
        switch (getSelectType())
        {
        case STANDARD:
        	select = new Select();
            select.setMultiSelect(false);
        	break;
        case RADIO:
        	select = new OptionGroup();
            select.setMultiSelect(false);
        	break;
        }
        for (ChoiceBase v: property.getAvailableValues())
        {
        	select.addItem(v);
        	if (v.getKey().equals(property.getValue()))
        	{
        	    select.setValue(v);
        	}
        }
        select.setConverter(new StringToChoiceBase(property));

        return select;
	}
    public AbstractField getTextField(MaduraPropertyWrapper property) {
    	AbstractTextField ret = null;
        if (property.isSecret())
        {
            ret = new PasswordField();
        }
        else
        {
        	ret = new TextField();
//        	int maxLength = property.getMaxLength();
//        	if (maxLength > 100) {
//        		ret = new TextArea();
//        	} else {
//        		ret = new TextField();
//        	}
        }
        ret.setMaxLength(property.getMaxLength());
        return ret;
    }
	public Button getButtonField(String name, MessageSource messageSource) {
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
		Button ret = new Button(messageSourceAccessor.getMessage(name,null,name));
		return ret;
	}

	public MaduraConverterFactory getMaduraConverterFactory() {
		return m_maduraConverterFactory;
	}
	public void setMaduraConverterFactory(
			MaduraConverterFactory maduraConverterFactory) {
		m_maduraConverterFactory = maduraConverterFactory;
	}
}
