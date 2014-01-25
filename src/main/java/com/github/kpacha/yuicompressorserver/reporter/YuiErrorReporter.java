package com.github.kpacha.yuicompressorserver.reporter;

import org.mozilla.javascript.EvaluatorException;

/**
 * A simple error reporter
 * 
 * @author kpacha
 */
public class YuiErrorReporter implements Reporter {

    private static final String LEVEL_WARNING = "WARNING";
    private static final String LEVEL_ERROR = "ERROR";

    private StringBuilder report = new StringBuilder();

    public void warning(String message, String sourceName, int line,
	    String lineSource, int lineOffset) {
	append(LEVEL_WARNING, message, line, lineSource);
    }

    public void error(String message, String sourceName, int line,
	    String lineSource, int lineOffset) {
	append(LEVEL_ERROR, message, line, lineSource);
    }

    public EvaluatorException runtimeError(String message, String sourceName,
	    int line, String lineSource, int lineOffset) {
	return new EvaluatorException(message, sourceName, line, lineSource,
		lineOffset);
    }

    private void append(String level, String message, int line,
	    String lineSource) {
	report.append("[" + level + "] " + message + " line " + line + ":\n"
		+ lineSource + "\n\n");
    }

    public String getReport() {
	return report.toString();
    }

}
