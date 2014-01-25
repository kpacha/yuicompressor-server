package com.github.kpacha.yuicompressorserver.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.reporter.Reporter;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * Wrapper for the {#com.yahoo.platform.yui.compressor.JavaScriptCompressor}
 * 
 * @author kpacha
 */
public class JavaScriptCompressorAdapter implements CompressorAdapter {

    private JavaScriptCompressor compressor;

    public JavaScriptCompressorAdapter(BufferedReader in, Reporter reporter)
	    throws EvaluatorException, IOException {
	compressor = new JavaScriptCompressor(in, reporter);
    }

    public void compress(Writer out, int linebreak) throws IOException {
	compressor.compress(out, linebreak, true, true, false, false);
    }
}
