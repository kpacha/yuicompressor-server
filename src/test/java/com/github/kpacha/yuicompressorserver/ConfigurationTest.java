package com.github.kpacha.yuicompressorserver;

import junit.framework.TestCase;

import org.apache.commons.cli.ParseException;

public class ConfigurationTest extends TestCase {

    public void testCacheEnabled() throws ParseException {
	Configuration config = new Configuration(new String[] {});
	assertTrue(config.isCacheEnabled());
    }

    public void testCacheIsDisabled() throws ParseException {
	Configuration config = new Configuration(new String[] { "-c" });
	assertFalse(config.isCacheEnabled());
    }

    public void testDefaultPort() throws ParseException {
	Configuration config = new Configuration(new String[] {});
	assertEquals(8080, config.getPort());
    }

    public void testCustomPort() throws ParseException {
	Configuration config = new Configuration(new String[] { "-p", "80" });
	assertEquals(80, config.getPort());
    }

    public void testDefaultAlgorithm() throws ParseException {
	Configuration config = new Configuration(new String[] {});
	assertEquals("md5", config.getAlgorithm());
    }

    public void testCustomAlgorithm() throws ParseException {
	String algorithm = "someAlgorithm";
	Configuration config = new Configuration(
		new String[] { "-h", algorithm });
	assertEquals(algorithm, config.getAlgorithm());
    }

    public void testAllIn() throws ParseException {
	String algorithm = "someAlgorithm";
	Configuration config = new Configuration(new String[] { "-h",
		algorithm, "-p", "80", "-c" });
	assertEquals(algorithm, config.getAlgorithm());
	assertEquals(80, config.getPort());
	assertFalse(config.isCacheEnabled());
    }
}
