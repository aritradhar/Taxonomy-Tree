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

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

public class Test 
{
	public static void main(String[] args) throws IOException 
	{
		FileWriter fw = new FileWriter("C:\\Work\\Projects\\PAMM\\text.txt");
		
		Random rand = new Random();
		byte[] b = new byte[8];
		
		for(int i = 0; i < 18000; i++)
		{
			if(i % 1000 == 0)
				System.out.println(i);
			
			int t = rand.nextInt(49) + 1;

			String s = "";
			for(int j = 0; j < t; j++)
			{
				rand.nextBytes(b);
				s = s.concat(Base64.encodeBase64String(b));
				if(j < t - 1)
					s = s.concat(" > ");
			}
			fw.append(s + "\n");
		}
		fw.close();
	}
}
