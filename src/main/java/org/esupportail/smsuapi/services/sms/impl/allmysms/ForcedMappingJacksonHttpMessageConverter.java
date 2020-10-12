package org.esupportail.smsuapi.services.sms.impl.allmysms;

import java.util.Arrays;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;

/* needed until allmysms correctly set it's response Content-Type to application/json instead of text/html */
public class ForcedMappingJacksonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public ForcedMappingJacksonHttpMessageConverter() {
        setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_HTML));
    }    
}
