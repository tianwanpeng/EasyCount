/*
 * Created on Jan 8, 2004
 */
package com.tencent.trc.graphtest;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collection;

import samples.preview_new_graphdraw.Coordinates;
import samples.preview_new_graphdraw.EdgeRenderer;
import samples.preview_new_graphdraw.VisEdge;
import edu.uci.ics.jung.algorithms.transformation.FoldingTransformer;

/**
 * @author danyelf
 */
public class ThickEdgeRenderer implements EdgeRenderer {
	private Color color;

	public ThickEdgeRenderer() {
		this.color = Color.black;
	}

	public ThickEdgeRenderer(Color c) {
		this.color = c;
	}

	public void renderEdge(Graphics g, VisEdge ec) {
		g.setColor(color);

		Graphics2D g2d = (Graphics2D) g;

		Collection ties = (Collection) ec.getEdge().getUserDatum(
				FoldingTransformer.FOLDED_DATA);

		int s = 0;
		if (ties != null) {
			s = ties.size();
		}

		g2d.setStroke(new BasicStroke(s));

		Coordinates v1 = ec.getFront();
		Coordinates v2 = ec.getBack();
		g.drawLine((int) v1.getX(), (int) v1.getY(), (int) v2.getX(),
				(int) v2.getY());
	}

}
