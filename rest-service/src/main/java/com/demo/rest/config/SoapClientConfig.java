package com.demo.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Configuration for the SOAP client.
 *
 * This sets up the WebServiceTemplate used to communicate with the SOAP service.
 * The Jaxb2Marshaller handles serialization/deserialization of SOAP messages.
 */
@Configuration
public class SoapClientConfig {

    @Value("${soap.service.url}")
    private String soapServiceUrl;

    /**
     * Configure the JAXB2 Marshaller.
     *
     * The marshaller converts Java objects to XML (for SOAP requests)
     * and XML back to Java objects (for SOAP responses).
     *
     * It's configured to scan the generated package where JAXB classes reside.
     */
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // Package containing the JAXB-generated classes from XSD
        marshaller.setContextPath("com.demo.rest.generated");
        return marshaller;
    }

    /**
     * Configure the WebServiceTemplate.
     *
     * This is the main client for making SOAP calls.
     * It uses the marshaller for XML conversion and the configured URL.
     */
    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setDefaultUri(soapServiceUrl);
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);
        return template;
    }
}
