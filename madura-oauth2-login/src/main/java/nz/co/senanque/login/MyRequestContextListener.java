package nz.co.senanque.login;

import javax.servlet.ServletRequestEvent;
import javax.servlet.annotation.WebListener;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
@WebListener
public class MyRequestContextListener extends RequestContextListener {
	

	public void requestInitialized(ServletRequestEvent requestEvent) {
		super.requestInitialized(requestEvent);
	}
}