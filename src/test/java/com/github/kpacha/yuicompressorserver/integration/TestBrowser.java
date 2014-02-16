package com.github.kpacha.yuicompressorserver.integration;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class TestBrowser {

    private HttpClient httpClient;

    public TestBrowser() {
	httpClient = HttpClientBuilder.create().build();
    }

    public TestResponse makeRequest(String url) {
	return makeRequest(url, null);
    }

    public TestResponse makeRequest(String url, Map<String, String> headers) {
	HttpGet getRequest = prepareGetRequest(url, headers);
	return executeRequest(getRequest);
    }

    public TestResponse makePostRequestWithFormParameters(String url,
	    Map<String, String> headers, Map<String, String> formParameters) {
	try {
	    HttpPost postRequest = preparePostRequest(url, headers,
		    formParameters);
	    return executeRequest(postRequest);
	} catch (UnsupportedEncodingException e) {
	    throw new RuntimeException(e);
	}

    }

    private HttpGet prepareGetRequest(String url, Map<String, String> headers) {
	HttpGet getRequest = new HttpGet(url);
	addHeaders(headers, getRequest);
	return getRequest;
    }

    private HttpPost preparePostRequest(String url,
	    Map<String, String> headers, Map<String, String> formParameters)
	    throws UnsupportedEncodingException {
	HttpPost postRequest = new HttpPost(url);
	addHeaders(headers, postRequest);

	// add form parameters:
	List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
	if (formParameters != null) {

	    for (Entry<String, String> parameter : formParameters.entrySet()) {

		formparams.add(new BasicNameValuePair(parameter.getKey(),
			parameter.getValue()));
	    }

	}
	// encode form parameters and add
	UrlEncodedFormEntity entity;
	entity = new UrlEncodedFormEntity(formparams);

	postRequest.setEntity(entity);
	return postRequest;
    }

    private void addHeaders(Map<String, String> headers, HttpRequestBase request) {
	if (headers != null) {
	    for (Entry<String, String> header : headers.entrySet()) {
		request.addHeader(header.getKey(), header.getValue());
	    }
	}
    }

    private TestResponse executeRequest(HttpRequestBase request) {

	TestResponse response;
	try {
	    HttpResponse httpResponse = httpClient.execute(request);
	    response = new TestResponse(httpResponse);
	    request.releaseConnection();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}

	return response;

    }

    public void shutdown() {
	httpClient.getConnectionManager().shutdown();
    }
}