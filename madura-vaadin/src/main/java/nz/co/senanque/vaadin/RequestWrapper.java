package nz.co.senanque.vaadin;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;

/**
 * @author Roger Parkinson
 *
 */
public class RequestWrapper {
	
	public static boolean isMobile() {
		VaadinRequest vaadinRequest = VaadinService.getCurrentRequest();

        boolean mobileUserAgent = vaadinRequest.getHeader("user-agent")
                .toLowerCase().contains("mobile");
        return mobileUserAgent;
	}
	
}
