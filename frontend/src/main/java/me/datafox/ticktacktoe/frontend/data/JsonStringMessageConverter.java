package me.datafox.ticktacktoe.frontend.data;

import org.springframework.messaging.converter.AbstractJsonMessageConverter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

/**
 * @author datafox
 */
public class JsonStringMessageConverter extends AbstractJsonMessageConverter {
    @Override
    protected Object fromJson(Reader reader, Type resolvedType) {
        try {
            char[] arr = new char[8192];
            StringBuilder buffer = new StringBuilder();
            int read;
            while((read = reader.read(arr, 0, arr.length)) != -1) {
                buffer.append(arr, 0, read);
            }
            reader.close();
            return buffer.toString();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Object fromJson(String payload, Type resolvedType) {
        return payload;
    }

    @Override
    protected void toJson(Object payload, Type resolvedType, Writer writer) {
        try {
            writer.write((String) payload);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String toJson(Object payload, Type resolvedType) {
        return (String) payload;
    }
}
