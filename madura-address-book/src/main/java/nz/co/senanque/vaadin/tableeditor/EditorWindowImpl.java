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
package nz.co.senanque.vaadin.tableeditor;

import java.util.List;

import nz.co.senanque.vaadin.MaduraForm;
import nz.co.senanque.vaadin.SimpleButtonPainter;
import nz.co.senanque.vaadin.SubmitButtonPainter;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.validationengine.ValidationObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Generic popup window that can be used to edit an object we pass to it.
 * The window is compatible with the jpa container.
 * You can pass a list of fields rather than have them all (which is the default).
 * You can extend this class to customise the actual fields and you can supply your own
 * field factory as well. It handles simple fields just fine but it doesn't handle fields
 * that relate to other records, which is why you might want to extend it.
 * 
 * @author Roger Parkinson
 *
 * @param <T>
 */

public class EditorWindowImpl<T> extends Window implements ClickListener, EditorWindow<T>, MessageSourceAware {

	private static final long serialVersionUID = -2089155892716330035L;
	private static Logger logger = LoggerFactory.getLogger(EditorWindowImpl.class);

    protected Button save;
    protected Button delete;
    protected Button close;

    protected T m_object;
    protected MaduraForm m_form;
    private List<String> m_fields;
    
    private String m_width = "400px";
    private boolean m_newRow;
    @Autowired private MaduraSessionManager m_maduraSessionManager;
	private MessageSource m_messageSource;
	private final String m_caption;
	
	public EditorWindowImpl(String caption) {
		m_caption = caption;
	}

//	public EditorWindowImpl(MaduraSessionManager maduraSessionManager, String caption, MessageSource messageSource) {
//		m_maduraSessionManager = maduraSessionManager;
//        m_form = new nz.co.senanque.vaadin.MaduraForm(maduraSessionManager);
//        setCaption(new MessageSourceAccessor(messageSource).getMessage(caption));
//    }
//	public void setCaption(String caption) {
//		super.setCaption(new MessageSourceAccessor(m_messageSource).getMessage(caption));
//	}
	
    public void initialize(List<String> fields) {
    	MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
    	setCaption(messageSourceAccessor.getMessage(m_caption));
    	if (m_form == null) {
    		m_form = new nz.co.senanque.vaadin.MaduraForm(m_maduraSessionManager);
    	}
        Layout main = new VerticalLayout();
        setContent(main);
        main.setWidth(getWindowWidth());

        setFields(fields);
        m_form.setSizeFull();
        
        main.addComponent(m_form);
        
        save = m_form.createButton(messageSourceAccessor.getMessage("editor.window.save"),new SubmitButtonPainter(m_maduraSessionManager),this);
        delete = m_form.createButton(messageSourceAccessor.getMessage("editor.window.delete"),new SimpleButtonPainter(m_maduraSessionManager),this);
        close = m_form.createButton(messageSourceAccessor.getMessage("editor.window.close"),new SimpleButtonPainter(m_maduraSessionManager),this);

        extraFields();
        HorizontalLayout actions = new HorizontalLayout();
        actions.addComponent(save);

        save.addClickListener(this);

        actions.addComponent(delete);
        delete.addClickListener(this);
        close.addClickListener(this);
        actions.addComponent(close);

        main.addComponent(actions);
    }
    protected void extraFields()
    {
    	
    }
    
    protected void setItemDataSource(BeanItem<T> newDataSource)
    {
        final ValidationObject o = (ValidationObject)newDataSource.getBean();
        m_maduraSessionManager.getValidationSession().bind(o);
        m_form.setFieldList(getFields());
        m_form.setItemDataSource(newDataSource);
        this.addCloseListener(new CloseListener(){

			private static final long serialVersionUID = -2096669984588309706L;

			public void windowClose(CloseEvent e)
            {
                // TODO: it would be better to selectively unbind
				m_maduraSessionManager.getValidationSession().unbindAll();
                
            }});
    }

     public void loadObject(T object, boolean newRow) {
        if (object == null) {
            close();
        } else {
			setItemDataSource(new BeanItem<T>(object));
            if (getParent() == null) {
            	UI.getCurrent().addWindow(this);
            	this.center();
            }
            m_object = object;
            m_newRow = newRow;
        }
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == delete) {
            fireEvent(new DeleteEvent<T>(event.getButton(),m_object));
        } else if (event.getButton() == save) {
            fireEvent(new SaveEvent<T>(event.getButton(),m_object));
        } else if (event.getButton() == close && isNewRow()) {
            fireEvent(new CancelEvent<T>(event.getButton(),m_object));
        }
        if (getParent() != null) {
        	UI.getCurrent().removeWindow(this);
        }
    }
	public List<String> getFields() {
		return m_fields;
	}
	public void setFields(List<String> fields) {
		if (m_fields == null)
		{
			m_fields = fields;
			m_form.setFieldList(fields);
		}
	}

	public String getWindowWidth() {
		return m_width;
	}

	public void setWindowWidth(String width) {
		m_width = width;
	}
    public boolean isNewRow()
    {
        return m_newRow;
    }
    public void setNewRow(boolean newRow)
    {
        m_newRow = newRow;
    }
	@Override
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
		
	}
	
}
