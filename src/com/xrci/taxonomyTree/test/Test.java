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


package com.xrci.taxonomyTree.test;

import java.util.ArrayList;
import java.util.Arrays;

import com.xrci.taxonomyTree.tree.ClassificationTree;

public class Test {
	
	public static void main(String[] args) {
		ClassificationTree<String> tree = new ClassificationTree<>();
		tree.insert(new String[] { "a", "c" }, true);
		tree.insert(new String[] { "a", "d" }, true);
		tree.insert(new String[] { "b", "e", "f" }, true);

		// tree.delete("a");
		// tree.addOrModifyWeight("f", 0.5f);
		// tree.addOrModifyWeight("c", 0.5f);
		// tree.addOrModifyWeight("d", 0.5f);

		// tree.insert("x", "a");
		tree.searchInleaves("c").isAdvertisementExists = true;
		tree.normalizeIsAdExists();
		
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
		tree.insert(new String[] { "a", "c" }, true);
		tree.insert(new String[] { "a", "d" }, true);
		tree.insert(new String[] { "b", "e", "f" }, true);

		tree.loadTreeData("tree.txt");
		tree.storeTreeData("treeR.txt");
	}
}
