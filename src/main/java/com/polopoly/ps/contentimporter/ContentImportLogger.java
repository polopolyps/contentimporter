package com.polopoly.ps.contentimporter;

public interface ContentImportLogger {
	
	void debug(String message);
	
	void info(String message);
	
	void warning(String message);
	
	void error(String message);

}
