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
package nz.co.senanque.vaadin.format;

import java.util.Collection;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import com.vaadin.addon.jpacontainer.EntityContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;

/**
 * Extension of the Vaadin table to recognise numeric types and adjust formatting to locale-specific and right justify.
 * Also supports locale driven headings and column selection.
 * 
 * Call setContainerDataSource with the extra parameters.
 * 
 * @author Roger Parkinson
 * 
 */
public class FormattingTable extends Table {

//	private static Logger m_logger = LoggerFactory.getLogger(FormattingTable.class);
	private static final long serialVersionUID = 8755024456768538497L;
	
	public FormattingTable() {
		super();
	}

	public FormattingTable(String message,
			EntityContainer<?> container) {
		super(message,container);
	}

	protected String formatPropertyValue(Object rowId, Object colId,
			Property<?> property) {
		// Format by property type
		Formatter formatter = FormatterFactory.getFormatter(property.getType());
		if (formatter != null) {
			// setColumnAlignment(colId, Table.ALIGN_RIGHT);
			return formatter.format(property.getValue());
		}
		return super.formatPropertyValue(rowId, colId, property);
	}

	/**
	 * Does the same as the normal setContainerDataSource but accepts the column list (subset) and the headings for each column.
	 * The headings are translated to the current locale.
	 * Column names for numeric values should be prefixed with '-' which will make the right justify.
	 * Numbers (Double, Float, Long, Integer, BigDecimal) will be formatted for the current locale.
	 * @param indexed
	 * @param allColumns
	 * @param headings
	 */
	public void setContainerDataSource(Container indexed, String[] allColumns, String[] headings, MessageSource messageSource) {
		super.setContainerDataSource(indexed);
		Collection<?> propertyIds = indexed.getContainerPropertyIds();
		Object[] visiblePropertyIds = new String[allColumns.length];
		String[] headings_ = new String[allColumns.length];
		int i = 0;
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
		for (int j = 0; j < allColumns.length; j++) {
			for (Object propertyId : propertyIds) {
				String thisColumn = allColumns[j];
				if (thisColumn.startsWith("-"))
				{
					thisColumn = thisColumn.substring(1);
					setColumnAlignment(thisColumn, Table.ALIGN_RIGHT);
				}
				if (propertyId.toString().equals(thisColumn)) {
					visiblePropertyIds[i] = propertyId;
					headings_[i] = messageSourceAccessor.getMessage(headings[j],headings[j]);
					i++;
					break;
				}
			}
		}
		/*		
		super.setVisibleColumns(allColumns);
		*/
		super.setVisibleColumns(visiblePropertyIds);
		super.setColumnHeaders(headings_);
	}
}
