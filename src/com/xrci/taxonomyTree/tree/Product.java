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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xrci.taxonomyTree.env.ENV;

public class Product<T> 
{
	T productName;
	String url;
	Set<Node<T>> parents;
	
	public Product(T productName) 
	{
		this.productName = productName;
		this.parents = Collections.emptySet();
	}
	
	public Product(T productName, String url)
	{
		this.productName = productName;
		this.url = url;
		this.parents = Collections.emptySet();
	}
	
	public Set<Node<T>> getParents()
	{
		return this.parents;
	}
	
	public void addParents(Set<Node<T>> parents)
	{
		if(this.parents.isEmpty())
		{
			this.parents = parents;
		}
		else
		{
			this.parents.addAll(parents);
		}
	}
	
	public void addParents(Node<T> parent)
	{
		if(this.parents.isEmpty())
		{
			this.parents = new HashSet<>();
		}
		this.parents.add(parent);
		
	}
	
	@Override
	public String toString() 
	{
		return this.productName.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) 
	{
		Product<T> other = (Product<T>) obj;
		return this.toString().equals(other.toString());
	}
	
	public BufferedImage getProductImage()
	{
		ENV.setProxy();
		BufferedImage image = null;
		Document doc = null;
		String imageUrl = null;
		
		try 
		{
			doc = Jsoup.connect(url).timeout(3000).userAgent("Mozilla").get();
		} 
		catch (Exception e) 
		{
			System.err.println("Error in document parsing");
		}
		
		Elements galleryImageElements = doc.select("ul#galleryImages");
		
		for(Element element : galleryImageElements)
		{
			Elements els = element.select("a[href]");
			//only one image element
			Element imageRefElement = els.get(0);
			imageUrl =  imageRefElement.attr("abs:href");		
		}
		
		try 
		{
		    URL url = new URL(imageUrl);
		    image = ImageIO.read(url);
		} 
		
		catch (IOException e) 
		{
			System.err.println("Error in image download");
		}
		
		File outputfile = new File("saved.jpg");
	    try 
	    {
			ImageIO.write(image, ENV.IMAGE_WRITE_OPTION, outputfile);
		} 
	    catch (IOException e) 
	    {
			System.err.println("Error in writing the image into the disk");
		}
		
		return image;
	}
}
