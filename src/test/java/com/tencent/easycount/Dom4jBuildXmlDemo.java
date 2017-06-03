package com.tencent.easycount;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

public class Dom4jBuildXmlDemo {
	public void build01() throws IOException {
		Document document = DocumentHelper.createDocument();
		Element graphml = document.addElement("graphml");

		Element graph = graphml.addElement("graph");
		graph.addAttribute("edgedefault", "undirected");
		Element key = graph.addElement("key");
		key.addAttribute("id", "name");
		key.addAttribute("for", "node");
		key.addAttribute("attr.name", "name");
		key.addAttribute("attr.type", "string");

		key = graph.addElement("key");
		key.addAttribute("id", "level");
		key.addAttribute("for", "node");
		key.addAttribute("attr.name", "level");
		key.addAttribute("attr.type", "string");

		System.out.println(document.asXML());
		Writer fileWriter = new FileWriter("testdata/module.xml");
		XMLWriter xmlWriter = new XMLWriter(fileWriter);
		xmlWriter.write(document);
		xmlWriter.flush();
		xmlWriter.close();
	}

	public static void main(String[] args) throws IOException {
		Dom4jBuildXmlDemo demo = new Dom4jBuildXmlDemo();
		demo.build01();
	}
}
