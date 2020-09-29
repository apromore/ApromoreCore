package org.apromore.integration.config;

import org.apromore.apmlog.impl.APMLogServiceImpl;
import org.apromore.common.ConfigBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

@Configuration
@ImportResource(value = {"classpath:META-INF/spring/calender-service.xml",
    "classpath:META-INF/spring/managerContext-jpa.xml"})
public class TestConfig {
  
  
  @Bean
  public static PropertyPlaceholderConfigurer properties() {
      PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
      Resource[] resources = new ClassPathResource[] { new ClassPathResource("database/test-config.properties") };
      ppc.setLocations(resources);
      ppc.setIgnoreUnresolvablePlaceholders(true);
      return ppc;
  }
  
  @Bean
  public APMLogServiceImpl apmLogService() {
      return new APMLogServiceImpl();
  }

  @Bean
  public ConfigBean config() {
      return new ConfigBean();
  }
  
}
