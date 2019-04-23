package khaos.tools.Graphics;

/**
 * @author Fernando Moreno Jabato <fmjabato@yahoo.es>
 */

import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;


public class GraphicDrawer {
	//CONSTANTS
	public static final int CircleLayout = 0;
	public static final int DAGLayout = 1;
	public static final int FRLayout = 2;
	public static final int ISOMLayout = 3;
	public static final int KKLayout = 4;
	public static final int SpringLayout = 5;
	
	//FIELDS
	private Vector<String> nodes;
	private Vector<Edge> edges;
	
	//CONSTRUCTORS
	public GraphicDrawer(){
		nodes = new Vector<String>();
		edges = new Vector<Edge>();
	}
	
	public GraphicDrawer(Vector<Edge> e){
		nodes = takeNodes(e);
		edges = e;
	}
	
	public GraphicDrawer(Vector<String> n,Vector<Edge> e){
		nodes = n;
		edges = e;
	}
	
	public GraphicDrawer(Set<String> n,Set<Edge> e){
		nodes = new Vector<String>(n);
		edges = new Vector<Edge>(e);
	}
	
	
	//METHODS
	/**
	 * Add a new edge to actual graph.
	 * @param edge to be added.
	 */
	public void addEdge(Edge edge){
		if(!edges.contains(edge))
			edges.add(edge);
	}
	
	/**
	 * Add a new node to actual graph.
	 * @param node to be added.
	 */
	public void addNode(String node){
		if(!nodes.contains(node))
			nodes.add(node);
	}
	
	/**
	 * This method evaluate some edge have been specified.
	 * @return true if there are any edge specified or false in other case.
	 */
	public boolean thereAreEdges(){
		return !edges.isEmpty();
	}
	
	/**
	 * This method evaluate some node have been specified.
	 * @return true if there are any node specified or false in other case.
	 */
	public boolean thereAreNodes(){
		return !nodes.isEmpty();
	}
	
	/**
	 * Evaluate if this edge are exists in the actual graph
	 * @param edge to be searched.
	 * @return true if this edge are in this graph or false in other case.
	 */
	public boolean containsEdge(Edge edge){
		return edges.contains(edge);
	}
	
	/**
	 * Evaluate if this node are exists in the actual graph
	 * @param node to be searched.
	 * @return true if this node are in this graph or false in other case.
	 */
	public boolean containsNode(String node){
		return nodes.contains(node);
	}
	
	/**
	 * This method cleans all edges specified on this graph.
	 */
	public void deleteAllEdges(){
		edges.removeAllElements();
	}
	
	/**
	 * This method cleans all nodes and edges specified on this graph.
	 */
	public void deleteAllNodes(){
		nodes.removeAllElements();
		deleteAllEdges();
	}
	
	/**
	 * Delete the specified edge. 
	 * @param edge index to be deleted.
	 * @return the deleted element.
	 * @throws IndexOutOfBoundsException.
	 */
	public Edge deleteEdge(int edge){
		return edges.remove(edge);
	}
	
	/**
	 * Delete the specified node and related edges. 
	 * @param node index to be deleted.
	 * @return the deleted element.
	 * @throws IndexOutOfBoundsException.
	 */
	public String deleteNode(int node){
		String n = nodes.elementAt(node);
		
		//Delete related edges
		for(int i=0; i<edges.size();++i)
			if(edges.elementAt(i).contains(n))
				edges.removeElementAt(i);
			
		return nodes.remove(node);
	}
	
	/**
	 * This method is used to obtain a panel with the actual graph using a 
	 * CircleLayout and an auto-calculated dimension to create the graph.
	 * @return a JPanel that contains the graph.
	 * @see edu.uci.ics.jung.visualization.VisualizationImageServer
	 */
	public VisualizationImageServer draw(){
		int dimension = numberNodes()*30 + numberEdges()*20;
		return draw(CircleLayout,dimension,dimension);
	}
	
