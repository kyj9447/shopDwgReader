package com.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {
    //@Value("${uploadPath}")
    //String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        String currentWorkingDir = System.getProperty("user.dir");
        log.info("currentWorkingDir " + currentWorkingDir);

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///" + currentWorkingDir + "/");
    }
}
