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

/**
 * Represents a listed product
 * @author Aritra Dhar
 *	
 */
public class Product 
{
	String productName;
	String url;
	Set<Node<String>> parents;
	Document doc;
	
	public Product(String productName) 
	{
		this.productName = productName;
		this.parents = Collections.emptySet();
		doc = null;
	}
	
	public Product(String productName, String url)
	{
		this.productName = productName;
		this.url = url;
		this.parents = Collections.emptySet();
		doc = null;
	}
	
	
	public Set<Node<String>> getParents()
	{
		return this.parents;
	}
	
	public void makeDoc()
	{
		try 
		{
			if(this.doc == null)
			{
				ENV.setProxy();
				this.doc = Jsoup.connect(url).timeout(3000).userAgent("Mozilla").get();
			}
		}	
		catch (Exception e) 
		{
			System.err.println("Error in document parsing");
		}
	}
	
	public void addParents(Set<Node<String>> parents)
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
	
	public void addParent(Node<String> parent)
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
	
	@Override
	public boolean equals(Object obj) 
	{
		Product other = (Product) obj;
		return this.toString().equals(other.toString());
	}
	
	/**
	 * This method will get the image in jpg format 
	 * from the product page of Morrisons.
	 * Execution will take time as it will query online.
	 * @return {@code BufferedImage} object of the image
	 */
	public BufferedImage getProductImage()
	{
		BufferedImage image = null;
		String imageUrl = null;
		
		this.makeDoc();
		
		Elements galleryImageElements = this.doc.select("ul#galleryImages");
		
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
		
		/*		
		File outputfile = new File("saved.jpg");
	    try 
	    {
			ImageIO.write(image, ENV.IMAGE_WRITE_OPTION, outputfile);
		} 
	    catch (IOException e) 
	    {
			System.err.println("Error in writing the image into the disk");
		}
		*/
		return image;
	}
	
	/**
	 * Execution will take time as it will query online.
	 * @return {@code ProductPrice} object of the product
	 */
	public ProductPrice getProductPrice()
	{
		this.makeDoc();
		ProductPrice productPrice = new ProductPrice(this);
		Element priceElement = this.doc.select("div.productPrice").first();
		
		Element wasPriceElement = priceElement.select("span.wasPrice").first();
		productPrice.wasPrice = wasPriceElement.text();
		
		Element nowPriceElement = priceElement.select("span.nowPrice").first();
		productPrice.nowPrice = nowPriceElement.text();
		
		Element currencyElement = priceElement.select("meta[content=GBP]").first();
		productPrice.currency = currencyElement.attr("content");
		
		Element pricePerWeightElement = priceElement.select("p.pricePerWeight").first();
		productPrice.pricePerWeight = pricePerWeightElement.text();
		
		return productPrice;
	}
}
