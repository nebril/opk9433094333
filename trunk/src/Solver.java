import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
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
	static PolygonSides ultimateAnswer;
	static PolygonSides between;
	static int nPolygons;
	static int minPolygons;
	static int choosedPolys;
	static int nMaxX = 800;
	static int nMaxY = 600;
	static Vector<Point> centerPoints = new Vector<Point>();
	static Vector<Point> allPoints;
	static long started;
	static long iterationStart;
	static long stopAfter = 10000;
	Boolean re = false;
	Boolean generated = false;
	Boolean be = false;
	int [][] densities;
	private Vector<Integer> [][] polysInDens;
	int side;
	Button generate;
	Button load;
	Checkbox doSteps;
	int [] test;
	PolygonSides [] pTest;
	Image backbuffer;
	Graphics backg;
	Label time;

	
	public void init(){
		resize(1000,700);
		setLayout(null);
		generate = new Button("Generuj");
		load = new Button("Otworz plik");
		doSteps = new Checkbox("Krokowo?");
		time = new Label("Czas wykonania");
		
		
		generate.setBounds(900, 20, 100, 30);
		load.setBounds(900, 100, 100, 30);
		doSteps.setBounds(900, 150, 100, 30);
		time.setBounds(900,200,100,30);
		add(generate);
		add(load);
		add(doSteps);
		add(time);
		
		generate.addActionListener(this);
		load.addActionListener(this);
		addMouseMotionListener(this); 
		
		
		
		//nPolygons = rn.nextInt(25) + 5;
		//nPolygons = 7;//polygon count +1
		
		new Thread(this, "Interface").start();
		
		backbuffer = createImage( 1000, 700 );
	      backg = backbuffer.getGraphics();
	      backg.setColor( Color.white );


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
		tiniestArea = Integer.MAX_VALUE;
		
		combGen = new CombinationGenerator(pTest.length, minPolygons);
		int [] indices;
		PolygonSides instance[];
		ultimateAnswer = new PolygonSides();
		between = new PolygonSides();
		while(combGen.hasMore()){
			System.out.println("Nowa kombinacja!");
			indices = combGen.getNext();
			instance = new PolygonSides[minPolygons];
			
			for(int i = 0 ; i < minPolygons ; ++i){
				instance[i] = pTest[indices[i]];
			}
			
			generated = false;
			wwotoczce = new Vector<PolygonSides>();
			for(int i = 0 ; i < instance.length ; ++i){
				wwotoczce.add(instance[i]);
				wwotoczce.get(i).setSides();
			}
			
			theAnswer = new PolygonSides();
			
			grah = new Graham(getPointsFromPolygons(instance));
			theAnswer = grah.GrahamScan();
			theAnswer.setSides();
			/*between.setFromSides(theAnswer.sides);
			be = true;
			try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			PolygonSides [] t = new PolygonSides [1];
			t[0] = theAnswer;
			theAnswer.setSides();
			
			ulepsz_otoczke(getPointsFromPolygons(t), stripPointsFromPolygon(getPointsFromPolygons(instance), theAnswer));
			if(this.validatePoly(theAnswer)){
				if(pole_wielokata(theAnswer) < tiniestArea){
					ultimateAnswer.setFromSides(theAnswer.sides);
					tiniestArea = pole_wielokata(theAnswer);
				}
				System.out.println("Zatwierdzam");
			}else{
				System.out.println("Niewali");
			}
			/*between.setFromSides(theAnswer.sides);
			be = true;
			try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			generated = true;
		}
		time.setText((System.currentTimeMillis() - this.started) + "ms");
	}
	
	public void update(Graphics g){
		//backg.fillRect(0, 0, 1000, 700);
		backg.clearRect(0, 0, nMaxX, nMaxY);
		if(re){
			for(int i = 0 ; i < nPolygons ; i++){
				backg.setColor(Color.black);
				backg.drawPolygon(polygons[i].xpoints, polygons[i].ypoints, polygons[i].npoints);
				
				//g.fillOval(centerPoints.get(i).x, centerPoints.get(i).y, 10, 10);
			}
			/*for(int i = 0 ; i < wwotoczce.size() ; ++i){
				g.drawOval(PolygonCenterOfMass(wwotoczce.get(i)).x, PolygonCenterOfMass(wwotoczce.get(i)).y, 10, 10);
			}*/
		}
		if(be){
			backg.setColor(Color.blue);
			backg.drawPolygon(between.xpoints, between.ypoints, between.npoints);
		}
		if(generated){
			backg.setColor(Color.red);
			backg.drawPolygon(ultimateAnswer.xpoints, ultimateAnswer.ypoints, ultimateAnswer.npoints);
			//g.drawLine((int)tempLine.x1, (int)tempLine.y1, (int)tempLine.x2, (int)tempLine.y2);
		}
		
		g.drawImage( backbuffer, 0, 0, this );
	}
	
	public void paint(Graphics g){
		update(g);
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
		this.started = System.currentTimeMillis();
		System.out.println("Starting");
		setDensityAreas();
		setClosePolygons();
		System.out.println("Starting thread");
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

	CombinationGenerator combGen;
	
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
		
		PolygonSides[] result = new PolygonSides[choosedPolys];
		Vector <Point> areas = new Vector <Point>();
		areas.add(new Point(denseI,denseJ));
		int p = densities[denseI][denseJ];
		if(densities[denseI][denseJ] > choosedPolys){

		}else{
			int currentI = denseI, currentJ = denseJ;
			//System.out.println(currentI+"x"+currentJ);
			while(p < choosedPolys){
				int itemp = currentI, jtemp = currentJ;
				int maxDens2 = Integer.MIN_VALUE;
				boolean changed = false;
					
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
					
					if(currentI == itemp && currentJ == jtemp){
						itemp = denseI;
						jtemp = denseJ;
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
		
		for(int i = 0 ; i < choosedPolys ; ++i){
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
	
	@SuppressWarnings("unchecked")
	private void setDensityAreas(){
		if(nPolygons == 4){
			side = 3;
		}else if(nPolygons <= 9){
			side = 4;
		}else if(nPolygons <= 15){
			side = 5;
		}else if(nPolygons <= 20){
			side = 6;
		}else{
			side = 7;
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
	}
	
	/*private PolygonSides joinPolygons(PolygonSides p1, PolygonSides p2){
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
	}*/
	
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
	
	public Point PolygonCenterOfMass(PolygonSides poly)
	{
		float cx=0,cy=0;
		float A=(float)UnsignedPolygonArea(poly);
		Point res = new Point(0,0);
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
	
	/*private float getLineLengthSq(Line2D.Float l){
		return (l.x1-l.x2)*(l.x1-l.x2) + (l.y1-l.y2)*(l.y1-l.y2);
	}*/
	
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
	        choosedPolys = nPolygons*3/5;
	        polygons = new PolygonSides[nPolygons];
	        wszystkie_krawedzie = new Vector<Line2D.Float>();
	        allPoints = new Vector<Point>();
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
	        		allPoints.add(new Point(Integer.parseInt(strXArray[i]), Integer.parseInt(strYArray[i])));
	        		if(i > 0 ){
	        			wszystkie_krawedzie.add(new Line2D.Float(Integer.parseInt(strXArray[i-1]), Integer.parseInt(strYArray[i-1]), Integer.parseInt(strXArray[i]), Integer.parseInt(strYArray[i])));
	        		}
	        	}
	        	centerPoints.add(PolygonCenterOfMass(polygons[whichPoly]));
	        	polygons[whichPoly].setSides();
	        	
	        	++whichPoly;
	        }
	        generated = false;
	        be = false;
	        re = true;
	}
	
	
	private boolean lineIntersectsPoly(Line2D line, PolygonSides poly){
		for(int i = 0 ; i < poly.sides.size() ; ++i){
			if(lineCommonPoint(line, poly.sides.get(i))){
				continue;
			}
			if(line.intersectsLine(poly.sides.get(i))){
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

	private float tiniestArea = Integer.MAX_VALUE;

	private Vector <Line2D.Float> wszystkie_krawedzie;
	private Vector<PolygonSides> wwotoczce;

	private Vector<Point> getPointsFromPolygons(PolygonSides p[]){
		Vector<Point> t = new Vector<Point>();
		
		for(int i = 0 ; i < p.length ; ++i){
			for(int j = 0 ; j < p[i].npoints ; ++j){
				t.add(new Point(p[i].xpoints[j], p[i].ypoints[j]));
			}
		}

		return t;
	}
	
	private Graham grah;
	private class Graham{
		  Point p[];
		  int n;

		  public Graham(Vector<Point> points){
			  p = new Point[points.size()];
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

		  private int Area (Point p0, Point p1, Point p2) {
			    int dx1 = p1.x - p0.x, dy1 = p1.y - p0.y;
			    int dx2 = p2.x - p0.x, dy2 = p2.y - p0.y;
			    return dx1 * dy2 - dx2 * dy1;
		  }

		  void SwapPoints (int i, int j) {
		    Point tmp;
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
	
	
	private PolygonSides getPolyByPoint(Point point){
		for(int i = 0 ; i < wwotoczce.size(); ++i){
			for(int j = 0 ; j < wwotoczce.get(i).npoints ; ++j){
				if(wwotoczce.get(i).xpoints[j] == point.x && wwotoczce.get(i).ypoints[j] == point.y){
					return wwotoczce.get(i);
				}
			}
		}
		
		return null;
	}
	
	private int getPointIndexOnPoly(Point point, PolygonSides ps){
		for(int i = 0 ; i < ps.npoints ; ++i){
			if((point.x == ps.xpoints[i]) && (point.y == ps.ypoints[i])){
				return i;
			}
		}
		
		return -1;
	}
	
	@SuppressWarnings("unused")
	private void printSides(PolygonSides ot){
		for(int i = 0 ; i < ot.sides.size()  ; ++i){
			printSide(ot.sides.get(i) , i);
	    }
	}
	
	private void printSide(Line2D.Float l, int i){
		System.out.println("Krawedz "+i+": x1="+l.x1+";y1="+l.y1+";x2="+l.x2+";y2="+l.y2);
	}
	
	private void printCenter(Line2D.Float l){
		Point t = getLineCenter(l);
		System.out.println("Srodek: x="+t.x+";y="+t.y);
	}
	
	private void ulepsz_otoczke(Vector<Point> w_otoczki,Vector<Point> punkty_rozw){
		iterationStart = System.currentTimeMillis();
		between = new PolygonSides();
		System.out.println("ulepszam otoczke");
	    PolygonSides stara_otoczka = new PolygonSides(theAnswer.xpoints, theAnswer.ypoints, theAnswer.npoints);
	    PolygonSides nowa_otoczka = new PolygonSides(theAnswer.xpoints, theAnswer.ypoints, theAnswer.npoints);
	    between.setFromSides(nowa_otoczka.sides);
	    be = true;
	    PolygonSides tempP1, tempP2;
	    boolean flaga = true, outFlag = true;
	    tiniestArea = this.pole_wielokata(theAnswer);
	    while(outFlag){
	    	System.out.println("Nowy przebieg!");
	    	outFlag = false;
	    	flaga = true;
		    while(flaga){
		    	System.out.println("Nowy przebieg--");
		    	flaga = false;
			    for(int i = 0 ; i < nowa_otoczka.sides.size(); ++i){
			    	tempP1 = getPolyByPoint(new Point ((int)nowa_otoczka.sides.get(i).x1, (int)nowa_otoczka.sides.get(i).y1));
			    	tempP1.setSides();
			    	tempP2 = getPolyByPoint(new Point ((int)nowa_otoczka.sides.get(i).x2, (int)nowa_otoczka.sides.get(i).y2));
			    	tempP2.setSides();
			    	if(tempP1 == null || tempP2 == null){
			    		System.out.println("AAAAAA!");
			    		return;
			    	}
			    	Point point1 = new Point ((int)nowa_otoczka.sides.get(i).x1,(int) nowa_otoczka.sides.get(i).y1);
			    	Point point2 = new Point ((int)nowa_otoczka.sides.get(i).x2, (int)nowa_otoczka.sides.get(i).y2);
			    	if(!tempP1.Equals(tempP2)){
			    		Line2D.Float tempLine = null;
			    		Line2D.Float tempLine2 = null;
			    		int counter = 0;
			    		for(int j=0 ; j < punkty_rozw.size() ; ++j){
			    			boolean intersects = false;
			    			//System.out.println("Szukam1");
			    			tempLine = new Line2D.Float(point1, punkty_rozw.get(j));
			    			tempLine2 = new Line2D.Float(punkty_rozw.get(j), point2);
			    			
			    			//System.out.println("Sprawdzam"+tempLine.x1+" "+tempLine.y1+" "+tempLine.x2+" "+tempLine.y2);
			    			if(lineIntersectsPoly(tempLine, nowa_otoczka) || lineIntersectsPoly(tempLine2, nowa_otoczka) || lineInsidePoly(tempLine, tempP1) || lineInsidePoly(tempLine2, tempP1) || lineInsidePoly(tempLine, tempP2) || lineInsidePoly(tempLine2, tempP2)){
			    				/*System.out.println("Zepsute1");
			    				printSide(tempLine, -1);
			    				printSide(tempLine2, -1);
			    				System.out.println("-----------");*/
			    				intersects = true;
			    			}
			    			if(!intersects){
				    			for(int k = 0 ; k < wwotoczce.size() ; ++k){
				    				//System.out.println("Szukam2");
				    				if(lineIntersectsPoly(tempLine, wwotoczce.get(k)) || lineIntersectsPoly(tempLine2, wwotoczce.get(k)) ){
				    					//System.out.println("ZEPSUTE2");
				    					intersects = true;
				    					break;
				    				}
				    			}
			    			}
			    			if(!intersects){
			    				between.setFromSides(nowa_otoczka.sides);
			    				if(doSteps.getState()){
					    			try {
										System.in.read();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
				    			}
				    			between.setFromSides(nowa_otoczka.sides);
			    				System.out.println("Dodaje zamiast");
		    					printSide(nowa_otoczka.sides.get(i+counter), i+counter);
		    					printSide(tempLine2, i+counter-1);
		    					printCenter(tempLine2);
		    					printSide(tempLine, i+counter);
		    					printCenter(tempLine);
		    					System.out.println("---------");
		    					stara_otoczka.setFromSides(nowa_otoczka.sides);
		    					nowa_otoczka.sides.remove(i+counter);
		    					nowa_otoczka.sides.insertElementAt(tempLine2, i+counter);
		    					nowa_otoczka.sides.insertElementAt(tempLine, i+counter);
	
		    					
		    					nowa_otoczka.setPoints();
		    					counter++;
		    					boolean outside = false;
		    					for(int k = 0 ; k < wwotoczce.size() ; ++k){
		    						for(int h = 0 ; h < wwotoczce.get(k).npoints ; ++h){
			    						if(!nowa_otoczka.contains(new Point(wwotoczce.get(k).xpoints[h], wwotoczce.get(k).ypoints[h]))){
			    							boolean miniFlag = false;
			    							for(int g = 0 ; g < nowa_otoczka.npoints ; ++g){
			    								if(wwotoczce.get(k).xpoints[h] == nowa_otoczka.xpoints[g] && wwotoczce.get(k).ypoints[h] == nowa_otoczka.ypoints[g]){
			    									miniFlag = true;
			    								}
			    							}
			    							if(!miniFlag){
				    							outside = true;
				    							
				    							System.out.println("COFAM");
				    							System.out.println(wwotoczce.get(k).xpoints[h]+"x"+ wwotoczce.get(k).ypoints[h]);
				    							//return;
				    							break;
			    							}
			    						}
			    						if(outside){
			    							break;
			    						}
		    						}
		    						if(outside){
		    							break;
		    						}
		    					}
		    					if(outside){
		    						nowa_otoczka.setFromSides(stara_otoczka.sides);
		    						punkty_rozw.remove(j);
		    					}else{
		    						stara_otoczka.setFromSides(nowa_otoczka.sides);
		    						punkty_rozw.remove(j);
		    						j--;
					    			flaga = true;
					    			outFlag = true;
					    			break;
		    					}
			    			}else{
			    				//System.out.println("nie dodaje");
			    			}
			    		}
			    		
			    	}
			    }
		    }
		    
		    flaga = true;
		    while(flaga){
		    	flaga = false;
			    for(int i = 0 ; i < nowa_otoczka.sides.size(); ++i){
			    	tempP1 = getPolyByPoint(new Point ((int)nowa_otoczka.sides.get(i).x1, (int)nowa_otoczka.sides.get(i).y1));
			    	tempP1.setSides();
			    	tempP2 = getPolyByPoint(new Point ((int)nowa_otoczka.sides.get(i).x2, (int)nowa_otoczka.sides.get(i).y2));
			    	tempP2.setSides();
			    	if(tempP1 == null || tempP2 == null){
			    		System.out.println("AAAAAA!");
			    		return;
			    	}
			    	int pIndex1, pIndex2;
			    	Point point1 = new Point ((int)nowa_otoczka.sides.get(i).x1,(int) nowa_otoczka.sides.get(i).y1);
			    	Point point2 = new Point ((int)nowa_otoczka.sides.get(i).x2, (int)nowa_otoczka.sides.get(i).y2);
			    	if(tempP1.Equals(tempP2)){
			    		//TODO: side on the same poly
			    		pIndex1 = getPointIndexOnPoly(point1, tempP1);
			    		pIndex2 = getPointIndexOnPoly(point2, tempP1);
			    		if(pIndex1 == getPointIndex(tempP1, pIndex2, -1) || pIndex1 == getPointIndex(tempP1, pIndex2, 1)){
			    			
			    			//Vertex cannot be split
			    		}else{
			    			nowa_otoczka.sides.remove(i);
			    			flaga = true;
			    			outFlag = true;
			    			int counter = 0;
			    			int step = -1;
			    			/*if(getPointIndex(tempP1, pIndex1 , 1) == pIndex2){
		                        step = -1;
			    			}else{
		                        step = -1;
			    			}*/
		
			    			for(int j = pIndex1 ; j != pIndex2 ; j = getPointIndex(tempP1, j, step)){
			    				nowa_otoczka.sides.insertElementAt(new Line2D.Float(tempP1.xpoints[j], tempP1.ypoints[j], tempP1.xpoints[getPointIndex(tempP1,j,step)],tempP1.ypoints[getPointIndex(tempP1,j,step)] ), i + counter);
			    				nowa_otoczka.setPoints();
			    				System.out.println("Dodaje odcinek na wielokacie j:"+j);
			    				if(doSteps.getState()){
				    				try {
										System.in.read();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
			    				}
			    				between.setFromSides(nowa_otoczka.sides);
			    				//w_otoczki.insertElementAt(new Point(tempP1.xpoints[j], tempP1.ypoints[j]), i + counter);
			    				punkty_rozw.remove(new Point(tempP1.xpoints[j], tempP1.ypoints[j]));
			    				outFlag = true;
			    				++counter;
			    			}
			    		}
			    	}
			    }
		    }
	    }
	    System.out.println("Punkty rozw: "+punkty_rozw.size());
	    theAnswer.setFromSides(nowa_otoczka.sides);
	    be = false;
	    	//printSides(theAnswer);
	    //generated = true;
	    System.out.println("koncze otoczke");
	}
	
	private boolean lineInsidePoly(Line2D.Float l, PolygonSides poly){
		int i1 = getPointIndexOnPoly(new Point((int)l.x1, (int)l.y1), poly);
		int i2 = getPointIndexOnPoly(new Point((int)l.x2, (int)l.y2), poly);
		//System.out.println("i1:"+i1+"; i2:"+i2);
		if(i1 == -1 || i2 == -1){
			//System.out.println("f");
			return false;
		}
		if(Math.abs(i1-i2) == 1 || Math.abs(i1-i2) == poly.npoints-1 ){
			//System.out.println("f");
			return false;
		}else{
			//System.out.println("t");
			return true;
		}
	}
	
	private boolean isPolygonVertex(PolygonSides p, Line2D.Float l){
		
		for(int i = 1 ; i < p.npoints ; ++i){
			if(haveSamePoints(new Line2D.Float(p.xpoints[i-1] , p.ypoints[i-1], p.xpoints[i] , p.ypoints[i]), l)){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean haveSamePoints(Line2D.Float line1, Line2D.Float line2){
		if((line1.x1 == line2.x1 && line1.y1 == line2.y1 && line1.x2 == line2.x2 && line1.y2 == line2.y2 ) || (line1.x1 == line2.x2 && line1.y1 == line2.y2 && line1.x2 == line2.x1 && line1.y2 == line2.y1 )){
			return true;
		}
		return false;
	}
	
	/*private boolean haveCommonPoint(Line2D.Float line1, Line2D.Float line2){
		if(line1.getP1().equals(line2.getP1()) || line1.getP2().equals(line2.getP2()) || line1.getP1().equals(line2.getP2()) || line1.getP2().equals(line2.getP1())){
			return true;
		}
		return false;
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
	*/
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

		public void setPoints(){
			this.setFromSides(this.sides);
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
	
	float pole_wielokata(Vector <Point> p){
	    int suma = 0;
	    for(int i = 0 ; i < p.size()-1 ; i++)
	    {
	        suma += (p.get(i).x * p.get(i+1).y - p.get(i+1).x * p.get(i).y);
	    }
	    suma += (p.get(p.size()-1).x * p.get(0).y - p.get(0).x * p.get(p.size()-1).y);

	    return (float) 0.5*suma;
	}
	
	float pole_wielokata(Polygon p){
	    int suma = 0;
	    for(int i = 0 ; i < p.npoints-1 ; i++) {
	        suma += (p.xpoints[i] * p.ypoints[i+1] - p.xpoints[i+1] * p.ypoints[i]);
	    }
	    suma += (p.xpoints[p.npoints-1] * p.ypoints[0] - p.xpoints[0] * p.ypoints[p.npoints-1]);

	    return (float) 0.5*suma;
	}
	
	private Vector<Point> stripPointsFromPolygon(Vector<Point> points, Polygon p){
		for(int j = 0 ; j < p.npoints ; ++j){
			points.remove(new Point(p.xpoints[j], p.ypoints[j]));
			//System.out.println("Removing");
		}
		
		return points;
	}
	
	private Point getLineCenter(Line2D.Float l){
		return new Point((int)(l.x1+l.x2)/2, (int)(l.y1 + l.y2)/2);
	}
	
	private boolean validatePoly(PolygonSides poly){
		for(int i = 0 ; i < poly.sides.size() ; ++i){
			for(int j = 0 ; j < poly.sides.size() ; ++j){
				if(i == j || i == j+1 || i == j-1 || lineCommonPoint(poly.sides.get(i), poly.sides.get(j))){
					continue;
				}else{
					if(poly.sides.get(i).intersectsLine(poly.sides.get(j))){
						System.out.println("Źle linia i:"+i+" j:"+j);
						printSide(poly.sides.get(i),i);
						printSide(poly.sides.get(j),j);
						return false;
					}
				}
			}
			for(int j = 0 ; j < polygons.length ; ++j){
				if(this.lineIntersectsPoly(poly.sides.get(i), polygons[j])){
					System.out.println("Źle poly");
					return false;
				}
			}
		}
		return true;
	}
}


