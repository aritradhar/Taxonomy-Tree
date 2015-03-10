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
			String[] tokens = st.split(" > ");
			out.add(tokens);
		}
		
		br.close();
		return out;
	}
	
	public ClassificationTree makeTree()
	{
		ClassificationTree ct = new ClassificationTree();
		for(String[] elements : this.treeNodes)
		{
			ct.insert(elements);
		}
		
		return ct;
	}
	
	/*
	 * Test
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException 
	{
		ParseTree pt = new ParseTree("C:\\Work\\Projects\\PAMM\\taxonomy.en-US.txt");
		ClassificationTree ct = pt.makeTree();
		Node root = ct.ROOT;
		
		System.out.println(ct.leaves());
	}
}
