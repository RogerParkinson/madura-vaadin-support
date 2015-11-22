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

import nz.co.senanque.vaadin.MaduraFieldGroup;
import nz.co.senanque.vaadin.MaduraSessionManager;
import nz.co.senanque.validationengine.ValidationObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
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

	private static final long serialVersionUID = -1L;

    protected Button save;
    protected Button delete;
    protected Button close;

    protected T m_object;
    protected MaduraFieldGroup m_maduraFieldGroup;
    private List<String> m_fields;
    
    private String m_width = "400px";
    private boolean m_newRow;
    @Autowired private MaduraSessionManager m_maduraSessionManager;
	private MessageSource m_messageSource;
	private final String m_caption;
	private final String m_submitStyle;
	private final Layout m_panel = new VerticalLayout();
	
	public EditorWindowImpl(String caption) {
		m_caption = caption;
		m_submitStyle = null;
	}

    public EditorWindowImpl(String caption, String submitStyle) {
		m_caption = caption;
		m_submitStyle = submitStyle;
	}

	public void initialize(List<String> fields) {
    	MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
    	setCaption(messageSourceAccessor.getMessage(m_caption));
    	m_maduraFieldGroup = m_maduraSessionManager.createMaduraFieldGroup();
        Layout main = new VerticalLayout();
        setContent(main);
        main.setWidth(getWindowWidth());

        setFields(fields);
        main.addComponent(m_panel);
        
        save = m_maduraFieldGroup.createSubmitButton("editor.window.save", this);
        delete = m_maduraFieldGroup.createButton("editor.window.delete",this);
        close = m_maduraFieldGroup.createButton("editor.window.close",this);

        extraFields(main);
        HorizontalLayout actions = new HorizontalLayout();
        actions.addComponent(save);

        if (m_submitStyle != null) {
	        save.setClickShortcut( KeyCode.ENTER ) ;
	        save.addStyleName( m_submitStyle ) ;
        }

        actions.addComponent(delete);
        actions.addComponent(close);
        actions.setMargin(true);
        actions.setSpacing(true);
        main.addComponent(actions);
        this.addCloseListener(new CloseListener(){

			private static final long serialVersionUID = -1L;

			public void windowClose(CloseEvent e)
            {
				m_maduraFieldGroup.unbind();
            }});
    }

	protected void extraFields(Layout main) {
	}
    
	protected void setItemDataSource(BeanItem<T> newDataSource) {
		m_maduraFieldGroup.buildAndBind(m_panel,getFields(),(BeanItem<ValidationObject>)newDataSource);
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
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
		
	}
	
}
