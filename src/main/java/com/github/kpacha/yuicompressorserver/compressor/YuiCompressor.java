package com.github.kpacha.yuicompressorserver.compressor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.AdapterFactory;
import com.github.kpacha.yuicompressorserver.adapter.CompressorAdapter;
import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.reporter.Reporter;

/**
 * Proxy for the compression adapters
 * 
 * @author kpacha
 */
public class YuiCompressor implements Compressor {

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
    public void compress(String contentType, String charset, BufferedReader in,
	    PrintWriter out, Reporter reporter) throws EvaluatorException,
	    IOException, UnknownContentTypeException {
	CompressorAdapter compressor = adapterFactory
		.getCompressorByContentType(contentType, in, reporter);
	compressor.compress(out, -1);
    }

}