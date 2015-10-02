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

import java.io.Serializable;

/**
 * The container we use in the {@link nz.co.senanque.vaadinsupport.tableeditor.TableEditorLayout} implements this interface.
 * 
 * @author Roger Parkinson
 *
 */
public interface Editor<T> {

	public T get(Serializable target);
	public void merge(T object);
	public Object getIdForPojo(T object);
    public Object addItem() throws UnsupportedOperationException;
}
