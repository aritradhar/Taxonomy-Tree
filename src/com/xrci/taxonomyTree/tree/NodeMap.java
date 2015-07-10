//*************************************************************************************
//*********************************************************************************** *
//author Aritra Dhar 																* *
//Research Engineer																  	* *
//Xerox Research Center India													    * *
//Bangalore, India																    * *
//--------------------------------------------------------------------------------- * * 
///////////////////////////////////////////////// 									* *
//The program will do the following:::: // 											* *
///////////////////////////////////////////////// 									* *
//version 1.0 																		* *
//*********************************************************************************** *
//*************************************************************************************


package com.xrci.taxonomyTree.tree;

import java.io.Serializable;
import java.util.HashMap;

public class NodeMap<T> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 344341782020678543L;
	public HashMap<T, Node<T>> NODE_MAP;
	
	public NodeMap() {
		NODE_MAP = new HashMap<>();
	}
}
