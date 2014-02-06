package com.github.kpacha.yuicompressorserver.adapter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;

import junit.framework.TestCase;

import com.github.kpacha.yuicompressorserver.reporter.Reporter;
import com.github.kpacha.yuicompressorserver.reporter.YuiErrorReporter;

public class AdapterFactoryTest extends TestCase {

    private AdapterFactory adapter;
    private BufferedReader bufferedReader;
    private Reporter reporter;

    public void setUp() throws IOException {
	adapter = new AdapterFactory();
	bufferedReader = mock(BufferedReader.class);
	reporter = new YuiErrorReporter();
    }

    public void testBuildCssAdapter() throws IOException,
	    UnknownContentTypeException {
	when(bufferedReader.read()).thenReturn(Character.getNumericValue('U'))
		.thenReturn(-1);
	assertEquals(
		CssCompressorAdapter.class,
		adapter.getCompressorByContentType("css", bufferedReader,
			reporter).getClass());
    }

    public void testBuildJsAdapter() throws IOException,
	    UnknownContentTypeException {
	when(bufferedReader.read((char[]) any(), anyInt(), anyInt()))
		.thenReturn(Character.getNumericValue('{'))
		.thenReturn(Character.getNumericValue('}')).thenReturn(-1);
	assertEquals(
		JavaScriptCompressorAdapter.class,
		adapter.getCompressorByContentType("js",
			bufferedReader, reporter).getClass());
    }

    public void testThrowExceptionForUnknownTypes() throws IOException {
	String contentType = "unknown-content-type";
	try {
	    adapter.getCompressorByContentType(contentType, null, null);
	    fail("An UnknownContentTypeException was expected!");
	} catch (UnknownContentTypeException e) {
	    assertEquals("Unknown content type : " + contentType,
		    e.getMessage());
	}
    }
}
