package com.github.kpacha.yuicompressorserver.compressor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.reporter.Reporter;

/**
 * The compressor interface
 * 
 * @author kpacha
 */
public interface Compressor {

    /**
     * Write the compressed version of the content of the BufferedReader param
     * into the received PrintWriter
     * 
     * @param contentType
     * @param charset
     * @param in
     * @param out
     * @param reporter
     * @throws EvaluatorException
     * @throws IOException
     * @throws UnknownContentTypeException
     */
    public void compress(String contentType, String charset, BufferedReader in,
	    PrintWriter out, Reporter reporter) throws EvaluatorException,
	    IOException, UnknownContentTypeException;
}
