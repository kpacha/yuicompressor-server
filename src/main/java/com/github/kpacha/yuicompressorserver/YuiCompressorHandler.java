package com.github.kpacha.yuicompressorserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.mozilla.javascript.EvaluatorException;

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
			String file = request.getParameter(FILES_PARAMETER);
			String type = request.getParameter(TYPE_PARAMETER);
			String input = request.getParameter(INPUT_PARAMETER);
			logger.info("File received: " + file);
			logger.info("Type: " + type);

			checkIntegrity( hasher.getHash(input, CHARSET), request.getHeader(HttpHeader.CONTENT_MD5.asString()));
			sendHeaders(request, response, type);

			String compressedOutput = compressor.compress(type, CHARSET,
					input, reporter);

			response.setHeader(HttpHeader.CONTENT_MD5.asString(),
					hasher.getHash(compressedOutput, CHARSET));
			response.getWriter().write(compressedOutput);
			response.flushBuffer();
		} catch (IllegalArgumentException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.warn(e.getMessage());
			response.getWriter().print(e.getMessage());
		} catch (EvaluatorException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.warn(e.getMessage());
			logger.warn(reporter.getReport());
			response.getWriter().print(reporter.getReport());
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(
					request.getParameter(FILES_PARAMETER) + " has failed: "
							+ reporter.getReport() + e.getMessage());
			logger.warn(e.getMessage());
		}
	}

	private void checkIntegrity(String md5_input, String md5Header) {
		if( !md5_input.equals(md5Header))
		{
			throw new IllegalArgumentException(MD5_HEADER_NOT_SET);
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
