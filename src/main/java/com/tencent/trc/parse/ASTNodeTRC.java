package com.tencent.trc.parse;

import java.io.Serializable;
import java.util.ArrayList;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import com.tencent.trc.util.graph.GraphWalker.Node;

/**
 *
 */
public class ASTNodeTRC extends CommonTree implements Node, Serializable {
	private static final long serialVersionUID = 1L;

	private ASTNodeTRCOrigin origin;

	public ASTNodeTRC() {
	}

	/**
	 * Constructor.
	 * 
	 * @param t
	 *            Token for the CommonTree Node
	 */
	public ASTNodeTRC(Token t) {
		super(t);
	}

	public ArrayList<Node> getChildren() {
		if (super.getChildCount() == 0) {
			return null;
		}

		ArrayList<Node> ret_vec = new ArrayList<Node>();
		for (int i = 0; i < super.getChildCount(); ++i) {
			ret_vec.add((Node) getChild(i));
		}

		return ret_vec;
	}

	public String getName() {
		return (Integer.valueOf(super.getToken().getType())).toString();
	}

	/**
	 * @return information about the object from which this ASTNode originated,
	 *         or null if this ASTNode was not expanded from an object reference
	 */
	public ASTNodeTRCOrigin getOrigin() {
		return origin;
	}

	/**
	 * Tag this ASTNode with information about the object from which this node
	 * originated.
	 */
	public void setOrigin(ASTNodeTRCOrigin origin) {
		this.origin = origin;
	}

	public String dump() {
		StringBuilder sb = new StringBuilder();

		sb.append('(');
		sb.append(toString());
		ArrayList<Node> children = getChildren();
		if (children != null) {
			for (Node node : getChildren()) {
				if (node instanceof ASTNodeTRC) {
					sb.append(((ASTNodeTRC) node).dump());
				} else {
					sb.append("NON-ASTNODE!!");
				}
			}
		}
		sb.append(')');
		return sb.toString();
	}
}
