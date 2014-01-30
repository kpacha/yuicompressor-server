package com.github.kpacha.yuicompressorserver.compressor;

import java.io.BufferedReader;
import java.io.IOException;

import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.reporter.Reporter;

/**
 * The compressor interface
 * 
 * @author kpacha
 */
abstract public class Compressor {

    /**
     * Write the compressed version of the content of the BufferedReader param
     * into the received PrintWriter
     * 
     * @param contentType
     * @param charset
     * @param in
     * @param reporter
     * @return
     * @throws EvaluatorException
     * @throws IOException
     * @throws UnknownContentTypeException
     */
    public String compress(String contentType, String charset,
	    BufferedReader in, Reporter reporter) throws EvaluatorException,
	    IOException, UnknownContentTypeException {
	return compress(contentType, charset, getContent(in), reporter);
    }

    abstract public String compress(String contentType, String charset,
	    String in, Reporter reporter) throws EvaluatorException,
	    IOException, UnknownContentTypeException;

    private String getContent(BufferedReader in) throws IOException {
	StringBuffer srcsb = new StringBuffer();
	String line;
	while ((line = in.readLine()) != null) {
	    srcsb.append(line + "\n");
	}
	return srcsb.toString();
    }
}
