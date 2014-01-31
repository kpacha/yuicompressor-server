package com.github.kpacha.yuicompressorserver;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

public class Configuration {
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_ALGORITHM = "md5";
    private static Logger logger = Logger.getLogger(Configuration.class);

    private CommandLine cmd;
    private String algorithm;
    private int port;
    private boolean cacheEnabled;

    public Configuration(String[] args) throws ParseException {
	initCmdLine(args);
	algorithm = initAlgorithm();
	port = initPort();
	cacheEnabled = initCacheEnabled();
    }

    public boolean isCacheEnabled() {
	return cacheEnabled;
    }

    public int getPort() {
	return port;
    }

    public String getAlgorithm() {
	return algorithm;
    }

    private void initCmdLine(String[] args) throws ParseException {
	CommandLineParser parser = new GnuParser();
	cmd = parser.parse(getOptions(), args);
    }

    private Options getOptions() {
	Options options = new Options();

	options.addOption("p", true, "port (default 8080)");
	options.addOption("c", false, "disable cache");
	options.addOption("h", true, "hashing algorithm");

	return options;
    }

    private String initAlgorithm() {
	String hashAlgorithm = cmd.getOptionValue("h");
	if (hashAlgorithm == null) {
	    hashAlgorithm = DEFAULT_ALGORITHM;
	}
	logger.info("Selected hashing algorithm: " + hashAlgorithm);
	return hashAlgorithm;
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
