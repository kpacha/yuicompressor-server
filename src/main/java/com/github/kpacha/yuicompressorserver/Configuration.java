package com.github.kpacha.yuicompressorserver;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

public class Configuration {
    private static final int DEFAULT_PORT = 8080;
    private static Logger logger = Logger.getLogger(Configuration.class);

    private CommandLine cmd;
    private int port;
    private boolean cacheEnabled;

    public Configuration(String[] args) throws ParseException {
	initCmdLine(args);
	port = initPort();
	cacheEnabled = initCacheEnabled();
    }

    public boolean isCacheEnabled() {
	return cacheEnabled;
    }

    public int getPort() {
	return port;
    }

    private void initCmdLine(String[] args) throws ParseException {
	CommandLineParser parser = new GnuParser();
	Options options = getOptions();
	cmd = parser.parse(options, args);
    }

    private Options getOptions() {
	Options options = new Options();

	options.addOption("p", true, "port (default 8080)");
	options.addOption("c", false, "disable cache");

	return options;
    }

    private int initPort() {
	Integer port;
	if (cmd.hasOption("p")) {
	    port = Integer.parseInt(cmd.getOptionValue("p"));
	} else {
	    port = DEFAULT_PORT;
	}
	logger.info("Selected port: " + port);
	return port;
    }

    private boolean initCacheEnabled() {
	boolean isEnabled = !cmd.hasOption("c");
	logger.info("Cache enabled: " + isEnabled);
	return isEnabled;
    }

}
