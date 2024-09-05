package com.arpanrec.minerva.state;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration {

    //    @Bean
//    public RequestAndResponseLoggingFilter requestResponseLoggingFilter() {
//        return new RequestAndResponseLoggingFilter();
//    }
    @Bean
    public FilterRegistrationBean<RequestAndResponseLoggingFilter> loggingFilter() {
        FilterRegistrationBean<RequestAndResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestAndResponseLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
