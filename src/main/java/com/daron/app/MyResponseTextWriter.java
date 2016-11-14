package com.daron.app;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces(MediaType.TEXT_PLAIN)
public class MyResponseTextWriter implements MessageBodyWriter<MyResponse> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == MyResponse.class;
    }

    @Override
    public long getSize(MyResponse myBean, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(MyResponse myBean,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream)
            throws IOException, WebApplicationException {

        try {
            PrintWriter printWriter = new PrintWriter(entityStream);
            printWriter.append(myBean.toString() + "\n");
            printWriter.flush();

        } catch (Exception exc) {
            throw new ProcessingException("Error serializing a MyBean to the output stream", exc);
        }
    }
}