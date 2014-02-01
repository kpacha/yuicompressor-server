package com.github.kpacha.yuicompressorserver.compressor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.reporter.Reporter;
import com.github.kpacha.yuicompressorserver.utils.Md5Hasher;

/**
 * In-memory cache for compression requests
 * 
 * @author kpacha
 */
public class CachedCompressor extends Compressor {
    private static Logger logger = Logger.getLogger(CachedCompressor.class);

    private Compressor actualCompressor;
    private Cache cache;
    private Md5Hasher hasher;

    /**
     * The default constructor
     * 
     * @param actualCompressor
     * @param bufferedContentHasher
     * @param cache
     */
    public CachedCompressor(Compressor actualCompressor,
	    Md5Hasher bufferedContentHasher, Cache cache) {
	this.actualCompressor = actualCompressor;
	this.cache = cache;
	hasher = bufferedContentHasher;
    }

    /**
     * If the request is cached get its value. If it's not cached, delegate the
     * compression and cache the result. Finally, write the compressed response
     * into the PrintWriter.
     */
    public String compress(String contentType, String charset, String in,
	    Reporter reporter) throws EvaluatorException, IOException,
	    UnknownContentTypeException {
	try {
	    return getCachedOrCompressedOutput(contentType, charset, in,
		    reporter);
	} catch (NoSuchAlgorithmException e) {
	    return getCompressedResult(contentType, charset, in, reporter);
	}
    }

    private String getCachedOrCompressedOutput(String contentType,
	    String charset, String in, Reporter reporter) throws IOException,
	    NoSuchAlgorithmException, UnknownContentTypeException {
	String hash = hasher.getHash(in, charset);
	Element element = cache.get(hash);
	if (element == null) {
	    cache.put(new Element(hash, getCompressedResult(contentType,
		    charset, in, reporter)));
	    element = cache.get(hash);
	}
	return (String) element.getObjectValue();
    }

    private String getCompressedResult(String contentType, String charset,
	    String in, Reporter reporter) throws IOException,
	    UnknownContentTypeException {
	logger.debug("Delegating the compression");
	return actualCompressor.compress(contentType, charset, in, reporter);
    }
}
