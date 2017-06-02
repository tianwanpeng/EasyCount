package com.tencent.trc.util.graph;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class GraphXmlBuilder {

	final private Document document;
	final private Element graph;

	public GraphXmlBuilder() {
		document = DocumentHelper.createDocument();
		Element graphml = document.addElement("graphml");

		graph = graphml.addElement("graph");
		graph.addAttribute("edgedefault", "directed");
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
	}

	public void addNode(String id, String name, String level) {
		Element node = graph.addElement("node");
		node.addAttribute("id", id);
		node.addElement("data").addAttribute("key", "name").setText(name);
		node.addElement("data").addAttribute("key", "level").setText(level);
	}

	public void addEdge(String source, String target) {
		graph.addElement("edge").addAttribute("source", source)
				.addAttribute("target", target);
	}

	public String build() {
		return document.asXML();
	}

	public static void main(String[] args) {
		GraphXmlBuilder builder = new GraphXmlBuilder();
		builder.addNode("aa", "ada", "1");
		System.out.println(builder.build());
	}

}
