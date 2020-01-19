# Readme

This Project is an Exercise in an object  **Object-oriented**  programming (**OOP**) course in  **Ariel university**.

Authors:  **Ibrahem chahine, Ofir Peller.**

**NOTICE:** The project uses multiple packages, jar files, and libraries. Please make sure to setup your enviroment correctly when running this project. If you run in to trouble, refere to the Wiki for help or contact us.

## Description

This project's goal is to let the user view a GUI for an automatic game resembling Pacman. This game will run automatically, utilizing algorithms with the goal of achiveing the highest score possible, in the given time.
A manual mode is also avilable.

**In the algorithms package** 

**-GraphAlgo class**

A class implementing the graph_algorithms class.
Using smart algorithms we can conclude the best paths for us to take within the given graph.

```
init(graph g) - Inits with graph g.
copy() - Returns a deepcopy of the graph.
save(String file_name) - Saves the Project to file.
init(String file_name) - Inits the Project from file.
isConnected() - Check if the Graph is srongly connected.
shortestPathDist(int src, int dest) - Returns the Destination of the shortest path from src to dest. 
shortestPath(int src, int dest) - Returns a list of nodes representing the shortest path from src to dest.
TSP(List<Integer> targets) - Returns a list of nodes representing the TSP path from the targets.
```

**-MyMinHeap class**

A simple class to create and manage a minHeap. This is used to improve the copmlexity of our algorithms.

```
public MyMinheap(int maxSize) - Constructor. Build a new minHeap with as many as maxSize nodes.
public void add(Node newNode) - A method to add a node to an existing minHeap.
public int parent(int child) - returns the location of the parent node in the minHeap.
public int leftChild(int parentInt) - returns the location of the leftChild node in the minHeap.
public int rightChild(int parentInt) - returns the location of the rightChild node in the minHeap.
public boolean isLeaf(int pos) - returns true if the node in the given position is a leaf.
public void minHeapify(int v) -  a method to keep the property of the minHeap by reorganizing it, if needed.
public Node heapExtractMin() - returns the minimum node currently in the heap, while keeping the property of the minHeap.
public void swap(int src, int dst) - replaces the location of the given nodes in the minHeap.
public Node heapMinimum() - return the minimum node in the minHeap, without extracting it.
public boolean isEmpty() - returns false if the minHeap doesn't contain any nodes.
```

**In the dataStructure package** 

**-DGraph class**

Implemetns graph class.
A class that represents and contains the components of the graph, such as Nodes, Edges, Fruits and Robots.
There are 2 constructors. A default constructor and a copy constructor.
The user can use the following methods :
```
getNode(int key) - Will return the node data with the key.
getEdge(int src, int dest) - Will return the Edge data with the source and destination keys.
addNode(node_data n) - This method adds a node to the Graph.
connect(int src, int dest, double w) - This method creates an edge such that the source is src and the destination in dest.
getV() - Will return a shallow copy of the Collection of Nodes in the Graph.
getE(int node_id) - Will return a shallow copy of the Collection of Edges in the Graph.
removeNode(int key) - This method removes the node the belongs to the given key.
removeEdge(int src, int dest) - This method removes the edge of the src anf dest.
nodeSize() - Will return the number of nodes in the graph.
edgesize() - Will return the number of edges in the graph.
getMC - Will return the number of changes in the graph. 
public void addRobot(Robot r) - adds the given robot to the graph's Robots HashMap.
public void addFruit(Fruit f) - adds the given fruit to the graph's Fruits HashMap, getting it's edge as well.
public void removeFruit(Fruit f) - removes the given fruit from the graph.
public void init(String graphJson) - inits the graph from a given Json format string.
```

In this package there are also the Edge and Node classes, that implement edge_data and node_data respectively.
They are pretty straight forward so we will not go in to details here (otherwise this readme will get too long).
You can view our previous project, where we did do so, if you'd like ()


**In the gameClient package** 

**-AutomaticGameClass class**
The goal of this class is to utilize smart algorithms in order to get the best result (highest score) we can, in each game. You can run this class and ask for any of 0-23 of the avilable levels.

```
public static void runAuto(DGraph gameGraph , int scenario_num) - this Method will run and manage a game automatically. For the given graph and game level (scenario_num), it will utilize smart algorithms in order to get the best score we can, within the given scenario.
The method initializes the game given to it from the server and than, while the time limit isn't over, directs each robot to a fruit, according to our algorithms.
The method also uses the KML class in order to log the game and export it after it is over.
After the game is done, the score will be presented to the user.

private static void moveRobots(game_service game, DGraph gameGraph, GraphGUI gui) - Within the given values, this method sets each robot's next node to go to, if he isn't currently moving to another node.
If a robot is currently moving, and thus doesn't need to have it's next node set, it updates the position of the Robot object representing it in the gameGraph object, so it's location can be displayed to the user.

private static int nextNode(game_service game, int robotId, DGraph gameGraph) - For the given values, this method calculates the best next Node to go to, for the given robot in the game.
```

