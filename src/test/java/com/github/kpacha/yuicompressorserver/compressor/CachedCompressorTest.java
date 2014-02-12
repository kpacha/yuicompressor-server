package com.github.kpacha.yuicompressorserver.compressor;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.mozilla.javascript.EvaluatorException;

import com.github.softonic.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.softonic.yuicompressorserver.compressor.CachedCompressor;
import com.github.softonic.yuicompressorserver.compressor.Compressor;
import com.github.softonic.yuicompressorserver.reporter.Reporter;
import com.github.softonic.yuicompressorserver.reporter.YuiErrorReporter;
import com.github.softonic.yuicompressorserver.utils.Md5Hasher;

public class CachedCompressorTest extends TestCase {
    private Compressor compressor;
    private CachedCompressor cachedCompressor;
    private String expectedContentType = "some-content-type";
    private String charset = "UTF-8";
    private String input = "some uncompressed input";
    private String output = "some nice compressed output";
    private Reporter reporter;
    private Md5Hasher hasher;
    private CacheManager cacheManager;

    public void setUp() throws NoSuchAlgorithmException, IOException {
	compressor = mock(Compressor.class);
	hasher = mock(Md5Hasher.class);
	when(hasher.getHash((String) any(), (String) any())).thenReturn(
		"someHash");
	cachedCompressor = new CachedCompressor(compressor, hasher,
		getFreshCache());
	expectedContentType = "some-content-type";
	reporter = new YuiErrorReporter();
	new PrintWriter(new StringWriter());
    }

    public void tearDown() {
	cacheManager.shutdown();
    }

    public void testCompress() throws EvaluatorException, IOException,
	    UnknownContentTypeException, NoSuchAlgorithmException {
	when(compressor.compress(expectedContentType, charset, input, reporter))
		.thenReturn(output);
	assertEquals(output, cachedCompressor.compress(expectedContentType,
		charset, input, reporter));

	verify(compressor).compress(expectedContentType, charset, input,
		reporter);
	verify(hasher).getHash(input, charset);
    }

    public void testGetCachedCompress() throws EvaluatorException, IOException,
	    UnknownContentTypeException, NoSuchAlgorithmException {
	when(compressor.compress(expectedContentType, charset, input, reporter))
		.thenReturn(output);
	assertEquals(output, cachedCompressor.compress(expectedContentType,
		charset, input, reporter));

	assertEquals(output, cachedCompressor.compress(expectedContentType,
		charset, input, reporter));

	verify(compressor, times(1)).compress(expectedContentType, charset,
		input, reporter);
	verify(hasher, times(2)).getHash(input, charset);
    }

    private Cache getFreshCache() {
	cacheManager = CacheManager.create();
	cacheManager.addCache(new Cache("testCache", 1, false, false, 2, 2));
	return cacheManager.getCache("testCache");
    }
}
