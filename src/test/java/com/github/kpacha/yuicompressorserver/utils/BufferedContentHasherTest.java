package com.github.kpacha.yuicompressorserver.utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

public class BufferedContentHasherTest extends TestCase {

    public void testHash() throws IOException, NoSuchAlgorithmException {
	BufferedContentHasher hasher = new BufferedContentHasher("SHA-1");
	assertEquals("89d492733910a2006758993715ccf73dafb85574",
		hasher.getHash("supu", "UTF-8"));
    }
}
