package khaos.DREAMProject.DREAM3.Solutions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.VisualizationImageServer;
import khaos.tools.Graphics.Edge;
import khaos.tools.Graphics.GraphicDrawer;

public class D3_GraphicDrawer extends GraphicDrawer{
	//FIELDS
	
	//CONSTRUCTORS
	public D3_GraphicDrawer(){
		super();
	}
	
	public D3_GraphicDrawer(Vector<String> n,Vector<Edge> e){
		super(n,e);
	}
	
	public D3_GraphicDrawer(Set<String> n,Set<Edge> e){
		super(n,e);
	}
	
	public D3_GraphicDrawer(Vector<Edge> e){
		super(e);
	}
	
	//METHODS
	@Override
	/**
	 * This method is used to obtain a panel with the actual graph using a 
	 * CircleLayout and an auto-calculated dimension to create the graph.
	 * @return a JPanel that contains the graph.
	 * @warning use it only if your edges are instances of class PSM_Dream3.Force
	 * @see edu.uci.ics.jung.visualization.VisualizationImageServer
	 */
	public VisualizationImageServer draw(){
		int dimension = numberNodes()*30 + numberEdges()*20;
		return this.draw(CircleLayout,new Dimension(dimension,dimension),true,Color.gray);
	}
	
	@Override
	/**
	 * This method is used to obtain a panel with the actual graph using the
	 * layout and dimension specified.
	 * @param layout to be used.
	 * @param width of the graph image.
	 * @param height of the graph image.
	 * @return a JPanel that contains the graph.
	 * @warning use it only if your edges are instances of class PSM_Dream3.Force
	 * @see edu.uci.ics.jung.visualization.VisualizationImageServer
	 */
	public VisualizationImageServer draw(int layout,int width, int height){
		return this.draw(layout,new Dimension(width,height),true,Color.gray);
	}
	
	
	/**
	 * This method is used to obtain a panel with the actual graph using the
	 * layout and dimension specified.
	 * @param layout to be used.
	 * @param dimension of the graph image.
	 * @param backgroundColor is the color that will be used for background.
	 * @return a JPanel that contains the graph.
	 * @warning use it only if your edges are instances of class PSM_Dream3.Force
	 * @see edu.uci.ics.jung.visualization.VisualizationImageServer
	 */
	public VisualizationImageServer draw(int layout,Dimension dimension, Color backgroundColor){
		return this.draw(layout,dimension,true,backgroundColor);
	}
	
	
	/**
	 * This method is used to obtain a panel with the actual graph using the
	 * layout and dimension specified.
	 * @param layout to be used.
	 * @param dimension of the graph image.
	 * @param vertexLabelInside a boolean used to indicate if we want that the vertex label appear inside of the vertex or outside.
	 * @param backgroundColor is the color that will be used for background.
	 * @return a JPanel that contains the graph.
	 * @warning use it only if your edges are instances of class PSM_Dream3.Force
	 * @see edu.uci.ics.jung.visualization.VisualizationImageServer
	 */
	public VisualizationImageServer draw(int layout,Dimension dimension, boolean vertexLabelInside,Color backgroundColor){
		VisualizationImageServer vs = super.draw(layout, dimension, true, backgroundColor);
		
		//Adding color edges
  		Transformer<Object,Paint> edgePaint = new Transformer<Object,Paint>() {

              @Override
              public Paint transform(Object o) {
            	  //Take name with format "force/freq" 
            	  String s = (String) o;
            	  
            	  try{
	            	  double force = Double.valueOf(s.split("/")[0]);
	            	  
	            	  //Thresholds-> [0,0.25)=White, [0.25,0.5)=Yellow, [0.5,0.75)=Orange, [0.75,Infinite)=Red
	                  if (force < 0.25)
	                      return (Paint) Color.WHITE;
	                  else if(force < 0.5)
	                      return  (Paint) Color.YELLOW;
	                  else if(force < 0.75)
	                	  return (Paint) Color.ORANGE;
	                  else
	                	  return (Paint) Color.RED;
            	  }catch(NumberFormatException nfe){
            		  System.err.println("The edge name should be a number or have the format \"number/(...)\"");
            		  return (Paint) Color.WHITE;
            	  }
              }
          };  
  		
  		vs.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
		
		return vs;
	}
	
	
}//END CLASS
