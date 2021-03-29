package org.apromore.plugin.portal.config;

import java.util.ArrayList;
import java.util.List;

import org.apromore.editor.server.AlternativesRenderer;
import org.apromore.editor.server.TemporaryFileServlet;
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.plugin.portal.bpmneditor.PluginsHandlerServlet;
import org.apromore.portal.servlet.PortalPluginResourceServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BPMNEditorConfig {
	
	@Bean
	public ServletRegistrationBean bpmnServletBean() {
		ServletRegistrationBean bean = new ServletRegistrationBean(new PluginsHandlerServlet(),
				"/bpmneditor/bpmneditor_plugins");
		bean.setLoadOnStartup(1);
		return bean;
	}
	
	@Bean
	public ServletRegistrationBean pdfServlet() {
		ServletRegistrationBean bean = new ServletRegistrationBean(new AlternativesRenderer(),
				"/bpmneditor/editor/pdf");
		bean.setLoadOnStartup(1);
		return bean;
	}
	
	@Bean
	public ServletRegistrationBean tmpFileServlet() {
		ServletRegistrationBean bean = new ServletRegistrationBean(new TemporaryFileServlet(),
				"/tmp/*");
		bean.setLoadOnStartup(1);
		return bean;
	}
		
}
