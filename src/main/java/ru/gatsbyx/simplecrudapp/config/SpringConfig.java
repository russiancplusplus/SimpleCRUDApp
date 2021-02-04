package ru.gatsbyx.simplecrudapp.config;

import org.springframework.context.ApplicationContext;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@Configuration
@Configurable
@ComponentScan("ru.gatsbyx.simplecrudapp") // base-package
@EnableWebMvc // mvc::annotation-driven
public class SpringConfig implements WebMvcConfigurer {
	private final ApplicationContext applicationContext;
	
	@Autowired
	public SpringConfig(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	/* 
	<bean id="templateResolver" class="org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver">
	    <property name="prefix" value="/WEB-INF/views/"/>
	    <property name="suffix" value=".html"/>
    </bean> */ 
	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(applicationContext);
		templateResolver.setPrefix("/WEB-INF/views/");
		templateResolver.setSuffix(".html");
		return templateResolver;
	}
	
	/*
	<bean id="templateEngine" class="org.thymeleaf.spring5.SpringTemplateEngine">
        <property name="templateResolver" ref="templateResolver"/>
        <property name="enableSpringELCompiler" value="true"/>
    </bean> */
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);
		return templateEngine;
	}
	
	/*
    <bean class="org.thymeleaf.spring5.view.ThymeleafViewResolver">
        <property name="templateEngine" ref="templateEngine"/>
        <property name="order" value="1"/>
        <property name="viewNames" value="*"/>
    </bean> */
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		registry.viewResolver(resolver);
	}
	
    @Bean 
    public static PropertySourcesPlaceholderConfigurer configurer() { 
    	 PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
         ppc.setLocation(new ClassPathResource("db.properties"));
         return ppc; 
    } 
    
    @Bean
    public DataSource dataSource(String host, String login,
    		String password) {
    	DriverManagerDataSource dataSource = new DriverManagerDataSource();
    	
    	dataSource.setDriverClassName("org.postgresql.Driver");
    	dataSource.setUrl(host);
    	dataSource.setUsername(login);
    	dataSource.setPassword(password);
    	
    	return dataSource;
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate(@Value("${host}") String host, @Value("${login}") String login,
    		@Value("${password}") String password) {
    	return new JdbcTemplate(dataSource(host, login, password));
    }
	
}
