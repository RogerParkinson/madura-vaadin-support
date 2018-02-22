package nz.co.senanque.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired AuthenticationSuccessHandler authenticationSuccessHandler;
	
	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
		auth.inMemoryAuthentication().withUser("user").password("user").roles("USER");
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        http
	        .csrf()
	        	.disable() // Use Vaadin's CSRF protection
	        .authorizeRequests()
	        	.antMatchers("/login-resources/**").permitAll()
	        	.anyRequest().authenticated() // User must be authenticated to access any part of the application
	        	.and()
//        	.httpBasic()
//	        	.and()
	        .formLogin()
	        	.loginPage("/login")
	        	.permitAll() // Login page is accessible to anybody
	        	.successHandler(authenticationSuccessHandler)
	        	.failureUrl("/login.jsp?error=login-failed")
	        	.and()
	        .logout()
	        	.logoutUrl("/logout")
	        	.logoutSuccessUrl("/login.jsp?msg=logged-out")
	        	.permitAll() // Logout success page is accessible to anybody
	        	.and()
	        .sessionManagement()
	        	.sessionFixation()
	        	.newSession(); // Create completely new session
	}
}
