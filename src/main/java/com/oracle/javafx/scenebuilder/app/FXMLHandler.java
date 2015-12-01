package com.oracle.javafx.scenebuilder.app;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * FXMLHandler is a custom DefaultHandler that helps the FXMLReader class to get
 * the information it needs for the creation of FXMLNodes.
 * 
 *
 */
public class FXMLHandler extends DefaultHandler {

	private String controller;
	private ArrayList<FXMLNode> includes = new ArrayList<>();
	private FXMLNode fxmlnode;
	private String path;
	private String pathNoFileName;

	/**
	 * Constructor. A new Instance is needed for each included FXML file.
	 * 
	 * @param path
	 *            - Path to included FXML file.
	 */
	public FXMLHandler(String path) {
		this.path = path;
		this.pathNoFileName = path.replace(Paths.get(path).getFileName().toString(), "");
	}

	/**
	 * Method that is called when an XML Document was fully read. Creates an
	 * FXMLNode with information of the FXML that was read.
	 */
	@Override
	public void endDocument() throws SAXException {
		// creates an FXMLNode after parsing the document
		fxmlnode = new FXMLNode(path, controller, includes);
	}

	/**
	 * Returns an FXMLNode of the read document.
	 * 
	 * @return FXMLNode.
	 */
	public FXMLNode getFXMLNode() {
		return fxmlnode;
	}

	/**
	 * Method that is called when each XML tag is read. This method implements a
	 * recursive method which follows a Depth-first search to find all included
	 * FXML files.
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (attributes.getValue("fx:controller") != null) // gets the
		                                                  // controllers name
			controller = attributes.getValue("fx:controller");

		if (qName.equalsIgnoreCase("fx:include"))// gets the included fxml
			if (attributes.getValue("source") != null) {
				String source = attributes.getValue("source");
				// normalizes the relative path of the include with the
				// currently read FXML
				String includedPath = Paths.get(pathNoFileName + source).normalize().toString();

				try {
					// creates a new FXMLHandler instance for each include.
					// Each FXMLHandler creates an FXMLNode, see: endDocument()
					FXMLHandler fxmlHandler = new FXMLHandler(includedPath);
					XMLReader reader = XMLReaderFactory.createXMLReader();
					reader.setContentHandler(fxmlHandler);
					reader.parse(includedPath); // "loop" / looks inside the
					                            // include until innermost
					                            // include is found

					// after parsing the FXML, an FXMLNode will be
					// created, see: endDocument()

					// adds found include to the include list (will become a
					// child of the FXMLNode in the current FXMLHandler
					// instance); no children for innermost include
					if (fxmlHandler.getFXMLNode() != null)
						includes.add(fxmlHandler.getFXMLNode());

				} catch (IOException | SAXException e) {
					System.out.println("No FXML file at " + includedPath);
					includes.add(null);
				}
			}
	}

}
