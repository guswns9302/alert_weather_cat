package com.exithere.rain.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RequiredArgsConstructor
@Component
public class RestUtils<T> {
    private final RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    public ResponseEntity<T> get(String url, HttpHeaders headers) {
        return this.callApiEndpoint(url, HttpMethod.GET, headers, null, (Class<T>)Object.class);
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<T> get(URI uri, HttpHeaders headers) {
        return this.callApiEndpoint(uri, HttpMethod.GET, headers, null, (Class<T>)Object.class);
    }

    public ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> clazz) {
        return this.callApiEndpoint(url, HttpMethod.GET, headers, null, clazz);
    }

    public ResponseEntity<T> get(URI uri, HttpHeaders headers, Class<T> clazz) {
        return this.callApiEndpoint(uri, HttpMethod.GET, headers, null, clazz);
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<T> post(String url, HttpHeaders headers, Object body) {
        return this.callApiEndpoint(url, HttpMethod.POST, headers, body, (Class<T>)Object.class);
    }

    public ResponseEntity<T> post(String url, HttpHeaders headers, Object body, Class<T> clazz) {
        return this.callApiEndpoint(url, HttpMethod.POST, headers, body, clazz);
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<T> put(String url, HttpHeaders headers, Object body) {
        return this.callApiEndpoint(url, HttpMethod.PUT, headers, body, (Class<T>)Object.class);
    }

    public ResponseEntity<T> put(String url, HttpHeaders headers, Object body, Class<T> clazz) {
        return this.callApiEndpoint(url, HttpMethod.PUT, headers, body, clazz);
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<T> delete(String url, HttpHeaders headers, Object body) {
        return this.callApiEndpoint(url, HttpMethod.DELETE, headers, body, (Class<T>)Object.class);
    }

    public ResponseEntity<T> delete(String url, HttpHeaders headers, Object body, Class<T> clazz) {
        return this.callApiEndpoint(url, HttpMethod.DELETE, headers, body, clazz);
    }

    public ResponseEntity<T> callApiEndpoint(String url, HttpMethod method, HttpHeaders headers, Object body, Class<T> clazz) {
        return restTemplate.exchange(url, method, new HttpEntity<>(body, headers), clazz);
    }

    public ResponseEntity<T> callApiEndpoint(URI uri, HttpMethod method, HttpHeaders headers, Object body, Class<T> clazz) {
        return restTemplate.exchange(uri, method, new HttpEntity<>(body, headers), clazz);
    }
}
