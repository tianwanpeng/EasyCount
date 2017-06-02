package com.tencent.easycount.parse;

import java.io.Serializable;
import java.util.ArrayList;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import com.tencent.easycount.util.graph.GraphWalker.Node;

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
	public ASTNodeTRC(final Token t) {
		super(t);
	}

	@Override
	public ArrayList<Node> getChildren() {
		if (super.getChildCount() == 0) {
			return null;
		}

		final ArrayList<Node> ret_vec = new ArrayList<Node>();
		for (int i = 0; i < super.getChildCount(); ++i) {
			ret_vec.add((Node) getChild(i));
		}

		return ret_vec;
	}

	@Override
	public String getName() {
		return (Integer.valueOf(super.getToken().getType())).toString();
	}

	/**
	 * @return information about the object from which this ASTNode originated,
	 *         or null if this ASTNode was not expanded from an object reference
	 */
	public ASTNodeTRCOrigin getOrigin() {
		return this.origin;
	}

	/**
	 * Tag this ASTNode with information about the object from which this node
	 * originated.
	 */
	public void setOrigin(final ASTNodeTRCOrigin origin) {
		this.origin = origin;
	}

	public String dump() {
		final StringBuilder sb = new StringBuilder();

		sb.append('(');
		sb.append(toString());
		final ArrayList<Node> children = getChildren();
		if (children != null) {
			for (final Node node : getChildren()) {
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
