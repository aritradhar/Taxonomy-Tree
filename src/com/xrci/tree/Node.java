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


package com.xrci.tree;

import java.util.Collections;
import java.util.List;

/**
 * Node for the classification tree
 * @author Aritra Dhar
 */
public class Node 
{
	int id;
	String node;
	List<Node> children;
	Node parent;
	
	public Node(String node)
	{
		this.id = 0;
		this.node = node;
		//empty list denotes to a leaf node
		this.children = Collections.emptyList();
		this.parent = null;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		Node other = (Node) obj;
		return this.node.equals(other.node) && this.parent.equals(other.parent);
	}
	
	@Override
	public int hashCode() 
	{
		int hash = 0;
		for(int i = 0; i < this.node.length(); i++)
		{
			hash += i * this.node.charAt(i);
		}
		for(int i = 0; i < this.parent.node.length(); i++)
		{
			hash += i * this.parent.node.charAt(i);
		}
		return hash;
	}
}
