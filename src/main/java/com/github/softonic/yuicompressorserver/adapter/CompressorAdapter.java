package com.github.softonic.yuicompressorserver.adapter;

import java.io.IOException;
import java.io.Writer;

/**
 * Simple interface for the adapter layer. Adapters are intended to interact
 * with the compressors from the yuicompressor package
 * 
 * @author kpacha
 */
public interface CompressorAdapter {
    public void compress(Writer out, int linebreak) throws IOException;
}