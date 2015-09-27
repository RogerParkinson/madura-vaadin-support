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

import nz.co.senanque.vaadin.permissionmanager.PermissionManager;

import org.springframework.context.MessageSource;

import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * 
 * Button (or menu item) painters are all called to help render the button.
 * Depending on the implementation the button may be disabled or invisible
 * This also allows us to add a permission to a button and that disables it if they don't have permission.
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
abstract class AbstractButtonPainter implements ButtonPainter,MenuItemPainter, Serializable
{
	private static final long serialVersionUID = -1182823026389331490L;
	private final PermissionManager m_permissionManager;
	private String m_permissionName;
	private final MessageSource m_messageSource;
	
	protected AbstractButtonPainter(PermissionManager permissionManager, MessageSource messageSource)
	{
		m_permissionManager = permissionManager;
		m_messageSource = messageSource;
	}

    /* (non-Javadoc)
     * @see nz.co.senanque.vaadin.ButtonPainter#paint(com.vaadin.ui.Button)
     */
    public void paint(Button button)
    {
        if (!getPermissionManager().hasPermission(
                getPermissionName()))
        {
            button.setEnabled(false);
        }
    }

    public void paint(MenuItem menuItem)
    {
        if (!getPermissionManager().hasPermission(
                getPermissionName()))
        {
        	menuItem.setEnabled(false);
        }
    }
    public String getPermissionName()
    {
        return m_permissionName;
    }

    public void setPermissionName(String permissionName)
    {
        m_permissionName = permissionName;
    }

	public PermissionManager getPermissionManager() {
		return m_permissionManager;
	}

	public MessageSource getMessageSource() {
		return m_messageSource;
	}

}
