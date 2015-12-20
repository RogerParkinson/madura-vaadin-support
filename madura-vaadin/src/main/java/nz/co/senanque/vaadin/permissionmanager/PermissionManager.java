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
package nz.co.senanque.vaadin.permissionmanager;

import java.util.Set;

import com.vaadin.ui.UI;


/**
 * 
 * Implement this interface when building a permission manager.
 * The permission manager is a repository for the permissions allowed to the current user.
 * It also contains the user name and should send a change user event to any listeners added 
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public interface PermissionManager
{
	public static final String PERMISSIONS = "nz.co.senanque.login.RequestValidator.AUTHENTICATED";

    public abstract boolean hasPermission(String permission);

    public abstract void setCurrentUser(String currentUser);

    public abstract String getCurrentUser();
    public void setPermissionsList(Set<String> permissionsList);

	public abstract void close(UI ui);

}