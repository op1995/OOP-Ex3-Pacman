package gameClient;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.Node;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;
import utils.Range;
public class GraphGUI{
	/** Holds the graph GUI component */
	GraphComponent graphComponent; 
	DGraph Graph;
	static JFrame frame;
	public static int NODE_RADIUS = 10;
	/** The window */
	/** The node last selected by the user. */
	private Node chosenNode;
	/** The robot last selected by the user. */
	Robot chosenRobot;
	/** The fruit last selected by the user. */
	Fruit chosenFruit;
	/** Text box for editing node's data. */
	public JTextField enterNodeData;
	/** Text box for editing edge's data. */
	public JMenuBar File;
	public JButton SaveImage;
	private int ImageCount;
	private int width;
	private int height;
	private Range rangex;
	private Range rangey;
	boolean chooseRobot;
	boolean AddRobot;
	game_service game;
	boolean a = false;
	Thread thread;
	Robot_Algo RobotAlgo;
	private static boolean windowsOn = false;
	/**
	 *  Constructor that builds a completely empty graph.
	 */
	public GraphGUI() {
		this.Graph = new DGraph();
		initializeGraph();
		this.ImageCount = 0;
		this.graphComponent = new GraphComponent(this.Graph);
		this.RobotAlgo = new Robot_Algo(Graph);
		rangex=new Range(35.1850, 35.2150);
		rangey=new Range(32.0950, 32.1130);
		width=this.graphComponent.width;
		height=this.graphComponent.height;
	}
	public GraphGUI(DGraph g) {
		this.Graph = g;
		initializeGraph();
		this.ImageCount = 0;
		this.graphComponent = new GraphComponent(g);
		this.RobotAlgo = new Robot_Algo(Graph);
		rangex=new Range(35.1850,35.2150);
		rangey=new Range(32.0950, 32.1130);
		width=this.graphComponent.width;
		height=this.graphComponent.height;
	}
	JMenu menu = new JMenu();
	JMenu submenu = new JMenu();  
	JMenuItem saveImage = new JMenuItem();
	JMenuItem Information = new JMenuItem();
	/**
	 *  Create and show the GUI.
	 */
	public void createAndShowGUI() {
		// Create and set up the window.
		windowsOn = true;
		frame = new JFrame("The Maze Of Waze");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar mb=new JMenuBar();  
		menu=new JMenu("Menu");  
		saveImage=new JMenuItem("Save Image"); 
		menu.add(saveImage);
		menu.add(Information);
		mb.add(menu);  
		frame.setJMenuBar(mb);  
		// Add components
		createComponents(frame.getContentPane());
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}
	/**
	 *  Create the components and add them to the frame.
	 *  @param pane the frame to which to add them
	 */
	public void createComponents(Container pane) {
		pane.add(this.graphComponent);
		MyMouseListener ml = new MyMouseListener();
		this.graphComponent.addMouseListener(ml);
		this.graphComponent.addMouseMotionListener(ml);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				windowsOn = false;
			}
		});
		/*save Image **/
		saveImage.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				graphComponent.saveImage("image"+ImageCount,"png");
				ImageCount++;
			}
		});	
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
//				if(!GraphComponent.ManualModeOn) {
//					int newheight =  e.getComponent().getHeight();
//					int newwidth = e.getComponent().getWidth();
//					graphComponent.height = newheight;
//					graphComponent.width = newwidth;
//				}
//				
			}
		});
	}
	/**
	 *  Execute the application.
	 */
	public void execute() {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if(windowsOn) {
						frame.setVisible(false);
						createAndShowGUI();
					}
					else {
						windowsOn = true;
						createAndShowGUI();
					}
				} catch (Exception e) {System.out.println(e);}
			}
		});
	}

	/**
	 *  The obligatory main method for the application.  With no
	 *  arguments the application will read the graph from the standard
	 *  input; with one argument (a file name) it will read the graph
	 *  from the named file.
	 *
	 *  @param args  the command-line arguments
	 */
	public static void main(String[] args) throws IOException {
		GraphGUI graphicGraph;
		graphicGraph = new GraphGUI();
		graphicGraph.execute();
	}
	/**
	 *  A mouse listener to handle click and drag actions on nodes.
	 */
	private class MyMouseListener extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			chooseRobot = true;
		}
		public void mousePressed(MouseEvent e) {
			double mouseX = e.getX();
			double mouseY = e.getY();
			double X=width/rangex.get_length();
			double Y=(-height)/rangey.get_length();
			if(chooseRobot == true) {
				try {
					for (int robot : Graph.Robots.keySet()) {
						int robotX = (int) ((Graph.Robots.get(robot).getPos().x()-rangex.get_min())*X);
						int robotY = (int) ((Graph.Robots.get(robot).getPos().y()-rangey.get_max())*Y);
						if (Math.sqrt((robotX-mouseX)*(robotX-mouseX)+(robotY-mouseY)*(robotY-mouseY)) <= GraphComponent.NODE_RADIUS+1) {
							try {
								chosenRobot = Graph.Robots.get(robot);
								System.out.println("chosenRobot.getID() = "+ chosenRobot.getID());
							} catch (Exception e2) {
								System.out.println(e2);
							}
						}
					}
					for (int i = 0; i < Graph.Fruits.length; i++) {
						if(Graph.Fruits[i]==null) {continue;}
						Fruit fruit = Graph.Fruits[i];
						int fruitX = (int) ((fruit.getPos().x()-rangex.get_min())*X);//TODO change name this is fruit
						int fruitY = (int) ((fruit.getPos().y()-rangey.get_max())*Y);//TODO
						if (Math.sqrt((fruitX-mouseX)*(fruitX-mouseX)+(fruitY-mouseY)*(fruitY-mouseY)) <= GraphComponent.NODE_RADIUS+1) {
							try {
								System.out.println(fruit.toString());
								chosenFruit = fruit;
							} catch (Exception e2) {
								System.out.println(e2);
							}
						}
					}
					for (int node : Graph.getNodes().keySet()) {
						double nodeX = Graph.getNode(node).getLocation().x();
						double nodeY = Graph.getNode(node).getLocation().y();
						if (Math.sqrt((nodeX-mouseX)*(nodeX-mouseX)+(nodeY-mouseY)*(nodeY-mouseY)) <= GraphComponent.NODE_RADIUS) {
							try {
								if(Graph.getNodes().containsKey(node)) {
									chosenNode = (Node) Graph.getNodes().get(node);
								}
							} catch (Exception e2) {
								System.out.println(e2);
							}
						}
					}
					if(chosenFruit != null && chosenRobot != null) {
						Graph_Algo Algo = new Graph_Algo(Graph);
						int src = chosenRobot.getSrc();
						int dest = chosenFruit.getEdge().getSrc();
						int dest2 = chosenFruit.getEdge().getDest();
						ArrayList<node_data> Path = new ArrayList<node_data>();
						Path = (ArrayList<node_data>) Algo.shortestPath(src, dest);
						Path.add(Graph.getNodes().get(dest2));
						Graph.Robots.get(chosenRobot.getID()).setPathToFruit(Path);
						chosenFruit = null;
						chosenRobot = null;
					}
					chosenFruit = null;
				} catch (Exception e2) {e2.printStackTrace();}
			}//up till now we choose a robot and a fruit
		}
	}
	private void initializeGraph() {}
}