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

/**
 * n-ary tree to index product taxonomy
 * as a classification tree
 * @author Aritra Dhar
 * <p>

 * </p>
 */
public class ClassificationTree 
{
	private Node ROOT;
	private int size;
	private boolean sizeCalled;
	private int leafCount_internal;
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
	
	@Override
	public String toString() 
	{
		return this.ROOT.toString();
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
			this.leafCount_internal = this.leaveCount();
			this.sizeCalled = true;
			return this.leafCount_internal;
		}
		else
		{
			return this.leafCount_internal;
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
				if(ne.isLeaf())
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
	 * @param  search {@code search} string
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
	
	/**
	 * 
	 * @param search string
	 * @return returns a list of nodes
	 */
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
		list1.add(currNode);
		
		while(!list1.isEmpty())
		{
			for(Node ni : list1)
			{
				if(ni.isLeaf())
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
	
	public List<Node> totalNodesUnder(String search)
	{
		Node node = this.search(search);
		
		if(node == null)
			return Collections.emptyList();
		
		List<Node> list1 = new ArrayList<Node>();
		List<Node> list2 = new ArrayList<Node>();
		
		List<Node> out = new ArrayList<Node>();
		list1.add(node);
		
		while(!list1.isEmpty())
		{
			for(Node ne : list1)
			{
				if(!ne.isLeaf())
				{
					out.addAll(ne.children);
					list2.addAll(ne.children);
				}
			}
			list1.clear();
			list1.addAll(list2);
			list2.clear();
		}
		
		
		return out;
	}
	
	public List<Node> levelOrder(int level)
	{
		List<Node> out = new ArrayList<>();
		
		Node node = this.ROOT;
		List<Node> list1 = new ArrayList<Node>();
		List<Node> list2 = new ArrayList<Node>();
		list1.add(node);
		int i = 0;
		
		while(!list1.isEmpty())
		{
			if(i == level)
				break;
			
			for(Node ne : list1)
			{
				if(!ne.isLeaf())
				{
					if(i+1 == level)
						out.addAll(ne.children);
					list2.addAll(ne.children);
				}
			}
			list1.clear();
			list1.addAll(list2);
			list2.clear();
			i++;
			
		}
		
		return out;
	}
	
	public void addOrModifyWeight(String search, float weight)
	{
		Node node = this.search(search);
		if(node == null)
			throw new RuntimeException(search + " not found in the tree");
		
		if(!node.isLeaf())
			throw new RuntimeException("Can not modify weight of non leaf node");
		
		node.weight = weight;
		this.normalizeWeight();
	}
	
	/**
	 * experimental features
	 * @param search {@code search} string to 
	 * delete and all if its children
	 * 
	 * @return {@code boolean value}
	 * {@code true} if found and deleted
	 * {@code false} if the {@code search}
	 * string not found
	 */
	public boolean delete(String search)
	{
		Node currNode = this.search(search);
		
		if(currNode == null)
			return false;
		
		List<Node> children = currNode.parent.children;
		int index = 0;
		for(Node child : children)
		{
			if(child.node.equals(currNode.node))
				break;
			
			index++;
		}
		children.remove(index);
		currNode = null;
		this.normalizeWeight();
		return true;
	}

	public void normalizeWeight()
	{
		this.normalizeWeight_recursive(this.ROOT);
	}
	
	private float normalizeWeight_recursive(Node node)
	{
		if(node.isLeaf())
			return node.weight;
		
		for(Node child : node.children)
		{
			node.weight += normalizeWeight_recursive(child);
		}
		
		return node.weight;
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
		
		//tree.delete("a");
		tree.addOrModifyWeight("e", 0.5f);
		tree.normalizeWeight();
		
		System.out.println(tree.totalLeavesUnder("root").size());
		System.out.println(tree.search("g"));
	}
}
 