package nz.co.senanque.maduramobiledemo.touchkit;

import javax.annotation.PostConstruct;

import nz.co.senanque.vaadin.MaduraSessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.spring.annotation.UIScope;

@SuppressWarnings("serial")
@UIScope
@Component("menu-view")
public class MenuView extends NavigationView {
	
	@Autowired private MaduraSessionManager m_maduraSessionManager;
	@Autowired private FormView m_formView;

    public MenuView() {
    }
	@PostConstruct
	public void init() {
        setCaption("Menu");

        final VerticalComponentGroup content = new VerticalComponentGroup();
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_maduraSessionManager.getMessageSource());
        NavigationButton button = new NavigationButton(messageSourceAccessor.getMessage("Form"));
        button.addClickListener(new NavigationButtonClickListener() {
            @Override
            public void buttonClick(NavigationButtonClickEvent event) {
                getNavigationManager().navigateTo(m_formView);
            }
        });
        content.addComponent(button);
        setContent(content);
    }

	public MaduraSessionManager getMaduraSessionManager() {
		return m_maduraSessionManager;
	}

	public void setMaduraSessionManager(MaduraSessionManager maduraSessionManager) {
		m_maduraSessionManager = maduraSessionManager;
	};
}
