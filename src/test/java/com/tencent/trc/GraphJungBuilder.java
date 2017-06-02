package com.tencent.trc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EllipseVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.PickableVertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.visualization.DefaultGraphLabelRenderer;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.DAGLayout;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ViewScalingControl;

public class GraphJungBuilder extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4981302125901329739L;

	/**
	 * the graph
	 */
	final public SparseGraph graph;
	final public StringLabeller sl;
	final public Vertex root;

	/**
	 * the visual component and renderer for the graph
	 */

	public GraphJungBuilder() {

		root = new DirectedSparseVertex();
		// create a simple graph for the demo
		graph = new SparseGraph();
		sl = StringLabeller.getLabeller(graph);
		// Vertex[] vertices = createVertices(root, 11);
		// createEdges(vertices);
	}

	public Vertex addVertex(String vname) {
		Vertex v = new DirectedSparseVertex();
		graph.addVertex(v);
		try {
			sl.setLabel(v, vname);
		} catch (UniqueLabelException e) {
			e.printStackTrace();
		}
		return v;
	}

	public void addEdge(Vertex v1, Vertex v2) {
		graph.addEdge(new DirectedSparseEdge(v1, v2));
	}

	public void draw() {
		final PluggableRenderer pr = new PluggableRenderer();

		pr.setVertexPaintFunction(new PickableVertexPaintFunction(pr,
				Color.black, Color.white, Color.yellow));
		pr.setEdgePaintFunction(new PickableEdgePaintFunction(pr, Color.black,
				Color.cyan));
		pr.setGraphLabelRenderer(new DefaultGraphLabelRenderer(Color.cyan,
				Color.cyan));

		pr.setVertexShapeFunction(new EllipseVertexShapeFunction());

		Layout layout = new DAGLayout(graph);

		final VisualizationViewer vv = new VisualizationViewer(layout, pr,
				new Dimension(400, 400));
		vv.setPickSupport(new ShapePickSupport());
		pr.setEdgeShapeFunction(new EdgeShape.QuadCurve());
		vv.setBackground(Color.white);

		// add a listener for ToolTips
		vv.setToolTipFunction(new DefaultToolTipFunction());

		Container content = getContentPane();
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		content.add(panel);

		final PluggableGraphMouse graphMouse = new PluggableGraphMouse();
		graphMouse.add(new PickingGraphMousePlugin());
		graphMouse.add(new ScalingGraphMousePlugin(new ViewScalingControl(),
				InputEvent.CTRL_MASK));
		graphMouse.add(new ScalingGraphMousePlugin(
				new CrossoverScalingControl(), 0));

		vv.setGraphMouse(graphMouse);

		final ScalingControl scaler = new CrossoverScalingControl();

		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});

		JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
		scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

		JPanel controls = new JPanel();
		scaleGrid.add(plus);
		scaleGrid.add(minus);
		controls.add(scaleGrid);

		content.add(controls, BorderLayout.SOUTH);

		JFrame frame = new JFrame();
		Container content1 = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		content1.add(this);
		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * a driver for this demo
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Container content = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		content.add(new TreeLayoutDemo());
		frame.pack();
		frame.setVisible(true);
	}

}
