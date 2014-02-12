package com.github.softonic.yuicompressorserver.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import com.yahoo.platform.yui.compressor.CssCompressor;

/**
 * Wrapper for the {#com.yahoo.platform.yui.compressor.CssCompressor}
 * 
 * @author kpacha
 */
public class CssCompressorAdapter implements CompressorAdapter {

    private CssCompressor compressor;

    public CssCompressorAdapter(BufferedReader in) throws IOException {
	compressor = new CssCompressor(in);
    }

    public void compress(Writer out, int linebreak) throws IOException {
	compressor.compress(out, linebreak);
    }
}