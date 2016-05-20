package com.pickzy.moresdk;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;


public class XML2Parser {
public static String xml = null;
public static Document doc = null;
	public String getXmlFromUrl(String url) {
		 xml = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient(); //Api -1 ->DefaultHttpClient
			HttpGet httpget=new HttpGet(url);//Api -1 -> HttpGet
			HttpResponse httpResponse = httpClient.execute(httpget);//Api -1 ->HttpResponse
			HttpEntity httpEntity = httpResponse.getEntity();//Api -1 ->
			xml = EntityUtils.toString(httpEntity);//Api -1 -> EntityUtils
		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		return xml;
	}

	public Document getDomElement(String xml) { //Api -1 -> Document
		 doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); //Api -1 -> DocumentBuilderFactory
		try {
			DocumentBuilder db = dbf.newDocumentBuilder(); //Api -1 -> DocumentBuilderFactory
			InputSource is = new InputSource(); 	//Api -1 -> InputSource
			is.setCharacterStream(new StringReader(xml));	//Api -1 -> StringReader
			doc = db.parse(is);
		} catch (ParserConfigurationException e) {
			Log.e("ParserConfigurationException", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("SAXException", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("IOException", e.getMessage());
			return null;
		}catch(Exception e){
			Log.e("Exception", e.getMessage());
			return null;
		}
		return doc;
	}

	public final String getElementValue(Node elem) {//Api -1 -> Node
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return null;
	}
	public String getValue(Element item, String str) {//Api -1 -> Element
		NodeList n = item.getElementsByTagName(str);
		return this.getElementValue(n.item(0));
	}
}
