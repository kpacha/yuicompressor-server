package com.github.kpacha.yuicompressorserver;

import junit.framework.TestCase;

import org.apache.commons.cli.ParseException;

public class ConfigurationTest extends TestCase {

    public void testCacheEnabled() throws ParseException {
	Configuration config = new Configuration(new String[] {});
	assertFalse(config.isCacheEnabled());
    }

    public void testCacheIsDisabled() throws ParseException {
	Configuration config = new Configuration(new String[] { "-c" });
	assertTrue(config.isCacheEnabled());
    }

    public void testDefaultPort() throws ParseException {
	Configuration config = new Configuration(new String[] {});
	assertEquals(8080, config.getPort());
    }

    public void testCustomPort() throws ParseException {
	Configuration config = new Configuration(new String[] { "-p", "80" });
	assertEquals(80, config.getPort());
    }

    public void testDefaultMaxFormSize() throws ParseException {
	Configuration config = new Configuration(new String[] {});
	assertEquals( 1024000, config.getMaxFormSize());
    }

    public void testCustomMaxFormSize() throws ParseException {
	Configuration config = new Configuration(new String[] { "-m", "1024" });
	assertEquals(1024, config.getMaxFormSize());
    }

    public void testAllIn() throws ParseException {
	Configuration config = new Configuration(new String[] { "-p", "80",
		"-c", "-m", "1024" });
	assertEquals(80, config.getPort());
	assertTrue(config.isCacheEnabled());
	assertEquals(1024, config.getMaxFormSize());
    }
}
