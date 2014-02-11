package com.github.kpacha.yuicompressorserver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
	private String type;
	private String files;
	private String md5_hash;

	public void setUp() throws IOException {
		contentType = "text/javascript";
		encoding = "UTF-8";
		response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(mock(PrintWriter.class));

		input = "some uncompressed input";
		type = "js";
		files = "i18n.js";
		md5_hash = "19223c102370329f6ccc83e5c53d05f4";
	}

	public void testHandleGetOk() throws IOException, ServletException,
			UnknownContentTypeException, NoSuchAlgorithmException {
		httpMethod = "GET";
		doTestHandleOk();
		verifyCacheHeaders();
	}

	public void testHandleCssOk() throws IOException, ServletException,
			UnknownContentTypeException, NoSuchAlgorithmException {
		httpMethod = "GET";
		type = "css";
		contentType = "text/css";
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
			ServletException, UnknownContentTypeException, NoSuchAlgorithmException {
		httpMethod = "GET";
		String message = "some exception message";
		Compressor compressor = mock(Compressor.class);
		when(compressor.compress(
				(String) any(), (String) any(), (String) any(), (YuiErrorReporter) any())).thenThrow(new EvaluatorException(message));

		YuiCompressorHandler handler = new YuiCompressorHandler(compressor,
				getHasher());
		handler.handle("/", mock(Request.class), mockRequest(), response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	public void testHandleContentTypeException() throws IOException,
			ServletException, UnknownContentTypeException, NoSuchAlgorithmException {
		httpMethod = "GET";
		type = "unknown";
		String message = "some exception message";
		Compressor compressor = mock(Compressor.class);
		doThrow(new UnknownContentTypeException(message)).when(compressor)
				.compress(eq(type), eq(encoding), eq(input),
						(YuiErrorReporter) any());

		YuiCompressorHandler handler = new YuiCompressorHandler(compressor,
				getHasher());
		handler.handle("/", mock(Request.class), mockRequest(), response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}

	public void testMd5HeaderNotSetBadParameterIsShown() throws IOException,
			ServletException, UnknownContentTypeException,
			NoSuchAlgorithmException {
		httpMethod = "GET";
		String message = files + " has failed: Md5 header not setted in the header";
		Compressor compressor = mock(Compressor.class);
		HttpServletRequest request = mockRequest();
		when(request.getHeader(HttpHeader.CONTENT_MD5.asString())).thenReturn(null);

		YuiCompressorHandler handler = new YuiCompressorHandler(compressor,
				getHasher());
		handler.handle("/", mock(Request.class), request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		verify(response.getWriter()).print(message);
	}

	public void testMd5HeaderNotConsistentBadParameterIsShown() throws IOException,
			ServletException, UnknownContentTypeException,
			NoSuchAlgorithmException {
		httpMethod = "GET";
		String message = files + " has failed: Md5 header is not consistent";
		Compressor compressor = mock(Compressor.class);
		HttpServletRequest request = mockRequest();
		when(request.getHeader(HttpHeader.CONTENT_MD5.asString())).thenReturn("badmd5");

		YuiCompressorHandler handler = new YuiCompressorHandler(compressor,
				getHasher());
		handler.handle("/", mock(Request.class), request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		verify(response.getWriter()).print( message );
	}

	private Md5Hasher getHasher() throws IOException, NoSuchAlgorithmException {
		Md5Hasher hasher = mock(Md5Hasher.class);
		when(hasher.getHash(input, encoding)).thenReturn(md5_hash);
		return hasher;
	}

	private void doTestHandleOk() throws IOException, ServletException,
			UnknownContentTypeException, NoSuchAlgorithmException {
		String output = "some compressed output";

		Compressor compressor = mock(Compressor.class);
		when(
				compressor.compress((String) any(), (String) any(), eq(input),
						(YuiErrorReporter) any())).thenReturn(output);

		YuiCompressorHandler handler = new YuiCompressorHandler(compressor,
				getHasher());
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
		when(request.getParameter("type")).thenReturn(type);
		when(request.getParameter("files")).thenReturn(files);
		when(request.getParameter("input")).thenReturn(input);
		when(request.getHeader(HttpHeader.CONTENT_MD5.asString())).thenReturn(md5_hash);
		return request;
	}
}
