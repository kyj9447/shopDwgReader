package com.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //@Value("${uploadPath}")
    //String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        String currentWorkingDir = System.getProperty("user.dir");
        //System.out.println(currentWorkingDir);

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///" + currentWorkingDir + "/");
    }
}
