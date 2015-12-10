package nz.co.senanque.vaadin;

import java.util.List;

import nz.co.senanque.validationengine.ValidationObject;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar.MenuItem;

public interface MaduraFieldGroup {

	/**
	 * Establish a data source. The data source must be a {@code BeanItem<ValidationObject>} or we throw an exception.
	 * This establishes the Madura binding for the fields. 
	 * @see com.vaadin.data.fieldgroup.FieldGroup#setItemDataSource(com.vaadin.data.Item)
	 */
	public abstract void setItemDataSource(Item itemDataSource);

	public abstract void buildAndBind(AbstractComponentContainer panel, List<String> fields,
			BeanItem<ValidationObject> itemDataSource);

	public abstract void unbind();

	/**
	 * Create a button that only enables when all the required fields are completed without error.
	 * @param name
	 * @param listener
	 * @return button
	 */
	public abstract Button createSubmitButton(String name,
			ClickListener listener);

	/**
	 * Create a simple button.
	 * @param name
	 * @param listener
	 * @return button
	 */
	public abstract Button createButton(String name, ClickListener listener);

	/**
	 * Create a button that enables and disables depending on the value of the boolean propertyId.
	 * @param name
	 * @param propertyId
	 * @param listener
	 * @return button
	 */
	public abstract Button createFieldButton(String name, String propertyId,
			ClickListener listener);

	/**
	 * Create a button that only enables when all the required fields are completed without error.
	 * @param name
	 * @param permission
	 * @param listener
	 * @return button
	 */
	public abstract Button createSubmitButton(String name, String permission,
			ClickListener listener);

	/**
	 * Create a simple button.
	 * @param name
	 * @param permission
	 * @param listener
	 * @return button
	 */
	public abstract Button createButton(String name, String permission,
			ClickListener listener);

	/**
	 * Create a button that enables and disables depending on the value of the boolean propertyId.
	 * @param name
	 * @param permission
	 * @param propertyId
	 * @param listener
	 * @return
	 */
	public abstract Button createFieldButton(String name, String propertyId,
			String permission, ClickListener listener);

	/**
	 * Creates an extended command for use with a {@link com.vaadin.ui.MenuBar.MenuItem}.
	 * @param listener
	 * @return commandExt
	 */
	public abstract CommandExt createMenuItemCommand(ClickListener listener);

	/**
	 * Creates an extended command for use with a {@link com.vaadin.ui.MenuBar.MenuItem}.
	 * The MenuItem will disable until all the fields are clean.
	 * @param listener
	 * @return commandExt
	 */
	public abstract CommandExt createMenuItemCommandSubmit(
			ClickListener listener);

	/**
	 * Creates an extended command for use with a {@link com.vaadin.ui.MenuBar.MenuItem}.
	 * If the current user does not have the given permission it remains disabled
	 * @param permission
	 * @param listener
	 * @return commandExt
	 */
	public abstract CommandExt createMenuItemCommand(String permission,
			ClickListener listener);

	/**
	 * Creates an extended command for use with a {@link com.vaadin.ui.MenuBar.MenuItem}.
	 * The MenuItem will disable until all the fields are clean.
	 * If the current user does not have the given permission it remains disabled
	 * @param permission
	 * @param listener
	 * @return commandExt
	 */
	public abstract CommandExt createMenuItemCommandSubmit(String permission,
			ClickListener listener);

	/**
	 * Tells the madura session about this {@link com.vaadin.ui.MenuBar.MenuItem}.
	 * @param field
	 */
	public abstract void bind(MenuItem field);

	public abstract void bind(Field<?> nameField, Object propertyId);

	public abstract void bind(Label orderStatusLabel, Object propertyId);

	public abstract void setReadOnly(boolean b);

	public abstract void buildAndBindMemberFields(Object objectWithMemberFields);


}