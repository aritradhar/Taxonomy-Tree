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

package com.xrci.taxonomyTree.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.xrci.taxonomyTree.tree.ClassificationTree;
import com.xrci.taxonomyTree.tree.Node;
import com.xrci.taxonomyTree.tree.Product;
import com.xrci.taxonomyTree.tree.ProductStore;

public class ParseTree {
	String filePath;
	ArrayList<String[]> treeNodes;
	boolean legacyMode;

	/**
	 * 
	 * @param filePath
	 *            Taxonomy tree file path
	 * @param legacyMode
	 *            To make it compatible with Google's taxonomy tree. False to
	 *            use with Morrisons Taxonomy tree
	 * @throws IOException
	 */
	public ParseTree(String filePath, boolean legacyMode) throws IOException {
		this.filePath = filePath;
		this.treeNodes = this.doParse();
		this.legacyMode = legacyMode;
	}

	private ArrayList<String[]> doParse() throws IOException {
		ArrayList<String[]> out = new ArrayList<String[]>();
		BufferedReader br = new BufferedReader(new FileReader(this.filePath));
		String st = "";

		while ((st = br.readLine()) != null) {
			if (st.isEmpty()) {
				continue;
			}

			String[] tokens = st.split(">");
			out.add(tokens);
		}

		br.close();
		return out;
	}

	/**
	 * Make a classification tree from the parse tree. Also extend the tree such
	 * that all the leaves are at the same level
	 * 
	 * @param bringToSameLevel
	 *            true to do bringLeavesToSameLevel() operation.
	 * @return ClassificationTree object
	 */
	public ClassificationTree<String> makeTree(boolean bringToSameLevel) {
		ClassificationTree<String> ct = new ClassificationTree<>();

		if (this.legacyMode) {
			for (String[] elements : this.treeNodes) {
				ct.insert(elements, true);
			}
		} else {
			for (String[] elements : this.treeNodes) {
				ct.insert(elements);
			}
		}

		if (bringToSameLevel) {
			ct.bringLeavesToSameLevel();
		}
		return ct;
	}

	/*
	 * Test
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();

		// ParseTree pt = new
		// ParseTree("C:\\Work\\Projects\\PAMM\\taxonomy.en-US.txt", true);
		ParseTree pt = new ParseTree(
				"C:\\Users\\w4j3yyfd\\workspace\\WebCrawler\\Merged_tree.txt",
				false);
		// ParseTree pt = new ParseTree("C:\\Work\\Projects\\PAMM\\text.txt");
		ClassificationTree<String> ct = pt.makeTree(false);
		Node<String> root = ct.getTree();

		System.out.println("Size : " + ct.size());
		System.out.println("Leaf nodes : " + ct.leafCount());
		System.out.println("Max level : " + ct.height());
		// System.out.println(ct.levelOrder(1));
		// System.out.println(root.hashCode());
		// ct.addOrModifyWeight("Modeling Clay & Dough", 0.5f);

		long start1 = System.currentTimeMillis();
		ct.normalize();

		// ct.bringLeavesToSameLevel();
		// ClassificationTree ct1 = new ClassificationTree(root);
		// System.out.println(ct1.size());
		// System.out.println(ct1.leaves());
		// System.out.println(ct1.height());

		System.out.println("--- After Normalize ---");
		System.out.println("Size : " + ct.size());
		System.out.println("Leaf nodes : " + ct.leafCount());
		System.out.println("Max level : " + ct.height());
		System.out.println("Max fan-out : " + ct.maxFanOut());
		long end = System.currentTimeMillis();

		ct.storeTreeData("GCT.txt");

		// ct = null;
		// ct = pt.makeTree();
		// ct.loadTreeData("GCT.txt");

		System.out.println("Normalization time : " + (end - start1) + " ms");
		System.out.println("Total execution time : " + (end - start) + " ms");

		System.out.println("Total plroducts : "
				+ ProductStore.ProductMap.size());
		Product p = ProductStore.ProductMap
				.get("Quavers Cheese Flavour Crisps Multipack 6 x 16.4g");

		/*
		 * for(Product pr : ProductStore.ProductMap.values()) { BufferedImage br
		 * = pr.getProductImage(); ImageIO.write(br, "jpg", new
		 * File("ProductImages\\" + pr.toString().replaceAll("\\.", "_") +
		 * ".jpg")); System.out.println(" downloaded..."); }
		 */
		// p.getProductImage();
		// p.getProductPrice();

		for (Node<String> n : p.getParents()) {
			System.out.println(ct.getTraceUptoRoot(n));
		}
	}
}
