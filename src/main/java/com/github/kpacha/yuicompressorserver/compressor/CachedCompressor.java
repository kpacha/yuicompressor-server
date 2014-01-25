package com.github.kpacha.yuicompressorserver.compressor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;

import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.reporter.Reporter;
import com.github.kpacha.yuicompressorserver.utils.BufferedContentHasher;

/**
 * In-memory cache for compression requests
 * 
 * @author kpacha
 */
public class CachedCompressor implements Compressor {

    private Compressor actualCompressor;
    private ConcurrentHashMap<String, String> cache;
    private BufferedContentHasher hasher;

    /**
     * The default constructor
     * 
     * @param actualCompressor
     * @param bufferedContentHasher
     */
    public CachedCompressor(Compressor actualCompressor,
	    BufferedContentHasher bufferedContentHasher) {
	this.actualCompressor = actualCompressor;
	cache = new ConcurrentHashMap<String, String>();
	hasher = bufferedContentHasher;
    }

    /**
     * If the request is cached get its value. If it's not cached, delegate the
     * compression and cache the result. Finally, write the compressed response
     * into the PrintWriter.
     */
    public void compress(String contentType, String charset, BufferedReader in,
	    PrintWriter out, Reporter reporter) throws EvaluatorException,
	    IOException, UnknownContentTypeException {
	String hash = hasher.getHash(in, charset);
	String compressedOutput = cache.get(hash);
	if (compressedOutput == null) {
	    compressedOutput = getCompressedOutput(contentType, charset, in,
		    reporter);
	    cache.put(hash, compressedOutput);
	}
	out.write(compressedOutput);
    }

    private String getCompressedOutput(String contentType, String charset,
	    BufferedReader in, Reporter reporter) throws IOException,
	    UnknownContentTypeException {
	StringWriter sw = new StringWriter();
	actualCompressor.compress(contentType, charset, in,
		new PrintWriter(sw), reporter);
	return sw.getBuffer().toString();
    }
}
