package com.github.kpacha.yuicompressorserver.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

public class BufferedContentHasherTest extends TestCase {

    public void testHash() throws IOException, NoSuchAlgorithmException {
	BufferedReader in = mock(BufferedReader.class);
	when(in.readLine()).thenReturn("supu").thenReturn(null);
	BufferedContentHasher hasher = new BufferedContentHasher("SHA-1");
	assertEquals("89d492733910a2006758993715ccf73dafb85574",
		hasher.getHash(in, "UTF-8"));
    }
}
