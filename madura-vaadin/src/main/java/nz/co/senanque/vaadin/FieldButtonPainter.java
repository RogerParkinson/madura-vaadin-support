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

	public final String m_propertyName;
	public MaduraPropertyWrapper m_property;   
    private final MaduraSessionManager m_maduraSessionManager;

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
    	if (getProperty() == null) {
    		return;
    	}
        Boolean b = (Boolean)m_property.getValue();
        if (b != null && b)
        {
            button.setEnabled(true);
        }
        else
        {
            button.setEnabled(false);
        }
        super.paint(button);
    }
    public void paint(MenuItem menuItem)
    {
    	if (m_property == null) {
    		return;
    	}
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

   public MaduraPropertyWrapper getProperty()
    {
	   if (m_property == null) {
		   if (getPropertiesSource() != null) {
			   m_property = getPropertiesSource().findProperty(m_propertyName);
		   }
	   }
       return m_property;
    }
}
