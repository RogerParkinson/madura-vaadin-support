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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Roger Parkinson
 *
 */
@SpringView(name = DefaultView.VIEW_NAME)
public class DefaultView extends VerticalLayout implements View, MessageSourceAware {
    public static final String VIEW_NAME = "";
    @Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired private TableEditorLayout<?> m_tableEditorLayout;
	private MessageSource m_messageSource;

    /*
     * Defines the form, buttons and their connections to Madura
     * At this point we don't have an actual data object, so all this gets done once
     * and will not need redoing if we add or change a data object.
     */
    @PostConstruct
    void init() {
    	
    	final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);

    	final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        addComponent(layout);

        layout.addComponent(m_tableEditorLayout);
        HorizontalLayout buttons = new HorizontalLayout();
        layout.addComponent(buttons);
        layout.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);
        Button button = new Button(messageSourceAccessor.getMessage("logout"));
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                logout();
            }
        });
        buttons.addComponent(button);
		
    }
    private void logout() {
    	VaadinService.getCurrentRequest().getWrappedSession().invalidate();
    	getUI().close();
        String contextPath = VaadinService.getCurrentRequest().getContextPath();
        getUI().getPage().setLocation(contextPath);
    }
    /* 
     * This is where we establish the actual person object. 
     * We just get it from the UI object and assume to knows how to supply it(non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
    }
	@Override
	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}
}
