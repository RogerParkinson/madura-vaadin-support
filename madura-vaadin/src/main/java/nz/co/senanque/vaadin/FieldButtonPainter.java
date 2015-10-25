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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.co.senanque.vaadin.application.MaduraSessionManager;

import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * 
 * This button (or menu item) becomes enabled only if the relevant MaduraPropertyWrapper is true
 * The list of properties is ignored. The underlying property is not normally displayed
 * directly, but it does need to be on the validation object displayed on this form.
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class FieldButtonPainter extends AbstractButtonPainter
{
	private static final long serialVersionUID = 5050173897198341434L;
    private static final Logger logger = LoggerFactory.getLogger(FieldButtonPainter.class);

	public final String m_propertyName;
	public MaduraPropertyWrapper m_property;   
    private MaduraSessionManager m_maduraSessionManager;
	private MaduraForm m_form;

    public FieldButtonPainter(String propertyName, MaduraSessionManager maduraSessionManager)
    {
    	super(maduraSessionManager.getPermissionManager(),maduraSessionManager.getMessageSource());
        m_propertyName = propertyName;
        m_maduraSessionManager = maduraSessionManager;
    }
    
    public FieldButtonPainter(String propertyName, String permissionName, MaduraSessionManager maduraSessionManager)
    {
    	super(maduraSessionManager.getPermissionManager(),maduraSessionManager.getMessageSource());
        m_maduraSessionManager = maduraSessionManager;
        m_propertyName = propertyName;
        setPermissionName(permissionName);
    }
    
    public void paint(Button button)
    {
        Boolean b = (Boolean)m_property.getValue();
        if (b != null && b)
        {
            button.setEnabled(true);
        }
        else
        {
            button.setEnabled(false);
        }
        logger.debug("button {} enabled {}",button.getCaption(),button.isEnabled());
        super.paint(button);
    }
    public void paint(MenuItem menuItem)
    {
        Boolean b = (Boolean)m_property.getValue();
        if (b != null && b)
        {
        	menuItem.setEnabled(true);
        }
        else
        {
        	menuItem.setEnabled(false);
        }
        super.paint(menuItem);
    }

    public List<MaduraPropertyWrapper> getProperties()
    {
    	return new ArrayList<MaduraPropertyWrapper>();
    }
   public MaduraPropertyWrapper getProperty()
    {
        return m_property;
    }
	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}

	public void setMaduraSessionManager(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
	}

	public void setProperty(MaduraPropertyWrapper property) {
		m_property = property;
	}

	public void setProperties(
			List<MaduraPropertyWrapper> properties) {
		m_property = getMaduraSessionManager().findProperty(m_propertyName, properties);
	}

	@Override
	public void setForm(MaduraForm maduraForm) {
		m_form = maduraForm;
	}

	public MaduraForm getForm() {
		return m_form;
	}
}
