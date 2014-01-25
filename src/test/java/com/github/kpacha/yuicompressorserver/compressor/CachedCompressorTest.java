package com.github.kpacha.yuicompressorserver.compressor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.reporter.Reporter;
import com.github.kpacha.yuicompressorserver.reporter.YuiErrorReporter;
import com.github.kpacha.yuicompressorserver.utils.BufferedContentHasher;

public class CachedCompressorTest extends TestCase {
    private Compressor compressor;
    private CachedCompressor cachedCompressor;
    private String expectedContentType = "some-content-type";
    private String charset = "UTF-8";
    private Reporter reporter;
    private PrintWriter out;
    private BufferedContentHasher hasher;

    public void setUp() throws NoSuchAlgorithmException, IOException {
	compressor = mock(Compressor.class);
	hasher = mock(BufferedContentHasher.class);
	when(hasher.getHash((BufferedReader) any(), (String) any()))
		.thenReturn("someHash");
	cachedCompressor = new CachedCompressor(compressor, hasher);
	expectedContentType = "some-content-type";
	reporter = new YuiErrorReporter();
	out = new PrintWriter(new StringWriter());
    }

    public void testCompress() throws EvaluatorException, IOException,
	    UnknownContentTypeException, NoSuchAlgorithmException {
	InputStream in = new ByteArrayInputStream(
		"someContent".getBytes("UTF-8"));

	cachedCompressor.compress(expectedContentType, charset, in, out,
		reporter);

	verify(compressor).compress(eq(expectedContentType), eq(charset),
		(BufferedReader) any(), (PrintWriter) any(), eq(reporter));
	verify(hasher).getHash((BufferedReader) any(), (String) any());
    }

    public void testGetCachedCompress() throws EvaluatorException, IOException,
	    UnknownContentTypeException, NoSuchAlgorithmException {
	InputStream in = new ByteArrayInputStream(
		"someContent".getBytes("UTF-8"));

	cachedCompressor.compress(expectedContentType, charset, in, out,
		reporter);

	cachedCompressor.compress(expectedContentType, charset, in, out,
		reporter);

	verify(compressor, times(1)).compress((String) any(), (String) any(),
		(BufferedReader) any(), (PrintWriter) any(), (Reporter) any());
	verify(hasher, times(2))
		.getHash((BufferedReader) any(), (String) any());
    }
}
