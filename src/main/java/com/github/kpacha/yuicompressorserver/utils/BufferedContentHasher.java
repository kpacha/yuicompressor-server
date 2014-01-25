package com.github.kpacha.yuicompressorserver.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A simple BufferedReader content hasher
 * 
 * @author kpacha
 */
public class BufferedContentHasher {
    private MessageDigest md;

    /**
     * The defualt constructor sets up the internal message digester with the
     * received algorithm
     * 
     * @param algorithm
     * @throws NoSuchAlgorithmException
     */
    public BufferedContentHasher(String algorithm)
	    throws NoSuchAlgorithmException {
	md = MessageDigest.getInstance(algorithm);
    }

    /**
     * Hash the content of the BufferedReader
     * 
     * @param in
     * @param charset
     * @return
     * @throws IOException
     */
    public String getHash(BufferedReader in, String charset) throws IOException {
	return getDigest(parse(in), charset);
    }

    private StringBuffer parse(BufferedReader in) throws IOException {
	StringBuffer srcsb = new StringBuffer();
	String line;
	while ((line = in.readLine()) != null) {
	    srcsb.append(line);
	}
	return srcsb;
    }

    private String getDigest(StringBuffer input, String charset)
	    throws UnsupportedEncodingException {
	md.reset();
	return byteArrayToString(md.digest(input.toString().getBytes(charset)));
    }

    private String byteArrayToString(byte[] result) {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < result.length; i++) {
	    sb.append(Integer.toHexString((result[i] & 0xFF) | 0x100)
		    .substring(1, 3));
	}
	return sb.toString();
    }
}
