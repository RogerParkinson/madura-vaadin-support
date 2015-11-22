package nz.co.senanque.vaadin.directed;

import java.io.Serializable;

import nz.co.senanque.rules.RulesPlugin;
import nz.co.senanque.vaadin.MaduraSessionManager;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.ValidationObject;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.UIScope;

/**
 * The factory the creates the first directed popup window.
 * 
 * @author Roger Parkinson
 *
 */
@org.springframework.stereotype.Component
@UIScope
public class OneFieldWindowFactory implements Serializable {
	
	@Autowired RulesPlugin m_rulesPlugin;
	@Autowired MaduraSessionManager m_maduraSessionManager;
	
	public void createWindow(ValidationObject validationObject, String fieldName, String submitStyle) {
		FieldMetadata fieldMetadata = validationObject.getMetadata().getFieldMetadata(fieldName);
		if (fieldMetadata == null)
		{
			throw new RuntimeException("Failed to find field "+fieldName);
		}
		// this resets all the unknown fields making the scenario repeatable
		m_rulesPlugin.clearUnknowns(validationObject);
		FieldMetadata fm =  m_rulesPlugin.getEmptyField(fieldMetadata);
		if (fm != null)
		{
			OneFieldWindow ofw = new OneFieldWindow(m_rulesPlugin,fm,fieldMetadata,m_maduraSessionManager,submitStyle);
			ofw.load();
		}
		else
		{
			OneFieldWindow ofw = new OneFieldWindow(m_rulesPlugin,fieldMetadata,m_maduraSessionManager,submitStyle);
			ofw.load();
		}
		
	}
	
	public RulesPlugin getRulesPlugin() {
		return m_rulesPlugin;
	}

	public void setRulesPlugin(RulesPlugin rulesPlugin) {
		m_rulesPlugin = rulesPlugin;
	}

	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}

	public void setMaduraSessionManager(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
	}

}
