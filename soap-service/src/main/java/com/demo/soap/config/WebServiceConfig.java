package com.demo.soap.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * Configuration for Spring Web Services (SOAP).
 *
 * This configuration:
 * 1. Registers the MessageDispatcherServlet at /ws/*
 * 2. Exposes the WSDL at /ws/orders.wsdl
 * 3. Configures the XSD schema for JAXB binding
 */
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    public static final String NAMESPACE_URI = "http://demo.com/soap/order";

    /**
     * Register the MessageDispatcherServlet.
     * This servlet handles all SOAP requests.
     */
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    /**
     * Generate WSDL from XSD schema.
     * The WSDL will be available at: http://localhost:8081/ws/orders.wsdl
     *
     * Spring WS automatically generates the WSDL based on:
     * - The XSD schema (defines message types)
     * - Request/Response naming convention (*Request -> *Response)
     */
    @Bean(name = "orders")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema ordersSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("OrdersPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(NAMESPACE_URI);
        wsdl11Definition.setSchema(ordersSchema);
        return wsdl11Definition;
    }

    /**
     * Load the XSD schema from classpath.
     */
    @Bean
    public XsdSchema ordersSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/order.xsd"));
    }
}
