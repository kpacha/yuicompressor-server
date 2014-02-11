package com.github.softonic.yuicompressorserver.compressor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.mozilla.javascript.EvaluatorException;

import com.github.softonic.yuicompressorserver.adapter.AdapterFactory;
import com.github.softonic.yuicompressorserver.adapter.CompressorAdapter;
import com.github.softonic.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.softonic.yuicompressorserver.reporter.Reporter;

/**
 * Proxy for the compression adapters
 * 
 * @author kpacha
 */
public class YuiCompressor extends Compressor {

    private static final int LINE_BREAK = -1;
    private AdapterFactory adapterFactory;

    /**
     * The default constructor
     * 
     * @param adapterFactory
     */
    public YuiCompressor(AdapterFactory adapterFactory) {
	this.adapterFactory = adapterFactory;
    }

    /**
     * Ask for the right adapter and delegate the compression to the returned
     * one
     */
    public String compress(String contentType, String charset, String in,
	    Reporter reporter) throws EvaluatorException, IOException,
	    UnknownContentTypeException {
	return getCompressedOutput(getAdapter(contentType, in, reporter));
    }

    private String getCompressedOutput(CompressorAdapter adapter)
	    throws IOException {
	StringWriter writer = new StringWriter();
	adapter.compress(writer, LINE_BREAK);
	return writer.toString();
    }

    private CompressorAdapter getAdapter(String contentType, String in,
	    Reporter reporter) throws IOException, UnknownContentTypeException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(
		new ByteArrayInputStream(in.getBytes())));
	return adapterFactory.getCompressorByContentType(contentType, reader,
		reporter);
    }

}
