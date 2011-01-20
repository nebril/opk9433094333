import java.util.Random;
import java.awt.*;
import java.awt.geom.Line2D;
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
	static int minPolygons;
	static int nMaxX = 800;
	static int nMaxY = 600;
	static Vector<Point> centerPoints = new Vector<Point>();
	Boolean re = false;
	Boolean generated = false;
	int [][] densities;
	private Vector<Integer> [][] polysInDens;
	int side;
	Button generate;
	Button load;
	Random randGen = new Random();
	
	public void init(){
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
	
	
	private void generate(){
		if(re == false){
			return;
		}

		setDensityAreas();
		Polygon test[] = getClosePolygons();
		theAnswer = getPolyOnPolygons(test);
		theAnswer = getConcavedPoly(theAnswer);
		generated = true;
		//TODO
		
	}
	private Polygon getConcavedPoly(Polygon poly){
		
		
		
		return poly;
	}
	private Polygon[] getClosePolygons(){
		int denseI = 0, denseJ = 0;
		int maxDens = Integer.MIN_VALUE;
		for(int i = 0 ; i < side ; ++i){
			for(int j = 0 ; j < side ; ++j){
				if(densities[i][j] > maxDens){
					denseI = i;
					denseJ = j;
					maxDens = densities[i][j];
				}
			}
		}
		
		Polygon[] result = new Polygon[minPolygons];
		Vector <Point> areas = new Vector <Point>();
		areas.add(new Point(denseI,denseJ));
		int p = densities[denseI][denseJ];
		if(densities[denseI][denseJ] > minPolygons){

		}else{
			int currentI = denseI, currentJ = denseJ;
			//System.out.println(currentI+"x"+currentJ);
			while(p < minPolygons){
				int itemp = currentI, jtemp = currentJ;
				//int maxDens2 = Integer.MIN_VALUE;
				while(areas.contains(new Point(itemp,jtemp))){
					int temp = currentI;
					do{
						int t = randGen.nextInt() % 3 - 1;
						temp = currentI + t;
						//System.out.println("t1:"+t);
					}while(temp < 0 || temp >= side);
					
					itemp = temp;
					temp = currentJ;
					
					do{
						int t = randGen.nextInt() % 3 - 1;
						temp = currentJ + t;
						//System.out.println("t2:"+t);
					}while(temp < 0 || temp >= side);
					
					jtemp = temp;
					//System.out.println(itemp+"x"+jtemp);
				}
				currentI = itemp;
				currentJ = jtemp;

				p += densities[currentI][currentJ];
				areas.add(new Point(currentI,currentJ));
			}
		}
		int c = 0;
		Polygon firstPolys[] = new Polygon[p];
		int indexes[] = new int[p];
		for(int i = 0 ; i < areas.size() ; ++i){
			for(int j = 0 ; j < polysInDens[areas.get(i).x][areas.get(i).y].size() ; ++j){
				firstPolys[c] = polygons[polysInDens[areas.get(i).x][areas.get(i).y].get(j)];
				indexes[c] = polysInDens[areas.get(i).x][areas.get(i).y].get(j);
				//System.out.println(polysInDens[areas.get(i).x][areas.get(i).y].get(j));
				c++;
			}
		}
		
		indexes = orderPolys(indexes);
		
		for(int i = 0 ; i < minPolygons ; ++i){
			result[i] = polygons[indexes[i]];
		}
		return result;
	}
	
	private int[] orderPolys(int indexes[]){
		int start = 0;
		float minY = Integer.MAX_VALUE;
		float minX = Integer.MAX_VALUE;
		for(int i = 0 ; i < indexes.length ; ++i){
			if(centerPoints.get(i).x < minX){
				if(centerPoints.get(i).y < minY){
					start = i;
					minY = centerPoints.get(i).y;
					minX = centerPoints.get(i).x;
				}
			}
		}
		
		int result[] = new int[indexes.length];

		result[0] = start;
		for(int i = 1 ; i < result.length ; ++i){
			int minLength = Integer.MAX_VALUE;
			for(int j = 0 ; j < indexes.length ; ++j){
				if(result[i-1] == indexes[j]){
					continue;
				}
				int length = getLengthSquared(new Line2D.Float(centerPoints.get(result[i-1]).x , centerPoints.get(result[i-1]).y, centerPoints.get(indexes[j]).x , centerPoints.get(indexes[j]).y));
				if(length < minLength){
					minLength = length;
					result[i] = indexes[j];
				}
			}
		}

		return indexes;
	}
	
	private void setDensityAreas(){
		side = 3;
		densities = new int[side][side];

		polysInDens = new Vector[side][side];
		for(int i = 0 ; i < side ; ++i){
			for(int j = 0 ; j < side ; ++j){
				densities[i][j] = 0;
				polysInDens[i][j] = new Vector<Integer>();
			}
		}
		
		for(int i = 0 ; i < side ; ++i){
			for(int j = 0 ; j < side ; ++j){
				Rectangle rec = new Rectangle(i*nMaxX/side, j*nMaxY/side, nMaxX/side, nMaxY/side);
				for(int k = 0 ; k < nPolygons ; ++k){
					if(rec.contains(centerPoints.get(k))){
						densities[i][j]++;
						polysInDens[i][j].add(k);
					}
				}
			}
		}
		
		for(int i = 0 ; i < side ; ++i){
			for(int j = 0 ; j < side ; ++j){
				System.out.println(i+"x"+j+" density: "+densities[i][j]);
			}
		}
	}
	
	private Polygon joinPolygons(Polygon p1, Polygon p2){
		Line2D.Float bound = new Line2D.Float(PolygonCenterOfMass(p1), PolygonCenterOfMass(p2));
		int p1Point1 = 0, p1Point2 = 0, p2Point1 = 0, p2Point2 = 0, temp;
		float maxDistSq = Integer.MIN_VALUE;
		Line2D.Float temp1;
		Line2D.Float temp2;
		
		for(int i = 0 ; i < p1.npoints ; i++){
			int iminus = getPointIndex(p1, i , -1);
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
		if(temp1.intersectsLine(temp2) ){
			temp = p1Point1;
			p1Point1 = p1Point2;
			p1Point2 = temp;
			temp1 = new Line2D.Float(p1.xpoints[p1Point1], p1.ypoints[p1Point1], p2.xpoints[p2Point1], p2.ypoints[p2Point1]);
			temp2 = new Line2D.Float(p1.xpoints[p1Point2], p1.ypoints[p1Point2], p2.xpoints[p2Point2], p2.ypoints[p2Point2]);
		}
		boolean t1,t2,t3,t4;
		t1=t2=t3=t4=false;
		while((t1 = lineIntersectsPoly(temp1, p1)) || (t2 = lineIntersectsPoly(temp2, p1)) || (t3 = lineIntersectsPoly(temp1, p2)) || (t4 = lineIntersectsPoly(temp2, p2))){
			if(t1){
				if(p1Point1 == getPointIndex(p1, p1Point2, -1)){
					p1Point1 = this.getPointIndex(p1, p1Point1, -1);
				}else{
					p1Point1 = this.getPointIndex(p1, p1Point1, 1);
				}
			}
			if(t2){
				if(p1Point2 == getPointIndex(p1, p1Point1, -1)){
					p1Point2 = this.getPointIndex(p1, p1Point2, -1);
				}else{
					p1Point2 = this.getPointIndex(p1, p1Point2, 1);
				}
			}
			if(t3){
				if(p2Point2 == getPointIndex(p2, p2Point1, -1)){
					p2Point2 = this.getPointIndex(p2, p2Point2, -1);
				}else{
					p2Point2 = this.getPointIndex(p2, p2Point2, 1);
				}
			}
			if(t4){
				if(p2Point2 == getPointIndex(p2, p2Point1, -1)){
					p2Point2 = this.getPointIndex(p2, p2Point2, -1);
				}else{
					p2Point2 = this.getPointIndex(p2, p2Point2, 1);
				}
			}
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
			result.addPoint(p1.xpoints[i], p1.ypoints[i]);
		}
		result.addPoint(p1.xpoints[p1Point1], p1.ypoints[p1Point1]);
		
		if(getPointIndex(p2, p2Point1 , -1) == p2Point2){
			step = 1;
		}else{
			step = -1;
		}
		for(int i = p2Point1 ; i != p2Point2 ; i = getPointIndex(p2, i , step)){
			result.addPoint(p2.xpoints[i], p2.ypoints[i]);
		}
		result.addPoint(p2.xpoints[p2Point2], p2.ypoints[p2Point2]);
		
		return result;
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
	        if(nPolygons % 2 == 1){
	        	minPolygons = nPolygons/2 + 1;
	        }else{
	        	minPolygons = nPolygons/2;
	        }
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
	
	private Polygon getPolyOnPolygons(Polygon polys[]){
		System.out.println("length: "+polys.length);
		int remaining = 0;
		int step;
		int i = 0;
		for(step = 1 ; step < polys.length ; step *= 2){
			for(i = 0 ; i + step < polys.length ; i += step + 1){
				//System.out.println("Joining "+i+" and "+(i+step));

				polys[i] = joinPolygons(polys[i], polys[i+step]);
			}
		}
		for(; i < polys.length ; ++i){
			polys[0] = joinPolygons(polys[0], polys[i]);
		}
		
		return polys[0];
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
			if(lineCommonPoint(line, polyLine)){
				continue;
			}
			if(line.intersectsLine(polyLine)){
				return true;
			}
		}
		return false;
	}

	private boolean lineCommonPoint(Line2D l1, Line2D l2){
		Point p1 = new Point((int)l1.getX1(), (int)l1.getY1());
		Point p2 = new Point((int)l1.getX2(), (int)l1.getY2());
		Point p3 = new Point((int)l2.getX1(), (int)l2.getY1());
		Point p4 = new Point((int)l2.getX2(), (int)l2.getY2());
		
		return (p1.equals(p3) || p1.equals(p4) || p2.equals(p3) || p2.equals(p4));
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
