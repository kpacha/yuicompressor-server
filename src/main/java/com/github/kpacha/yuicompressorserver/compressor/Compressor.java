package com.github.kpacha.yuicompressorserver.compressor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.reporter.Reporter;

/**
 * The compressor interface
 * 
 * @author kpacha
 */
abstract public class Compressor {

    byte[] content;

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
    public void compress(String contentType, String charset, InputStream in,
	    PrintWriter out, Reporter reporter) throws EvaluatorException,
	    IOException, UnknownContentTypeException {
	content = getContent(in);
	compress(contentType, charset, getBufferedReader(), out, reporter);
    }

    abstract public void compress(String contentType, String charset,
	    BufferedReader in, PrintWriter out, Reporter reporter)
	    throws EvaluatorException, IOException, UnknownContentTypeException;

    protected BufferedReader getBufferedReader() throws IOException {
	return new BufferedReader(new InputStreamReader(
		new ByteArrayInputStream(content)));
    }

    private byte[] getContent(InputStream in) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	byte[] buffer = new byte[1024];
	int len;
	while ((len = in.read(buffer)) > -1) {
	    baos.write(buffer, 0, len);
	}
	baos.flush();

	return baos.toByteArray();
    }
}
