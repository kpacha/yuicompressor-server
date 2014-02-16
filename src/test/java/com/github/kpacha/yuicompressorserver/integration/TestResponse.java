package com.github.kpacha.yuicompressorserver.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

public class TestResponse {
    private String content;
    private String md5;
    private int status;

    public TestResponse(HttpResponse httpResponse) throws IOException {
	content = getResponseContent(httpResponse);
	md5 = httpResponse.getLastHeader("Content-MD5").getValue();
	status = httpResponse.getStatusLine().getStatusCode();
    }

    public String getContent() {
	return content;
    }

    public String getMd5() {
	return md5;
    }

    public int getStatus() {
	return status;
    }

    private String getResponseContent(HttpResponse httpResponse)
	    throws IOException {

	BufferedReader br = new BufferedReader(new InputStreamReader(
		(httpResponse.getEntity().getContent())));

	String output;
	StringBuffer sb = new StringBuffer();
	while ((output = br.readLine()) != null) {
	    sb.append(output);
	}
	return sb.toString();
    }

}
