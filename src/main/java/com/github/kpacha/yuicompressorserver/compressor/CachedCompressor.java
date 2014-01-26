package com.github.kpacha.yuicompressorserver.compressor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.reporter.Reporter;
import com.github.kpacha.yuicompressorserver.utils.BufferedContentHasher;

/**
 * In-memory cache for compression requests
 * 
 * @author kpacha
 */
public class CachedCompressor extends Compressor {

    private Compressor actualCompressor;
    private Cache cache;
    private BufferedContentHasher hasher;

    /**
     * The default constructor
     * 
     * @param actualCompressor
     * @param bufferedContentHasher
     * @param cache
     */
    public CachedCompressor(Compressor actualCompressor,
	    BufferedContentHasher bufferedContentHasher, Cache cache) {
	this.actualCompressor = actualCompressor;
	this.cache = cache;
	hasher = bufferedContentHasher;
    }

    /**
     * If the request is cached get its value. If it's not cached, delegate the
     * compression and cache the result. Finally, write the compressed response
     * into the PrintWriter.
     */
    public void compress(String contentType, String charset, byte[] in,
	    PrintWriter out, Reporter reporter) throws EvaluatorException,
	    IOException, UnknownContentTypeException {
	String hash = hasher.getHash(getBufferedReader(in), charset);
	Element element = cache.get(hash);
	if (element == null) {
	    element = new Element(hash, getCompressedOutput(contentType,
		    charset, in, reporter));
	    cache.put(element);
	}
	out.write((String) element.getObjectValue());
    }

    private String getCompressedOutput(String contentType, String charset,
	    byte[] in, Reporter reporter) throws IOException,
	    UnknownContentTypeException {
	StringWriter sw = new StringWriter();
	actualCompressor.compress(contentType, charset, in,
		new PrintWriter(sw), reporter);
	return sw.getBuffer().toString();
    }
}
