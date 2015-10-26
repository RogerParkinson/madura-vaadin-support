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
package nz.co.senanque.madurarulesdemo;

import nz.co.senanque.rules.RulesPlugin;
import nz.co.senanque.vaadin.FieldFactory;
import nz.co.senanque.vaadin.MaduraPropertyWrapper;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.validationengine.FieldMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Roger Parkinson
 *
 */
public class OneFieldWindow extends Window {

	private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(OneFieldWindow.class);
    private VerticalLayout main;
    
	public OneFieldWindow(final RulesPlugin rulesPlugin, final FieldMetadata fm,final FieldMetadata fieldMetadata, final MaduraSessionManager maduraSessionManager)
	{
        main = new VerticalLayout();
        setContent(main);
        setModal(true);

        main.setMargin(true);
        main.setSpacing(true);
        main.setWidth("320px");
        
        MaduraPropertyWrapper maduraPropertyWrapper = maduraSessionManager.getMaduraPropertyWrapper(fm);
        FieldFactory fieldFactory = maduraSessionManager.getFieldFactory();
        final Field field = fieldFactory.createFieldByPropertyType(maduraPropertyWrapper);
        field.setBuffered(false);
        main.addComponent(field);
        Button buttonNotknown = fieldFactory.createButton("NotKnown", new Button.ClickListener(){

			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				close();
				rulesPlugin.setNotKnown(fm);
				FieldMetadata fm1 =  rulesPlugin.getEmptyField(fieldMetadata);
				logger.debug("Found empty field {}",(fm1==null)?"null":fm1.getName());
				if (fm1 != null)
				{
					OneFieldWindow ofw = new OneFieldWindow(rulesPlugin,fm1, fieldMetadata, maduraSessionManager);
					ofw.load();
				}
				else
				{
					OneFieldWindow ofw = new OneFieldWindow(rulesPlugin,fieldMetadata, maduraSessionManager);
					ofw.load();
				}
				
			}});
        HorizontalLayout buttons = new HorizontalLayout();
        main.addComponent(buttons);
        buttons.addComponent(buttonNotknown);
		Button buttonOK = fieldFactory.createButton("OK",
				new Button.ClickListener() {

					private static final long serialVersionUID = 1L;

					public void buttonClick(ClickEvent event) {
						logger.debug("set {} to {}", fm.getName(),
								field.getValue());
						FieldMetadata fm1 = rulesPlugin
								.getEmptyField(fieldMetadata);
						logger.debug("Found empty field {}",
								(fm1 == null) ? "null" : fm1.getName());
						close();
						if (fm1 != null) {
							OneFieldWindow ofw = new OneFieldWindow(
									rulesPlugin, fm1, fieldMetadata,
									maduraSessionManager);
							ofw.load();
						} else {
							OneFieldWindow ofw = new OneFieldWindow(
									rulesPlugin, fieldMetadata,
									maduraSessionManager);
							ofw.load();
						}
					}
				});
        buttonOK.setClickShortcut(KeyCode.ENTER );
        buttonOK.addStyleName(ValoTheme.BUTTON_PRIMARY);

		buttons.addComponent(buttonOK);

        field.focus();
        buttons.setComponentAlignment(buttonNotknown, Alignment.BOTTOM_LEFT);
        buttons.setComponentAlignment(buttonOK, Alignment.BOTTOM_RIGHT);
	}

	public OneFieldWindow(RulesPlugin rulesPlugin, FieldMetadata fieldMetadata, MaduraSessionManager maduraSessionManager) {
        main = new VerticalLayout();
        setContent(main);
        setModal(true);

        main.setMargin(true);
        main.setSpacing(true);
        main.setWidth("320px");
        
        MaduraPropertyWrapper maduraPropertyWrapper = maduraSessionManager.getMaduraPropertyWrapper(fieldMetadata);
        FieldFactory fieldFactory = maduraSessionManager.getFieldFactory();
        Field field = fieldFactory.createFieldByPropertyType(maduraPropertyWrapper);
        field.setReadOnly(true);
        main.addComponent(field);

        HorizontalLayout buttons = new HorizontalLayout();
        main.addComponent(buttons);
        Button buttonOK = fieldFactory.createButton("OK", new Button.ClickListener(){

			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				close();
			}});
        buttonOK.setClickShortcut(KeyCode.ENTER );
        buttonOK.addStyleName(ValoTheme.BUTTON_PRIMARY);

        buttons.addComponent(buttonOK);
        
        buttons.setComponentAlignment(buttonOK, Alignment.BOTTOM_RIGHT);
	}

	public void load() {
    	if (getParent() == null) {
    		UI.getCurrent().addWindow(this);
        	this.center();
        }
	}
}
