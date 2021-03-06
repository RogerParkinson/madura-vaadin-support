package nz.co.senanque.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired AuthenticationSuccessHandler authenticationSuccessHandler;
	@Autowired UserDetailsService userDetailsService;
	
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }
    @Bean(name = "default-users")
    public PropertiesFactoryBean defaultUsers() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("default-users.properties"));
        return bean;
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
