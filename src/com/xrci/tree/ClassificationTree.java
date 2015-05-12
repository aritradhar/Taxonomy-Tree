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
import java.util.HashMap;
import java.util.List;

/**
 * n-ary tree to index product taxonomy
 * as a generic classification tree
 * @author Aritra Dhar
 * <p>

 * </p>
 */
public class ClassificationTree<T> 
{
	public static final String ROOT_NODE = "root";
	private Node<T> ROOT;
	private int size;
	private boolean sizeCalled;
	private int leafCount_internal;
	private int height;
	private int recalulatedSize;
	private int recalculatedHeight;
	
	public ClassificationTree(Node<T> Root)
	{
		this.ROOT = Root;
		this.sizeCalled = false;
		this.recalculateLeafCount();
		this.size = this.recalulatedSize;
		this.height = this.recalculatedHeight;
	}
	
	@Override
	public String toString() 
	{
		return this.ROOT.toString();
	}
	
	@SuppressWarnings("unchecked")
	public ClassificationTree()
	{
		this.ROOT = new Node<T>((T) ROOT_NODE);
		sizeCalled = false;
	}

	public Node<T> getTree()
	{
		return this.ROOT;
	}
	
	/**
	 * 
	 * @return no of nodes in the classification tree
	 */
	public int size()
	{
		return this.size;
	}
	
