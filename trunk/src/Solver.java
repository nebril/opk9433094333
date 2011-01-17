import java.util.Random;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.applet.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;



public class Solver extends Applet implements ActionListener, MouseMotionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Polygon polygons[];
	static Polygon theAnswer;
	static int nPolygons;
	static int nMaxX = 800;
	static int nMaxY = 600;
	static Vector<Point> centerPoints = new Vector<Point>();
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
		addMouseMotionListener(this); 
		//nPolygons = rn.nextInt(25) + 5;
		//nPolygons = 7;//polygon count +1
	} // init()
	
	public void paint(Graphics g)
	{
		if(re){
			for(int i = 0 ; i < nPolygons ; i++){
				g.setColor(Color.black);
				g.drawPolygon(polygons[i].xpoints, polygons[i].ypoints, polygons[i].npoints);
				//g.fillOval(centerPoints.get(i).x, centerPoints.get(i).y, 10, 10);
			}
		}
		if(generated){
			g.setColor(Color.red);
			g.drawPolygon(theAnswer.xpoints, theAnswer.ypoints, theAnswer.npoints);
			//g.drawLine((int)tempLine.x1, (int)tempLine.y1, (int)tempLine.x2, (int)tempLine.y2);
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
	        centerPoints = new Vector<Point>();
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
	        	//System.out.println(points);
	        	String strXArray[] = line1.split(" ");
	        	String strYArray[] = line2.split(" ");
	        	polygons[whichPoly] = new Polygon();
	        	for(int i = 0 ; i < points ; ++i){
	        		polygons[whichPoly].addPoint(Integer.parseInt(strXArray[i]), Integer.parseInt(strYArray[i]));
	        	}
	        	centerPoints.add(PolygonCenterOfMass(polygons[whichPoly]));
	        	++whichPoly;
	        }
	        generated = false;
	        re = true;
	}
	private void generate(){
		if(re == false){
			return;
		}
		
		//theAnswer = joinPolygons(polygons[0], polygons[1]);
		/*theAnswer = joinPolygons(polygons[0], polygons[2]);
		theAnswer = joinPolygons(theAnswer, polygons[3]);
		theAnswer = joinPolygons(theAnswer, polygons[1]);*/
		
		generated = true;
		//TODO
		
	}
	
	private void getDensityAreas(){
		
	}
	

	private Polygon joinPolygons(Polygon p1, Polygon p2){
		Line2D.Float bound = new Line2D.Float(PolygonCenterOfMass(p1), PolygonCenterOfMass(p2));
		int p1Point1 = 0, p1Point2 = 0, p2Point1 = 0, p2Point2 = 0, temp;
		float maxDistSq = Integer.MIN_VALUE;
		Line2D.Float temp1;
		Line2D.Float temp2;
		
		for(int i = 0 ; i < p1.npoints ; i++){
			int iminus = getPointIndex(p1, i , -1);
			System.out.println(iminus);
			if(bound.intersectsLine(p1.xpoints[iminus], p1.ypoints[iminus], p1.xpoints[i], p1.ypoints[i])){
				temp1 = new Line2D.Float(bound.x1, bound.x2, p1.xpoints[iminus],p1.ypoints[iminus]);
				temp2 = new Line2D.Float(bound.x1, bound.x2, p1.xpoints[i],p1.ypoints[i]);
				if(getLineLengthSq(temp1)+getLineLengthSq(temp2) > maxDistSq){
					maxDistSq = getLineLengthSq(temp1)+getLineLengthSq(temp2);
					p1Point1 = iminus;
					p1Point2 = i;
				}
			}
		}
		maxDistSq = Integer.MIN_VALUE;

		for(int i = 0 ; i < p2.npoints ; i++){
			int iminus = getPointIndex(p2, i , -1);
			if(bound.intersectsLine(p2.xpoints[iminus], p2.ypoints[iminus], p2.xpoints[i], p2.ypoints[i])){
				System.out.println("Bound enter:");
				temp1 = new Line2D.Float(bound.x1, bound.x2, p2.xpoints[iminus],p2.ypoints[iminus]);
				temp2 = new Line2D.Float(bound.x1, bound.x2, p2.xpoints[i],p2.ypoints[i]);
				if(getLineLengthSq(temp1)+getLineLengthSq(temp2) > maxDistSq){
					maxDistSq = getLineLengthSq(temp1)+getLineLengthSq(temp2);
					p2Point1 = iminus;
					p2Point2 = i;
				}
			}
		}
		
		temp1 = new Line2D.Float(p1.xpoints[p1Point1], p1.ypoints[p1Point1], p2.xpoints[p2Point1], p2.ypoints[p2Point1]);
		temp2 = new Line2D.Float(p1.xpoints[p1Point2], p1.ypoints[p1Point2], p2.xpoints[p2Point2], p2.ypoints[p2Point2]);
		if(lineIntersectsPoly(temp1, p1) || lineIntersectsPoly(temp1, p2) || lineIntersectsPoly(temp2, p1) || lineIntersectsPoly(temp2, p2)){
			System.out.println("mixup1:");
			temp = p1Point1;
			p1Point1 = p1Point2;
			p1Point2 = temp;
			temp1 = new Line2D.Float(p1.xpoints[p1Point1], p1.ypoints[p1Point1], p2.xpoints[p2Point1], p2.ypoints[p2Point1]);
			temp2 = new Line2D.Float(p1.xpoints[p1Point2], p1.ypoints[p1Point2], p2.xpoints[p2Point2], p2.ypoints[p2Point2]);
		}
		if(temp1.intersectsLine(temp2) ){
			System.out.println("mixup2:");
			temp = p1Point1;
			p1Point1 = p1Point2;
			p1Point2 = temp;
			temp1 = new Line2D.Float(p1.xpoints[p1Point1], p1.ypoints[p1Point1], p2.xpoints[p2Point1], p2.ypoints[p2Point1]);
			temp2 = new Line2D.Float(p1.xpoints[p1Point2], p1.ypoints[p1Point2], p2.xpoints[p2Point2], p2.ypoints[p2Point2]);
		}

		Polygon result = new Polygon();

		int step;
		if(getPointIndex(p1, p1Point2 , 1) == p1Point1){
			step = -1;
		}else{
			step = 1;
		}
		for(int i = p1Point2 ; i != p1Point1 ; i = getPointIndex(p1, i , step)){
			System.out.println(i);
			result.addPoint(p1.xpoints[i], p1.ypoints[i]);
		}
		result.addPoint(p1.xpoints[p1Point1], p1.ypoints[p1Point1]);
		
		if(getPointIndex(p2, p2Point1 , -1) == p2Point2){
			step = 1;
		}else{
			step = -1;
		}
		System.out.println("Adding points from second");
		System.out.println(p2Point1);
		System.out.println(p2Point2);
		for(int i = p2Point1 ; i != p2Point2 ; i = getPointIndex(p2, i , step)){
			System.out.println(i);
			result.addPoint(p2.xpoints[i], p2.ypoints[i]);
		}
		System.out.println("Adding point from second");
		result.addPoint(p2.xpoints[p2Point2], p2.ypoints[p2Point2]);
		
		return result;
	}
	
	private Polygon[] orderPolys(Polygon polys[]){
		
		
		
		return polys;
	}
	
	public double UnsignedPolygonArea(Polygon poly)
	{
		int i,j;
		double area = 0;

		for (i=0;i<poly.npoints;i++) {
			j = (i + 1) % poly.npoints;
			area += poly.xpoints[i] * poly.ypoints[j];
			area -= poly.ypoints[i] * poly.xpoints[j];
		}
		area /= 2.0;

	   return(area);
	   //return(area < 0 ? -area : area);
	}
	
	public Point PolygonCenterOfMass(Polygon poly)
	{
		float cx=0,cy=0;
		float A=(float)UnsignedPolygonArea(poly);
		Point res = new Point();
		int i,j;

		float factor=0;
		for (i=0;i<poly.npoints;i++) {
			j = (i + 1) % poly.npoints;
			factor=(poly.xpoints[i]*poly.ypoints[j]-poly.xpoints[j]*poly.ypoints[i]);
			cx+=(poly.xpoints[i]+poly.xpoints[j])*factor;
			cy+=(poly.ypoints[i]+poly.ypoints[j])*factor;
		}
		A*=6.0f;
		factor=1/A;
		cx*=factor;
		cy*=factor;
		res.x=(int) cx;
		res.y=(int) cy;
		return res;
	}
	
	private float getLineLengthSq(Line2D.Float l){
		return (l.x1-l.x2)*(l.x1-l.x2) + (l.y1-l.y2)*(l.y1-l.y2);
	}
	
	private int getPointIndex(Polygon poly, int offset, int steps){
		//System.out.println("Getindex--------");
		int pos;
		//System.out.println("steps1:"+steps);
		steps = steps % poly.npoints;
		//System.out.println("steps2:"+steps);
		pos = offset + steps;
		//System.out.println("offset:"+offset);
		//System.out.println("pos:"+pos);
		if(pos < 0){
			//System.out.println("less than 0");
			pos = poly.npoints + pos;
		}else if(pos >= poly.npoints){
			//System.out.println("more than n");
			pos = pos % poly.npoints;
		}
		//System.out.println("Getindex="+pos);
		return pos;
	}
	
	private int getLengthSquared(Line2D.Float line){
		return (int) ((line.x1 - line.x2)*(line.x1 - line.x2) + (line.y1 - line.y2)*(line.y1 - line.y2));
	}
	
	private Polygon getPolyOnPolygons(Polygon polys[]){
		Polygon result = new Polygon();
		Vector<Point> pLeft = new Vector<Point>();
		Vector<Point> pRight = new Vector<Point>();
		int endIndexes[] = new int[2];
		int opposite;

		int adj[];
		for(int i = 1 ; i < polys.length ; ++i){
			adj = getClosestLinesPositions(polys[i-1], polys[i]);
			if(i == 1){
				opposite = getPointIndex(polys[i-1], adj[0], polys[i-1].npoints/2);
				for(int j = opposite ; j < polys[i-1].npoints ; ++j){
					pLeft.add(new Point(polys[i-1].xpoints[j],polys[i-1].ypoints[j]));
				}
				for(int j = getPointIndex(polys[i-1], opposite, -1) ; j >= 0 ; --j){
					pRight.add(new Point(polys[i-1].xpoints[j],polys[i-1].ypoints[j]));
				}
				pLeft.add((new Point(polys[i].xpoints[adj[1]],polys[i].ypoints[adj[1]])));
				pRight.add((new Point(polys[i].xpoints[adj[3]],polys[i].ypoints[adj[3]])));
			}else if(i == polys.length-1){
				for(int j = endIndexes[0] ; j != endIndexes[1] ; j = getPointIndex(polys[i], j, 1)){
					pLeft.add(new Point(polys[i-1].xpoints[j],polys[i-1].ypoints[j]));
				}
			}else{
				for(int j = endIndexes[0] ; j < polys[i-1].npoints ; ++j){
					pLeft.add(new Point(polys[i-1].xpoints[j],polys[i-1].ypoints[j]));
				}
				
				for(int j = endIndexes[1] ; j >= 0 ; --j){
					pRight.add(new Point(polys[i-1].xpoints[j],polys[i-1].ypoints[j]));
				}
				
				pLeft.add((new Point(polys[i].xpoints[adj[1]],polys[i].ypoints[adj[1]])));
				pRight.add((new Point(polys[i].xpoints[adj[3]],polys[i].ypoints[adj[3]])));
			}
			endIndexes[0] = adj[1];
			endIndexes[1] = adj[3];
		}
		for(int i = 0 ; i < pLeft.size() ; ++i){
			System.out.println("{"+pLeft.get(i).x+","+pLeft.get(i).y+"}");
			result.addPoint(pLeft.get(i).x, pLeft.get(i).y);
		}
		System.out.println();
		for(int i = pRight.size()-1 ; i >=0 ; --i){
			System.out.println("{"+pRight.get(i).x+","+pRight.get(i).y+"}");
			result.addPoint(pRight.get(i).x, pRight.get(i).y);
		}
		
		return result;
	}
	/*
	 * Returns four ints - positions of points in two polygons, between which the lines should be drawn.
	 * result[0] - point in p1 in line1
	 * result[1] - point in p2 in line1
	 * result[2] - point in p1 in line2
	 * result[3] - point in p2 in line2
	 */
	private int[] getClosestLinesPositions(Polygon p1, Polygon p2){
		int result[] = new int[4];
		int closePoints[] = getClosestNodesPositions(p1,p2);
		int minIndex1, minIndex2;
		Line2D.Float lines[] = new Line2D.Float[9];
		int indexes[][] = new int[9][2];
		//int lengthsSquared[] = new int[9];
		while(true){
			int minSquaredL1 = Integer.MAX_VALUE, 
			minSquaredL2 = Integer.MAX_VALUE;
			minIndex1 = 0;
			minIndex2 = 0;
			int k = 0;
			for(int i = -1 ; i < 2 ; ++i){
				for(int j = -1 ; j < 2 ; ++j){
					int index1 = getPointIndex(p1, closePoints[0], i);
					int index2 = getPointIndex(p2, closePoints[1], j);
	
					lines[k] = new Line2D.Float(p1.xpoints[index1], p1.ypoints[index1], p2.xpoints[index2], p2.ypoints[index2]);
					indexes[k][0] = index1;
					indexes[k][1] = index2;
					++k;
				}
			}
			for(int i = 0 ; i < 9 ; ++i){
				if(lineIntersectsPoly(lines[i], p1) || lineIntersectsPoly(lines[i], p2)){
					//lengthsSquared[i] = -1;
				}else{
					if(getLengthSquared(lines[i]) < minSquaredL1){
						minIndex1 = i;
					}else if(getLengthSquared(lines[i]) < minSquaredL2){
						minIndex2 = i;
					}
					//lengthsSquared[i] = getLengthSquared(lines[i]);
				}
			}
			if(minSquaredL1 == Integer.MAX_VALUE || minSquaredL2 == Integer.MAX_VALUE){
				System.out.println("Error: getClosestLinesPositions");
			}else{
				break;
			}
		}
		
		result[0] = indexes[minIndex1][0];
		result[1] = indexes[minIndex1][1];
		result[2] = indexes[minIndex2][0];
		result[3] = indexes[minIndex2][1];
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
	
	private boolean lineIntersectsPoly(Line2D line, Polygon poly){
		Line2D polyLine;
		for(int i = 1 ; i <poly.npoints ; ++i){
			polyLine = new Line2D.Float();
			polyLine.setLine(poly.xpoints[i-1], poly.ypoints[i-1] , poly.xpoints[i], poly.ypoints[i]);
			if(line.intersectsLine(polyLine)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		getAppletContext().showStatus(e.getX()+"x"+e.getY());
	}
}
