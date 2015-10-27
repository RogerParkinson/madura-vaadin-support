/**
 * 
 */
package nz.co.senanque.addressbook;

import javax.annotation.PostConstruct;

import nz.co.senanque.vaadin.application.MaduraSessionManager;
import nz.co.senanque.vaadin.tableeditor.TableEditorLayout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Roger Parkinson
 *
 */
@UIScope
@Component
public class DefaultView extends VerticalLayout implements MessageSourceAware {

	@Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired private TableEditorLayout<?> m_tableEditorLayout;
	private MessageSource m_messageSource;

    @PostConstruct
    void init() {
    	
    	final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);

    	final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        addComponent(layout);

        layout.addComponent(m_tableEditorLayout);
    }
	@Override
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}
}
