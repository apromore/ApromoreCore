/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.config;

import javax.servlet.MultipartConfigElement;
import org.apromore.editor.server.AlternativesRenderer;
import org.apromore.editor.server.ImageToPdfRenderer;
import org.apromore.editor.server.TemporaryFileServlet;
import org.apromore.plugin.portal.bpmneditor.PluginsHandlerServlet;
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
	public ServletRegistrationBean imageToPdfServlet() {
		ServletRegistrationBean bean = new ServletRegistrationBean(new ImageToPdfRenderer(),
			"/bpmneditor/editor/pdf");
		MultipartConfigElement config = new MultipartConfigElement(
			"/tmp",
			1024 * 1024 * 50L,
			1024 * 1024 * 50L,
			1024 * 1024 * 50
		);
		bean.setMultipartConfig(config);
		bean.setLoadOnStartup(1);
		return bean;
	}

	@Bean
	public ServletRegistrationBean pdfServlet() {
		ServletRegistrationBean bean = new ServletRegistrationBean(new AlternativesRenderer(),
			"/bpmneditor/editor/svg2pdf");
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