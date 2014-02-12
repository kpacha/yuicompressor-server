package com.github.softonic.yuicompressorserver.adapter;

import java.io.BufferedReader;
import java.io.IOException;

import com.github.softonic.yuicompressorserver.reporter.Reporter;

/**
 * Simple factory for the compressor adapter package
 * 
 * @author kpacha
 */
public class AdapterFactory {

    private static final String CSS = "css";
    private static final String JS = "js";

    public CompressorAdapter getCompressorByContentType(String contentType,
	    BufferedReader in, Reporter reporter) throws IOException,
	    UnknownContentTypeException {
	if (contentType.equals(CSS)) {
	    return new CssCompressorAdapter(in);
	} else if (contentType.equals(JS)) {
	    return new JavaScriptCompressorAdapter(in, reporter);
	}
	throw new UnknownContentTypeException(contentType);
    }
}
