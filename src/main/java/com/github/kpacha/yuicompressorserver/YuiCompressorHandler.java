package com.github.kpacha.yuicompressorserver;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.compressor.Compressor;
import com.github.kpacha.yuicompressorserver.reporter.Reporter;
import com.github.kpacha.yuicompressorserver.reporter.YuiErrorReporter;
import com.github.kpacha.yuicompressorserver.utils.Md5Hasher;

/**
 * The yuicompressor jetty-based servlet handler
 * 
 * It just deals with the request and response objects and delegates the
 * compression stuff to the injected compressor
 * 
 * @author kpacha
 */
public class YuiCompressorHandler extends AbstractHandler {
	private static final String FILES_PARAMETER = "files";
	private static final String TYPE_PARAMETER = "type";
	private static final String INPUT_PARAMETER = "input";
	private static Logger logger = Logger.getLogger(YuiCompressorHandler.class);
	private static final String CHARSET = "UTF-8";
	private static final String MD5_HEADER_NOT_SET = "Md5 header not setted in the header";
	private static final String MD5_HEADER_NOT_EQUAL = "Md5 header is not consistent";

	private Compressor compressor;
	private Md5Hasher hasher;

	/**
	 * Default constructor.
	 * 
	 * @param compressor
	 * @param hasher
	 */
	public YuiCompressorHandler(Compressor compressor, Md5Hasher hasher) {
		this.compressor = compressor;
		this.hasher = hasher;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jetty.server.Handler#handle(java.lang.String,
	 * org.eclipse.jetty.server.Request, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		baseRequest.setHandled(true);
		Reporter reporter = new YuiErrorReporter();
		try {
			doHandle(request, response, reporter);
		} catch (IllegalArgumentException e) {
			handleException(request, response, e);
		} catch (EvaluatorException e) {
			handleException(request, response, e, reporter);
		} catch (Exception e) {
			handleException(request, response, e);
		}
	}

	private void handleException(HttpServletRequest request,
			HttpServletResponse response, Exception e, Reporter reporter)
			throws IOException {
		handleException(request, response, e);
		logger.warn(reporter.getReport());
		response.getWriter().print(reporter.getReport());
	}

	private void handleException(HttpServletRequest request,
			HttpServletResponse response, Exception e) throws IOException {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().print(
				request.getParameter(FILES_PARAMETER) + " has failed: "
						+ e.getMessage());
		logger.warn(e.getMessage());
	}

	private void doHandle(HttpServletRequest request,
			HttpServletResponse response, Reporter reporter)
			throws IOException, NoSuchAlgorithmException,
			UnknownContentTypeException {
		String file = request.getParameter(FILES_PARAMETER);
		String type = request.getParameter(TYPE_PARAMETER);
		String input = request.getParameter(INPUT_PARAMETER);
		logger.info("File received: " + file);
		logger.info("Type: " + type);

		checkIntegrity(hasher.getHash(input, CHARSET),
				request.getHeader(HttpHeader.CONTENT_MD5.asString()));
		
		String compressedOutput = compressor.compress(type, CHARSET, input,
				reporter);
		sendHeaders(request, response, type);
		setResponse(response, compressedOutput);
	}

	private void setResponse(HttpServletResponse response,
			String compressedOutput) throws IOException,
			NoSuchAlgorithmException {
		response.setHeader(HttpHeader.CONTENT_MD5.asString(),
				hasher.getHash(compressedOutput, CHARSET));
		response.getWriter().write(compressedOutput);
		response.flushBuffer();
	}

	private void checkIntegrity(String md5_input, String md5Header) {
		if (md5Header == null) {
			throw new IllegalArgumentException(MD5_HEADER_NOT_SET);
		} else if (!md5Header.equals(md5_input)) {
			logger.info(md5_input + " is not equals to header-md5: "
					+ md5Header);
			throw new IllegalArgumentException(MD5_HEADER_NOT_EQUAL);
		}
	}

	private void sendHeaders(HttpServletRequest request,
			HttpServletResponse response, String type) {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding(CHARSET);
		response.setContentType(getContentType(type));
		long now = System.currentTimeMillis();
		response.setDateHeader(HttpHeader.DATE.asString(), now);
		if (isCacheable(request)) {
			response.setHeader(HttpHeader.CACHE_CONTROL.asString(),
					"public, max-age=31536000");
			response.setDateHeader(HttpHeader.EXPIRES.asString(),
					now + 31536000000L);
		}
	}

	private String getContentType(String type) {
		if (type.equals("js")) {
			type = "javascript";
		}

		return "text/" + type;
	}

	private boolean isCacheable(HttpServletRequest request) {
		return request.getMethod().equals("GET")
				|| request.getMethod().equals("POST");
	}
}
