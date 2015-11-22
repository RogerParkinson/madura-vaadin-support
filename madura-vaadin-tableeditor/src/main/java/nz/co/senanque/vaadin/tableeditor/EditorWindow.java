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

import java.util.List;

import nz.co.senanque.vaadin.MaduraSessionManager;

import com.vaadin.ui.Component;

public interface EditorWindow<T> extends Component {

	public void loadObject(T object, boolean newRow);
	public void initialize(List<String> fields, MaduraSessionManager maduraSessionManager);

}