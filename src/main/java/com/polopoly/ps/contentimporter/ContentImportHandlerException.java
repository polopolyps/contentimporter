package com.polopoly.ps.contentimporter;

@SuppressWarnings("serial")
public class ContentImportHandlerException extends Exception {

	public ContentImportHandlerException() {
		super();
	}

	public ContentImportHandlerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ContentImportHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContentImportHandlerException(String message) {
		super(message);
	}

	public ContentImportHandlerException(Throwable cause) {
		super(cause);
	}

}
