package com.oracle.javafx.scenebuilder.app;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.IOException;

/**
 * FXMLReader is a Class that can parse an FXML file and information from it in
 * the form of an FXMLNode. It uses the SAX parser XMLReader.
 * 
 *
 * @see FXMLNode
 * @see XMLReader
 */
public class FXMLReader {

	/**
	 * Returns an FXMLNode from the given FXML String path.
	 * 
	 * @param path
	 *            - Path to the FXML
	 * @return An FXMLNode with information about its contents.
	 * @see {@link FXMLNode}
	 */
	public static FXMLNode parseFXML(String path) {
		FXMLNode root = null;

		try {
			FXMLHandler fxmlHandler = new FXMLHandler(path);
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(fxmlHandler);
			reader.parse(path);
			root = fxmlHandler.getFXMLNode();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

		return root;
	}

	/**
	 * Returns an FXMLNode from the given FXML file.
	 * 
	 * @param fxml
	 *            - FXML file to be read.
	 * @return An FXMLNode with information about its contents.
	 * @see {@link FXMLNode}
	 */
	public static FXMLNode parseFXML(File fxml) {
		return parseFXML(fxml.getPath());
	}

}
