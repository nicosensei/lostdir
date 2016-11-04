package com.github.nicosensei.lostdir.helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * A preobjectigured Jackson Databind {@link ObjectMapper}.
 * Offers the following three operations:
 * <ul>
 * <li>deserialization: JSON content to POJO</li>
 * <li>serialization: POJO to JSON content</li>
 * <li>conversion: POJO to generic {@link Map} (useful for Elasticsearch API calls)</li>
 * </ul>
 *
 * @author Nicolas Giraud
 */
public class GenericJsonObjectMapper<T> extends ObjectMapper {

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public GenericJsonObjectMapper() {
        super();
        setPropertyNamingStrategy(propertyNamingStrategyFactory());
        disable(SerializationFeature.INDENT_OUTPUT);
        disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
    }

    public GenericJsonObjectMapper enablePrettyPrint() {
        enable(SerializationFeature.INDENT_OUTPUT);
        return this;
    }

    public GenericJsonObjectMapper disablePrettyPrint() {
        disable(SerializationFeature.INDENT_OUTPUT);
        return this;
    }

    public final T deserialize(final InputStream in, final Class<? extends T> beanClass) throws IOException {
        return readValue(in, beanClass);
    }

    public final T deserialize(final String in, final Class<? extends T> beanClass) throws IOException {
        return readValue(in, beanClass);
    }

    public final T deserialize(final Map<String, Object> jsonAsMap, final Class<? extends T> beanClass) throws IOException {
        return deserialize(gson.toJson(jsonAsMap), beanClass);
    }

    public final Map<String, Object> asMap(final String jsonFilePath, final String encoding)
            throws IOException {
        return asMap(new InputStreamReader(new FileInputStream(jsonFilePath), encoding));
    }

    @SuppressWarnings("unchecked")
    public final Map<String, Object> asMap(final Reader reader) throws IOException {
        return readValue(reader, new TypeReference<Map<String, Object>>(){});
    }

    public final Map<String, Object> asMap(final InputStream in, final String charset) throws IOException {
        return asMap(new InputStreamReader(in, charset));
    }

    public final void serialize(final T object, final OutputStream out) throws IOException {
        writeValue(out, object);
    }

    public final void serialize(final T object, final Writer out) throws IOException {
        writeValue(out, object);
    }

    public final String asString(final T object) throws IOException {
        final StringWriter sw = new StringWriter();
        final PrintWriter out = new PrintWriter(sw);
        writeValue(out, object);
        out.close();
        return sw.toString();
    }

    public final Map<String, Object> asMap(final T object) {
        return (Map<String, Object>) convertValue(object, Map.class);
    }

    /**
     * Override to provide a custom naming convention.
     *
     * @return a new instance of {@link CamelCaseToSnakeCase}
     */
    protected PropertyNamingStrategy propertyNamingStrategyFactory() {
        return PropertyNamingStrategy.SNAKE_CASE;
    }

}
