package com.github.kpacha.yuicompressorserver.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A simple BufferedReader content hasher
 * 
 * @author kpacha
 */
public class Md5Hasher {
    private static final String ALGORITHM = "md5";

    /**
     * Hash the received String
     * 
     * @param in
     * @param charset
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public String getHash(String in, String charset) throws IOException,
	    NoSuchAlgorithmException {
	return getDigest(in, charset);
    }

    private String getDigest(String message, String charset)
	    throws UnsupportedEncodingException, NoSuchAlgorithmException {
	return getDigest(message.getBytes(charset));
    }

    private String getDigest(byte[] message)
	    throws UnsupportedEncodingException, NoSuchAlgorithmException {
	MessageDigest md = MessageDigest.getInstance(ALGORITHM);
	return byteArrayToString(md.digest(message));
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
