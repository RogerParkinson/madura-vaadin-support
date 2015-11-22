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

import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * 
 * Only enable the button (or menu item) if the required fields are filled in.
 * Also check the permission.
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class SubmitButtonPainter extends AbstractButtonPainter
{
    private static final long serialVersionUID = 5303047518541446210L;

    public SubmitButtonPainter(MaduraSessionManager maduraSessionManager)
    {
    	super(maduraSessionManager.getPermissionManager(),maduraSessionManager.getMessageSource());
    }
    public SubmitButtonPainter(String permissionName,MaduraSessionManager maduraSessionManager)
    {
    	super(maduraSessionManager.getPermissionManager(),maduraSessionManager.getMessageSource());
        setPermissionName(permissionName);
    }

    public void paint(Button button)
    {
    	if (getPropertiesSource() != null && getPropertiesSource().isReadOnly()) {
    		button.setEnabled(false);
    	} else {
            button.setEnabled(isEnabled());
    	}
        super.paint(button);
    }

    public void paint(MenuItem menuItem)
    {
    	menuItem.setEnabled(isEnabled());
        super.paint(menuItem);
    }

	private boolean isEnabled() {
		boolean ret = true;
		for (MaduraPropertyWrapper property : getProperties()) {
			Object value = property.getValue();
			boolean hasNoValue = (value == null || "".equals(value));
			if ((property.getErrorText() != null)
					|| (property.isRequired() && hasNoValue)) {
				ret = false;
				break;
			}
		}
		return ret;
	}

    public MaduraPropertyWrapper getProperty()
    {
        return null;
    }
}
