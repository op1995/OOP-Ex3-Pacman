 package gameClient;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import dataStructure.node_data;
import utils.Point3D;
import utils.Range;
public class GraphGUI{
    /** Holds the graph GUI component */
    GraphComponent graphComponent; 
    private DGraph Graph;
    /** The window */
    private static JFrame frame;
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
  	private boolean chooseRobot;
  	private boolean AddRobot;
  	private game_service game;
  	boolean a = false;
  	int scenario_num = 0;
    private static boolean windowsOn = false;
    /**
     *  Constructor that builds a completely empty graph.
     */
    public GraphGUI() {
		this.Graph = new DGraph();
		initializeGraph();
		this.ImageCount = 0;
		this.graphComponent = new GraphComponent(this.Graph);
		rangex=new Range(35.1850,35.2150);
		rangey=new Range(32.0950, 32.1130);
		width=1100;
		height=700;
    }
    public GraphGUI(DGraph g) {
		this.Graph = g;
		initializeGraph();
		this.ImageCount = 0;
		this.graphComponent = new GraphComponent(g);
		rangex=new Range(35.1850,35.2150);
		rangey=new Range(32.0950, 32.1130);
		width=1100;
		height=700;
    }
    JMenu menu = new JMenu();
    JMenu submenu = new JMenu();  
    JMenuItem saveImage = new JMenuItem();
    JMenuItem Load = new JMenuItem();
	JMenuItem Help = new JMenuItem();
	JMenuItem Auto = new JMenuItem(); 
	JMenuItem Player = new JMenuItem();
	JMenuItem StartGame = new JMenuItem();
	JMenuItem ChooseDest = new JMenuItem();

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
    	    submenu=new JMenu("play");  
    	    saveImage=new JMenuItem("Save Image");  
    	    Load=new JMenuItem("Load Game");  
    	    Help=new JMenuItem("Help");  
    	    Auto=new JMenuItem("Auto");  
    	    Player=new JMenuItem("Player");
    	    StartGame=new JMenuItem("StartGame");
    	    ChooseDest=new JMenuItem("ChooseDest");
    	    menu.add(saveImage); menu.add(Load); menu.add(Help);  
    	    submenu.add(Auto); submenu.add(Player);  submenu.add(StartGame);
    	    menu.add(submenu);
    	    mb.add(menu);  
    	    mb.add(ChooseDest);
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
		/*Load Game **/
		Load.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser LoadFromFile = new JFileChooser();
				LoadFromFile.setDialogTitle("Choose a file to load");
				LoadFromFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int returnVal = LoadFromFile.showOpenDialog(pane);
				File file = LoadFromFile.getSelectedFile();
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					Scanner sc;
					try {
						sc = new Scanner(file);
						String graph = sc.nextLine();
						Graph.init(graph);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} 					
				}
				graphComponent = new GraphComponent(Graph);
				graphComponent.repaint();
				execute();
			}
		});
		Help.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO pop up a Tutorial.
			}
		});
		Player.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseRobot = false;
				try {
					MakePlayer();
					AddRobot = true;
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(frame, e2);
				}
			}
		});		
		Auto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});	
		/*
		 * for algorithms.
		 */
		StartGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AddRobot = false;
				try {
					chooseRobot = true;
					game.startGame();
				} catch (Exception e2) {}
			}
		});	
		ChooseDest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					a = true;
				} catch (Exception e2) {}
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
    public void MakePlayer() {
    	scenario_num = Integer.valueOf(JOptionPane.showInputDialog("Input a scenario Number between 0 to 23."));
		if(scenario_num > 23 || scenario_num <0) {
			JOptionPane.showMessageDialog(frame, "Please input a legal scenario Number"
					+ "The Number should be between 0 to 23");
		}
		game = Game_Server.getServer(scenario_num);
		String g = game.getGraph();
		Graph.init(g);
		execute();
		String info = game.toString();
		JSONObject line;
		try {
			line = new JSONObject(info);
			System.out.println(info);
			System.out.println(g);
			// the list of fruits should be considered in your solution
			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {
				try {
					Graph.addFruit(new Fruit(f_iter.next()));
				} catch (Exception e2) {}
			}	
		}
		catch (JSONException e2) {e2.printStackTrace();}
    }
    public void userPlay() {
    	// TODO implement.
    }
    /**
     *  A mouse listener to handle click and drag actions on nodes.
     */
    private class MyMouseListener extends MouseAdapter {
//		/** How far off the center of the node was the click? */
//		private int deltaX;
//		private int deltaY;
//		/**
//		 *  This finds the shortest distance between mouse click point and line.
//		 *  Used to tell whether user clicked within 5px of an edge.
//		 *
//		 *  @return the distance between the point and the line
//		 */
//		public double findDist(Edge edge,
//				       double edgeX1, double edgeY1, double edgeX2, double edgeY2,
//				       double mouseX, double mouseY) {
//		    double edgeSlope = (edgeY2 - edgeY1) / (edgeX2 - edgeX1);
//		    double dist;
//		
//		    if (edgeSlope == 0.0) {
//			dist = -1.0;
//		    } else {
//			double perpSlope = (-1) * (1 / edgeSlope);
//			/* solving intersection:
//			   edge equation: y = edgeSlope(x) - edgeSlope(edgeX1) + edgeY1
//			   perp equation: y = perpSlope(x) - perpSlope(mouseX) + mouseY
//			   x = (edgeSlope*edgeX1 - edgeY1 - perpSlope*mouseX + mouseY) / (edgeSlope - perpSlope)
//			*/
//			double commonX = (edgeSlope*edgeX1 - edgeY1 - perpSlope*mouseX + mouseY) / (edgeSlope - perpSlope);
//			double commonY = edgeSlope*(commonX - edgeX1) + edgeY1;
//			double dx = Math.abs(mouseX - commonX);
//			double dy = Math.abs(mouseY - commonY);
//			dist = Math.sqrt(dx*dx + dy*dy);
//		    }
//		    return dist;
//		}
//
//		/**
//		 *  Tells whether x is less than y and y is less than z.
//		 *
//		 *  @return true if x <= y <= z
//		 */
//		public boolean ordered(double x, double y, double z) {
//		    return (x <= y) && (y <= z);
//		}

		public void mouseDragged(MouseEvent e) {
			
		}
		public void mouseReleased(MouseEvent e) {
			//AddRobot = true;
			chooseRobot = true;
		}
		public void mousePressed(MouseEvent e) {
		    double mouseX = e.getX();
		    double mouseY = e.getY();
		    double X=width/rangex.get_length();
			double Y=(0-height)/rangey.get_length();
			if(chooseRobot == true) {
				try {
					for (int robot : Graph.Robots.keySet()) {
				    	int robotX = (int) ((Graph.Robots.get(robot).getPos().x()-rangex.get_min())*X);
						int robotY = (int) ((Graph.Robots.get(robot).getPos().y()-rangey.get_max())*Y);
						if (Math.sqrt((robotX-mouseX)*(robotX-mouseX)+(robotY-mouseY)*(robotY-mouseY)) <= GraphComponent.NODE_RADIUS+1) {
						    try {
						    	if(Graph.Robots.containsKey(robot)) {//TODO not needed condition (always true)
							    	chosenRobot = Graph.Robots.get(robot);
							    	System.out.println(chosenRobot.getID());
							    }
							} catch (Exception e2) {
								System.out.println(e2);
							}
						}
				    }
				    for (Fruit fruit : Graph.Fruits.keySet()) {
				    	int robotX = (int) ((fruit.getPos().x()-rangex.get_min())*X);//TODO change name this is fruit
						int robotY = (int) ((fruit.getPos().y()-rangey.get_max())*Y);//TODO
						if (Math.sqrt((robotX-mouseX)*(robotX-mouseX)+(robotY-mouseY)*(robotY-mouseY)) <= GraphComponent.NODE_RADIUS+1) {
						    try {
						    	if(Graph.Fruits.containsKey(fruit)) {//TODO not needed condition (always true) 
							    	chosenFruit = fruit;
							    	System.out.println(fruit.getType());
							    }
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
				} catch (Exception e2) {}
//			    if(game.isRunning()) {
//			    	try {
//			    		Graph_Algo Algo = new Graph_Algo(Graph);
//			    		ArrayList<node_data> PathToFruit = (ArrayList<node_data>) Algo.shortestPath(chosenRobot.getSrc(), chosenFruit.getEdge().getSrc());
//			    		System.out.println(chosenFruit.getEdge().getSrc());
//			    		for(int i = 1; i<PathToFruit.size(); i++) {
//			    			game.chooseNextEdge(chosenRobot.getID(), PathToFruit.get(i).getKey());
//			    			chosenRobot.setPos(Graph.Nodes.get(chosenRobot.getSrc()).getLocation());
//			    			graphComponent.repaint();
//			    		}
//					} catch (Exception e2) {}
//			    }
//			    else {
//			    	System.out.println(game.toString());
//			    }
			}//up till now we choose a robot and a fruit
			if(AddRobot == true) {//not all robots were added yet
				for (int v : Graph.Nodes.keySet()) {
			    	int nodex = (int) ((Graph.Nodes.get(v).getLocation().x()-rangex.get_min())*X);
					int nodey = (int) ((Graph.Nodes.get(v).getLocation().y()-rangey.get_max())*Y);
					if (Math.sqrt((nodex-mouseX)*(nodex-mouseX)+(nodey-mouseY)*(nodey-mouseY)) <= GraphComponent.NODE_RADIUS+1) {
						game.addRobot(v);
						try {
							AddRobot = false;
							Thread.sleep(100);
						} catch (Exception e2) {}
						Iterator<String> r_iter = game.getRobots().iterator();
						Graph.Robots.clear();
						while(r_iter.hasNext()) {
							Graph.addRobot(new Robot(r_iter.next()));
						}
						graphComponent.repaint();
					}
				}
			}
		}
		public void mouseClicked(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
    }
    public void AddRobot() {
    	
    }
    private void initializeGraph() {}
}