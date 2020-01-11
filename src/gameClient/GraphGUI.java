 package gameClient;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import dataStructure.DGraph;
import dataStructure.Edge;
import dataStructure.Node;
import gameClient.GraphComponent;
import utils.Point3D;
import utils.Range;

import java.io.*;
import java.util.*;
public class GraphGUI{
    /** Holds the graph GUI component */
    private GraphComponent graphComponent; 
    private DGraph Graph;
    /** The window */
    private static JFrame frame;
    /** The node last selected by the user. */
    private Node chosenNode;
    /** The robot last selected by the user. */
    private Robot chosenRobot;
    /** The fruit last selected by the user. */
    private Fruit chosenFruit;
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
	JMenuItem i4 = new JMenuItem(); 
	JMenuItem i5 = new JMenuItem();
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
    	    i4=new JMenuItem("Auto");  
    	    i5=new JMenuItem("Player");  
    	    menu.add(saveImage); menu.add(Load); menu.add(Help);  
    	    submenu.add(i4); submenu.add(i5);  
    	    menu.add(submenu);  
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
		
    }

    /**
     *  Disables all text fields and sets default text within them to empty.
     */
    public void disableAll() {
		enterNodeData.setText("");
		enterNodeData.setEnabled(false);
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
		/** How far off the center of the node was the click? */
		private int deltaX;
		private int deltaY;
		/**
		 *  This finds the shortest distance between mouse click point and line.
		 *  Used to tell whether user clicked within 5px of an edge.
		 *
		 *  @return the distance between the point and the line
		 */
		public double findDist(Edge edge,
				       double edgeX1, double edgeY1, double edgeX2, double edgeY2,
				       double mouseX, double mouseY) {
		    double edgeSlope = (edgeY2 - edgeY1) / (edgeX2 - edgeX1);
		    double dist;
		
		    if (edgeSlope == 0.0) {
			dist = -1.0;
		    } else {
			double perpSlope = (-1) * (1 / edgeSlope);
			/* solving intersection:
			   edge equation: y = edgeSlope(x) - edgeSlope(edgeX1) + edgeY1
			   perp equation: y = perpSlope(x) - perpSlope(mouseX) + mouseY
			   x = (edgeSlope*edgeX1 - edgeY1 - perpSlope*mouseX + mouseY) / (edgeSlope - perpSlope)
			*/
			double commonX = (edgeSlope*edgeX1 - edgeY1 - perpSlope*mouseX + mouseY) / (edgeSlope - perpSlope);
			double commonY = edgeSlope*(commonX - edgeX1) + edgeY1;
			double dx = Math.abs(mouseX - commonX);
			double dy = Math.abs(mouseY - commonY);
			dist = Math.sqrt(dx*dx + dy*dy);
		    }
		    return dist;
		}

		/**
		 *  Tells whether x is less than y and y is less than z.
		 *
		 *  @return true if x <= y <= z
		 */
		public boolean ordered(double x, double y, double z) {
		    return (x <= y) && (y <= z);
		}

		public void mouseDragged(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		    double mouseX = e.getX();
		    double mouseY = e.getY();
		    double X=width/rangex.get_length();
			double Y=(0-height)/rangey.get_length();
		    // Recognize when a Robot is pressed.
		    for (int robot : Graph.Robots.keySet()) {
		    	int robotX = (int) ((Graph.Robots.get(robot).getPos().x()-rangex.get_min())*X);
				int robotY = (int) ((Graph.Robots.get(robot).getPos().y()-rangey.get_max())*Y);
				if (Math.sqrt((robotX-mouseX)*(robotX-mouseX)+(robotY-mouseY)*(robotY-mouseY)) <= GraphComponent.NODE_RADIUS+1) {
				    try {
				    	if(Graph.Robots.containsKey(robot)) {
					    	chosenRobot = Graph.Robots.get(robot);
					    }
					} catch (Exception e2) {
						System.out.println(e2);
					}
				}
		    }
		    for (Fruit fruit : Graph.Fruits.keySet()) {
		    	int robotX = (int) ((fruit.getPos().x()-rangex.get_min())*X);
				int robotY = (int) ((fruit.getPos().y()-rangey.get_max())*Y);
				if (Math.sqrt((robotX-mouseX)*(robotX-mouseX)+(robotY-mouseY)*(robotY-mouseY)) <= GraphComponent.NODE_RADIUS+1) {
				    try {
				    	if(Graph.Fruits.containsKey(fruit)) {
					    	chosenFruit = fruit;
					    }
					} catch (Exception e2) {
						System.out.println(e2);
					}
				}
		    }
	    }
		public void mouseClicked(MouseEvent e) {
			double mouseX = e.getX();
		    double mouseY = e.getY();
		    double X=width/rangex.get_length();
			double Y=(0-height)/rangey.get_length();
		    // Recognize when a Robot is pressed.
		    for (int robot : Graph.Robots.keySet()) {
		    	int robotX = (int) ((Graph.Robots.get(robot).getPos().x()-rangex.get_min())*X);
				int robotY = (int) ((Graph.Robots.get(robot).getPos().y()-rangey.get_max())*Y);
				if (Math.sqrt((robotX-mouseX)*(robotX-mouseX)+(robotY-mouseY)*(robotY-mouseY)) <= GraphComponent.NODE_RADIUS+1) {
				    try {
				    	if(Graph.Robots.containsKey(robot)) {
					    	chosenRobot = Graph.Robots.get(robot);
					    }
					} catch (Exception e2) {
						System.out.println(e2);
					}
				}
		    }
		    for (Fruit fruit : Graph.Fruits.keySet()) {
		    	int robotX = (int) ((fruit.getPos().x()-rangex.get_min())*X);
				int robotY = (int) ((fruit.getPos().y()-rangey.get_max())*Y);
				if (Math.sqrt((robotX-mouseX)*(robotX-mouseX)+(robotY-mouseY)*(robotY-mouseY)) <= GraphComponent.NODE_RADIUS+1) {
				    try {
				    	if(Graph.Fruits.containsKey(fruit)) {
					    	chosenFruit = fruit;
					    }
					} catch (Exception e2) {
						System.out.println(e2);
					}
				}
		    }
//		    if (chosenRobot != null) {
//				deltaX = chosenRobot.getPos().ix() - e.getX();
//				deltaY = chosenRobot.getPos().iy() - e.getY();
//		    }
		    // Recognize when a fruit is clicked.
		}
		public void mouseMoved(MouseEvent e) {	
		} 
    }
    public void PrintWhite() {
		for(int u : Graph.getEdges().keySet()) {
	    	for (int v : Graph.getEdges().get(u).keySet()) {
				Graph.getEdge(u).get(v).setTag(0);	
			}
	    }
		graphComponent.repaint();
	}
    private void initializeGraph() {}
}