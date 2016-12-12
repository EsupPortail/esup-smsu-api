package org.esupportail.smsuapi.services.sms.impl.allmysms;

import java.util.Arrays;

import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.MediaType;

/* needed until allmysms correctly set it's response Content-Type to application/json instead of text/html */
public class ForcedMappingJacksonHttpMessageConverter extends MappingJacksonHttpMessageConverter {

    public ForcedMappingJacksonHttpMessageConverter() {
        setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_HTML));
    }    
}
