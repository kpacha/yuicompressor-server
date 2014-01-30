package com.github.kpacha.yuicompressorserver.compressor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import junit.framework.TestCase;

import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.AdapterFactory;
import com.github.kpacha.yuicompressorserver.adapter.CompressorAdapter;
import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.reporter.YuiErrorReporter;

public class YuiCompressorTest extends TestCase {

    private String tokenToPass;
    private String expectedContentType;
    private YuiErrorReporter reporter;
    private AdapterFactory adapterFactory;

    public void setUp() throws IOException, UnknownContentTypeException {
	tokenToPass = "this text is passed to the adapter and then, it gets back!";
	expectedContentType = "some-content-type";
	reporter = new YuiErrorReporter();
	adapterFactory = mock(AdapterFactory.class);
	when(
		adapterFactory.getCompressorByContentType(
			eq(expectedContentType), (BufferedReader) any(),
			eq(reporter))).thenReturn(
		new MockedAdapter(tokenToPass));
    }

    public void testCompress() throws EvaluatorException, IOException,
	    UnknownContentTypeException {
	YuiCompressor compressor = new YuiCompressor(adapterFactory);

	checkCompressedOutput(compressor.compress(expectedContentType, "utf-8",
		tokenToPass, reporter));
    }

    public void testGetBufferedReaderContent() throws EvaluatorException,
	    IOException, UnknownContentTypeException {
	BufferedReader bufferedReader = new BufferedReader(
		new InputStreamReader(new ByteArrayInputStream(
			tokenToPass.getBytes())));

	YuiCompressor compressor = new YuiCompressor(adapterFactory);

	checkCompressedOutput(compressor.compress(expectedContentType, "utf-8",
		bufferedReader, reporter));
    }

    private void checkCompressedOutput(String compressedOutput) {
	assertEquals(tokenToPass, compressedOutput);
    }

    private class MockedAdapter implements CompressorAdapter {

	private String token;

	public MockedAdapter(String token) throws IOException {
	    this.token = token;
	}

	@Override
	public void compress(Writer out, int linebreak) throws IOException {
	    out.write(token);
	}
    }
}
