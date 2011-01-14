import java.util.Random;
import java.awt.*;
import java.awt.geom.Line2D;
import java.applet.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;


public class Solver extends Applet implements ActionListener{
	static Polygon polygons[];
	static Polygon theAnswer;
	static int nPolygons;
	static int nMaxX = 800;
	static int nMaxY = 600;
	Boolean re = false;
	Boolean generated = false;
	
	Button generate;
	Button load;
	
	public void init()
	{
		resize(1000,800);
		setLayout(null);
		generate = new Button("Generuj");
		load = new Button("Otworz plik");
		
		generate.setBounds(900, 20, 100, 30);
		load.setBounds(900, 100, 100, 30);
		add(generate);
		add(load);
		
		generate.addActionListener(this);
		load.addActionListener(this);
		
		//nPolygons = rn.nextInt(25) + 5;
		//nPolygons = 7;//polygon count +1
	} // init()
	
	public void paint(Graphics g)
	{
		if(re){
			for(int i = 0 ; i < nPolygons ; i++){
				g.setColor(Color.black);
				g.drawPolygon(polygons[i].xpoints, polygons[i].ypoints, polygons[i].npoints);
			}
		}
		if(generated){
			g.setColor(Color.red);
			g.drawPolygon(theAnswer.xpoints, theAnswer.ypoints, theAnswer.npoints);
		}
	} // paint()
	
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == generate){
			generate();
			repaint();
		}else if(evt.getSource() == load){
			try {
				load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}
	
	public String loadFile(Frame f, String title, String defDir, String fileType) {
	  FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
	  fd.setFile(fileType);
	  fd.setDirectory(defDir);
	  fd.setLocation(50, 50);
	  fd.setVisible(true);
	  return fd.getFile();
	 }

	
	public void load() throws IOException{
		String fileName = loadFile(new Frame(), "Open...", ".\\", "*.txt");
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String line1;
	        String line2;
	        
	        line1 = br.readLine();
	        if(line1 == null){
	        	System.out.println("File empty!");
	        	return;
	        }
	        nPolygons = Integer.parseInt(line1);
	        polygons = new Polygon[nPolygons];
	        int whichPoly = 0;
	        while((line1 = br.readLine()) != null){
	        	line2 = br.readLine();
	        	if(line2 == null){
	        		System.out.println("Error reading file!");
	        		return;
	        	}
	        	int points = line1.replaceAll("[^ ]", "").length();
	        	System.out.println(points);
	        	String strXArray[] = line1.split(" ");
	        	String strYArray[] = line2.split(" ");
	        	polygons[whichPoly] = new Polygon();
	        	for(int i = 0 ; i < points ; ++i){
	        		polygons[whichPoly].addPoint(Integer.parseInt(strXArray[i]), Integer.parseInt(strYArray[i]));
	        	}
	        	++whichPoly;
	        }
	        generated = false;
	        re = true;
	}
	private void generate(){
		if(re == false){
			return;
		}
		theAnswer = getPolyOnPolygons(polygons);
		generated = true;
		//TODO
		
	}
	
	private Polygon getPolyOnPolygons(Polygon polys[]){
		Polygon result = new Polygon();
		int adj[];
		for(int i = 1 ; i < polys.length ; ++i){
			adj = getClosestNodesPositions(polygons[i-1], polygons[i]);
			//TODO
			if(i == 1){
				
			}else if(i == polys.length-1){
				
			}else{
				
			}

		}
		
		
		return result;
	}
	
	private int[] getClosestNodesPositions(Polygon p1, Polygon p2){
		Point point1, point2;
		double minDistance = Double.MAX_VALUE;
		int result[] = new int[2];
		for(int i = 0 ; i < p1.npoints ; ++i){
			point1 = new Point(p1.xpoints[i], p1.ypoints[i]);
			for(int j = 0 ; j < p2.npoints ; ++j){
				point2 = new Point(p2.xpoints[j], p2.ypoints[j]);
				if(point1.distance(point2) < minDistance){
					minDistance = point1.distance(point2);
					result[0] = i;
					result[1] = j;
				}
			}
		}
		return result;
	}
	
	private Point[] getNeighbourPoints(Polygon poly, Point point, int position ){
		Point result[] = new Point[2];
		//TODO
		return result;
	}
	
	private Point getAdjPoint(Polygon poly, int offset, int steps){
		Point result;
		int position = offset + (steps & poly.npoints);
		if(position >= 0 && position < poly.npoints){
		}else if(position < 0){
			position = poly.npoints + position;
		}else if(position >= poly.npoints){
			position = position % poly.npoints;
		}
		result = new Point(poly.xpoints[position], poly.ypoints[position]);
		
		return result;
	}
}
