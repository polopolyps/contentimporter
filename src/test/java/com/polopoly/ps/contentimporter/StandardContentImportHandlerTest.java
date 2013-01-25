package com.polopoly.ps.contentimporter;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import com.polopoly.cm.xml.DocumentImporter;
import com.polopoly.ps.contentimporter.StandardContentImportHandler;
import com.polopoly.ps.contentimporter.hotdeploy.text.TextContentParser;
import com.polopoly.ps.contentimporter.hotdeploy.text.TextContentSet;
import com.polopoly.ps.contentimporter.hotdeploy.text.TextContentXmlWriter;

public class StandardContentImportHandlerTest {
	
	private StandardContentImportHandler target;
	private Logger LOGGER;
	private DocumentImporter documentImporter;
	
	@Before
	public void beforeTest() {
		LOGGER = mock(Logger.class);
		documentImporter = mock(DocumentImporter.class);
		target = new StandardContentImportHandler(documentImporter);
		StandardContentImportHandler.LOGGER = LOGGER;
	}
	
	@Test
	public void shouldLogWarningIfResourceSetIsNull() {
				
		Set<URL> resources = null;
		target.importContent(resources);
		
		verify(LOGGER).log(Level.WARNING, 
				StandardContentImportHandler.WARNING_RESOURCE_SET_WAS_NULL);
		
	}
	
	@Test
	public void shouldLogWarningIfResourceSetIsEmpty() {
				
		Set<URL> resources = new HashSet<URL>();
		target.importContent(resources);
		
		verify(LOGGER).log(Level.WARNING, 
				StandardContentImportHandler.WARNING_RESOURCE_SET_WAS_EMPTY);
		
	}
	
	@Test
	public void shouldLogWarningIfResourceIsNull() throws Exception {
		
		URL resource = null;
		Set<URL> resources = new HashSet<URL>();
		resources.add(resource);
		target.importContent(resources);
		
		verify(LOGGER).log(Level.WARNING, 
				String.format(StandardContentImportHandler.WARNING_RESOURCE_FILE_NOT_FOUND, "null"));
	}
	
	@Test
	public void shouldLogWarningIfResourceNotFound() throws Exception {
				
		String filePath = "file://this-file-does-not-exist.content";

		Set<URL> resources = new HashSet<URL>();
		resources.add(new URL(filePath));
		target.importContent(resources);
		
		verify(LOGGER).log(Level.WARNING, 
				String.format(StandardContentImportHandler.WARNING_RESOURCE_FILE_NOT_FOUND, filePath));
	}
	
	
	@Test
	public void shouldLogWarningIfFileTypeNotSupported() throws Exception {
				
		String fileName = StandardContentImportHandlerTest.class.getSimpleName() + ".txt";
		
		URL resource = this.getClass().getResource("/" + fileName);
		
		Set<URL> resources = new HashSet<URL>();
		resources.add(resource);
		target.importContent(resources);
		
		verify(LOGGER).log(Level.WARNING, 
				String.format(StandardContentImportHandler.WARNING_RESOURCE_TYPE_NOT_SUPPORTED, 
						resource.toString()));
	}
	
	@Test
	public void shouldForDotContentCatchExceptionAndLog() throws Exception {
		String fileName = "StandardContentImportHandlerTest-imported.content";
		
		URL resourceURL = this.getClass().getResource("/" + fileName);
		
		Set<URL> resources = new HashSet<URL>();
		resources.add(resourceURL);
		
		Exception toBeThrown = new Exception("Exception");
		doThrow(toBeThrown).when(documentImporter).importXML(anyString());	
		
		target.importContent(resources);
		
		verify(LOGGER).log(Level.SEVERE, 
				           StandardContentImportHandler.SEVERE_CONTENT_IMPORT_FAILED, 
				           toBeThrown);
	}
	
	@Test
	public void shouldForDotXMLCatchExceptionAndLog() throws Exception {
		String fileName = "StandardContentImportHandlerTest.xml";
		
		URL resourceURL = this.getClass().getResource("/" + fileName);
		
		Set<URL> resources = new HashSet<URL>();
		resources.add(resourceURL);
		
		Exception toBeThrown = new Exception("Exception");
		doThrow(toBeThrown).when(documentImporter).importXML(new File(resourceURL.getFile()));	
		
		target.importContent(resources);
		
		verify(LOGGER).log(Level.SEVERE, 
				           StandardContentImportHandler.SEVERE_CONTENT_IMPORT_FAILED, 
				           toBeThrown);
	}
	
	@Test
	public void shouldImportDotContentAsXML() throws Exception {
		String fileName = "StandardContentImportHandlerTest-imported.content";
		
		URL resourceURL = this.getClass().getResource("/" + fileName);
		String filePath = resourceURL.getFile();
		
		InputStream inputStream = new FileInputStream(new File(filePath));
		TextContentParser textContentParser = new TextContentParser(inputStream, resourceURL, resourceURL.getFile());
		TextContentSet textContentSet = textContentParser.parse();
		StringWriter writer = new StringWriter();
		TextContentXmlWriter contentXmlWriter = new TextContentXmlWriter(writer);
		contentXmlWriter.write(textContentSet);
		contentXmlWriter.close();
		
		String expectedResult = writer.getBuffer().toString();
		
		Set<URL> resources = new HashSet<URL>();
		resources.add(resourceURL);
		target.importContent(resources);
		
		verify(documentImporter).importXML(expectedResult);
		verify(LOGGER).log(Level.INFO, String.format(StandardContentImportHandler.INFO_CONTENT_IMPORT_SUCCEDED, filePath));
	}
	
	@Test
	public void shouldImportDotXMLAsXML() throws Exception {
		String fileName = "StandardContentImportHandlerTest.xml";

		URL resourceURL = this.getClass().getResource("/" + fileName);
		String filePath = resourceURL.getFile();
		
		File file = new File(filePath);
		
		Set<URL> resources = new HashSet<URL>();
		resources.add(resourceURL);
		
		target.importContent(resources);
		
		verify(documentImporter).importXML(file);
		verify(LOGGER).log(Level.INFO, 
				String.format(StandardContentImportHandler.INFO_CONTENT_IMPORT_SUCCEDED, filePath));
	}
}
