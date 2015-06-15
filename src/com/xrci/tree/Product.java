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
import java.util.HashSet;
import java.util.Set;

public class Product<T> 
{
	T productName;
	String url;
	Set<Node<T>> parents;
	
	public Product(T productName) 
	{
		this.productName = productName;
		this.parents = Collections.emptySet();
	}
	
	public Product(T productName, String url)
	{
		this.productName = productName;
		this.url = url;
		this.parents = Collections.emptySet();
	}
	
	public Set<Node<T>> getParents()
	{
		return this.parents;
	}
	
	public void addParents(Set<Node<T>> parents)
	{
		if(this.parents.isEmpty())
		{
			this.parents = parents;
		}
		else
		{
			this.parents.addAll(parents);
		}
	}
	
	public void addParents(Node<T> parent)
	{
		if(this.parents.isEmpty())
		{
			this.parents = new HashSet<>();
		}
		this.parents.add(parent);
		
	}
	
	@Override
	public String toString() 
	{
		return this.productName.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) 
	{
		Product<T> other = (Product<T>) obj;
		return this.toString().equals(other.toString());
	}
}
