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

package com.xrci.taxonomyTree.env;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

public class ENV {
	public static final boolean PAPER_TEST = true;
	public static final int TIMEOUT_RETRY = 5;
	public static final String proxyServer = "proxy.eur.xerox.com";
	public static final String proxyPort = "8000";
	public static final String IMAGE_WRITE_OPTION = "jpg";

	public static void setProxy() {
		System.setProperty("http.proxyHost", proxyServer);
		System.setProperty("http.proxyPort", proxyPort);

		System.setProperty("https.proxyHost", proxyServer);
		System.setProperty("https.proxyPort", proxyPort);

		System.setProperty("ftp.proxyHost", proxyServer);
		System.setProperty("ftp.proxyPort", proxyPort);
	}
}

class Util
{
	public static final int COMPRESSION_OPTION = 6;
	
	public static void LZMA_ZIP(String src, String dest) throws IOException
	{
		FileInputStream inFile = new FileInputStream(src);
		FileOutputStream outfile = new FileOutputStream(dest);

		LZMA2Options options = new LZMA2Options();

		options.setPreset(COMPRESSION_OPTION); 

		XZOutputStream out = new XZOutputStream(outfile, options);

		byte[] buf = new byte[8192];
		int size;
		while ((size = inFile.read(buf)) != -1)
		   out.write(buf, 0, size);

		inFile.close();
		out.finish();
		out.close();
		outfile.close();
	}
	
	public static byte[] LZMA_ZIP(byte[] src) throws IOException
	{
		ByteArrayInputStream inB = new ByteArrayInputStream(src);
		ByteArrayOutputStream outB = new ByteArrayOutputStream(); 
		
		LZMA2Options options = new LZMA2Options();

		options.setPreset(COMPRESSION_OPTION); 

		XZOutputStream out = new XZOutputStream(outB, options);
		
		byte[] buf = new byte[100];
		int size;
		while ((size = inB.read(buf)) != -1)
		   out.write(buf, 0, size);
		
		inB.close();
		out.finish();
		out.close();
		outB.close();
		
		return outB.toByteArray();
	}
	
	public static void LZMA_UNZIP(String src, String dest) throws IOException
	{
		FileInputStream inFile = new FileInputStream(src);
		FileOutputStream outfile = new FileOutputStream(dest);
		
		XZInputStream in = new XZInputStream(inFile);
		
		byte[] buf = new byte[8192];
		int size;
		while ((size = in.read(buf)) != -1)
		   outfile.write(buf, 0, size);

		inFile.close();
		in.close();
		outfile.close();

	}
	
	public static byte[] LZMA_UNZIP(byte[] source) throws IOException
	{
		ByteArrayInputStream inBytes = new ByteArrayInputStream(source);
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		
		XZInputStream in = new XZInputStream(inBytes);
		
		byte[] buf = new byte[100];
		int size;
		while ((size = in.read(buf)) != -1)
			outBytes.write(buf, 0, size);

		inBytes.close();
		in.close();
		outBytes.close();

		return outBytes.toByteArray();
	}
}
