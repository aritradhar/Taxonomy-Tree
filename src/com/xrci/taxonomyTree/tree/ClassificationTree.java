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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * n-ary tree to index product taxonomy as a generic classification tree
 * 
 * @author Aritra Dhar
 *         <p>
 * 
 *         </p>
 */
public class ClassificationTree<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -317766179709642912L;
	public static Node<?> DUMMY_NODE;
	static {
		DUMMY_NODE = new Node<String>("Dummy");
	}

	public static final String ROOT_NODE = "root";
	private Node<T> ROOT;
	private int size;
	private boolean sizeCalled;
	private int leafCount_internal;
	private int height;
	private int recalulatedSize;
	private int recalculatedHeight;

	public ClassificationTree(Node<T> Root) {
		this.ROOT = Root;
		this.sizeCalled = false;
		this.recalculateLeafCount();
		this.size = this.recalulatedSize;
		this.height = this.recalculatedHeight;
	}

	@Override
	public String toString() {
		return this.ROOT.toString();
	}

	@SuppressWarnings("unchecked")
	public ClassificationTree() {
		this.ROOT = new Node<T>((T) ROOT_NODE);
		sizeCalled = false;
	}

	/**
	 * This will return the head of the classification tree
	 * 
	 * @return ROOT node
	 */
	public Node<T> getTree() {
		return this.ROOT;
	}

	/**
	 * 
	 * @return no of nodes in the classification tree
	 */
	public int size() {
		return this.size;
	}

	/**
	 * 
	 * @return total number of leaf nodes in the classification tree
	 */
	public int leafCount() {
		if (!this.sizeCalled) {
			this.leafCount_internal = this.recalculateLeafCount();
			this.sizeCalled = true;
			return this.leafCount_internal;
		} else {
			return this.leafCount_internal;
		}
	}

	/**
	 * n
	 * 
	 * @return Maximum number of children of a node
	 */
	@SuppressWarnings("unchecked")
	public int maxFanOut() {
		int fanOut = 0;
		List<Node<T>> nodes = this.totalNodesUnder((T) ROOT_NODE);
		for (Node<T> node : nodes) {
			if (fanOut < node.children.size()) {
				fanOut = node.children.size();
			}
		}

		return fanOut;
	}

	/**
	 * May give incorrect height if any unsafe operation is done without doing
	 * any consistency check. Otherwise safe to use.
	 * 
	 * @return tree height
	 */
	public int height() {
		return this.height;
	}

	/**
	 * @param search
	 *            node string
	 * @return Weight
	 */
	@SuppressWarnings("unchecked")
	public float getWeight(T search) {
		if (search instanceof Node<?>) {
			return getWeight(((Node<T>) search).node);
		}

		if (search == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		Node<T> node = this.search(search);

		if (node == null) {
			throw new IllegalArgumentException(search
					+ " not exists in the tree");
		}

		return node.weight;
	}

	/**
	 * 
	 * @param search
	 *            Node string
	 * @return Database indices
	 */
	public List<Integer> getDatabaseIndex(T search) {
		if (search == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		Node<T> node = this.search(search);

		if (node == null) {
			throw new IllegalArgumentException(search
					+ " not exists in the tree");
		}

		return node.databaseIndex;
	}

	/**
	 * bring all leaf nodes at same level. Uses unsafe delete method. Handles
	 * tree consistency after bulk unsafe delete operations.
	 */
	@SuppressWarnings("unchecked")
	public void bringLeavesToSameLevel() {
		List<Node<T>> leaves = this.getAllLeaves();

		// Manipulate the node only if the node is a leaf node
		for (Node<T> node : leaves) {
			if (node.level < this.height) {
				Node<T> tempNode = new Node<>(node);
				Node<T> parent = node.parent;
				this.deleteUnsafe(node);
				int toInsertIter = this.height - node.level;

				for (int i = 0; i < toInsertIter; i++) {
					Node<T> Dummy = new Node<T>((T) "Dummy");
					this.inserUnsafe(Dummy, parent);
					parent = Dummy;
				}
				/*
				 * insert the original leaf node at the back of the newly
				 * inserted dummy node
				 */
				this.inserUnsafe(tempNode, parent);
			}
		}
		// consistency check as deleteUnsafe() will not do that
		this.normalizeDatabaseIndex();
		// tree size fix
		this.sizeCalled = false;
		this.recalculateLeafCount();
		this.size = this.recalulatedSize;
		this.height = this.recalculatedHeight;

	}

	/**
	 * not to be used in general scenario
	 * 
	 * @param node
	 * @param parent
	 */
	private void inserUnsafe(Node<T> node, Node<T> parent) {
		if (parent.children.isEmpty()) {
			parent.children = new ArrayList<>();
		}

		parent.children.add(node);
		parent.weight += node.weight;

		if (parent.databaseIndex.isEmpty()) {
			parent.databaseIndex = new ArrayList<>();
		}

		parent.databaseIndex.addAll(node.databaseIndex);

		node.parent = parent;
		node.level = node.parent.level + 1;
	}

	/**
	 * General insert method Last element of the elements is the actual product
	 * 
	 * @param elements
	 *            to be inserted
	 */
	public void insert(T[] _elements) {
		if (_elements == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		T[] elements = Arrays.copyOf(_elements, _elements.length - 2);
		T productName = _elements[_elements.length - 2];
		String url = (String) _elements[_elements.length - 1];

		Product product = (!ProductStore.ProductMap.containsKey(productName
				.toString())) ? new Product(productName.toString(), url)
				: ProductStore.ProductMap.get(productName);

		if (!ProductStore.ProductMap.containsKey(productName.toString())) {
			ProductStore.ProductMap.put(productName.toString(), product);
		}

		// update the max height of the tree
		if (elements.length > this.height) {
			this.height = elements.length;
		}

		// needs to recalculate for the size function
		this.sizeCalled = false;

		Node<T> temp = this.ROOT;

		int i = 0;
		for (T element : elements) {
			if (element == null) {
				System.err.println("null elemnt fount in argument, skipping");
				continue;
			}

			if (element.toString().equals(ROOT_NODE)) {
				throw new IllegalArgumentException("Node name " + ROOT_NODE
						+ " is forbidden ");
			}

			if (temp.children.isEmpty()) {
				Node<T> newNode = new Node<>(element, ++i);
				temp.children = new ArrayList<Node<T>>();
				temp.children.add(newNode);
				newNode.parent = temp;
				temp = newNode;
				size++;
			}

			else {
				boolean found = false;
				for (Node<T> nodeIn : temp.children) {
					if (nodeIn.node.equals(element)) {
						i++;
						temp = nodeIn;
						found = true;
						// System.out.println("hit");
						break;
					}
				}

				if (!found) {
					Node<T> newNode = new Node<T>(element);
					// temp.children = new ArrayList<Node>();
					temp.children.add(newNode);
					newNode.parent = temp;
					newNode.level = newNode.parent.level + 1;
					temp = newNode;
					size++;
				}
			}
		}

		temp.addProduct(product);

	}

	/**
	 * General insert method
	 * 
	 * @param _elements
	 * @param leagacyMode
	 *            Works with google's Taxonomy tree
	 */
	public void insert(T[] elements, boolean leagacyMode) {
		if (elements == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		// update the max height of the tree
		if (elements.length > this.height) {
			this.height = elements.length;
		}

		// needs to recalculate for the size function
		this.sizeCalled = false;

		Node<T> temp = this.ROOT;

		int i = 0;
		for (T element : elements) {
			if (element == null) {
				System.err.println("null elemnt fount in argument, skipping");
				continue;
			}

			if (element.toString().equals(ROOT_NODE)) {
				throw new IllegalArgumentException("Node name " + ROOT_NODE
						+ " is forbidden ");
			}

			if (temp.children.isEmpty()) {
				Node<T> newNode = new Node<>(element, ++i);
				temp.children = new ArrayList<Node<T>>();
				temp.children.add(newNode);
				newNode.parent = temp;
				temp = newNode;
				size++;
			}

			else {
				boolean found = false;
				for (Node<T> nodeIn : temp.children) {
					if (nodeIn.node.equals(element)) {
						i++;
						temp = nodeIn;
						found = true;
						// System.out.println("hit");
						break;
					}
				}

				if (!found) {
					Node<T> newNode = new Node<T>(element);
					// temp.children = new ArrayList<Node>();
					temp.children.add(newNode);
					newNode.parent = temp;
					newNode.level = newNode.parent.level + 1;
					temp = newNode;
					size++;
				}
			}
		}
	}

	/**
	 * 
	 * @param node
	 *            Node object to insert
	 * @param parent
	 *            Parent node under which {@code node} to be inserted
	 */
	public void insert(Node<T> node, T parent) {
		if (node == null || parent == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		Node<T> parentSearch = search(parent);

		if (parentSearch == null) {
			throw new IllegalArgumentException("Parent " + parent
					+ " not found");
		}

		parentSearch.children.add(node);
		node.parent = parentSearch;
		node.level = parentSearch.level + 1;

		Node<T> tempNode = parentSearch;

		// update data up-to root node
		while (tempNode.parent != null) {
			tempNode.weight += node.weight;
			tempNode.databaseIndex.addAll(node.databaseIndex);

			tempNode = tempNode.parent;
		}
	}

	public void insert(Node<T> node, Node<T> parent) {
		if (node == null || parent == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		Node<T> parentSearch = search(parent);

		if (parentSearch == null) {
			throw new IllegalArgumentException("Parent " + parent.node
					+ " not found");
		}

		// for empty list
		if (parentSearch.children.isEmpty()) {
			parentSearch.children = new ArrayList<>();
		}

		parentSearch.children.add(node);
		node.parent = parentSearch;
		node.level = parentSearch.level + 1;

		Node<T> tempNode = parentSearch;

		// update data up-to root node
		while (tempNode.parent != null) {
			tempNode.weight += node.weight;
			tempNode.databaseIndex.addAll(node.databaseIndex);

			tempNode = tempNode.parent;
		}
	}

	/**
	 * 
	 * @param Searchnode
	 *            node to be inserted
	 * @param parent
	 *            Node which is the parent
	 */
	public void insert(T Searchnode, T parent) {
		if (Searchnode == null || parent == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		Node<T> parentSearch = search(parent);

		if (parentSearch == null) {
			throw new IllegalArgumentException("Parent " + parent
					+ " not found");
		}

		Node<T> node = new Node<>(Searchnode);

		parentSearch.children.add(node);
		node.parent = parentSearch;
		node.level = parentSearch.level + 1;

		Node<T> tempNode = parentSearch;

		// update data up-to root node
		while (tempNode.parent != null) {
			tempNode.weight += node.weight;
			tempNode.databaseIndex.addAll(node.databaseIndex);

			tempNode = tempNode.parent;
		}
	}

	/**
	 * Consistency checker. For any unsafe method use this one to repair the
	 * consistency variables of the tree. All the unsafe methods and consistency
	 * check is kept private to make it off limit.
	 * 
	 * @return Total number of leaf nodes
	 */
	private int recalculateLeafCount() {
		int rc = 0, rh = 0;
		int c = 0;
		Node<T> temp = ROOT;
		List<Node<T>> list1 = new ArrayList<Node<T>>();
		List<Node<T>> list2 = new ArrayList<Node<T>>();

		list1.add(temp);

		while (!list1.isEmpty()) {
			for (Node<T> ne : list1) {
				if (ne.isLeaf()) {
					c++;
				} else {
					list2.addAll(ne.children);
				}
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

	public Node<T> search(Node<T> search) {
		Node<T> toSearch = search;
		Node<T> searched = search(toSearch.node);
		if (searched.equals(toSearch)) {
			return toSearch;
		} else {
			return null;
		}
	}

	/**
	 * @param search
	 *            {@code search} string
	 * @return {@code null} if not found else returns {@code node}
	 */
	public Node<T> search(T search) {
		if (search == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		Node<T> temp = ROOT;
		List<Node<T>> list1 = new ArrayList<Node<T>>();
		List<Node<T>> list2 = new ArrayList<Node<T>>();

		list1.add(temp);

		while (!list1.isEmpty()) {
			for (Node<T> ne : list1) {
				if (ne.node.equals(search)) {
					return ne;
				}
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
	 * @param search
	 *            leaf id to search
	 * @return Leaf object if found else null
	 */
	public Node<T> searchInleaves(T search) {
		List<Node<T>> leaves = this.getAllLeaves();
		for (Node<T> leaf : leaves) {
			if (search.equals(leaf.node)) {
				return leaf;
			}
		}

		return null;
	}

	/**
	 * 
	 * @return list of leaves
	 */
	@SuppressWarnings("unchecked")
	public List<Node<T>> getAllLeaves() {
		return this.totalLeavesUnder((T) ROOT_NODE);
	}

	/**
	 * @param search
	 *            string
	 * @return returns a list of nodes
	 */
	@SuppressWarnings("unchecked")
	public List<Node<T>> totalLeavesUnder(T search) {
		if (search instanceof Node<?>) {
			return totalLeavesUnder(((Node<T>) search).node);
		}

		if (search == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		Node<T> currNode = this.search(search);

		if (currNode == null) {
			return Collections.emptyList();
		}

		List<Node<T>> list1 = new ArrayList<Node<T>>();
		List<Node<T>> list2 = new ArrayList<Node<T>>();

		// still confused if to return an empty list
		if (currNode.children.isEmpty()) {
			return Arrays.asList(new Node[] { currNode });
		}

		List<Node<T>> leaves = new ArrayList<Node<T>>();
		list1.add(currNode);

		while (!list1.isEmpty()) {
			for (Node<T> ni : list1) {
				if (ni.isLeaf()) {
					leaves.add(ni);
				} else {
					list2.addAll(ni.children);
				}
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
	 * @param search
	 *            string
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Node<T>> totalNodesUnder(T search) {
		if (search instanceof Node<?>) {
			return totalNodesUnder(((Node<T>) search).node);
		}

		if (search == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		Node<T> node = this.search(search);

		if (node == null) {
			throw new IllegalArgumentException("node " + search + " not found");
		}

		if (node.isLeaf()) {
			return Collections.emptyList();
		}

		List<Node<T>> list1 = new ArrayList<Node<T>>();
		List<Node<T>> list2 = new ArrayList<Node<T>>();

		List<Node<T>> out = new ArrayList<Node<T>>();
		list1.add(node);

		while (!list1.isEmpty()) {
			for (Node<T> ne : list1) {
				if (!ne.isLeaf()) {
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

	/**
	 * 
	 * @param level
	 *            the level number
	 * @return List of Classification tree nodes at {@code level}
	 */
	public List<Node<T>> levelOrder(int level) {
		if (level < 0 || level > this.height) {
			throw new IllegalArgumentException("level " + level + " is invalid");
		}

		List<Node<T>> out = new ArrayList<>();

		Node<T> node = this.ROOT;
		List<Node<T>> list1 = new ArrayList<Node<T>>();
		List<Node<T>> list2 = new ArrayList<Node<T>>();
		list1.add(node);
		int i = 0;

		while (!list1.isEmpty()) {
			if (i == level) {
				break;
			}

			for (Node<T> ne : list1) {
				if (!ne.isLeaf()) {
					if (i + 1 == level) {
						out.addAll(ne.children);
					}
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
	 * Safe. Check for consistency
	 * 
	 * @param search
	 *            target node
	 * @param weight
	 *            new weight
	 */
	public void addOrModifyWeight(T search, float weight) {

		if (search == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		if (weight == 0) {
			System.err.println("0 weight given. Skipped");
			return;
		}

		if (weight < 0) {
			throw new IllegalArgumentException("Invalid weight");
		}

		Node<T> node = this.search(search);
		if (node == null) {
			throw new IllegalArgumentException(search
					+ " not exists in the tree");
		}

		if (!node.isLeaf()) {
			throw new RuntimeException(
					"Can not modify weight of non leaf node : " + search);
		}

		node.weight = weight;
		this.normalizeWeight();
		this.normalizeWeightProp();
	}

	/**
	 * This is added only to use in Morrisons as there can be same node which is
	 * both internal and lead :|
	 * 
	 * Need to do consistency check
	 * 
	 * @param searchNode
	 *            {@code Node} object
	 * @param weight
	 *            new weight
	 */
	public void addOrModifyWeight(Node<T> searchNode, float weight) {
		if (searchNode == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		if (weight == 0) {
			System.err.println("0 weight given. Skipped");
			return;
		}

		if (weight < 0) {
			throw new IllegalArgumentException("Invalid weight");
		}

		searchNode.weight = weight;
	}

	/**
	 * Optimized for Morrisons classification tree. normalized to 1 is not
	 * supported.
	 * 
	 * @param searchNodeight
	 * @param weight
	 */
	public void addOrModifyWeight_leaf(Node<T> searchNode, float weight) {
		if (searchNode == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		if (weight == 0) {
			System.err.println("0 weight given. Skipped");
			return;
		}

		if (weight < 0) {
			throw new IllegalArgumentException("Invalid weight");
		}

		searchNode.weight = weight;
		this.normalizeWeight();
	}

	public void addOrModifyDatabaseIndex_leaf(Node<T> searchNode,
			List<Integer> databaseIndex) {
		if (searchNode == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		if (databaseIndex.size() == 0) {
			System.err.println("0 size database index given. Skipped");
			return;
		}

		if (searchNode.databaseIndex.isEmpty()) {
			searchNode.databaseIndex = new ArrayList<>();
		}

		searchNode.databaseIndex.addAll(databaseIndex);
		this.normalizeDatabaseIndex();
	}

	/**
	 * Unsafe only to be used to load the tree from storage. Consistency check
	 * is the responsibility of the caller.
	 * 
	 * @param search
	 *            target node
	 * @param weight
	 *            new weight
	 */
	private void addOrModifyWeightUnsafe(T search, float weight) {

		if (search == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		if (weight == 0) {
			System.err.println("0 weight given. Skipped");
			return;
		}

		if (weight < 0) {
			throw new IllegalArgumentException("Invalid weight");
		}

		Node<T> node = this.search(search);
		if (node == null) {
			throw new IllegalArgumentException(search
					+ " not exists in the tree");
		}

		if (!node.isLeaf()) {
			throw new RuntimeException(
					"Can not modify weight of non leaf node : " + search);
		}

		node.weight = weight;
	}

	/**
	 * Safe. Check for consistency
	 * 
	 * @param search
	 *            target node
	 * @param databaseIndex
	 *            modified indices
	 */
	@SuppressWarnings("unchecked")
	public void addOrModifyDatabaseIndex(T search, List<Integer> databaseIndex) {
		if (search instanceof Node<?>) {
			addOrModifyDatabaseIndex(((Node<T>) search).node, databaseIndex);
		}

		if (search == null || databaseIndex == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		Node<T> node = this.search(search);
		if (node == null) {
			throw new IllegalArgumentException(search
					+ " not exists in the tree");
		}

		if (!node.isLeaf()) {
			throw new RuntimeException(
					"Can not modify weight of non leaf node : " + search);
		}

		node.databaseIndex = databaseIndex;
		this.normalizeDatabaseIndex();
	}

	/**
	 * Unsafe, only to be used to load the tree. Consistency check is the
	 * responsibility of the caller
	 * 
	 * @param search
	 *            target node
	 * @param databaseIndex
	 *            modified indices
	 */
	@SuppressWarnings("unchecked")
	private void addOrModifyDatabaseIndexUnsafe(T search,
			List<Integer> databaseIndex) {
		if (search instanceof Node<?>) {
			addOrModifyDatabaseIndex(((Node<T>) search).node, databaseIndex);
		}

		if (search == null || databaseIndex == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		Node<T> node = this.search(search);
		if (node == null) {
			throw new IllegalArgumentException(search
					+ " not exists in the tree");
		}

		if (!node.isLeaf()) {
			throw new RuntimeException(
					"Can not modify weight of non leaf node : " + search);
		}

		node.databaseIndex = databaseIndex;
	}

	/**
	 * experimental features
	 * 
	 * @param search
	 *            {@code search} string to delete and all if its children. Fully
	 *            consistency check. Safe to use. Slow.
	 * 
	 * @return {@code boolean value} {@code true} if found and deleted
	 *         {@code false} if the {@code search} string not found
	 */
	public boolean delete(T search) {
		if (search == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		Node<T> currNode = this.search(search);

		if (currNode == null) {
			return false;
		}

		List<Node<T>> children = currNode.parent.children;
		int index = 0;
		for (Node<T> child : children) {
			if (child.node.equals(currNode.node)) {
				break;
			}

			index++;
		}
		children.remove(index);
		currNode = null;
		// weight + database index fix
		this.normalizeDatabaseIndex();
		// tree size fix
		this.sizeCalled = false;
		this.recalculateLeafCount();
		this.size = this.recalulatedSize;
		this.height = this.recalculatedHeight;
		return true;
	}

	/**
	 * Unsafe delete method. Only to be used when to bring all the leaf nodes to
	 * the same level. No consistency check for quick execution. Consistency
	 * check is the responsibility of the caller.
	 * 
	 * @param search
	 */

	private void deleteUnsafe(Node<T> searchNode) {
		if (searchNode == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		List<Node<T>> neighbours = searchNode.parent.children;

		int index = 0;
		for (Node<T> neighbour : neighbours) {
			if (neighbour.node == searchNode.node) {
				break;
			}

			index++;
		}
		neighbours.remove(index);
		searchNode = null;
		this.sizeCalled = false;

	}

	/**
	 * Normalize the tree in one go calls : {@code normalizeWeightProp()}
	 * {@code normalizeDatabaseIndex()} {@code normalizeWeight()}
	 */

	public void normalize() {
		this.normalizeWeightProp();
		this.normalizeDatabaseIndex();
		this.normalizeWeight();
	}
	
	public void normalize_leaf(){
		//TODO
	}

	/**
	 * Make sure the leaf level have total weight 1 This may create a problem.
	 * Now we are going to just add all the weight and normalize them when need
	 * to fetch advertisements.
	 */
	public void normalizeWeightProp() {
		List<Node<T>> leaves = this.getAllLeaves();
		float total_weight = 0.0f;
		for (Node<T> leaf : leaves) {
			total_weight += leaf.weight;
		}

		if (total_weight == 1.0f || total_weight == 0.0f) {
			return;
		}

		for (Node<T> leaf : leaves) {
			leaf.weight = leaf.weight / total_weight;
		}
	}

	/**
	 * Propagate the database index upto the root node
	 */
	public void normalizeDatabaseIndex() {
		this.normalizeDatabaseIndex_recursive(this.ROOT);
	}

	private List<Integer> normalizeDatabaseIndex_recursive(Node<T> node) {
		if (node.isLeaf()) {
			return node.databaseIndex;
		}

		// make sure we are not putting duplicates
		node.databaseIndex = new ArrayList<>();

		for (Node<T> child : node.children) {
			node.databaseIndex.addAll(normalizeDatabaseIndex_recursive(child));
		}

		return node.databaseIndex;
	}
	
	/**
	 * Propagate the advertisement list upto the root node
	 */
	public void normalizeAdvertisements() {
		this.normalizeAdvertisement_recursive(this.ROOT);
	}
	
	private List<String> normalizeAdvertisement_recursive(Node<T> node) {
		if (node.isLeaf()) {
			return node.advertisements;
		}

		// make sure we are not putting duplicates
		node.advertisements = new ArrayList<>();

		for (Node<T> child : node.children) {
			node.advertisements.addAll(normalizeAdvertisement_recursive(child));
		}

		return node.advertisements;
	}
	
	/**
	 * Propagate the IsAdExists list upto the root node
	 */
	public void normalizeIsAdExists() {
		List<Node<T>> leaves = this.getAllLeaves();
		
		for(Node<T> leaf : leaves){
			Node<T> temp = leaf;
			
			if(!temp.isAdvertisementExists)
				continue;
			
			while(true)
			{
				if(temp.parent == null)
					break;
				
				if(temp.parent.isAdvertisementExists)
					break;
				else
					temp.parent.isAdvertisementExists = true;
				temp = temp.parent;
			}
		}
	}
	
	/**
	 * Propagate weights upto root
	 */
	public void normalizeWeight() {
		this.normalizeWeight_recursive(this.ROOT);
	}

	private float normalizeWeight_recursive(Node<T> node) {
		if (node.isLeaf()) {
			return node.weight;
		}

		node.weight = 0.0f;
		for (Node<T> child : node.children) {
			node.weight += normalizeWeight_recursive(child);
		}

		return node.weight;
	}

	/**
	 * Experimental feature to change the weight only through the path to the
	 * root node without changing the entire tree
	 * 
	 * need to do consistency check
	 * 
	 * @param node
	 * @param weight
	 */
	@SuppressWarnings("unchecked")
	public void addOrModifyWeight_efficient(T search, float weight) {
		if (search instanceof Node<?>) {
			Node<T> searchNode = (Node<T>) search;
			Node<T> node = this.search(searchNode.node);
			if (node.equals(searchNode)) {
				throw new IllegalArgumentException(searchNode.node
						+ " not exists in the tree");
			}

			this.weightDeltaFix(searchNode, weight);
		}

		if (search == null) {
			throw new IllegalArgumentException("null argument passed");
		}

		if (weight < 0) {
			throw new IllegalArgumentException("Invalid weight");
		}

		if (weight == 0) {
			System.err.println("0 weight given. Skipped");
			return;
		}

		Node<T> node = this.search(search);
		if (node == null) {
			throw new IllegalArgumentException(search
					+ " not exists in the tree");
		}

		this.weightDeltaFix(node, weight);

	}

	private void weightDeltaFix(Node<T> node, float weight) {
		float delta = weight - node.weight;
		node.weight = weight;
		while (node.parent != null) {
			node = node.parent;
			node.weight += delta;
		}
	}

	/**
	 * Store the entire tree to storage
	 * 
	 * @param file
	 *            path to the file in String
	 */
	public void storeTreeData(String file) {
		if (file == null || file.length() == 0) {
			throw new IllegalArgumentException("Illigal file argument");
		}

		List<Node<T>> leaves = this.getAllLeaves();

		try {
			FileWriter fw = new FileWriter(file);
			for (Node<T> leaf : leaves) {
				fw.append(leaf.node.toString() + ">" + leaf.weight + ">");
				if (leaf.databaseIndex.size() > 0) {
					for (int i : leaf.databaseIndex) {
						fw.append(i + ">");
					}
				}
				fw.append("\n");
			}
			fw.close();
		}

		catch (IOException ex) {
			System.err.println("Problem with file");
			ex.printStackTrace();
		}
	}

	/**
	 * Store the entire tree to storage. This is to be used for Morrisons
	 * taxonomy tree.
	 * 
	 * @param file
	 *            path to the file in String
	 */
	public void storeTreeDataNew(String file) {
		if (file == null || file.length() == 0) {
			throw new IllegalArgumentException("Illigal file argument");
		}

		List<Node<T>> leaves = this.getAllLeaves();

		try {
			FileWriter fw = new FileWriter(file);
			for (Node<T> leaf : leaves) {
				if (leaf.weight == 0) {
					continue;
				}
				ArrayList<Node<T>> trace = this.getTraceUptoRoot(leaf);

				for (Node<T> traceNode : trace) {
					fw.append(traceNode.toString() + ">");
				}
				fw.append(leaf.weight + "\n");
			}
			fw.close();
		}

		catch (IOException ex) {
			System.err.println("Problem with file");
			ex.printStackTrace();
		}
	}

	/**
	 * Load the tree data from storage to tree object
	 * 
	 * @param file
	 *            path to the file in String
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void loadTreeData(String file) {
		if (file == null || file.length() == 0) {
			throw new IllegalArgumentException("Illigal file argument");
		}
		if (!new File(file).exists()) {
			throw new RuntimeException(file + "missing");
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = "";

			while ((str = br.readLine()) != null) {
				String[] data = str.split(">");
				T nodeName = (T) data[0];
				float weight = Float.parseFloat(data[1]);
				this.addOrModifyWeightUnsafe(nodeName, weight);
				if (data.length == 2) {
					continue;
				}

				String[] databaseIndex = Arrays.copyOfRange(data, 2,
						data.length);

				Integer[] dataInt = new Integer[databaseIndex.length];

				int i = 0;
				for (String s : databaseIndex) {
					dataInt[i++] = Integer.parseInt(s);
				}
				this.addOrModifyDatabaseIndexUnsafe(nodeName,
						new ArrayList<Integer>(Arrays.asList(dataInt)));
			}

			br.close();
		} catch (IOException ex) {
			System.err.println("Problem with file");
			ex.printStackTrace();
		}
		this.normalize();
	}

	/**
	 * Load the tree data from storage to tree object. To be used with Morrisons
	 * taxonomy trees
	 * 
	 * @param file
	 *            path to the file in String
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void loadTreeDataNew(String file) {
		if (file == null || file.length() == 0) {
			throw new IllegalArgumentException("Illigal file argument");
		}
		if (!new File(file).exists()) {
			throw new RuntimeException(file + "missing");
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = "";

			while ((str = br.readLine()) != null) {
				String[] data = str.split(">");
				String[] trace = Arrays.copyOfRange(data, 0, data.length - 1);
				float weight = Float.parseFloat(data[data.length - 1]);

				Node<T> foundNode = this.goToNodeFromTrace((T[]) trace);
				this.addOrModifyWeight_leaf(foundNode, weight);
			}

			br.close();
		} catch (IOException ex) {
			System.err.println("Problem with file");
			ex.printStackTrace();
		}
		this.normalize();
	}

	private Node<T> goToNodeFromTrace(T[] trace) {
		Node<T> temp = ROOT;
		for (T nodeName : trace) {
			for (Node<T> child : temp.children) {
				if (child.node.toString().equals(nodeName.toString())) {
					temp = child;
					break;
				}
			}
		}

		return temp;
	}

	/**
	 * Give back a stack trace up to root node
	 * 
	 * @param node
	 *            Starting point for back tracing
	 * @return
	 */
	public ArrayList<Node<T>> getTraceUptoRoot(Node<T> node) {
		if (node == null) {
			throw new IllegalArgumentException("null argument");
		}
		ArrayList<Node<T>> lst = new ArrayList<>();

		Node<T> temp = node;
		while (temp.parent != null) {
			lst.add(temp);
			temp = temp.parent;
		}
		// added root
		// lst.add(temp);

		Collections.reverse(lst);
		return lst;
	}

	/**
	 * Select top k elements
	 * 
	 * @param k
	 *            Top {@code k} nodes to fetch
	 * @return
	 */
	public Set<Node<T>> getTopKLeaves(int k, Random rand) {
		List<Node<T>> leaves = new ArrayList<>(this.getAllLeaves());
		int i = 0;

		Collections.sort(leaves, new NodeComparator());
		
		Float total = 0.0f;
		for (Node<T> leaf : leaves) {
			total += leaf.weight;
		}
		// normalize
		for (Node<T> leaf : leaves) {
			leaf.weight /= total;
		}

		Set<Node<T>> topKLeaves = new HashSet<>();
		Float cumulativeWeight = 0.0f;

		for (Node<T> leaf : leaves) {

			Float p = rand.nextFloat();
			if (i == k) {
				break;
			}
			cumulativeWeight += leaf.weight;

			if (cumulativeWeight >= p) {
				topKLeaves.add(leaf);
				i++;
			}
		}
		if (topKLeaves.size() < k) {
			int left = k - topKLeaves.size();
			int t = 0;
			for (Node<T> leaf : leaves) {
				if (t == left) {
					break;
				}
				if (topKLeaves.contains(leaf)) {
					continue;
				} else {
					topKLeaves.add(leaf);
					t++;
				}
			}
		}

		return topKLeaves;
	}

	/**
	 * Select top k elements
	 * 
	 * @param k
	 *            Top {@code k} nodes to fetch
	 * @return
	 */
	public ArrayList<Node<T>> getTopKLeaves(int k) {
		List<Node<T>> leaves = this.getAllLeaves();
		int i = 0;

		leaves.sort(new NodeComparator());
		// Collections.sort(leaves, new NodeComparator());
		ArrayList<Node<T>> topKLeaves = new ArrayList<>();

		for (Node<T> leaf : leaves) {

			if (i == k) {
				break;
			}

			topKLeaves.add(leaf);
			i++;
		}

		return topKLeaves;
	}
	
	@SuppressWarnings("unchecked")
	public ClassificationTree<T> deepClone()
	{
		 try {
		     ByteArrayOutputStream baos = new ByteArrayOutputStream();
		     ObjectOutputStream oos = new ObjectOutputStream(baos);
		     oos.writeObject(this);
		     ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		     ObjectInputStream ois = new ObjectInputStream(bais);
		     return (ClassificationTree<T>) ois.readObject();
		   }
		   catch (Exception e) {
		     System.err.println("Error happed in clone phase");
		     return null;
		   }
	}

	/**
	 * test
	 */
	/*public static void main(String[] args) {
		ClassificationTree<String> tree = new ClassificationTree<>();
		tree.insert(new String[] { "a", "c" });
		tree.insert(new String[] { "a", "d" });
		tree.insert(new String[] { "b", "e", "f" });

		// tree.delete("a");
		// tree.addOrModifyWeight("f", 0.5f);
		// tree.addOrModifyWeight("c", 0.5f);
		// tree.addOrModifyWeight("d", 0.5f);

		// tree.insert("x", "a");
		tree.bringLeavesToSameLevel();
		// tree.addOrModifyWeight_efficient("c", 0);
		// tree.normalizeWeight();
		tree.normalize();
		tree.addOrModifyDatabaseIndex("c",
				new ArrayList<>(Arrays.asList(new Integer[] { 0, 1, 2, 3 })));
		tree.addOrModifyDatabaseIndex("d",
				new ArrayList<>(Arrays.asList(new Integer[] { 4, 5, 6, 7 })));

		System.out.println(tree.getDatabaseIndex(ClassificationTree.ROOT_NODE));

		System.out.println(tree.getWeight(ClassificationTree.ROOT_NODE));
		System.out.println(tree.getAllLeaves().size());
		System.out.println(tree.search("g"));

		tree.storeTreeData("tree.txt");

		// recreating the same tree

		tree = null;
		tree = new ClassificationTree<>();
		tree.insert(new String[] { "a", "c" });
		tree.insert(new String[] { "a", "d" });
		tree.insert(new String[] { "b", "e", "f" });

		tree.loadTreeData("tree.txt");
		tree.storeTreeData("treeR.txt");
	}*/
}
