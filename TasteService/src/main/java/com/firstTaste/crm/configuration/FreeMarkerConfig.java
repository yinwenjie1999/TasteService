package com.firstTaste.crm.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

public class FreeMarkerConfig {
	@Autowired
    protected freemarker.template.Configuration configuration;  
    @Autowired  
    protected FreeMarkerViewResolver resolver;  
    @Autowired  
    protected InternalResourceViewResolver springResolver;
    
    @PostConstruct
    public void  setSharedVariable(){  
    	configuration.setDateFormat("yyyy/MM/dd");  
        configuration.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");  
    }
}
