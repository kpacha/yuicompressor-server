/**
 * 
 */
package com.github.kpacha.yuicompressorserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.compressor.Compressor;
import com.github.kpacha.yuicompressorserver.reporter.Reporter;
import com.github.kpacha.yuicompressorserver.reporter.YuiErrorReporter;

/**
 * The yuicompressor jetty-based servlet handler
 * 
 * It just deals with the request and response objects and delegates the
 * compression stuff to the injected compressor
 * 
 * @author kpacha
 */
public class YuiCompressorHandler extends AbstractHandler {

    private Compressor compressor;

    /**
     * Default constructor.
     * 
     * @param compressor
     */
    public YuiCompressorHandler(Compressor compressor) {
	this.compressor = compressor;
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
	    compressor.compress(request.getContentType(),
		    request.getCharacterEncoding(), request.getInputStream(),
		    response.getWriter(), reporter);
	} catch (EvaluatorException e) {
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    response.getWriter().print(reporter.getReport());
	} catch (Exception e) {
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    response.getWriter().print(e.getMessage());
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
