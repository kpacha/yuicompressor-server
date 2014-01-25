package com.github.kpacha.yuicompressorserver.reporter;

import junit.framework.TestCase;

import org.mozilla.javascript.EvaluatorException;

public class YuiErrorReporterTest extends TestCase {

    private YuiErrorReporter reporter;
    private String message;
    private String sourceName;
    private int line;
    private int lineOffset;
    private String lineSource;

    public void setUp() {
	reporter = new YuiErrorReporter();
	message = "some message to report";
	sourceName = "someSourceName";
	line = 101;
	lineOffset = 1;
	lineSource = "the line source";
    }

    public void testWarning() {
	reporter.warning(message, sourceName, line, lineSource, lineOffset);
	assertEquals("[WARNING] " + message + " line " + line + ":\n"
		+ lineSource + "\n\n", reporter.getReport());
    }

    public void testError() {
	reporter.error(message, sourceName, line, lineSource, lineOffset);
	assertEquals("[ERROR] " + message + " line " + line + ":\n"
		+ lineSource + "\n\n", reporter.getReport());
    }

    public void testRuntimeError() {
	EvaluatorException exception = reporter.runtimeError(message,
		sourceName, line, lineSource, lineOffset);
	assertEquals(message + " (" + sourceName + "#" + line + ")",
		exception.getMessage());
	assertEquals(sourceName, exception.sourceName());
	assertEquals(line, exception.lineNumber());
	assertEquals(lineSource, exception.lineSource());
	assertEquals(lineOffset, exception.columnNumber());
    }
}
