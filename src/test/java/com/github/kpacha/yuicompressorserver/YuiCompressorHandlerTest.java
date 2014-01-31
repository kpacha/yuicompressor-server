package com.github.kpacha.yuicompressorserver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.compressor.Compressor;
import com.github.kpacha.yuicompressorserver.reporter.YuiErrorReporter;
import com.github.kpacha.yuicompressorserver.utils.Md5Hasher;

public class YuiCompressorHandlerTest extends TestCase {
    private String contentType;
    private String encoding;
    private String httpMethod;
    private HttpServletResponse response;
    private String input;
    private BufferedReader bufferedReader;

    public void setUp() throws IOException {
	contentType = "text/css";
	encoding = "UTF-8";
	response = mock(HttpServletResponse.class);
	when(response.getWriter()).thenReturn(mock(PrintWriter.class));

	input = "some uncompressed input";
	bufferedReader = new BufferedReader(new InputStreamReader(
		new ByteArrayInputStream(input.getBytes())));
    }

    public void testHandleGetOk() throws IOException, ServletException,
	    UnknownContentTypeException, NoSuchAlgorithmException {
	httpMethod = "GET";
	doTestHandleOk();
	verifyCacheHeaders();
    }

    public void testHandlePostOk() throws IOException, ServletException,
	    UnknownContentTypeException, NoSuchAlgorithmException {
	httpMethod = "POST";
	doTestHandleOk();
	verifyCacheHeaders();
    }

    public void testHandleNonCacheableOk() throws EvaluatorException,
	    IOException, ServletException, UnknownContentTypeException,
	    NoSuchAlgorithmException {
	httpMethod = "PUT";
	doTestHandleOk();
    }

    public void testHandleEvaluatorException() throws IOException,
	    ServletException, UnknownContentTypeException {
	httpMethod = "GET";
	String message = "some exception message";
	Compressor compressor = mock(Compressor.class);
	doThrow(new EvaluatorException(message)).when(compressor).compress(
		eq(contentType), eq(encoding), eq(bufferedReader),
		(YuiErrorReporter) any());

	YuiCompressorHandler handler = new YuiCompressorHandler(compressor,
		mock(Md5Hasher.class));
	handler.handle("/", mock(Request.class), mockRequest(), response);

	verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    public void testHandleContentTypeException() throws IOException,
	    ServletException, UnknownContentTypeException {
	httpMethod = "GET";
	contentType = "unknown";
	String message = "some exception message";
	Compressor compressor = mock(Compressor.class);
	doThrow(new UnknownContentTypeException(message)).when(compressor)
		.compress(eq(contentType), eq(encoding), eq(bufferedReader),
			(YuiErrorReporter) any());

	YuiCompressorHandler handler = new YuiCompressorHandler(compressor,
		mock(Md5Hasher.class));
	handler.handle("/", mock(Request.class), mockRequest(), response);

	verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    private void doTestHandleOk() throws IOException, ServletException,
	    UnknownContentTypeException, NoSuchAlgorithmException {
	String output = "some compressed output";

	Compressor compressor = mock(Compressor.class);
	when(
		compressor.compress((String) any(), (String) any(),
			eq(bufferedReader), (YuiErrorReporter) any()))
		.thenReturn(output);

	YuiCompressorHandler handler = new YuiCompressorHandler(compressor,
		new Md5Hasher());
	handler.handle("/", mock(Request.class), mockRequest(), response);

	verify(response).setStatus(HttpServletResponse.SC_OK);
	verify(response).setCharacterEncoding(encoding);
	verify(response).setContentType(contentType);
	verify(response).setDateHeader(eq(HttpHeader.DATE.asString()),
		anyLong());
    }

    private void verifyCacheHeaders() {
	verify(response).setHeader(eq(HttpHeader.CACHE_CONTROL.asString()),
		eq("public, max-age=31536000"));
	verify(response).setDateHeader(eq(HttpHeader.EXPIRES.asString()),
		anyLong());
    }

    private HttpServletRequest mockRequest() throws IOException {
	HttpServletRequest request = mock(HttpServletRequest.class);
	when(request.getContentType()).thenReturn(contentType);
	when(request.getCharacterEncoding()).thenReturn(encoding);
	when(request.getMethod()).thenReturn(httpMethod);
	when(request.getReader()).thenReturn(bufferedReader);
	return request;
    }
}
