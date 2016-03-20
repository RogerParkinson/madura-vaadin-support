/*******************************************************************************
 * Copyright (c)2015 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.vaadin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

/**
 * @author Roger Parkinson
 *
 */
@Component("applicationVersion")
public class ApplicationVersionFactory implements FactoryBean<String>,ServletContextAware {
	
	 String name = "/ApplicationVersion.properties";
	 Properties m_props = new Properties();
	private ServletContext m_servletContext;

	public ApplicationVersionFactory() {
	}

	@PostConstruct
	public void init() throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream(name);
		if (is == null && m_servletContext != null) {
			is = m_servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");
		}
		if (is == null) {
			return;
		}
		m_props.load(is);
	}
	
	@Override
	public String getObject() throws Exception {
		if (m_props.isEmpty()) {
			return "No version number available";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(m_props.getProperty("Implementation-Title"));
		sb.append("-");
		sb.append(m_props.getProperty("Implementation-Version"));
		sb.append(" ");
		sb.append(m_props.getProperty("implementation-build"));
		return sb.toString();
	}

	@Override
	public Class<?> getObjectType() {
		return String.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		m_servletContext = servletContext;
		
	}


}
