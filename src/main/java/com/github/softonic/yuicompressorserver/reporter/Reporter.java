package com.github.softonic.yuicompressorserver.reporter;

import org.mozilla.javascript.ErrorReporter;

/**
 * The reporter interface adds the getReport method in order to log it
 * 
 * @author kpacha
 */
public interface Reporter extends ErrorReporter {

    public String getReport();
}
