package com.github.kpacha.yuicompressorserver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.github.kpacha.yuicompressorserver.integration.TestBrowser;
import com.github.kpacha.yuicompressorserver.integration.TestResponse;
import com.github.softonic.yuicompressorserver.Configuration;
import com.github.softonic.yuicompressorserver.YuiCompressorServer;
import com.github.softonic.yuicompressorserver.utils.Md5Hasher;

abstract public class AbstractIntegrationTest {

    private static final int SERVICE_PORT = 8080;
    private static final int MAX_FORM_SIZE = 1024000;
    private static Md5Hasher hasher = new Md5Hasher();
    private static YuiCompressorServer server;
    private TestBrowser browser;

    @Before
    public void setUp() {
	browser = new TestBrowser();
    }

    @After
    public void tearDown() {
	browser.shutdown();
    }

    protected static void setUpBeforeClass(boolean isCacheEnabled)
	    throws Exception {
	Configuration configuration = mock(Configuration.class);
	when(configuration.isCacheEnabled()).thenReturn(isCacheEnabled);
	when(configuration.getPort()).thenReturn(SERVICE_PORT);
	when(configuration.getMaxFormSize()).thenReturn(MAX_FORM_SIZE);

	server = new YuiCompressorServer(configuration);
	server.run();
    }

    @AfterClass
    public static void shutdown() {
	server.shutdown();
    }

    @Test
    public void testMunge() throws Exception {
	doTestPostRequest("_munge.js");
    }

    @Test
    public void testStringCombo() throws Exception {
	doTestPostRequest("_string_combo.js");
    }

    @Test
    public void testBackgroundPosition() throws Exception {
	doTestPostRequest("background-position.css");
    }

    @Test
    public void testBoxModelHack() throws Exception {
	doTestPostRequest("box-model-hack.css");
    }

    @Test
    public void testBug2527974() throws Exception {
	doTestPostRequest("bug2527974.css");
    }

    @Test
    public void testBug2527991() throws Exception {
	doTestPostRequest("bug2527991.css");
    }

    @Test
    public void testBug2527998() throws Exception {
	doTestPostRequest("bug2527998.css");
    }

    @Test
    public void testBug2528034() throws Exception {
	doTestPostRequest("bug2528034.css");
    }

    @Test
    public void testCharsetMedia() throws Exception {
	doTestPostRequest("charset-media.css");
    }

    private void doTestPostRequest(final String resourceName)
	    throws IOException, NoSuchAlgorithmException {
	final String sampleJsCode = getFileContents("/" + resourceName);
	final String hash = hasher.getHash(sampleJsCode, "utf-8");
	String expectedMinifyedJsCode = getFileContents("/" + resourceName
		+ ".min");
	final String expectedHash = hasher.getHash(expectedMinifyedJsCode,
		"utf-8");
	final String extension = resourceName.substring(resourceName
		.lastIndexOf('.') + 1);

	Map<String, String> headers = new HashMap<String, String>(1) {
	    {
		put("Content-MD5", hash);
	    }
	};
	Map<String, String> formParameters = new HashMap<String, String>(3) {
	    {
		put("files", resourceName);
		put("type", extension);
		put("input", sampleJsCode);
	    }
	};

	TestResponse response = browser.makePostRequestWithFormParameters(
		"http://localhost:" + SERVICE_PORT + "/", headers,
		formParameters);

	assertEquals(expectedMinifyedJsCode, response.getContent());
	assertEquals(expectedHash, response.getMd5());
	assertEquals(200, response.getStatus());
    }

    private static String getFileContents(String path) {

	BufferedReader br = null;
	StringBuilder sb = new StringBuilder();

	String line;
	try {
	    InputStream stream = CacheEnabledIntegrationTest.class
		    .getResourceAsStream(path);

	    br = new BufferedReader(new InputStreamReader(stream));
	    while ((line = br.readLine()) != null) {
		sb.append(line);
	    }

	    stream.close();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (br != null) {
		try {
		    br.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}

	return sb.toString();

    }
}
