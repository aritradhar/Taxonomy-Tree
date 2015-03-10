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
import java.util.Collections;
import java.util.List;

public class ClassificationTree 
{
	private Node ROOT;
	private int size;
	private boolean sizeCalled;
	private int c;
	private int height;
	private int recalulatedSize;
	private int recalculatedHeight;
	
	public ClassificationTree(Node Root)
	{
		this.ROOT = Root;
		this.sizeCalled = false;
		this.leaveCount();
		this.size = this.recalulatedSize;
		this.height = this.recalculatedHeight;
	}
	
	
	public ClassificationTree()
	{
		this.ROOT = new Node("root");
		sizeCalled = false;
	}

	public Node getTree()
	{
		return this.ROOT;
	}
	
	public int size()
	{
		return this.size;
	}
	
	public int leaves()
	{
		if(!this.sizeCalled)
		{
			this.c = this.leaveCount();
			this.sizeCalled = true;
			return this.c;
		}
		else
		{
			return this.c;
		}
	}
	public int height()
	{
		return this.height;
	}

	public void insert(String[] elements)
	{
		if(elements.length > this.height)
			this.height = elements.length;
		
		//needs to recalculate for the size function
		this.sizeCalled = false;
		
		Node temp = this.ROOT;

		for(String element : elements)
		{
			if(temp.children.isEmpty())
			{
				Node newNode = new Node(element);
				temp.children = new ArrayList<Node>();
				temp.children.add(newNode);
				newNode.parent = temp;
				temp = newNode;
				size++;
			}

			else
			{
				boolean found = false;
				for(Node nodeIn : temp.children)
				{
					if(nodeIn.node.equals(element))
					{
						temp = nodeIn;
						found = true;
						//System.out.println("hit");
						break;
					}
				}

				if(!found)
				{
					Node newNode = new Node(element);
					//temp.children = new ArrayList<Node>();
					temp.children.add(newNode);
					newNode.parent = temp;
					temp = newNode;
					size++;
				}
			}
		}
	}
	
	private int leaveCount()
	{
		int rc = 0, rh = 0;
		int c = 0;
		Node temp = ROOT;
		List<Node> list1 = new ArrayList<Node>();
		List<Node> list2 = new ArrayList<Node>();
		
		list1.add(temp);
		
		while(!list1.isEmpty())
		{
			for(Node ne : list1)
			{
				if(ne.children.isEmpty())
					c++;
				else
					list2.addAll(ne.children);
			}
			rc += list2.size();
			list1.clear();
			list1.addAll(list2);
			list2.clear();
			rh++;
		}
		
		this.recalulatedSize = rc;
		this.recalculatedHeight = rh - 1;
		return c;
	}
	
	/**
	 * @param {@code search} string
	 * @return {@code null} if not found
	 * 		else returns {@code node}
	 */
	public Node search(String search)
	{
		Node temp = ROOT;
		List<Node> list1 = new ArrayList<Node>();
		List<Node> list2 = new ArrayList<Node>();
		
		list1.add(temp);
		
		while(!list1.isEmpty())
		{
			for(Node ne : list1)
			{
				if(ne.node.equals(search))
					return ne;
				list2.addAll(ne.children);
			}
			list1.clear();
			list1.addAll(list2);
			list2.clear();
		}
		return null;
	}
	
	
	public List<Node> totalLeavesUnder(String search)
	{
		Node currNode = this.search(search);
		
		if(currNode == null)
			return Collections.emptyList();
		
		List<Node> list1 = new ArrayList<Node>();
		List<Node> list2 = new ArrayList<Node>();
		
		//still confused if to return an empty list
		if(currNode.children.isEmpty())
			return Arrays.asList(new Node[]{currNode});
		
		List<Node> leaves = new ArrayList<Node>();
		while(!list1.isEmpty())
		{
			for(Node ni : list1)
			{
				if(ni.children.isEmpty())
					leaves.add(ni);
				else
					list2.addAll(ni.children);
			}
			list1.clear();
			list1.addAll(list2);
			list2.clear();
		}
		
		list1.add(currNode);
		
		return leaves;
	}


	/*
	 * test
	 */
	public static void main(String[] args) 
	{
		ClassificationTree tree = new ClassificationTree();
		tree.insert(new String[]{"a", "c"});
		tree.insert(new String[]{"a", "d"});
		tree.insert(new String[]{"b", "e", "f"});
		
		System.out.println(tree.search("g"));
	}
}
