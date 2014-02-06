package com.github.kpacha.yuicompressorserver;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

public class Configuration {
	private static final int DEFAULT_PORT = 8080;
	private static final int DEFAULT_MAX_FORM_SIZE = 1024000;
	private static Logger logger = Logger.getLogger(Configuration.class);

	private CommandLine cmd;
	private int port;
	private boolean cacheEnabled;
	private int max_form_size;

	public Configuration(String[] args) throws ParseException {
		initCmdLine(args);
		port = initPort();
		cacheEnabled = initCacheEnabled();
		max_form_size = initMaxFormSize();
	}

	public boolean isCacheEnabled() {
		return cacheEnabled;
	}

	public int getPort() {
		return port;
	}

	public int getMaxFormSize() {
		return max_form_size;
	}

	private void initCmdLine(String[] args) throws ParseException {
		CommandLineParser parser = new GnuParser();
		Options options = getOptions();
		cmd = parser.parse(options, args);
	}

	private Options getOptions() {
		Options options = new Options();

		options.addOption("p", true, "port (default 8080)");
		options.addOption("c", false, "enable cache");
		options.addOption("m", true, "max form size");

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
		boolean isEnabled = cmd.hasOption("c");
		logger.info("Cache enabled: " + isEnabled);
		return isEnabled;
	}

	private int initMaxFormSize() {
		Integer max_form_size;
		if (cmd.hasOption("m")) {
			max_form_size = Integer.parseInt(cmd.getOptionValue("m"));
		} else {
			max_form_size = DEFAULT_MAX_FORM_SIZE;
		}
		logger.info("Max form size: " + max_form_size);
		return max_form_size;
	}

}
