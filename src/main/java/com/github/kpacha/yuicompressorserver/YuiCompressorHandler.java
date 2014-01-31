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
import com.github.kpacha.yuicompressorserver.utils.BufferedContentHasher;

/**
 * The yuicompressor jetty-based servlet handler
 * 
 * It just deals with the request and response objects and delegates the
 * compression stuff to the injected compressor
 * 
 * @author kpacha
 */
public class YuiCompressorHandler extends AbstractHandler {
    private static Logger logger = Logger.getLogger(YuiCompressorHandler.class);

    private Compressor compressor;
    private BufferedContentHasher hasher;

    /**
     * Default constructor.
     * 
     * @param compressor
     * @param hasher
     */
    public YuiCompressorHandler(Compressor compressor,
	    BufferedContentHasher hasher) {
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
	    sendHeaders(request, response);
	    String charset = request.getCharacterEncoding();
	    String compressedOutput = compressor.compress(
		    request.getContentType(), charset, request.getReader(),
		    reporter);
	    response.setHeader(HttpHeader.CONTENT_MD5.asString(),
		    hasher.getHash(compressedOutput, charset));
	    response.getWriter().write(compressedOutput);
	    response.flushBuffer();
	} catch (EvaluatorException e) {
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    response.getWriter().print(reporter.getReport());
	    logger.warn(e.getMessage());
	    logger.warn(reporter.getReport());
	} catch (Exception e) {
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    response.getWriter().print(e.getMessage());
	    logger.warn(e.getMessage());
	}
    }

    private void sendHeaders(HttpServletRequest request,
	    HttpServletResponse response) {
	response.setStatus(HttpServletResponse.SC_OK);
	response.setCharacterEncoding(request.getCharacterEncoding());
	response.setContentType(request.getContentType());
	long now = System.currentTimeMillis();
	response.setDateHeader(HttpHeader.DATE.asString(), now);
	if (isCacheable(request)) {
	    response.setHeader(HttpHeader.CACHE_CONTROL.asString(),
		    "public, max-age=31536000");
	    response.setDateHeader(HttpHeader.EXPIRES.asString(),
		    now + 31536000000L);
	}
    }

    private boolean isCacheable(HttpServletRequest request) {
	return request.getMethod().equals("GET")
		|| request.getMethod().equals("POST");
    }
}
