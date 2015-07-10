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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.xrci.taxonomyTree.parse.ParseTree;
import com.xrci.taxonomyTree.tree.ClassificationTree;
import com.xrci.taxonomyTree.tree.Node;

public class MargeExperiment {

	public static void main(String[] args) throws IOException {
	
		ParseTree pt = new ParseTree("C:\\Work\\Projects\\PAMM\\taxonomy.en-US.txt", true);
		ClassificationTree<String> ct = pt.makeTree(false);
		
		List<ClassificationTree<String>> profiles = new ArrayList<ClassificationTree<String>>();
		List<Node<String>> leaves = ct.getAllLeaves();
		List<String> leafNames = new ArrayList<String>();
		int leafCount = ct.leafCount();
		for(Node<String> leaf : leaves){
			leafNames.add(leaf.node);
		}
		Random rand = new Random();
		long start = System.currentTimeMillis();	
		for(int i = 0; i < 1000; i++)
		{
			ClassificationTree<String> temp = ct.deepClone();
			for(int j = 0; j < 50; j++){
				int t = rand.nextInt(leafCount);
				float tempWeight = rand.nextFloat();
				temp.addOrModifyWeight_leaf(temp.getNodeMap().NODE_MAP.get(leafNames.get(t)), tempWeight);
			}
			profiles.add(temp);
		}
		long end = System.currentTimeMillis();
		double time = (double)((end - start)/1000);
		
		
		
		System.out.println("Avg time of clone : " + time + " ms");
		System.out.println("Time : " + (end - start) + " ms");

		System.out.println("Size : " + ct.size());
		System.out.println("Leaf nodes : " + ct.leafCount());
		System.out.println("Max level : " + ct.height());
	}
}
