package com.oracle.javafx.scenebuilder.app;

import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * FXMLNode is an Object that contains information about an FXML file.
 * <p>
 * Information includes: its {@link #path}, its {@link #name}, its
 * {@link #controller} name from the fx:controller attribute, and a list of
 * other included FXML files ({@link #children}) from the &ltfx:include&gt tag.
 *
 *
 */
public final class FXMLNode {

	/**
	 * ArrayList containing the children from this FXMLNode.
	 */
	private final ArrayList<FXMLNode> children;

	/**
	 * String with the package and name of this associated FXML Controller.
	 */
	private final String controller;

	/**
	 * String with the filename of this associated FXML file..
	 */
	private final String name;

	/**
	 * String with the path of this associated FXML file.
	 */
	private final String path;

	/**
	 * Constructor. Sets Information from an FXML file.
	 * 
	 * @param path
	 *            - Path to the FXML file
	 * @param controller
	 *            - controller name. Example: package.Controller
	 * @param children
	 *            - ArrayList of Included FXML files as FXMLNodes
	 */
	public FXMLNode(String path, String controller, ArrayList<FXMLNode> children) {
		this.path = path;
		this.name = Paths.get(path).getFileName().toString();
		this.controller = controller;
		this.children = children;
	}

	/**
	 * Returns FXMLNode of this childrens list. Same as .getChildren().get(int
	 * index).
	 * 
	 * @param index
	 *            - position in list
	 * @return FXMLNode of this childrens index.
	 */
	public FXMLNode getChild(int index) {
		return children.get(index);
	}

	/**
	 * Returns an ArrayList of FXMLNodes of this children.
	 * 
	 * @return An ArrayList of FXMLNodes
	 * 
	 * @see {@link #children}
	 */
	public ArrayList<FXMLNode> getChildren() {
		return children;
	}

	/**
	 * Returns the {@link #controller} of this FXMLNode.
	 * <p>
	 * Example: package.Controller
	 * 
	 * @return A String of the location of the Controller and its name. If there
	 *         is no controller it returns null.
	 */
	public String getController() {
		return controller;
	}

	/**
	 * Returns the {@link #name} of this associated FXML file.
	 * 
	 * @return A String with the name of this associated FXML file.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the path of this associated FXML file.
	 * 
	 * @return {@link #path}
	 */
	public String getPath() {
		return path;
	}
}
