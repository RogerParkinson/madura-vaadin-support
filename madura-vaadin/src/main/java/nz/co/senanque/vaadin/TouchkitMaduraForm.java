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

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Layout;

/**
 * Extend this abstract class to implement a specific form.
 * Site specific form will include buttons (and their action code) and
 * specify what it is to be mapped to etc.
 * 
 * @author Roger Parkinson
 *
 */
public class TouchkitMaduraForm extends MaduraForm {
		
	private static final long serialVersionUID = 1L;
	public TouchkitMaduraForm(AbstractLayout layout,MaduraSessionManager maduraSessionManager)
    {
    	super(layout,maduraSessionManager);
    	Layout footerLayout = new AbsoluteLayout();
    	this.setFooter(footerLayout);
    }
    public TouchkitMaduraForm(MaduraSessionManager maduraSessionManager)
    {
        this(new FormLayout(),maduraSessionManager);
    }
	
}
