package org.apromore.plugin.portal.config;

import org.apromore.plugin.portal.loganimation.LogAnimationPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogAnimationConfig {

	@Bean
	public LogAnimationPlugin logAnimationPluginCe() {
		LogAnimationPlugin lgPlugin = new LogAnimationPlugin();
		lgPlugin.setGroupLabel("Analyze");
		lgPlugin.setLabel("Animate logs CE");
		return lgPlugin;
	}



}
