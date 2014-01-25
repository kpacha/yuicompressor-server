package com.github.kpacha.yuicompressorserver.adapter;

public class UnknownContentTypeException extends Exception {

    private static final long serialVersionUID = -3242980899001766519L;

    public UnknownContentTypeException(String contentType) {
	super("Unknown content type : " + contentType);
    }

}
