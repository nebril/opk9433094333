import java.util.Random;
import java.util.Stack;
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



public class Solver extends Applet implements ActionListener, MouseMotionListener, Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static PolygonSides polygons[];
	static PolygonSides theAnswer;
	static int nPolygons;
	static int minPolygons;
	static int nMaxX = 800;
	static int nMaxY = 600;
	static Vector<Point> centerPoints = new Vector<Point>();
	static Vector<PointAlfa> allPoints;
	Boolean re = false;
	Boolean generated = false;
	int [][] densities;
	private Vector<Integer> [][] polysInDens;
	int side;
	Button generate;
	Button load;
	Random randGen = new Random();
	int [] test;
	PolygonSides [] pTest;
	
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
		
		new Thread(this, "Interface").start();

	} // init()
	
	public void run(){
		while(true){
			repaint();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void runCalc(){
		grah = new Graham(getPointsFromPolygons(pTest));
		theAnswer = grah.GrahamScan();
		
		PolygonSides [] t = new PolygonSides [1];
		t[0] = theAnswer;
		theAnswer.setSides();
		
		//theAnswer.sides.remove(0);
		//theAnswer.setFromSides(theAnswer.sides);
		ulepsz_otoczke(getPointsFromPolygons(t), getPointsFromPolygons(pTest));
		generated = true;
	}
	
	public void paint(Graphics g){
		g.clearRect(0, 0, nMaxX, nMaxY);
		if(re){
			for(int i = 0 ; i < nPolygons ; i++){
				g.setColor(Color.black);
				g.drawPolygon(polygons[i].xpoints, polygons[i].ypoints, polygons[i].npoints);
				
				//g.fillOval(centerPoints.get(i).x, centerPoints.get(i).y, 10, 10);
			}
			/*for(int i = 0 ; i < wwotoczce.size() ; ++i){
				g.drawOval(PolygonCenterOfMass(wwotoczce.get(i)).x, PolygonCenterOfMass(wwotoczce.get(i)).y, 10, 10);
			}*/
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
		}else if(evt.getSource() == load){
			try {
				load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void generate(){
		if(re == false){
			return;
		}
		setDensityAreas();
		setClosePolygons();
		wwotoczce = new Vector<PolygonSides>();
		for(int i = 0 ; i < pTest.length ; ++i){
			wwotoczce.add(pTest[i]);
			wwotoczce.get(i).setSides();
		}
		 new Thread(
            new Runnable() {
                public void run() {
                    runCalc();
                }
            }
		).start();

		//System.out.println("Area: "+UnsignedPolygonArea(theAnswer));
		//TODO
		
	}
	private PolygonSides getConcavedPoly(PolygonSides poly, int indexesIn[]){
		
		
		
		return poly;
	}
	private void setClosePolygons(){
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
		
		PolygonSides[] result = new PolygonSides[minPolygons];
		Vector <Point> areas = new Vector <Point>();
		areas.add(new Point(denseI,denseJ));
		int p = densities[denseI][denseJ];
		if(densities[denseI][denseJ] > minPolygons){

		}else{
			int currentI = denseI, currentJ = denseJ;
			//System.out.println(currentI+"x"+currentJ);
			while(p < minPolygons){
				int itemp = currentI, jtemp = currentJ;
				int maxDens2 = Integer.MIN_VALUE;
					
					if( currentJ != 0 && densities[currentI][currentJ-1] > maxDens2 && !areas.contains(new Point(currentI,currentJ-1))){
						maxDens2 = densities[currentI][currentJ-1];
						itemp = currentI;
						jtemp = currentJ - 1;
					}

					if(currentJ != side -1 && densities[currentI][currentJ+1] > maxDens2 && !areas.contains(new Point(currentI,currentJ+1))){
						maxDens2 = densities[currentI][currentJ+1];
						itemp = currentI;
						jtemp = currentJ + 1;
					}
					if(currentI != 0 && densities[currentI-1][currentJ] > maxDens2 && !areas.contains(new Point(currentI - 1,currentJ))){
						maxDens2 = densities[currentI-1][currentJ];
						itemp = currentI - 1;
						jtemp = currentJ;
					}
					if(currentI != side-1 && densities[currentI+1][currentJ] > maxDens2 && !areas.contains(new Point(currentI + 1,currentJ))){
						maxDens2 = densities[currentI+1][currentJ];
						itemp = currentI + 1;
						jtemp = currentJ;
					}
				currentI = itemp;
				currentJ = jtemp;

				p += densities[currentI][currentJ];
				areas.add(new Point(currentI,currentJ));
			}
		}
		int c = 0;
		pTest = new PolygonSides[p];
		test = new int[p];
		for(int i = 0 ; i < areas.size() ; ++i){
			for(int j = 0 ; j < polysInDens[areas.get(i).x][areas.get(i).y].size() ; ++j){
				pTest[c] = polygons[polysInDens[areas.get(i).x][areas.get(i).y].get(j)];
				test[c] = polysInDens[areas.get(i).x][areas.get(i).y].get(j);
				//System.out.println(polysInDens[areas.get(i).x][areas.get(i).y].get(j));
				c++;
			}
		}
		
		test = orderPolys(test);
		
		for(int i = 0 ; i < minPolygons ; ++i){
			pTest[i] = polygons[test[i]];
		}
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
		if(nPolygons == 4){
			side = 3;
		}else{
			side = 4;
		}
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
		
		/*for(int i = 0 ; i < side ; ++i){
			for(int j = 0 ; j < side ; ++j){
				System.out.println(i+"x"+j+" density: "+densities[i][j]);
			}
		}*/
	}
	
	private PolygonSides joinPolygons(PolygonSides p1, PolygonSides p2){
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
			System.out.println("Zwiecha");
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
		PolygonSides result = new PolygonSides();

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
	
	public double UnsignedPolygonArea(PolygonSides poly)
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
	   //return(area < 0 ? -area : area);//for unsigned
	}
	
	public PointAlfa PolygonCenterOfMass(PolygonSides poly)
	{
		float cx=0,cy=0;
		float A=(float)UnsignedPolygonArea(poly);
		PointAlfa res = new PointAlfa(0,0);
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
	
	private int getPointIndex(PolygonSides poly, int offset, int steps){
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
	        polygons = new PolygonSides[nPolygons];
	        wszystkie_krawedzie = new Vector<Line2D.Float>();
	        allPoints = new Vector<PointAlfa>();
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
	        	polygons[whichPoly] = new PolygonSides();
	        	for(int i = 0 ; i < points ; ++i){
	        		polygons[whichPoly].addPoint(Integer.parseInt(strXArray[i]), Integer.parseInt(strYArray[i]));
	        		allPoints.add(new PointAlfa(Integer.parseInt(strXArray[i]), Integer.parseInt(strYArray[i])));
	        		if(i > 0 ){
	        			wszystkie_krawedzie.add(new Line2D.Float(Integer.parseInt(strXArray[i-1]), Integer.parseInt(strYArray[i-1]), Integer.parseInt(strXArray[i]), Integer.parseInt(strYArray[i])));
	        		}
	        	}
	        	centerPoints.add(PolygonCenterOfMass(polygons[whichPoly]));
	        	
	        	++whichPoly;
	        }
	        generated = false;
	        re = true;
	}
	
	private PolygonSides getPolyOnPolygons(PolygonSides polys[]){
		System.out.println("length: "+polys.length);
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
	private int[] getClosestLinesPositions(PolygonSides p1, PolygonSides p2){
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
	
	private int[] getClosestNodesPositions(PolygonSides p1, PolygonSides p2){
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
	
	private boolean lineIntersectsPoly(Line2D line, PolygonSides poly){
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
	
	
	//new functions
	private boolean searchMore = false;
	private int countN;
	private int count2;
	private float tiniestArea = Integer.MAX_VALUE;
	private int startx;
	private int starty;
	private Vector <Line2D.Float> wszystkie_krawedzie;
	private Vector<PolygonSides> wwotoczce;
	private Vector<PointAlfa> punkty_rozwiazania = new Vector<PointAlfa>();
	/*
	
	private void szukaj_otoczki(int [] polys){
	
	    searchMore = true;
	    countN = 1;
	    Polygon best;
	    tiniestArea = Integer.MAX_VALUE;
	    int n = nPolygons;
	    int k = (int) Math.ceil((double)n/(double)2);
	    int comb[] = polys;
	    
	    sprawdz_otoczke(comb,k);
	    /*while(searchMore && (k<nPolygons))
        {
	    	count2 = k;
            for(int i = 0; i <= k ; i++)
            {
                comb[i] = i;
            }
            sprawdz_otoczke(comb,k);
            int combCount = 0;
            while((comb = next_comb(comb,k,n)) != null && combCount < (bin.binomial(n, k))){
                sprawdz_otoczke(comb,k);
                combCount++;
            }
            k++;
        }*/
	/*}
	
	private int[] next_comb(int comb[], int k, int n){
		//System.out.println(i);
		int i = k - 1;
		System.out.println("i:"+i);
		++comb[i];
		while ((i >= 0) && (comb[i] >= n - k + 1 + i)) {
			System.out.println("i:"+i);
			++comb[i];
			--i;
		}

		if (comb[0] > n - k)
			return null;

		for (i = i + 1; i < k; ++i)
			comb[i] = comb[i - 1] + 1;

		return comb;
	}
	
	private void sprawdz_otoczke(int comb[],int k){
		countN++;
	    //cout << licznik << "\r";
	    Vector<Polygon> checked = new Vector<Polygon>();
	    Vector<PointAlfa> pointsChecked = new Vector<PointAlfa>();


	    for(int i = 0 ; i < k ; i++){
	    	checked.add(polygons[comb[i]]);
	    }
	    for(int i = 0 ; i < checked.size() ; i++)
	    {
	        for(int j = 0 ; j < checked.get(i).npoints ; j++)
	        {
	        	pointsChecked.add(new PointAlfa(checked.get(i).xpoints[j],checked.get(i).ypoints[j]));
	        }
	    }

	    otoczka(pointsChecked,checked);

	}
	
	private void otoczka(Vector<PointAlfa> punkty_rozw,Vector<Polygon> spr_w){
		PointAlfa temp;
	    int startx,starty,ile = 0;
	    Stack<PointAlfa> stos = new Stack<PointAlfa>();
	    Vector <PointAlfa> punkty_otoczki = new Vector<PointAlfa>();
	    Vector <PointAlfa> tempp = punkty_rozw;
	    //=================PRZYGOTOWANIE PUNKTOW=================================

	    //znalezienie najnizej polozonego punktu
	    for(int i = 0 ; i < punkty_rozw.size() ; i++){
	        if(punkty_rozw.get(i).y > punkty_rozw.get(0).y){
	            temp = punkty_rozw.get(i);
	            punkty_rozw.set(0, punkty_rozw.get(i));
	            punkty_rozw.set(i, temp);
	        }
	    }

	    startx = punkty_rozw.get(0).x;           //przypisanie do zmiennych startx/starty wartosci najnizej polozonego punktu
	    starty = punkty_rozw.get(0).y;
	    temp = new PointAlfa(0,0);
	    PointAlfa t = new PointAlfa(0,0);
	    t.alfa = 0;
	    punkty_rozw.set(0, t);



	    //przeliczenie wspolrzednych i wsp alfa wzgledem punktu zerowego

	    for(int i = 1 ; i < punkty_rozw.size() ; i++)
	    {
	    	t = new PointAlfa(punkty_rozw.get(i).x - startx , starty - punkty_rozw.get(i).y);
	    	t.licz_alfa();
	    	punkty_rozw.set(i, t);
	    }

	    //posortowanie punktow wzgledem wsp alfa

	    for(int i = 0 ; i < punkty_rozw.size()-1 ; i++)
	    {
	        for(int j = 1 ; j < punkty_rozw.size()-1 ; j++)
	        {
	            if(punkty_rozw.get(j).alfa > punkty_rozw.get(j+1).alfa)
	            {
	                temp = punkty_rozw.get(j);
	                punkty_rozw.set(j,  punkty_rozw.get(j+1));
	                punkty_rozw.set(j+1, temp);
	            }
	        }
	    }



	    //===================SZUKANIE OTOCZKI=================================

	    stos.push(punkty_rozw.get(0));          //dodajemy 3 pierwsze punkty z listy na stos
	    stos.push(punkty_rozw.get(1));
	    stos.push(punkty_rozw.get(2));


	    for (int i=3;i<punkty_rozw.size();i++){                 //dopoki przejscie do nastepnego punkty z listy powoduje skret w prawo
			while(skret( (Stack<PointAlfa>) stos.clone(),punkty_rozw.get(i))==1)
			{
		        stos.pop();                                         //usuwamy wierzcholek ze szczytu stosu
		    }
		    stos.push(punkty_rozw.get(i));                          //jesli mamy skret w lewo, dodajemy punkt do stosu
	    }


	    while(!(stos.empty()))                                  //przerzucamy punkty ze stosu do wektora punkty_otoczki
	    {
	        punkty_otoczki.add((PointAlfa) stos.pop());
	    }

	                                                            //przywracamy wszystkim punktom pierwotne wartosci
	    punkty_otoczki.set(punkty_otoczki.size()-1, new PointAlfa(startx,starty));


	    for(int i = 0 ; i < punkty_otoczki.size()-1 ; i++){
	        punkty_otoczki.set(i, new PointAlfa(punkty_otoczki.get(i).x + startx, starty - punkty_otoczki.get(i).y));
	    }

	    System.out.println("Pole: "+pole_wielokata(punkty_otoczki));
	    System.out.println("Pole male: "+tiniestArea);
	    if(pole_wielokata(punkty_otoczki) < tiniestArea){
	    	System.out.println("Tajni");
	        for(int m = 0 ; m < wszystkie_krawedzie.size() ; m++){
	                for(int k = 0 ; k < punkty_otoczki.size()-1 ; k++){
	                    if(czy_przecinaja(punkty_otoczki.get(k),punkty_otoczki.get(k+1),
	                    		new PointAlfa(wszystkie_krawedzie.get(m).x1,wszystkie_krawedzie.get(m).y1),
	                    		new PointAlfa(wszystkie_krawedzie.get(m).x2,wszystkie_krawedzie.get(m).y2)))
	                    {
	                        ile++;
	                    }

	                    if(czy_przecinaja(punkty_otoczki.get(punkty_otoczki.size()-1),punkty_otoczki.get(0),
	                    		new PointAlfa(wszystkie_krawedzie.get(m).x1,wszystkie_krawedzie.get(m).y1),
	                    		new PointAlfa(wszystkie_krawedzie.get(m).x2,wszystkie_krawedzie.get(m).y2)))
	                    {
	                        ile++;
	                    }
	                }
	            }

	        if(ile > 0){
	        	System.out.println("Tu tez!");
	            ile = 0;
	        }else if(sprawdz_wielokaty(getPolygonOnPoints(punkty_otoczki),count2)){
	        	System.out.println("Tu weszlem!");

	        	//donothing
	        }else{
	        	System.out.println("Answeruje");
	        	generated = true;
	            theAnswer = new Polygon();
	            punkty_rozwiazania.clear();
	            wwotoczce.clear();
	            tiniestArea = (float) pole_wielokata(punkty_otoczki);
	            searchMore = false;
	            for(int i = 0 ; i < punkty_otoczki.size() ; i++){
	            	theAnswer.addPoint(punkty_otoczki.get(i).x, punkty_otoczki.get(i).y);
	            }
	            for(int i = 0 ; i < tempp.size() ; i++)
	            {
	                punkty_rozwiazania.add(tempp.get(i));
	            }
	            
	            for(int i = 0 ; i < spr_w.size() ; i++)
	            {
	                wwotoczce.add(spr_w.get(i));
	            }
	        }
	        
	        generated = true;
            theAnswer = new Polygon();
            punkty_rozwiazania.clear();
            wwotoczce.clear();
            tiniestArea = (float) pole_wielokata(punkty_otoczki);
            searchMore = false;
            for(int i = 0 ; i < punkty_otoczki.size() ; i++){
            	theAnswer.addPoint(punkty_otoczki.get(i).x, punkty_otoczki.get(i).y);
            }
            for(int i = 0 ; i < tempp.size() ; i++)
            {
                punkty_rozwiazania.add(tempp.get(i));
            }
            
            for(int i = 0 ; i < spr_w.size() ; i++)
            {
                wwotoczce.add(spr_w.get(i));
            }

	    }else{
	    	System.out.println("Nietajni");
	    }

	}
	
	private Polygon getPolygonOnPoints(Vector <PointAlfa> v){
		Polygon result = new Polygon();
		for(int i = 0 ; i < v.size() ; ++i){
			result.addPoint(v.get(i).x, v.get(i).y);
		}
		
		return result;
	}
	
	private class PointAlfa extends Point{
		
		public PointAlfa(int xx, int yy){
			x = xx;
			y = yy;
		}
		
		public PointAlfa(float x1, float y1) {
			x = (int)x1;
			y = (int)y1;
		}

		double alfa;
		int d;
		public void licz_alfa(){
			this.d = Math.abs(this.x) + Math.abs(this.y);
		    if(this.x < 0)
		    {
		        this.alfa = 2 - ((float)this.y/(float)this.d);
		    }
		    else
		    {
		        this.alfa = ((float)this.y/(float)this.d);
		    }
		}
	}
	
	private int skret(Stack<PointAlfa> S,PointAlfa p3){
		PointAlfa p2;
		PointAlfa p1;

		p2 = (PointAlfa) S.pop();
		p1= (PointAlfa) S.pop();

		if (wyznacznik(p1,p2,p3)>=0){
			return 0;
		}else{
			return 1;
		}
	}
	
	private double wyznacznik(PointAlfa pierwszy,PointAlfa drugi,PointAlfa trzeci){
	    double det1,det2,det;
	    det1 = (double)(pierwszy.x * drugi.y) + (double)(drugi.x * trzeci.y) + (double)(trzeci.x * pierwszy.y);
	    det2 = (double)(trzeci.x * drugi.y) + (double)(pierwszy.x * trzeci.y) + (double)(drugi.x * pierwszy.y);
	    det = (double)det1 - (double)det2;
	    return det;
	}
	
	float pole_wielokata(Vector <PointAlfa> p){
	    int suma = 0;
	    for(int i = 0 ; i < p.size()-1 ; i++)
	    {
	        suma += (p.get(i).x * p.get(i+1).y - p.get(i+1).x * p.get(i).y);
	    }
	    suma += (p.get(p.size()-1).x * p.get(0).y - p.get(0).x * p.get(p.size()-1).y);

	    return (float) 0.5*suma;
	}
	
	private boolean czy_przecinaja(PointAlfa p1,PointAlfa p2,PointAlfa p3,PointAlfa p4){
	    double d1,d2,d3,d4;
	    d1 = wyznacznik(p3,p4,p1);
	    d2 = wyznacznik(p3,p4,p2);
	    d3 = wyznacznik(p1,p2,p3);
	    d4 = wyznacznik(p1,p2,p4);
	    if((((d1<0.0)&&(d2>0.0)) || ((d1>0.0)&&(d2<0.0))) && (((d3<0.0)&&(d4>0.0)) | ((d3>0.0)&&(d4<0.0)))) return true;
	    else return false;
	}
	
	private boolean sprawdz_wielokaty(Polygon ot,int k){
	    int p = 0;
	    for(int i = 0 ; i < nPolygons ; i++){
	        for(int j = 0 ; j < polygons[i].npoints ; j++){
	            if(sprawdz_punkt(ot,new PointAlfa(polygons[i].xpoints[j], polygons[i].ypoints[j]))){
	                p++;
	                break;
	            }
	        }
	    }
	    if(p > k) return true;
	    else
	    {
	        return false;
	    }

	}
	
	private boolean sprawdz_punkt(Polygon w,PointAlfa p){
		int i,j;
	    boolean c = false;
	    for(i = 0, j = w.npoints-1 ; i < w.npoints ; j = i++)
	    {
	        if( ((w.ypoints[i] > p.y) != (w.ypoints[j] > p.y)) && (p.x < ((w.xpoints[j] - w.xpoints[i]) * (p.y-w.ypoints[i]) / (w.ypoints[j] - w.ypoints[i]) + w.xpoints[i]))){
	            c = !c;
	        }
	    }
	    /*for(int i = 1 ; i < w.npoints ; ++i){
	    	Line2D.Float temp = 
	    }*//*
	    return c;

	}
	
	private Binomial bin = new Binomial();
	
	private  class Binomial {

		   // return integer nearest to x
		   long nint(double x) {
		      if (x < 0.0) return (long) Math.ceil(x - 0.5);
		      return (long) Math.floor(x + 0.5);
		   }

		   // return log n!
		   double logFactorial(int n) {
		      double ans = 0.0;
		      for (int i = 1; i <= n; i++)
		         ans += Math.log(i);
		      return ans;
		   }

		   // return the binomial coefficient n choose k.
		   long binomial(int n, int k) {
		      return nint(Math.exp(logFactorial(n) - logFactorial(k) - logFactorial(n-k)));
		   }
		}*/
	private class PointAlfa extends Point{
		
		public PointAlfa(int xx, int yy){
			x = xx;
			y = yy;
		}
		
		public PointAlfa(float x1, float y1) {
			x = (int)x1;
			y = (int)y1;
		}

		double alfa;
		int d;
		public void licz_alfa(){
			this.d = Math.abs(this.x) + Math.abs(this.y);
		    if(this.x < 0)
		    {
		        this.alfa = 2 - ((float)this.y/(float)this.d);
		    }
		    else
		    {
		        this.alfa = ((float)this.y/(float)this.d);
		    }
		}
	}
	//even newer functions
	
	
	private Vector<PointAlfa> getPointsFromPolygons(PolygonSides p[]){
		Vector<PointAlfa> t = new Vector<PointAlfa>();
		
		for(int i = 0 ; i < p.length ; ++i){
			for(int j = 0 ; j < p[i].npoints ; ++j){
				t.add(new PointAlfa(p[i].xpoints[j], p[i].ypoints[j]));
			}
		}

		return t;
	}
	
	private Graham grah;
	private class Graham{
		  PointAlfa p[];
		  int n;

		  public Graham(Vector<PointAlfa> points){
			  p = new PointAlfa[points.size()];
			  n = points.size();
			  for(int i = 0 ; i < p.length ; ++i){
				  p[i] = points.get(i);
			  }
		  }
		  public PolygonSides GrahamScan () {
		    SelectMin();
		    SortPoints();

		    int stk[] = new int[n];
		    int top = 2;
		    stk[0] = 0; stk[1] = 1; stk[2] = 2;
		    for (int i = 3; i < n; i++) {
		      while (Area(p[stk[top-1]], p[stk[top]], p[i]) < 0)
			top--;
		      stk[++top] = i;
		    }

		    PolygonSides ch = new PolygonSides();
		    for (int i = 0; i <= top; i++)
		      ch.addPoint(p[stk[i]].x, p[stk[i]].y);
		    ch.addPoint(p[stk[0]].x, p[stk[0]].y);
		    return ch;
		  }

		  private int Area (PointAlfa p0, PointAlfa p1, PointAlfa p2) {
			    int dx1 = p1.x - p0.x, dy1 = p1.y - p0.y;
			    int dx2 = p2.x - p0.x, dy2 = p2.y - p0.y;
			    return dx1 * dy2 - dx2 * dy1;
		  }

		  void SwapPoints (int i, int j) {
		    PointAlfa tmp;
		    tmp = p[i]; p[i] = p[j]; p[j] = tmp;
		  }

		  void SelectMin () {
		    int ym = p[0].y;
		    int m = 0;
		    for (int i = 1; i < n; i++) {
		      if (ym > p[i].y || (ym == p[i].y && p[m].x > p[i].x)) {
			ym = p[i].y; m = i;
		      }
		    }
		    SwapPoints(0, m);
		  }

		  void SortPoints () {
		    for (int i = 1; i < n - 1; i++)
		      for (int j = i + 1; j < n; j++)
			if (Area(p[0], p[i], p[j]) < 0)
			  SwapPoints(i, j);
		  }
	}	
	
	
	private PolygonSides getPolyByPoint(PointAlfa point){
		for(int i = 0 ; i < wwotoczce.size(); ++i){
			for(int j = 0 ; j < wwotoczce.get(i).npoints ; ++j){
				if(wwotoczce.get(i).xpoints[j] == point.x && wwotoczce.get(i).ypoints[j] == point.y){
					return wwotoczce.get(i);
				}
			}
		}
		
		return null;
	}
	
	private int getPointIndexOnPoly(PointAlfa point, PolygonSides ps){
		for(int i = 0 ; i < ps.npoints ; ++i){
			if((point.x == ps.xpoints[i]) && (point.y == ps.ypoints[i])){
				return i;
			}
		}
		
		return -1;
	}
	
	private void printSides(PolygonSides ot){
		for(int i = 0 ; i < ot.sides.size()  ; ++i){
	    	System.out.println("Krawedz "+i+": x1="+ot.sides.get(i).x1+";x2="+ot.sides.get(i).x2+";y1="+ot.sides.get(i).y1+";y2="+ot.sides.get(i).y2);
	    }
	}
	
	private void ulepsz_otoczke(Vector<PointAlfa> w_otoczki,Vector<PointAlfa> punkty_rozw){
		System.out.println("ulepszam otoczke");
	    PolygonSides stara_otoczka = new PolygonSides(theAnswer.xpoints, theAnswer.ypoints, theAnswer.npoints);
	    PolygonSides nowa_otoczka = new PolygonSides(theAnswer.xpoints, theAnswer.ypoints, theAnswer.npoints);
	    PolygonSides tempP1, tempP2;
	    boolean flaga = true;
	    PointAlfa center = PolygonCenterOfMass(theAnswer);
	    
	    while(flaga){
	    	flaga = false;
		    for(int i = 0 ; i < stara_otoczka.sides.size(); ++i){
		    	tempP1 = getPolyByPoint(new PointAlfa (stara_otoczka.sides.get(i).x1, stara_otoczka.sides.get(i).y1));
		    	tempP1.setSides();
		    	tempP2 = getPolyByPoint(new PointAlfa (stara_otoczka.sides.get(i).x2, stara_otoczka.sides.get(i).y2));
		    	tempP2.setSides();
		    	if(tempP1 == null || tempP2 == null){
		    		System.out.println("AAAAAA!");
		    		return;
		    	}
		    	int pIndex1, pIndex2;
		    	
		    	if(tempP1.Equals(tempP2)){
		    		//TODO: side on the same poly
		    		pIndex1 = getPointIndexOnPoly(new PointAlfa (stara_otoczka.sides.get(i).x1, stara_otoczka.sides.get(i).y1), tempP1);
		    		pIndex2 = getPointIndexOnPoly(new PointAlfa (stara_otoczka.sides.get(i).x2, stara_otoczka.sides.get(i).y2), tempP1);
		    		if(pIndex1 == getPointIndex(tempP1, pIndex2, -1) || pIndex1 == getPointIndex(tempP1, pIndex2, 1)){
		    			
		    			//Vertex cannot be split
		    		}else{
		    			/*System.out.println("Npoints:"+stara_otoczka.npoints);
		    			System.out.println("i:"+i);
		    			System.out.println("x1:"+stara_otoczka.sides.get(i).x1);
		    			System.out.println("y1:"+stara_otoczka.sides.get(i).y1);
		    			System.out.println("x2:"+stara_otoczka.sides.get(i).x2);
		    			System.out.println("y2:"+stara_otoczka.sides.get(i).y2);
		    			System.out.println("pIndex1:"+pIndex1);
		    			System.out.println("pIndex2:"+pIndex2);*/
		    			stara_otoczka.sides.remove(i);
		    			flaga = true;
		    			int counter = 0;
		    			int step;
		    			if(pIndex1 > pIndex2){
		    				step = -1;
		    			}else{
		    				step = -1;
		    			}
		    			for(int j = pIndex1 ; j != getPointIndex(tempP1, pIndex2 ,step) ; j = getPointIndex(tempP1, j, step)){
		    				stara_otoczka.sides.insertElementAt(new Line2D.Float(tempP1.xpoints[j], tempP1.ypoints[j], tempP1.xpoints[getPointIndex(tempP1,j,step)],tempP1.ypoints[getPointIndex(tempP1,j,step)] ), i + counter);
		    				++counter;
		    			}
		    		}
		    	}else{
		    		
		    		//flaga = true;
		    		//TODO: side between polys
		    	}
		    }
	    }
	    
	    theAnswer.setFromSides(stara_otoczka.sides);
	    generated = true;

	    /*
	    for(it = 0 ; it < w_otoczki.size() ; it++)
	    {
	        for(it2 = 0 ; it2 < punkty_rozw.size() ; it2++)
	        {
	            if((w_otoczki.get(it).x == punkty_rozw.get(it2).x) && (w_otoczki.get(it).y == punkty_rozw.get(it2).y))
	            {
	            	punkty_rozw.remove(it2);
	                break;
	            }
	        }
	    }
	    
	    

	    while((punkty_rozw.size() > 0) && flaga){
		    oit = 0;//krawedzie nowej otoczki
		    ile_insertow = 0;
		    ile = nowa_otoczka.sides.size();
		    System.out.println("sides"+ile);
		    licznik = nowa_otoczka.sides.size()*punkty_rozw.size();
		    for(int i = 1 ; i <= ile  ; i++){
		        if(punkty_rozw.size() > 0)
		        {
		            for(it3 = 0 ; it3 < punkty_rozw.size() ; it3++)
		            {
		            	System.out.println("oit"+oit);
		            	System.out.println("i"+i);
		            	System.out.println("ile_insertow"+ile_insertow);
		                usuwany = it3;
		                temp = new PointAlfa(punkty_rozw.get(usuwany).x, punkty_rozw.get(usuwany).y);
		                temp1 = new PointAlfa((int)nowa_otoczka.sides.get(oit).x1,(int)nowa_otoczka.sides.get(oit).y1);
		                temp2 = new PointAlfa((int)nowa_otoczka.sides.get(oit).x2,(int)nowa_otoczka.sides.get(oit).y2);
	
		                nowa_otoczka.sides.remove(oit);
		                nowa_otoczka.sides.insertElementAt(new Line2D.Float((float)temp.x,(float)temp.y, (float)temp2.x, (float)temp2.y), oit);
		                nowa_otoczka.sides.insertElementAt(new Line2D.Float((float)temp1.x, (float)temp1.x,(float)temp.x, (float)temp.y), oit);
		                nowa_otoczka.setFromSides(nowa_otoczka.sides);
		                PolygonSides temp_otoczka = new PolygonSides();
		                temp_otoczka.setFromSides(nowa_otoczka.sides);
		                if(sprawdz(temp_otoczka,wwotoczce)){
		                	System.out.println("Super");
		                    ile_insertow++;
		                    punkty_rozw.remove(usuwany);
		                    stara_otoczka = new PolygonSides(nowa_otoczka.xpoints, nowa_otoczka.ypoints, nowa_otoczka.npoints);
		                    theAnswer = new PolygonSides(nowa_otoczka.xpoints, nowa_otoczka.ypoints, nowa_otoczka.npoints);
		                }else{
		                	System.out.println("Niesuper");
		                    licznik--;
		                    nowa_otoczka = new PolygonSides(stara_otoczka.xpoints, stara_otoczka.ypoints, stara_otoczka.npoints);
		                }
		                oit = i+ile_insertow;
		            }
		        }
	
		    }
		    if(licznik <= 0) flaga = false;
	    }

	    theAnswer.reset();
	    theAnswer.setFromSides(nowa_otoczka.sides);
	    generated = true;
	    */
		//TODO
		
		
	    System.out.println("koncze otoczke");
	}
	
	private boolean sprawdz(PolygonSides n_otoczka,Vector<PolygonSides> spr_w){
	    for(int i = 0 ; i < n_otoczka.sides.size() ; i++){
	        if(this.lineIntersectsPoly(n_otoczka.sides.get(i), n_otoczka)){
	        	return false;
	        }
	    }
	    int n = 0;
	    for(int i = 0 ; i < spr_w.size() ; i++){
	        for(int j = 0 ; j < spr_w.get(i).sides.size() ; j++){
	        	Point tp = new Point((int)Math.abs(spr_w.get(i).sides.get(j).x1 - spr_w.get(i).sides.get(j).x2), (int)Math.abs(spr_w.get(i).sides.get(j).y1 - spr_w.get(i).sides.get(j).y2));
	        	if(!(n_otoczka.contains(tp))){
                    n++;
                    if(n > spr_w.size() - (int)Math.ceil(nPolygons/2)){
                    	return false;
                    }
	        	}
	        }
	    }

	    for(int i = 0 ; i < n_otoczka.sides.size() ; i++){
	        for(int j = 0 ; j < spr_w.size() ; j++){
	        	if(this.lineIntersectsPoly(n_otoczka.sides.get(i), spr_w.get(j))){
	        		return false;
	        	}
	        }
	    }
	    return true;
	}
	
	private class PolygonSides extends Polygon{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public Vector<Line2D.Float> sides;
		
		
		public boolean Equals(PolygonSides p2){
			if(PolygonCenterOfMass(this).x == PolygonCenterOfMass(p2).x && PolygonCenterOfMass(this).y == PolygonCenterOfMass(p2).y){
				return true;
			}else{
				return false;
			}
		}
		
		public PolygonSides(int[] x, int[] y, int n) {
			xpoints = x;
			ypoints = y;
			npoints = n;
			this.setSides();
		}
		
		public PolygonSides(){
			
		}

		public void setSides(){
			sides = new Vector<Line2D.Float>();
			for(int i = 1 ; i < npoints ; ++i){
				sides.add(new Line2D.Float(xpoints[i-1], ypoints[i-1], xpoints[i], ypoints[i]));
			}
			sides.add(new Line2D.Float(xpoints[npoints-1], ypoints[npoints-1], xpoints[0], ypoints[0]));
		}
		
		public void setFromSides(Vector<Line2D.Float> s){
			sides = s;
			this.reset();
			for(int i = 0 ; i < s.size() ; ++i){
				this.addPoint((int)s.get(i).x1, (int) s.get(i).y1);
			}
		}
	}
}