	/**
	 * This method is used to obtain a panel with the actual graph using the
	 * layout and dimension specified.
	 * @param layout to be used.
	 * @param width of the graph image.
	 * @param height of the graph image.
	 * @return a JPanel that contains the graph.
	 * @see edu.uci.ics.jung.visualization.VisualizationImageServer
	 */
	public VisualizationImageServer draw(int layout,int width, int height){
		//return draw(layout,new Dimension(width,height),true,Color.gray);
        return draw(layout,new Dimension(width,height),true,Color.white);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	/**
	 * This method is used to obtain a panel with the actual graph using the
	 * layout and dimension specified.
	 * @param layout to be used.
	 * @param dimension of the graph image.
	 * @return a JPanel that contains the graph.
	 * @see edu.uci.ics.jung.visualization.VisualizationImageServer
	 */
	public VisualizationImageServer draw(int layout,Dimension dimension,boolean vertexLabelInside, Color backgroundColor){
		DirectedSparseGraph g = new DirectedSparseGraph();
		
		//Add nodes
	    for(Iterator<String> nodeSet = nodes.iterator(); nodeSet.hasNext();)
	    	g.addVertex(nodeSet.next());
	    
	    //Add edges
	    Edge e;
	    boolean withErrors;
	    String label;
	    for(Iterator<Edge> edgeSet = edges.iterator(); edgeSet.hasNext();){
	    	e = edgeSet.next(); 
	    	
	    	withErrors = true;
	    	label = e.getLabel();
	    	while(withErrors){
		    	try{
		    		g.addEdge(label, e.getSource(), e.getTarget());
		    		withErrors = false;
		    	}catch(IllegalArgumentException iae){
		    		withErrors = true;
		    		String zero = "0";
		    		label = zero + label;
		    	}
	    	}
	    }
	    
	    //Take layout
	    Layout ly;
	    
	    switch(layout){
	    	case 1: ly = new DAGLayout(g);
	    	break;
	    	case 2: ly = new FRLayout(g);
	    	break;
	    	case 3: ly = new ISOMLayout(g);
	    	break;
	    	case 4: ly = new KKLayout(g);
	    	break;
	    	case 5: ly = new SpringLayout(g);
	    	break;
	    	default: ly = new CircleLayout(g);
	    }
	    
	    //Create the image
	    VisualizationImageServer vs = new VisualizationImageServer(ly,dimension);
	    
	  //Some settings
	  	//Node labels
	    vs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
	    
	    if(vertexLabelInside)
	    	vs.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
	    //Background color
	    vs.setBackground(backgroundColor);
	    
	    return vs;
	}
	
	/**
	 * This method is used to find an edge on the specified edge set of this graph.
	 * @param edge to be searched.
	 * @return the index of the wanted edge or -1 if it's not on the set.
	 */
	public int edgeIndex(Edge edge){
		return edges.indexOf(edge);
	}

	/**
	 * This method is used to obtain an edge from edge set of this graph.
	 * @param index of the edge wanted.
	 * @return the element wanted.
	 * @throws IndexOutOfBoundsException.
	 */
	public Edge getEdge(int index){
		return edges.elementAt(index);
	}
	
	/**
	 * This method is used to obtain the actual specified edges set.
	 * @return a vector with all the specified edges for this graph.
	 */
	public Vector<Edge> getEdges(){
		return edges;
	}
	
	/**
	 * This method is used to obtain a node from node set of this graph.
	 * @param index of the node wanted.
	 * @return the element wanted.
	 * @throws IndexOutOfBoundsException.
	 */
	public String getNode(int index){
		return nodes.elementAt(index);
	}
	
	/**
	 * This method is used to obtain the actual specified nodes set.
	 * @return a vector with all the specified nodes for this graph.
	 */
	public Vector<String> getNodes(){
		return nodes;
	}
	
	/**
	 * This method is used to find a node on the specified node set of this graph.
	 * @param node to be searched.
	 * @return the index of the wanted node or -1 if it's not on the set.
	 */
	public int nodeIndex(String node){
		return nodes.indexOf(node);
	}
	
	/**
	 * @return the number of specified edges on this graph.
	 */
	public int numberEdges(){
		return edges.size();
	}
	
	/**
	 * @return the number of specified nodes on this graph.
	 */
	public int numberNodes(){
		return nodes.size();
	}
	
	/**
	 * This method is used to obtain all nodes specified in a edges set given.
	 * @param e is the edge set.
	 * @return a vector with all the nodes specified.
	 */
	public static Vector<String> takeNodes(Vector<Edge> e){
		Vector<String> nds = new Vector<String>();
		
		Edge eTemp;
		boolean src,trgt;
		
		for(Iterator<Edge> i = e.iterator(); i.hasNext();){
			eTemp = i.next();
			src=false;
			trgt=false;
			
			for(String n: nds){
				if(eTemp.getSource().equals(n)) 
					src=true;
				else if(eTemp.getTarget().equals(n)) //Target never equals source
					trgt=true;
			}
			
			if(!src)
				nds.add(eTemp.getSource());
			if(!trgt)
				nds.add(eTemp.getTarget());
		}
		
		return nds;
	}
	
	
	
	public static void main(String[] args){
		Vector<String> nodes = new Vector<String>();
		Vector<Edge> edges = new Vector<Edge>();
		
		nodes.add("1");
		nodes.add("2");
		nodes.add("3");
		
		edges.add(new Edge("A","1","2"));
		edges.add(new Edge("B","1","3"));
		
		GraphicDrawer gd = new GraphicDrawer(nodes,edges);
		
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(gd.draw());
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
}//END CLASS
