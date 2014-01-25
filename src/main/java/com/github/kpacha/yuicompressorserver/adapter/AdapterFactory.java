package com.github.kpacha.yuicompressorserver.adapter;

import java.io.BufferedReader;
import java.io.IOException;

import com.github.kpacha.yuicompressorserver.reporter.Reporter;

/**
 * Simple factory for the compressor adapter package
 * 
 * @author kpacha
 */
public class AdapterFactory {

    private static final String CSS = "text/css";
    private static final String JS = "text/javascript";

    public CompressorAdapter getCompressorByContentType(String contentType,
	    BufferedReader in, Reporter reporter) throws IOException,
	    UnknownContentTypeException {
	if (contentType.startsWith(CSS)) {
	    return new CssCompressorAdapter(in);
	} else if (contentType.startsWith(JS)) {
	    return new JavaScriptCompressorAdapter(in, reporter);
	}
	throw new UnknownContentTypeException(contentType);
    }
}