**-Fruit class**
A simple class representing a fruit in the game. Has a default constructor and a constructor using a Json formated string (as recived from the game server). Other then that only has sime basic getters and setters. (Pretty straight forward, detailing will make the readme too long).

**-GraphComponent class**
This class utilizes the DGraph class and paints a graph relevant to this task\project. This includes robots and fruits. Other than that just has some variables such as sizes and radiuses relevant to such objects.

**-GraphGUI class**
A class containing the settings and actionListeners of the graph. These settings are relevant for what fits the GUI of this project (more specific, less general, unlike similar classes before).

**-KML_Logger class**
This class is used to create KML files. These files are used as logs of runs of games.
The created KML file can be later used to run a visual simulation of the game in Google Earth.
The methods in the class get the nodes, edges, robots, and fruits of the given game. They are pretty straight forward, so there is no need to go in to detail about them here. You'll be able to understand it just by reading, given you are familiar with the KML formating.

**-MyGameGUI class**
This class's purpose is to run the main GUI, giving a visual representation of all of the project.
Running the class will result in asking the user to choose either the manual mode or the automatic mode, followed by choosing the level (0-23).

```
public static void main(String[] a) - the main method, used to run the class.
public static void runManual(DGraph gameGraph , int scenario_num) - given a graph and a game level (scenario number)
this method initializes the game and listens to the user's input, as it allows the user to manually control the robots on the graph.
private static void moveRobots(game_service game, DGraph gameGraph, GraphGUI gui) - This method is activated from the runManual method. It translates the given input from the user, to understandable commands for the code to deliver to the server, resulting in the robot moving along the graph.
private static int nextNode(int robotId, DGraph Graph) - gives back the next node on the robot's path to selected fruit, and updates the path (removes the acquired node).
```

**-Robot_Algo class**
The goal of this class is to utilize algorithms to make a calculated decision as to the fruit and path a given robot should take. These takes in to consideration the other robots currently on the graph as well, to maximize the gained score in the game.

```
EPS,1,2 - Epsilone values used to give leeway, how much is considered as acceptable distance, for the asked question.
isOnEdge - These various methods are aimed to determine if a given fruit is located on a certain edge in the graph, based on it's location (coordinates). Each method is used as a seprate "level" to go through, each with it's own purpose. These vary from making sure the fruit can legally be on the given edge (according to it's type), accquiring the location of the nodes of the tested edge, and checking the distance of the fruit from said nodes.
public Edge findEdge(Fruit f) - This method finds the edge the given fruit is on, in the graph.
public Fruit getClosestFruit(int robot, game_service game, DGraph gameGraph) - For the given values, this method returns the fruit closest to the given robot.
```

**-Robot class**
A simple class, of which each object represents a robot in the current game.
This class allows us to save and access the needed values of each of the game's robots, in order to make decisions such as where it should go next or if it needs to be directed at all.

The methods of this class include a constructor that parses a given Json string from the server, and other then that simple getters and setters.

**In the tests package** 
This class contains several Junit tests. These tests are aimed to help up make sure our project is working correctly. The tests in this package check the methods used in each class, to make sure they are functioning properly. These are pretty straight forward, so in order to keep this readme short, we will not go in to detail.

**In the utils package** 
This package contains several utilities given to us, to help us create and manage this project. These include the StdDraw class, that can be used to create a GUI, Point3D used to handle 3D locations of points on the graph, and Range, represents a simple 1D range of a shape [min,max].


This project further includes the data and pics folders, containing info about the different nodes and edges coordinates, our best scores in each game in KML files, and pictures used to display the fruits and robots on the screen.

The lib folder contains the Json jar, and the GameServer jar. The latter is the server that was written by our division's headmaster. This jar file gives and manages the games of this project. The updates and choosen next nodes to go to, the location of the fruits on the graph and the time management of the games are done by this jar file.

## Support

For help you can go to the javadoc. you can get a better explanations for the methods in the classes.

In the wiki we explain how to use this project, its prefered to read the instructions in the  **wiki pages.**

## Contributing

If you want to make changes to the code i will recommend to go over the tester before, it will help you to understand how the Methods and the Classes work. 

## Authors and acknowledgment

The Authors of this project are  **Ibrahem chahine, Ofir Peller.**

I want to thank all the GitHub open source users. Also, we thank :

1.  GeeksforGeeks.org,
2.  [stackoverflow.com].
3.  [GitHub.com].
4.  [[https://www.math.ucla.edu/~akrieger/teaching/18s/pic20a/examples/complex/](https://www.math.ucla.edu/~akrieger/teaching/18s/pic20a/examples/complex/)]
5.  [[https://www.makeareadme.com/#template-1](https://www.makeareadme.com/#template-1)]
6.  [youtube.com]
