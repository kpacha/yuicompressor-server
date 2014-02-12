package com.github.kpacha.yuicompressorserver.utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.github.softonic.yuicompressorserver.utils.Md5Hasher;

import junit.framework.TestCase;

public class Md5HasherTest extends TestCase {

    public void testHash() throws IOException, NoSuchAlgorithmException {
	Md5Hasher hasher = new Md5Hasher();
	assertEquals("c0ac7cbfa8b77237916c9e51f8ac927e",
		hasher.getHash("supu", "UTF-8"));
    }
}
