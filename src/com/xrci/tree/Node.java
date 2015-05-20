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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Node for the classification tree
 * @author Aritra Dhar
 */
public class Node<T>
{
	int id;
	T node;
	List<Node<T>> children;
	Node<T> parent;
	float weight;
	List<Integer> databaseIndex;
	int level;
	
	public Node(T node)
	{
		this.id = 0;
		this.node = node;
		this.level = 0;
		//empty list denotes to a leaf node
		this.children = Collections.emptyList();
		this.parent = null;
		this.weight = 0.0f;
		this.databaseIndex = Collections.emptyList();
	}
	
	public Node(T node, int level)
	{
		this.id = 0;
		this.node = node;
		this.level = level;
		//empty list denotes to a leaf node
		this.children = Collections.emptyList();
		this.parent = null;
		this.weight = 0.0f;
		this.databaseIndex = Collections.emptyList();
	}
	
	/**
	 * make a classification tree node from an existing node
	 * @param node input node
	 */
	public Node(Node<T> node)
	{
		this.node =node.node;
		this.id = node.id;
		this.level = node.level;
		this.children = node.children;
		this.parent = node.parent;
		this.weight = node.weight;
		this.databaseIndex = node.databaseIndex;
	}
	
	public void setDatabaseIndex(String[] indexString)
	{
		Integer[] data = new Integer[indexString.length];

		int i = 0;
		for(String s : indexString)
		{
			data[i++] = Integer.parseInt(s);
		}
		this.databaseIndex = new ArrayList<Integer>(Arrays.asList(data));
	}
	
	public boolean isLeaf()
	{
		return this.children.isEmpty();
	}
	
	public void setDatabaseIndex(List<Integer> databaseIndex) 
	{
		this.databaseIndex = databaseIndex;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) 
	{
		Node<T> other = (Node<T>) obj;
		return (this.parent != null) ? 
				this.node.equals(other.node) && this.parent.equals(other.parent) : 
					this.node.equals(other.node);
	}
	
	@Override
	public int hashCode() 
	{
		int hash = 0;
		for(int i = 0; i < this.node.toString().length(); i++)
		{
			hash += i * this.node.toString().charAt(i);
		}
		/*
		 * adjusted for root node
		 */
		if (parent != null)
		{
			for(int i = 0; i < this.parent.node.toString().length(); i++)
			{
				hash += i * this.parent.node.toString().charAt(i);
			}
		}
		return hash;
	}
	
	@Override
	public String toString() 
	{
		return this.node.toString();
	}
}
