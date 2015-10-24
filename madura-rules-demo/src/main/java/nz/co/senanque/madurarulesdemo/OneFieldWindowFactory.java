package nz.co.senanque.madurarulesdemo;

import nz.co.senanque.rules.RulesPlugin;
import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.ValidationObject;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Window;

/**
 * @author Roger Parkinson
 *
 */
@org.springframework.stereotype.Component
@UIScope
public class OneFieldWindowFactory {
	
	@Autowired RulesPlugin m_rulesPlugin;
	@Autowired MaduraSessionManager m_maduraSessionManager;
	
	public void createWindow(ValidationObject validationObject, String fieldName) {
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
			OneFieldWindow ofw = new OneFieldWindow(m_rulesPlugin,fm,fieldMetadata,m_maduraSessionManager);
			ofw.load();
		}
		else
		{
			OneFieldWindow ofw = new OneFieldWindow(m_rulesPlugin,fieldMetadata,m_maduraSessionManager);
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
