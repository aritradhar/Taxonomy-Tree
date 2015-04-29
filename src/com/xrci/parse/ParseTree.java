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


package com.xrci.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.xrci.tree.ClassificationTree;
import com.xrci.tree.Node;

public class ParseTree 
{
	String filePath;
	ArrayList<String[]> treeNodes;
	
	public ParseTree(String filePath) throws IOException
	{
		this.filePath = filePath;
		this.treeNodes = this.doParse();
	}	
	
	private ArrayList<String[]> doParse() throws IOException
	{
		ArrayList<String[]> out = new ArrayList<String[]>();
		BufferedReader br = new BufferedReader(new FileReader(this.filePath));
		String st = "";
		 
		while((st = br.readLine()) != null)
		{
			if(st.isEmpty())
				continue;
			
			String[] tokens = st.split(" > ");
			out.add(tokens);
		}
		
		br.close();
		return out;
	}
	
	public ClassificationTree<String> makeTree()
	{
		ClassificationTree<String> ct = new ClassificationTree<>();

		for(String[] elements : this.treeNodes)
		{
			ct.insert(elements);
		}
		
		return ct;
	}
	
	/*
	 * Test
	 */
	public static void main(String[] args) throws IOException 
	{
		long start = System.currentTimeMillis();
		
		ParseTree pt = new ParseTree("C:\\Work\\Projects\\PAMM\\taxonomy.en-US.txt");
		//ParseTree pt = new ParseTree("C:\\Work\\Projects\\PAMM\\text.txt");
		ClassificationTree<String> ct = pt.makeTree();
		Node<String> root = ct.getTree();
		
		System.out.println(ct.size());
		System.out.println(ct.leafCount());
		System.out.println(ct.height());
		//System.out.println(ct.levelOrder(1));
		System.out.println(root.hashCode());
		ct.addOrModifyWeight("Modeling Clay & Dough", 0.5f);
		
		//ClassificationTree ct1 = new ClassificationTree(root);
		//System.out.println(ct1.size());
		//System.out.println(ct1.leaves());
		//System.out.println(ct1.height());
		
		long end = System.currentTimeMillis();
		System.out.println("Total time : " + (end - start) + " ms");
	}
}
