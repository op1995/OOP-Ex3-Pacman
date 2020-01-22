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
import java.time.LocalDateTime;
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
public class GraphComponent extends JComponent{
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
	int width;
	int height;
	private Range rangex;
	private Range rangey;
	public int gameLevel;
	public int MyGrade;
	public int grade;
	public static String Info;
	public ArrayList<Integer> ScoresInScenario;
	public HashMap<Integer, HashMap<Integer,Integer>> scores;
	/** 
	 *  Constructor.
	 * @param Graph 
	 *
	 *  @param graphWithPlacement - the graph to draw
	 */
	public GraphComponent(DGraph Graph) {
		SimpleDB DB = new SimpleDB();
		this.gameLevel = 0;
		this.ScoresInScenario = new ArrayList<Integer>();
		this.graph = Graph;
		this.width=1100;
		this.height=700;
		this.grade = 0;
		setSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
		rangex=new Range(35.1850,35.2150);
		rangey=new Range(32.0950, 32.1130);
		DB.UpdateScores();
		this.scores = DB.getScores();
		
	}
	/**
	 * This method paints the nodes of this Graph.
	 * @param g Graphics.
	 */
	public void paintNodes(Graphics g) {
		double X=width/rangex.get_length();
		double Y=(0-height)/rangey.get_length();
		for(int v : this.graph.Nodes.keySet()) {
			int x0= (int) ((graph.getNodes().get(v).getLocation().x()-rangex.get_min())*X);
			int y0=(int) ((graph.getNodes().get(v).getLocation().y()-rangey.get_max())*Y);
			g.setColor(Color.yellow);
			g.fillOval(x0 - NODE_RADIUS, y0 - NODE_RADIUS, NODE_DIAMETER, NODE_DIAMETER);
			g.setColor(Color.black);
			g.drawString(String.valueOf(v), x0-NODE_RADIUS, y0-NODE_RADIUS);
		}
	}
	/**
	 * This method paints the Edges if this Graph.
	 * @param g Graphics.
	 * @param x0 The x position of the source node of an edge.
	 * @param y0 The y position of the source node of an edge.
	 * @param x1 The x position of the destination node of an edge.
	 * @param y1 The y position of the destination node of an edge.
	 * @param theta The angle of edge's arrow.
	 * @param edgeX The the x position of the edge.
	 * @param edgeX The the y position of the edge.
	 * @param arrowHead A polygon for the arrow head.
	 */
	public void paintEdges(Graphics g) {
		double X=width/rangex.get_length();
		double Y=(0-height)/rangey.get_length();
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
	}
	/**
	 * This method paint the Robots of this graph.
	 * @param r An ID of a robot in this Graph.
	 * @param x the x position of a Robot in the Graph.
	 * @param y the y position of a Robot in the Graph.
	 * @param image The image of the robot to paint.
	 * @param g Graphics.
	 */
	public void paintRobots(Graphics g) {
		double X=width/rangex.get_length();
		double Y=(0-height)/rangey.get_length();
		for (int r : this.graph.Robots.keySet()) {
			int x = (int) ((graph.Robots.get(r).getPos().x()-rangex.get_min())*X);
			int y = (int) ((graph.Robots.get(r).getPos().y()-rangey.get_max())*Y);
			//			g.setColor(Color.RED);
			//			g.drawString(graph.Robots.get(r).getPathToFruit().toString(), x-NODE_RADIUS, y-NODE_RADIUS);
			try {
				BufferedImage image = ImageIO.read(new File("pics\\pacman1.gif"));
				g.drawImage(image.getScaledInstance(NODE_RADIUS*5, -1, Image.SCALE_SMOOTH), x-NODE_DIAMETER, y-NODE_DIAMETER,null);
				g.setColor(Color.BLACK);
				//					g.drawString(String.valueOf(graph.Robots.get(r).getDest()), x-NODE_RADIUS, y-NODE_RADIUS);


			} catch (IOException e) {
				try {
					//						System.out.println("Couldn't find picture pacman1.gif. Trying linux path instead.");
					BufferedImage image = ImageIO.read(new File("pics/pacman1.gif"));
					g.drawImage(image.getScaledInstance(NODE_RADIUS*5, -1, Image.SCALE_SMOOTH), x-NODE_DIAMETER, y-NODE_DIAMETER,null);
					g.setColor(Color.BLACK);
				}catch (IOException f) {System.out.println(f);}
			}
		}

	}
	/**
	 * This method paint the Robots of this graph.
	 * @param f A Fruit in this Graph.
	 * @param x the x position of a Fruit in the Graph.
	 * @param y the y position of a Fruit in the Graph.
	 * @param image The image of the Fruit to paint.
	 * @param g Graphics.
	 */
	public void paintFruits(Graphics g) {
		double X=width/rangex.get_length();
		double Y=(0-height)/rangey.get_length();
		//		for(Fruit f : this.graph.Fruits.keySet()) {
		for (int i = 0; i < graph.Fruits.length; i++) {
			if(graph.Fruits[i]==null) {continue;}
			Fruit f = graph.Fruits[i];

			int x = (int) ((f.getPos().x()-rangex.get_min())*X);
			int y = (int) ((f.getPos().y()-rangey.get_max())*Y);
			//			String isAlive = "FALSE";
			//			if(f.getisAlive()) {isAlive="TRUE";}
			//			g.drawString(isAlive, x-NODE_DIAMETER, y-NODE_DIAMETER);
			if(f.getType() == -1) {
				try {
					BufferedImage image = ImageIO.read(new File("pics\\orange.gif"));
					g.drawImage(image.getScaledInstance(NODE_RADIUS*3, -1, Image.SCALE_SMOOTH), x-NODE_DIAMETER, y-NODE_DIAMETER,null);
					g.setColor(Color.BLACK);
				}
				catch (IOException e) {
					try {
						//							System.out.println("Couldn't find picture orange.gif. Trying linux path instead.");
						BufferedImage image = ImageIO.read(new File("pics/orange.gif"));
						g.drawImage(image.getScaledInstance(NODE_RADIUS*3, -1, Image.SCALE_SMOOTH), x-NODE_DIAMETER, y-NODE_DIAMETER,null);
						g.setColor(Color.BLACK);
					}
					catch (IOException f1) {System.out.println(f1);}
				}
			}
			else {
				try {
					BufferedImage image = ImageIO.read(new File("pics\\apple.gif"));
					g.drawImage(image.getScaledInstance(NODE_RADIUS*3, -1, Image.SCALE_SMOOTH), x-NODE_DIAMETER, y-NODE_DIAMETER,null);
					g.setColor(Color.BLACK);
				}
				catch (IOException e) {
					try {
						//							System.out.println("Couldn't find picture apple.gif. Trying linux path instead.");
						BufferedImage image = ImageIO.read(new File("pics/apple.gif"));
						g.drawImage(image.getScaledInstance(NODE_RADIUS*3, -1, Image.SCALE_SMOOTH), x-NODE_DIAMETER, y-NODE_DIAMETER,null);
						g.setColor(Color.BLACK);
					}
					catch (IOException f1) {System.out.println(f1);}
				}
			}

		}
	}
	/**
	 * This method paint this Graph.
	 */
	public void paint(Graphics g){
		try {

			paintEdges(g);
			paintNodes(g);
			paintFruits(g);
			paintRobots(g);
			g.setColor(Color.BLACK);
			if(this.scores.get(315554022).containsKey(this.gameLevel)) {
				this.MyGrade = this.scores.get(315554022).get(this.gameLevel);
//				Collections.sort( this.ScoresInScenario.indexOf(this.MyGrade) );
				for(int i : scores.keySet()) {
					if(!this.ScoresInScenario.contains(scores.get(i).get(gameLevel))){
						this.ScoresInScenario.add(scores.get(i).get(gameLevel));
					}
				}
				Info = "Score : "+String.valueOf(this.grade)+ "      Scenario : " + String.valueOf(this.gameLevel) +
						"    Rank In This scenario :" + String.valueOf(this.ScoresInScenario.indexOf(this.MyGrade))
						+ "   Number of played games in the Server : " + String.valueOf(scores.get(315554022).size());
				g.drawString(Info, 10, 10);
			}
			else {
				Info = "Score : "+String.valueOf(this.grade)+ "      Scenario : " + String.valueOf(this.gameLevel);
				g.drawString(Info, 10, 10);
			}
		} catch (Exception e) {e.printStackTrace();}

	}
	/**
	 * This method save an image to file.
	 * @param name The file name.
	 * @param type The type of the file.
	 */
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
	public void setGameLevel(int l) {
		if(l>23 || l<0) {
			throw new RuntimeException("Please pick a scenario between 0 to 23");
		}
		this.gameLevel = l;
	}
}

