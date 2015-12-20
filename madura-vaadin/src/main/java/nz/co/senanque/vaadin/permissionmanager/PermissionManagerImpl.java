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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import nz.co.senanque.login.AuthenticationDelegate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;

/**
 * 
 * Holds permissions for the current session
 * as well as the current user. A change user event is sent to registered consumers.
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
@Component("permissionManager")
@UIScope
public class PermissionManagerImpl implements PermissionManager, PermissionResolver, Serializable {
    
	private static final long serialVersionUID = -1L;
	private Set<String> m_permissionsList = new HashSet<String>();
    private String m_currentUser;
    
    @Autowired(required=false) PermissionResolver m_permissionResolver;

	public PermissionManagerImpl () {
    }
	
	@PostConstruct
	public void init() {
		if (m_permissionResolver == null) {
			m_permissionResolver = this;
		}
		m_permissionResolver.unpackPermissions();
	}
    
	/* (non-Javadoc)
     * @see nz.co.senanque.vaadin.permissions.PermissionManager#hasPermission(java.lang.String)
     */
	public boolean hasPermission(String permission) {
	    if (permission == null)
	    {
	        return true;
	    }
		return (m_permissionsList.contains(permission));
	}
	
	public void setPermissionsList(Set<String> permissionsList)
	{
	    m_permissionsList = permissionsList;
	}

    public String getCurrentUser()
    {
        return m_currentUser;
    }
    public void setCurrentUser(String currentUser)
    {
        m_currentUser = currentUser;
        ChangeUserEvent event = new ChangeUserEvent(currentUser);
    }

	public Set<String> getPermissionsList() {
		return Collections.unmodifiableSet(m_permissionsList);
	}

	public PermissionResolver getPermissionResolver() {
		return m_permissionResolver;
	}

	public void setPermissionResolver(PermissionResolver permissionResolver) {
		m_permissionResolver = permissionResolver;
	}

	@Override
	public void unpackPermissions() {
		WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
    	String currentUser = (String)session.getAttribute(AuthenticationDelegate.USERNAME);
    	@SuppressWarnings("unchecked")
		Set<String> currentPermissions = (Set<String>)session.getAttribute(AuthenticationDelegate.PERMISSIONS);
    	setPermissionsList(currentPermissions);
    	setCurrentUser(currentUser);
	}

	@Override
	public void close(UI ui) {
    	VaadinService.getCurrentRequest().getWrappedSession().invalidate();
    	ui.close();
        String contextPath = VaadinService.getCurrentRequest().getContextPath();
        ui.getPage().setLocation(contextPath);
	}

}
