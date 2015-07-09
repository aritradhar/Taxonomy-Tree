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

import com.xrci.taxonomyTree.parse.ParseTree;
import com.xrci.taxonomyTree.tree.ClassificationTree;

public class MargeExperiment {

	public static void main(String[] args) throws IOException {
	
		ParseTree pt = new ParseTree("C:\\Work\\Projects\\PAMM\\taxonomy.en-US.txt", true);
		ClassificationTree<String> ct = pt.makeTree(false);
		
		List<ClassificationTree<String>> profiles = new ArrayList<ClassificationTree<String>>();
		
		long start = System.currentTimeMillis();	
		for(int i = 0; i < 1000; i++)
		{
			ct.deepClone();
		}
		long end = System.currentTimeMillis();
		double time = (double)((end - start)/1000);
		System.out.println("Avg time of clone : " + time + " ms");

		System.out.println("Size : " + ct.size());
		System.out.println("Leaf nodes : " + ct.leafCount());
		System.out.println("Max level : " + ct.height());
	}
}
