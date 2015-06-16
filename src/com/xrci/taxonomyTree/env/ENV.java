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

public class ENV {
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
