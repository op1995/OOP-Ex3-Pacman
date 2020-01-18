package gameClient;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Server.game_service;
import dataStructure.DGraph;
import dataStructure.Edge;

public class KML implements Runnable {

	private game_service Game;
	private int Timer;
	private int Scenario_num;
	private DGraph gameGraph;
	public KML(DGraph graph,game_service game, int scenario_num) {
		this.gameGraph= graph;
		this.Game=game;
		this.Scenario_num=scenario_num;
		if(game.timeToEnd()>30000)
			this.Timer=70000;
		else
			this.Timer=30000;
	}
	public String createKMLfile() {
		String KML_Info=
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
						"<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\r\n"
						+ "<Document>\n" + 
						"<name>"+ this.Scenario_num + ".kml</name>\r\n";
		KML_Info+=addNodes();
		while(Game.isRunning()) {
			KML_Info+=Fruits();
			KML_Info+=Robots();
		}
		KML_Info+=	"<Style id=\"orange-5px\">" +
				"<LineStyle>" + 
				"<color>ff00aaff</color>"
				+ "<width>2</width>"+ 
				"</LineStyle>" +
				"</Style>" + 
				"<Placemark>\r\n" + 
				"<name>"+ this.Scenario_num +"</name>\r\n" + 
				"<styleUrl>#orange-5px</styleUrl>\r\n" + 
				"<LineString>\r\n" + 
				"<tessellate>1</tessellate>\r\n" + 
				"<coordinates>\n";
		for( int src : gameGraph.Edges.keySet() ) {
			for ( int dest : gameGraph.Edges.get(src).keySet() ) {		
				KML_Info+=gameGraph.Nodes.get(src).getLocation();
				KML_Info+="\r\n";
				KML_Info+=gameGraph.Nodes.get(dest).getLocation();
				KML_Info+="\r\n";
			}
		}
		KML_Info+="</coordinates>\r\n" +  
				"</LineString>\r\n" + 
				"</Placemark>\r\n" +  
				"</Document>\r\n" + 
				"</kml>\r\n";
		try {
			saveToFile(KML_Info);
		} catch (Exception e) {
			System.out.println(e);
		}
		return KML_Info;
	}
	public String Robots() {
		String RobotsInfo="";
		double timer=(Timer - Game.timeToEnd())/1000;
		String[] Colors= {"ff0000ff","ffff0000","ff800080","ff00ffff","ffff00ff",};
		int i=0;
		gameGraph.Robots = gameGraph.Robots;
		for(int robot: gameGraph.Robots.keySet()) {
			RobotsInfo+=	"<Placemark>\r\n"+
					"<TimeSpan>\r\n"+
					"<begin>"+LocalDateTime.now()+"</begin>\r\n" + 
					"<end>" + LocalDateTime.now() + "</end>\r\n" +
					"</TimeSpan>\r\n"+
					"<color>"+Colors[i]+"</color>"+
					"<Style id=\"mycustommarker\">\r\n" + 
					"<IconStyle>\r\n" + 
					"<Icon>\r\n" + 
					"<href>http://maps.google.com/mapfiles/ms/icons/hiker.png</href>" + 
					"</Icon>\r\n" + 
					"</IconStyle>\r\n" + 
					"</Style>\r\n"+
					"<Point>\r\n"+  
					"<coordinates>"+ gameGraph.Robots.get(robot).getPos() + "</coordinates>\r\n"+  
					"</Point>\r\n"+
					"</Placemark>\r\n";
			i++;
		}
		return RobotsInfo;
	}
	public String Fruits() {
		String FruitsInfo="";
		//double timer=(Timer - Game.timeToEnd())/1000;
		try {
			for(Fruit fruit: gameGraph.Fruits.keySet()) {
				FruitsInfo+=	"<Placemark>\r\n"+
						"<TimeSpan>\r\n"+
						"<begin>"+LocalDateTime.now()+"</begin>\r\n" + 
						"<end>" + LocalDateTime.now() + "</end>\r\n" +
						"</TimeSpan>\r\n"+
						"<Style id=\"mycustommarker\">\r\n" + 
						"<IconStyle>\r\n" + 
						"<Icon>\r\n" ; 
				if(fruit.getType()==-1) {
					FruitsInfo+=	"<href>http://maps.google.com/mapfiles/kml/shapes/mountains.png</href>";
				}
				else
					FruitsInfo+=	"<href>http://maps.google.com/mapfiles/kml/shapes/sailing.png</href>";
				FruitsInfo+=	"</Icon>\r\n" + 
						"</IconStyle>\r\n" + 
						"</Style>\r\n"+
						"<Point>\r\n"+  
						"<coordinates>"+ fruit.getPos() + "</coordinates>\r\n"+  
						"</Point>\r\n"+
						"</Placemark>\r\n";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return FruitsInfo;
	}
	public String addNodes() {
		String NodesInfo="";
		for(int node : gameGraph.Nodes.keySet()) {
			NodesInfo+=	"<Placemark>\r\n"+
					"<color>ff00aaff</color>"+
					"<Point>\r\n"+  
					"<coordinates>"+ gameGraph.Nodes.get(node).getLocation() + "</coordinates>\r\n"+  
					"</Point>\r\n"+
					"</Placemark>\r\n";
		}
		return NodesInfo;
	}
	public void saveToFile(String file_name) throws IOException {
		try {
			File f=new File(this.Scenario_num+".kml");
			PrintWriter printWriter=new PrintWriter(f);
			printWriter.write(file_name);
			printWriter.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	@Override
	public void run() {
		createKMLfile();
	}

}