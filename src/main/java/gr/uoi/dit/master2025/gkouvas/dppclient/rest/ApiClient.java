package gr.uoi.dit.master2025.gkouvas.dppclient.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.http.HttpClient;

/**
 * Base REST client providing JSON serializer and HttpClient instance.
 */
public abstract class ApiClient {

    //protected static final String BASE_URL = "http://localhost:8080";
    protected static final String BASE_URL = "https://192.168.0.105:8443";

    protected final HttpClient httpClient;
    protected final ObjectMapper mapper;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder().build();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
