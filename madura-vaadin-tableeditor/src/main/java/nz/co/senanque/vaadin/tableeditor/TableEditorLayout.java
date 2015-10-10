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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nz.co.senanque.vaadin.format.FormattingTable;
import nz.co.senanque.vaadin.permissionmanager.PermissionManager;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.metadata.ClassMetadata;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * Layout with a table that handles popup editors. It can be used quite generically with just Spring wiring:
 * 
 *   &lt;bean id="personTableLayout" class="nz.co.senanque.vaadinsupport.tableeditor.TableEditorLayout"&gt;
 *       &lt;property name="container" ref="personContainer"/&gt;
 *       &lt;property name="columns"&gt;
 *           &lt;list&gt;
 *               &lt;value&gt;name&lt;/value&gt;
 *               &lt;value&gt;address&lt;/value&gt;
 *               &lt;value&gt;email&lt;/value&gt;
 *           &lt;/list&gt;
 *       &lt;/property&gt;
 *       &lt;property name="editorWindow" ref="editorWindow"/&gt;
 *   &lt;/bean&gt;
 *   
 *   &lt;bean id="editorWindow" class="nz.co.senanque.vaadinsupport.tableeditor.EditorWindowImpl" &gt;
 *       &lt;constructor-arg ref="maduraSessionManager"/&gt;
 *       &lt;constructor-arg ref="viewManager"/&gt;
 *       &lt;constructor-arg value="person"/&gt;
 *       &lt;constructor-arg ref="messageSource"/&gt;
 *   &lt;/bean&gt;
 *
 * So you can inject your container, a list of fields and an editor implementation and you have a table that displays and edits
 * the contents of the container. Extend the EditorWindow to add special fields if necessary. You can also inject a list of headings
 * which will override the field names when displaying the table heading. Regardless these are translated to the current user's locale.
 * 
 *  The container needs to implement com.vaadin.addon.jpacontainer.Editor<T> if you want to use
 *  the editor functions (and if you don't you might wonder why you need this). The obvious container to use is com.vaadin.addon.jpacontainer.JPAContainerEditor<T>
 *  and you normally use a factory wired like this (using a locally defined Person type):
 *  
 *   &lt;bean id="personContainer" class="com.vaadin.addon.jpacontainer.JPAContainerEditorFactory"&gt;
 *   	&lt;property name="type" value="nz.co.senanque.addressbook.instances.Person"/&gt;
 *   &lt;/bean&gt;
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */

public class TableEditorLayout<T> extends CustomComponent implements InitializingBean, MessageSourceAware, Serializable {
	
	private static Logger m_logger = LoggerFactory.getLogger(TableEditorLayout.class);

	@AutoGenerated
	private VerticalLayout mainLayout;
	@AutoGenerated
	private FormattingTable table_1;
	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */
	private static final long serialVersionUID = 1565287876888837051L;
	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */
	@Autowired private Container.Filterable m_container;
	@Autowired private ValidationEngine m_validationEngine;

	private List<String> m_headings;
	private List<String> m_columns;
	private List<String> m_fields;
	
	private String m_permission;
	@Autowired transient private PermissionManager m_permissionManager;

	public List<String> getHeadings() {
		if (m_headings == null)
		{
			if (m_class != null) {
				m_headings = new ArrayList<String>();
				MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
				ClassMetadata cmd = m_validationEngine.getClassMetadata(m_class);
				for (String field: m_columns) {
					PropertyMetadata pm = cmd.getField(field);
					m_headings.add(pm.getLabelName(messageSourceAccessor));
				}
				return m_headings;
			}
			return getColumns();
		}
		return m_headings;
	}
	public void setHeadings(List<String> headings) {
		m_headings = headings;
	}
	public List<String> getColumns() {
		return m_columns;
	}
	public void setColumns(List<String> columns) {
		m_columns = columns;
	}
	public void setColumns(String[] strings) {
		m_columns = Arrays.asList(strings);
		
	}
	public List<String> getFields() {
		if (m_fields == null)
		{
			return getColumns();
		}
		return m_fields;
	}
	public void setFields(List<String> fields) {
		m_fields = fields;
	}

	@Autowired private EditorWindow<T> m_editorWindow;
	private transient MessageSource m_messageSource;
	private final String m_caption;
	private final Class<T> m_class;

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public TableEditorLayout(String caption, Class<T> clazz) {
		m_caption = caption;
		m_class = clazz;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		table_1.setWidth("100%");
		table_1.setHeight("100%");
		table_1.setPageLength(10);
		table_1.setSelectable(true);
		table_1.setImmediate(true);
		table_1.setColumnCollapsingAllowed(true);
	}
	
	public TableEditorLayout(String caption) {
		this(caption,null);
	}
    /**
     * Delete given object from Table. Table will delegate deletion to
     * its container.
     * 
     * @param object
     */
    public void deleteObject(T object) {
        Object id;
        try {
            id = getEditor().getIdForPojo(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        table_1.removeItem(id);
    }
    /**
     * Cancel given object from Table. Table will delegate deletion to
     * its container.
     * 
     * @param object
     */
    public void cancelObject(T object) {
        Long id;
        try {
            id = (Long)getEditor().getIdForPojo(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        table_1.removeItem(id);
    }
    /**
     * Adds new row to Table and selects new row. Table will delegate Item
     * creation to its container.
     */
    private void newRow() {
        Object newItemId = table_1.addItem();
        // open in editor window unless table is in content editable mode
        if (!table_1.isEditable()) {
            editObject((Long) newItemId, true);
        }
    }
    /**
     * Loads given object to Editor.
     * 
     * @param pojo
     */
    public void edit(T pojo, boolean newRow) {
    	getEditorWindow().loadObject(pojo, newRow);
    }
    /**
     * Loads the object into the editor window with a boolean indicating if it is a new row or not.
     * @param id
     * @param newRow
     */
    public void editObject(Long id, boolean newRow) {
        if (id == null) {
        	((Window)m_editorWindow).close();
        } else {
        	T pojo = (T) getEditor().get( id);
            edit(pojo,newRow);
        }
    }
    public void persistObject(T object) {
    	getEditor().merge(object);
        table_1.setValue(null);
    }

	public void afterPropertiesSet() throws Exception {
		final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
		setCaption(messageSourceAccessor.getMessage(m_caption));
		if (getEditorWindow() != null)
        {
            // Only set up the editor window if it is there
            getEditorWindow().initialize(getFields());
            getEditorWindow().addListener(new Listener(){
    
                private static final long serialVersionUID = -6498337136650101469L;

				public void componentEvent(Event event)
                {
                    if (event instanceof DeleteEvent)
                    {
                        deleteObject((T) ((DeleteEvent)event).getObject());
                        return;
                    }
                    if (event instanceof SaveEvent)
                    {
                        persistObject((T) ((SaveEvent)event).getObject());
                        return;
                    }
                    if (event instanceof CancelEvent)
                    {
                        cancelObject((T) ((CancelEvent)event).getObject());
                        return;
                    }
                }});
            table_1.addActionHandler(new Handler() {
                private static final long serialVersionUID = -5923269974651895441L;
                Action add = new Action(messageSourceAccessor.getMessage("tableeditorlayout.add",null,"add"));
                Action remove = new Action(messageSourceAccessor.getMessage("tableeditorlayout.delete",null,"delete"));
                Action edit = new Action(messageSourceAccessor.getMessage("tableeditorlayout.edit",null,"edit"));
                    
                public Action[] getActions(Object target, Object sender) {
                    List<Action> actions = new ArrayList<Action>();
                    if (getPermissionManager().hasPermission(getPermission()))
                    {
                        actions.add(add);
                        actions.add(remove);
                        actions.add(edit);
                    }
                    return actions.toArray(new Action[actions.size()]);
                }
    
                public void handleAction(Action action, Object sender, Object target) {
                    if (action == add) {
                        newRow();
                    } else if (action == remove) {
                        T w = (T) getEditor().get((Serializable) target);
                        deleteObject(w);
                    } else if (action == edit) {
                        editObject((Long) target, false);
                    }
                }
            });
        }
        table_1.setContainerDataSource(getContainer(),getColumns().toArray(new String[getColumns().size()]),getHeadings().toArray(new String[getHeadings().size()]),m_messageSource);

	}
	public void setContainer(Container.Filterable container) {
		m_container = container;
	}

	public Container.Filterable getContainer() {
		return m_container;
	}
	@SuppressWarnings("unchecked")
	private Editor<T> getEditor() {
		return (Editor<T>)m_container;
	}
	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// table_1
		table_1 = new FormattingTable();
		table_1.setImmediate(false);
		table_1.setWidth("-1px");
		table_1.setHeight("-1px");
		mainLayout.addComponent(table_1);
		
		return mainLayout;
	}
	public EditorWindow<T> getEditorWindow() {
		return m_editorWindow;
	}
	public void setEditorWindow(EditorWindow<T> editorWindow) {
		m_editorWindow = editorWindow;
	}
    public String getPermission()
    {
        return m_permission;
    }
    public void setPermission(String permission)
    {
        m_permission = permission;
    }
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}
	protected PermissionManager getPermissionManager() {
		return m_permissionManager;
	}
	protected void setPermissionManager(PermissionManager permissionManager) {
		m_permissionManager = permissionManager;
	}
	public ValidationEngine getValidationEngine() {
		return m_validationEngine;
	}
	public void setValidationEngine(ValidationEngine validationEngine) {
		m_validationEngine = validationEngine;
	}
}
