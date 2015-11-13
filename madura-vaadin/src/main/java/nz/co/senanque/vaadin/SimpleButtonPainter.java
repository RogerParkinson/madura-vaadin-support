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

import nz.co.senanque.vaadin.application.MaduraSessionManager;

import com.vaadin.ui.MenuBar.MenuItem;

/**
 * 
 * This painter just checks the permission.
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class SimpleButtonPainter extends AbstractButtonPainter
{

    private static final long serialVersionUID = -224212799086717190L;
	public SimpleButtonPainter(MaduraSessionManager maduraSessionManager)
    {
    	super(maduraSessionManager.getPermissionManager(),maduraSessionManager.getMessageSource());
    }
    public SimpleButtonPainter(String permissionName, MaduraSessionManager maduraSessionManager)
    {
    	super(maduraSessionManager.getPermissionManager(),maduraSessionManager.getMessageSource());
        setPermissionName(permissionName);
    }
    public void setProperties(List<MaduraPropertyWrapper> properties)
    {
    }
    public List<MaduraPropertyWrapper> getProperties()
    {
    	return new ArrayList<MaduraPropertyWrapper>();
    }
    public MaduraPropertyWrapper getProperty()
    {
        return null;
    }
    public void paint(com.vaadin.ui.Button button)
    {
        super.paint(button);
    }
    public void paint(MenuItem menuItem)
    {
        super.paint(menuItem);
    }

}
