package com.github.kpacha.yuicompressorserver.compressor;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.mozilla.javascript.EvaluatorException;

import com.github.kpacha.yuicompressorserver.adapter.AdapterFactory;
import com.github.kpacha.yuicompressorserver.adapter.CompressorAdapter;
import com.github.kpacha.yuicompressorserver.adapter.UnknownContentTypeException;
import com.github.kpacha.yuicompressorserver.reporter.Reporter;
import com.github.kpacha.yuicompressorserver.reporter.YuiErrorReporter;

public class YuiCompressorTest extends TestCase {

    public void testCompress() throws EvaluatorException, IOException,
	    UnknownContentTypeException {
	String expectedContentType = "some-content-type";
	CompressorAdapter adapter = mock(CompressorAdapter.class);
	Reporter reporter = new YuiErrorReporter();
	BufferedReader in = mock(BufferedReader.class);
	AdapterFactory adapterFactory = mock(AdapterFactory.class);
	when(
		adapterFactory.getCompressorByContentType(expectedContentType,
			in, reporter)).thenReturn(adapter);
	PrintWriter out = new PrintWriter(new StringWriter());

	YuiCompressor compressor = new YuiCompressor(adapterFactory);

	compressor.compress(expectedContentType, "someCharset", in, out, reporter);

	verify(adapter).compress(eq(out), eq(-1));
    }
}