	/**
	 * 
	 * @return total number of leaf nodes in 
	 * the classification tree
	 */
	public int leafCount()
	{
		if(!this.sizeCalled)
		{
			this.leafCount_internal = this.recalculateLeafCount();
			this.sizeCalled = true;
			return this.leafCount_internal;
		}
		else
		{
			return this.leafCount_internal;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int height()
	{
		return this.height;
	}
	
	@SuppressWarnings("unchecked")
	public float getWeight(T search)
	{
		if(search instanceof Node<?>)
			return getWeight(((Node<T>) search).node);
		
		if(search == null)
			throw new IllegalArgumentException("null argument passed");
			
		Node<T> node = this.search(search);
		
		if(node == null)
			throw new IllegalArgumentException(search + " not exists in the tree");
		
		return node.weight;
	}
	
	public List<Integer> getDatabaseIndex(T search)
	{
		if(search == null)
			throw new IllegalArgumentException("null argument passed");
			
		Node<T> node = this.search(search);
		
		if(node == null)
			throw new IllegalArgumentException(search + " not exists in the tree");
		
		return node.databaseIndex;
	}
	
	/**
	 * bring all leaf nodes at same level
	 */
	public void bringToSameLevel()
	{
		this.bringToSameLevel(ROOT);
	}
	
	private Node<T> bringToSameLevel(Node<T> node)
	{
		if(node.children.isEmpty() || node == null)
		{
			Node<T> tempNode = new Node<>(node);
			this.delete(node.node);
			return node;
		}
		else
		{
			for(Node<T> child : node.children)
			{
				return bringToSameLevel(child);
			}
			
			return null;
		}
	}
	/**
	 * General insert method
	 * @param elements to be inserted
	 */
	public void insert(T[] elements)
	{
		if(elements == null)
			throw new IllegalArgumentException("null argument passed");

		//update the max height of the tree
		if(elements.length > this.height)
			this.height = elements.length;
		
		//needs to recalculate for the size function
		this.sizeCalled = false;
		
		Node<T> temp = this.ROOT;

		for(T element : elements)
		{
			if(element == null)
			{
				System.err.println("null elemnt fount in argument, skipping");
				continue;
			}
			
			if(element.toString().equals(ROOT_NODE))
			{
				throw new IllegalArgumentException("Node name " + ROOT_NODE + " is forbidden " );
			}
			
			if(temp.children.isEmpty())
			{
				Node<T> newNode = new Node<>(element);
				temp.children = new ArrayList<Node<T>>();
				temp.children.add(newNode);
				newNode.parent = temp;
				temp = newNode;
				size++;
			}

			else
			{
				boolean found = false;
				for(Node<T> nodeIn : temp.children)
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
					Node<T> newNode = new Node<T>(element);
					//temp.children = new ArrayList<Node>();
					temp.children.add(newNode);
					newNode.parent = temp;
					temp = newNode;
					size++;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param node Node object to insert
	 * @param parent Parent node under which {@code node} to be inserted
	 */
	public void insert(Node<T> node, T parent)
	{
		if(node == null || parent == null)
			throw new IllegalArgumentException("null argument passed");
		
		Node<T> parentSearch = search(parent);
		
		if(parentSearch == null)
			throw new IllegalArgumentException("Parent " + parent + " not found");
		
		parentSearch.children.add(node);
		node.parent = parentSearch;
		
		Node<T> tempNode = parentSearch;
		
		//update data up-to root node
		while(tempNode.parent != null)
		{
			tempNode.weight += node.weight;
			tempNode.databaseIndex.addAll(node.databaseIndex);
			
			tempNode = tempNode.parent;
		}
	}
	
	public void insert(Node<T> node, Node<T> parent)
	{
		if(node == null || parent == null)
			throw new IllegalArgumentException("null argument passed");
		
		Node<T> parentSearch = search(parent);
		
		if(parentSearch == null)
			throw new IllegalArgumentException("Parent " + parent.node + " not found");
		
		parentSearch.children.add(node);
		node.parent = parentSearch;
		
		Node<T> tempNode = parentSearch;
		
		//update data up-to root node
		while(tempNode.parent != null)
		{
			tempNode.weight += node.weight;
			tempNode.databaseIndex.addAll(node.databaseIndex);
			
			tempNode = tempNode.parent;
		}
	}
	
	/**
	 * 
	 * @param Searchnode node to be inserted
	 * @param parent Node which is the parent
	 */
	public void insert(T Searchnode, T parent)
	{
		if(Searchnode == null || parent == null)
			throw new IllegalArgumentException("null argument passed");
		
		Node<T> parentSearch = search(parent);
		
		if(parentSearch == null)
			throw new IllegalArgumentException("Parent " + parent + " not found");
		
		Node<T> node = new Node<>(Searchnode);
		
		parentSearch.children.add(node);
		node.parent = parentSearch;
		
		Node<T> tempNode = parentSearch;
		
		//update data up-to root node
		while(tempNode.parent != null)
		{
			tempNode.weight += node.weight;
			tempNode.databaseIndex.addAll(node.databaseIndex);
			
			tempNode = tempNode.parent;
		}
	}
	
	private int recalculateLeafCount()
	{
		int rc = 0, rh = 0;
		int c = 0;
		Node<T> temp = ROOT;
		List<Node<T>> list1 = new ArrayList<Node<T>>();
		List<Node<T>> list2 = new ArrayList<Node<T>>();
		
		list1.add(temp);
		
		while(!list1.isEmpty())
		{
			for(Node<T> ne : list1)
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
	
	public Node<T> search(Node<T> search)
	{
		Node<T> toSearch = (Node<T>) search;
		Node<T> searched =  search(toSearch.node);
		if(searched.equals(toSearch))
			return toSearch;
		else
			return null;
	}
	/**
	 * @param  search {@code search} string
	 * @return {@code null} if not found
	 * 		else returns {@code node}
	 */

	public Node<T> search(T search)
	{
		
		if(search == null)
			throw new IllegalArgumentException("null argument passed");
			
		
		Node<T> temp = ROOT;
		List<Node<T>> list1 = new ArrayList<Node<T>>();
		List<Node<T>> list2 = new ArrayList<Node<T>>();
		
		list1.add(temp);
		
		while(!list1.isEmpty())
		{
			for(Node<T> ne : list1)
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
	 * @return list of leaves
	 */
	@SuppressWarnings("unchecked")
	public List<Node<T>> getAllLeaves()
	{
		return this.totalLeavesUnder((T) ROOT_NODE);
	}
	
	/**
	 * @param search string
	 * @return returns a list of nodes
	 */
	@SuppressWarnings("unchecked")
	public List<Node<T>> totalLeavesUnder(T search)
	{
		if(search instanceof Node<?>)
			return totalLeavesUnder(((Node<T>) search).node);
		
		
		if(search == null)
			throw new IllegalArgumentException("null argument passed");
			
		Node<T> currNode = this.search(search);
		
		if(currNode == null)
			return Collections.emptyList();
		
		List<Node<T>> list1 = new ArrayList<Node<T>>();
		List<Node<T>> list2 = new ArrayList<Node<T>>();
		
		//still confused if to return an empty list
		if(currNode.children.isEmpty())
			return Arrays.asList(new Node[]{currNode});
		
		List<Node<T>> leaves = new ArrayList<Node<T>>();
		list1.add(currNode);
		
		while(!list1.isEmpty())
		{
			for(Node<T> ni : list1)
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
	/**
	 * 
	 * @param search string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Node<T>> totalNodesUnder(T search)
	{
		if(search instanceof Node<?>)
			return totalNodesUnder(((Node<T>) search).node);
			
		if(search == null)
			throw new IllegalArgumentException("null argument passed");
			
		Node<T> node = this.search(search);
		
		if(node == null)
			throw new IllegalArgumentException("node " + search + " not found");
		
		if(node.isLeaf())
			return Collections.emptyList();
		
		List<Node<T>> list1 = new ArrayList<Node<T>>();
		List<Node<T>> list2 = new ArrayList<Node<T>>();
		
		List<Node<T>> out = new ArrayList<Node<T>>();
		list1.add(node);
		
		while(!list1.isEmpty())
		{
			for(Node<T> ne : list1)
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
	
	public List<Node<T>> levelOrder(int level)
	{
		if(level < 0 || level > this.height)
			throw new IllegalArgumentException("level " + level + " is invalid");
			
		List<Node<T>> out = new ArrayList<>();
		
		Node<T> node = this.ROOT;
		List<Node<T>> list1 = new ArrayList<Node<T>>();
		List<Node<T>> list2 = new ArrayList<Node<T>>();
		list1.add(node);
		int i = 0;
		
		while(!list1.isEmpty())
		{
			if(i == level)
				break;
			
			for(Node<T> ne : list1)
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
	
	/**
	 * 
	 * @param search target node
	 * @param weight new weight
	 */
	@SuppressWarnings("unchecked")
	public void addOrModifyWeight(T search, float weight)
	{
		if(search instanceof Node<?>)
			addOrModifyWeight(((Node<T>) search).node, weight);	
		
		if(search == null)
			throw new IllegalArgumentException("null argument passed");
		
		if(weight < 0)
			throw new IllegalArgumentException("Invalid weight");
		
		Node<T> node = this.search(search);
		if(node == null)
			throw new IllegalArgumentException(search + " not exists in the tree");
		
		if(!node.isLeaf())
			throw new RuntimeException("Can not modify weight of non leaf node : " + search);
		
		node.weight = weight;
		this.normalizeWeight();
	}
	
	/**
	 * 
	 * @param search target node
	 * @param databaseIndex modified indices
	 */
	@SuppressWarnings("unchecked")
	public void addOrModifyDatabaseIndex(T search, List<Integer> databaseIndex)
	{
		if(search instanceof Node<?>)
			addOrModifyDatabaseIndex(((Node<T>) search).node, databaseIndex);
		
		if(search instanceof Node<?>)
			addOrModifyDatabaseIndex(((Node<T>)search).node, databaseIndex);
			
		if(search == null || databaseIndex == null) 
			throw new IllegalArgumentException("null argument passed");
		
		Node<T> node = this.search(search);
		if(node == null)
			throw new IllegalArgumentException(search + " not exists in the tree");
		
		if(!node.isLeaf())
			throw new RuntimeException("Can not modify weight of non leaf node : " + search);
		
		node.databaseIndex = databaseIndex;
		this.normalizeDatabaseIndex();
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
	@SuppressWarnings("unchecked")
	public boolean delete(T search)
	{
		if(search instanceof Node<?>)
			return delete(((Node<T>) search).node);
		
		if(search == null)
			throw new IllegalArgumentException("null argument passed");
		
		Node<T> currNode = this.search(search);
		
		if(currNode == null)
			return false;
		
		List<Node<T>> children = currNode.parent.children;
		int index = 0;
		for(Node<T> child : children)
		{
			if(child.node.equals(currNode.node))
				break;
			
			index++;
		}
		children.remove(index);
		currNode = null;
		//weight + database index fix
		this.normalizeDatabaseIndex();
		//tree size fix
		this.sizeCalled = false;
		this.recalculateLeafCount();
		this.size = this.recalulatedSize;
		this.height = this.recalculatedHeight;
		return true;
	}

	public void normalize()
	{
		this.normalizeDatabaseIndex();
		this.normalizeWeight();
	}
	public void normalizeDatabaseIndex()
	{
		this.normalizeDatabaseIndex_recursive(this.ROOT);
	}
	
	public void normalizeWeight()
	{
		this.normalizeWeight_recursive(this.ROOT);
	}
	
	private float normalizeWeight_recursive(Node<T> node)
	{
		if(node.isLeaf())
			return node.weight;
		
		node.weight = 0.0f;
		for(Node<T> child : node.children)
		{
			node.weight += normalizeWeight_recursive(child);
		}
		
		return node.weight;
	}
	
	private List<Integer> normalizeDatabaseIndex_recursive(Node<T> node)
	{
		if(node.isLeaf())
			return node.databaseIndex;
		
		node.databaseIndex = new ArrayList<>();
		
		for(Node<T> child : node.children)
		{
			node.databaseIndex.addAll(normalizeDatabaseIndex_recursive(child));
		}
		
		return node.databaseIndex;
	}
	
	/**
	 * Experimental feature to change the weight 
	 * only through the path to the root node without
	 * changing the entire tree
	 * 
	 * @param node
	 * @param weight
	 */
	@SuppressWarnings("unchecked")
	public void addOrModifyWeight_efficient(T search, float weight)
	{
		if(search instanceof Node<?>)
		{
			Node<T> searchNode = (Node<T>) search;
			Node<T> node = this.search(searchNode.node);
			if(node.equals(searchNode))
				throw new IllegalArgumentException(searchNode.node + " not exists in the tree");
			
			this.weightDeltaFix(searchNode, weight);
		}
		
		if(search == null)
			throw new IllegalArgumentException("null argument passed");
		
		if(weight < 0)
			throw new IllegalArgumentException("Invalid weight");
		
		Node<T> node = this.search(search);
		if(node == null)
			throw new IllegalArgumentException(search + " not exists in the tree");
		
		this.weightDeltaFix(node, weight);
		
	}
	private void weightDeltaFix(Node <T>node, float weight)
	{
		float delta = weight - node.weight;
		node.weight = weight;
		while(node.parent != null)
		{
			node = node.parent;
			node.weight += delta;
		}
	}

	/*
	 * test
	 */
	public static void main(String[] args) 
	{
		ClassificationTree<String> tree = new ClassificationTree<>();
		tree.insert(new String[]{"a", "c"});
		tree.insert(new String[]{"a", "d"});
		tree.insert(new String[]{"b", "e", "f"});
		
		//tree.delete("a");
		tree.addOrModifyWeight("f", 0.5f);
		tree.addOrModifyWeight("c", 0.5f);
		tree.insert(new String[]{"a", "c"});
		tree.insert("x", "a");
		//tree.addOrModifyWeight_efficient("c", 0);
		//tree.normalizeWeight();
		tree.addOrModifyDatabaseIndex("c", new ArrayList<>(Arrays.asList(new Integer[]{0,1,2,3})));
		tree.addOrModifyDatabaseIndex("d", new ArrayList<>(Arrays.asList(new Integer[]{4,5,6,7})));
		
		System.out.println(tree.getDatabaseIndex("a"));
		
		System.out.println(tree.getWeight(ClassificationTree.ROOT_NODE));
		System.out.println(tree.getAllLeaves().size());
		System.out.println(tree.search("g"));
	}
}
 