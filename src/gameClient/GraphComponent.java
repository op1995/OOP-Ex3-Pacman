package gameClient;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import dataStructure.DGraph;
import dataStructure.Node;
import utils.Point3D;
import utils.Range;

/**
 *  A component that draws a picture of a graph.
 *  @author Ibrahem Chahine, Ofir Peller.
 */
public class GraphComponent extends JComponent {
	  private static final long serialVersionUID = -4822415017845138079L;
	
	 /** The radius of the circle to draw for a node. */
	  public static final int NODE_RADIUS = 10;
	
	  /** The diameter of the circle to draw for a node. */
	  public static final int NODE_DIAMETER = 2 * NODE_RADIUS;
	
	  /** The length of a stroke of an arrow head. */
	  public static final int ARROW_HEAD_LENGTH = 15;
	
	  /** The angle of the stroke of an arrow head with respect to the line. */
	  public static final double ARROW_ANGLE = 9.0*Math.PI/10.0;
	  /** 
	   *  The graph to draw.  Note that this graph is based on the Node and Edge classes,
	   *  so the data associated with a node includes its key ,position and color.
	   */
	  DGraph graph;
	  private int width;
	  private int height;
	  private Range rangex;
	  private Range rangey;
	  /** 
	   *  Constructor.
	 * @param Graph 
	   *
	   *  @param graphWithPlacement - the graph to draw
	   */
	  public GraphComponent(DGraph Graph) {
	    this.graph = Graph;
	    setSize(new Dimension(1100, 700));
	    setPreferredSize(new Dimension(1100, 700));
	    rangex=new Range(35.1850,35.2150);
		rangey=new Range(32.0950, 32.1130);
		width=1100;
		height=700;
	  }
	  /**
	   *  This method Draws the graph.
	   */
	  public void paint(Graphics g){
			double X=width/rangex.get_length();
			double Y=(0-height)/rangey.get_length();
			for(int v : this.graph.Nodes.keySet()) {
				int x0= (int) ((graph.getNodes().get(v).getLocation().x()-rangex.get_min())*X);
				int y0=(int) ((graph.getNodes().get(v).getLocation().y()-rangey.get_max())*Y);
				g.setColor(Color.yellow);
				g.fillOval(x0 - NODE_RADIUS, y0 - NODE_RADIUS, NODE_DIAMETER, NODE_DIAMETER);
			}
			for(int v : this.graph.Nodes.keySet()) {
				for(int u : this.graph.Edges.get(v).keySet()) {
					int x0= (int) ((graph.getNodes().get(v).getLocation().x()-rangex.get_min())*X);
					int y0=(int) ((graph.getNodes().get(v).getLocation().y()-rangey.get_max())*Y);
					int x1=(int) ((graph.getNodes().get(u).getLocation().x()-rangex.get_min())*X);
					int y1=(int) ((graph.getNodes().get(u).getLocation().y()-rangey.get_max())*Y);
					g.setColor(Color.BLUE);
					g.drawLine(x0, y0, x1, y1);
					g.setColor(Color.CYAN);
					double theta = Math.atan2(y1 - y0, x1 - x0);  // angle of edge's arrow
					double edgeX = x1 - Math.cos(theta)*NODE_RADIUS+1;
					double edgeY = y1 - Math.sin(theta)*NODE_RADIUS+1;
					double arrowX1 = edgeX + Math.cos(theta-ARROW_ANGLE)*ARROW_HEAD_LENGTH;
				    double arrowY1 = edgeY + Math.sin(theta-ARROW_ANGLE)*ARROW_HEAD_LENGTH;
				    double arrowX2 = edgeX + Math.cos(theta+ARROW_ANGLE) * ARROW_HEAD_LENGTH;
				    double arrowY2 = edgeY + Math.sin(theta+ARROW_ANGLE) * ARROW_HEAD_LENGTH;
				    Polygon arrowHead = new Polygon(new int[]{(int)Math.round(edgeX),
				                                              (int)Math.round(arrowX1),
				                                              (int)Math.round(arrowX2)},
				                                    new int[]{(int)Math.round(edgeY),
				                                              (int)Math.round(arrowY1),
				                                              (int)Math.round(arrowY2)}, 3);
				    g.fillPolygon(arrowHead);
				    g.setColor(Color.RED);
					double Weight=graph.getEdges().get(v).get(u).getWeight();
					Weight = (double) ((int) (Weight * 10)) / (10);
					g.drawString(Double.toString(Weight), x1*75/100 + x0*1/4, y1*75/100 + y0*1/4);
				}
			}
			for (int r : this.graph.Robots.keySet()) {
				int x0 = (int) ((graph.Robots.get(r).getPos().x()-rangex.get_min())*X);
				int y0 = (int) ((graph.Robots.get(r).getPos().y()-rangey.get_max())*Y);
				try {
					BufferedImage image = ImageIO.read(new File("pics\\pacman1.gif"));
					g.drawImage(image.getScaledInstance(NODE_RADIUS*5, -1, Image.SCALE_SMOOTH), x0-NODE_DIAMETER, y0-NODE_DIAMETER,null);
					g.setColor(Color.BLACK);
				} catch (IOException e) {System.out.println(e);}
			}
		    for(Fruit f : this.graph.Fruits.keySet()) {
		    	int x0 = (int) ((f.getPos().x()-rangex.get_min())*X);
				int y0 = (int) ((f.getPos().y()-rangey.get_max())*Y);
				try {
					if(f.getType() == -1) {
						BufferedImage image = ImageIO.read(new File("pics\\orange.gif"));
						g.drawImage(image.getScaledInstance(NODE_RADIUS*3, -1, Image.SCALE_SMOOTH), x0-NODE_DIAMETER, y0-NODE_DIAMETER,null);
						g.setColor(Color.BLACK);
					}
					else {
						BufferedImage image = ImageIO.read(new File("pics\\apple.gif"));
						g.drawImage(image.getScaledInstance(NODE_RADIUS*3, -1, Image.SCALE_SMOOTH), x0-NODE_DIAMETER, y0-NODE_DIAMETER,null);
						g.setColor(Color.BLACK);
					}
				} catch (IOException e) {System.out.println(e);}
		    }
		}
	  public void saveImage(String name,String type) {
		  	BufferedImage image = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g2 = image.createGraphics();
			paint(g2);
			try{
				ImageIO.write(image, type, new File(name+"."+type));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	  /**
		 * 
		 * @param data denote some data to be scaled
		 * @param r_min the minimum of the range of your data
		 * @param r_max the maximum of the range of your data
		 * @param t_min the minimum of the range of your desired target scaling
		 * @param t_max the maximum of the range of your desired target scaling
		 * @return
		 */
		private double scale(double data, double r_min, double r_max, double t_min, double t_max)
		{
			double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
			return res;
		}
	  }
	
