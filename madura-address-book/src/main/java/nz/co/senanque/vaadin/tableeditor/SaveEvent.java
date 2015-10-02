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

import com.vaadin.ui.Button;
import com.vaadin.ui.Component.Event;

/**
 * 
 * The save event carries the object in question
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class SaveEvent<T> extends Event
{
    private static final long serialVersionUID = 3458404128400413489L;
	private final T m_object;
    public SaveEvent(Button button, T object)
    {
        super(button);
        m_object = object;
    }
    public T getObject()
    {
        return m_object;
    }

}
