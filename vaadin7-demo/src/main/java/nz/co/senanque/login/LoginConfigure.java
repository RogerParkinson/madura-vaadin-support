package nz.co.senanque.login;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author Roger Parkinson
 *
 */
@Configuration
@ComponentScan("nz.co.senanque.login")
public class LoginConfigure {

	@Autowired LoginParams loginParams;


	@Bean
	public MethodInvokingFactoryBean methodInvokingFactoryBean() {
	    MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
	    methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
	    methodInvokingFactoryBean.setTargetMethod("setStrategyName");
	    methodInvokingFactoryBean.setArguments(new String[]{SecurityContextHolder.MODE_INHERITABLETHREADLOCAL});
	    return methodInvokingFactoryBean;
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
	    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
	    Resource resource = new ClassPathResource("public.txt");
	    String publicKey = null;
	    try {
	        publicKey = IOUtils.toString(resource.getInputStream());
	    } catch (final IOException e) {
	        throw new RuntimeException(e);
	    }
	    converter.setVerifierKey(publicKey);
	    return converter;
	}
	
	@Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
 
    
}
