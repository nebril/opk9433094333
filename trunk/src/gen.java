import java.util.Random;
import java.awt.*;
import java.awt.geom.Line2D;
import java.applet.*;


/**
 * @author termit
 *
 */
public class gen extends Applet {

	static int nVertices;
	static int nPolygons;
	static int nMaxX = 800;
	static int nMaxY = 600;
	static int[] arXVals0;
	static int[] arYVals0;
	static int[] arXVals;
	static int[] arYVals;
	static Polygon polygons[];
	Random rn;

	/**
	 * @param args
	 */

	public void init()
	{
		setSize(nMaxX+2, nMaxY);
		rn = new Random();
		//nPolygons = rn.nextInt(25) + 5;
		nPolygons = 7;//polygon count +1
	} // init()

	
	private void drawPoints()
	{
		for( int i = 0; i < nVertices; i++ )
		{
			arXVals0[i] = rn.nextInt(nMaxX);
			arYVals0[i] = rn.nextInt(nMaxY);
		}

	} // drawPoints()


	private void sortByX(int left, int right)
	{
		int piv = arXVals0[rn.nextInt(right-left+1) + left];
		int i, j, tmp;
		i = left;
		j = right;
		do {
			while (arXVals0[i] < piv)
				i++;
			while (arXVals0[j] > piv)
				j--;
			if (i<=j) {
				
				tmp = arXVals0[i];
				arXVals0[i] = arXVals0[j];
				arXVals0[j] = tmp;
				tmp = arYVals0[i];
				arYVals0[i] = arYVals0[j];
				arYVals0[j] = tmp;
				i++; j--;
			}
		} while (i <= j);

		if (j > left) sortByX(left, j);
		if (i < right) sortByX(i, right);
	} // sortByX()

	private boolean pointIsAboveLine(int pointIndex, double a, double b)
	{
		return ( a * arXVals0[pointIndex] + b < arYVals0[pointIndex] );
	}
	private void makeOrder()
	{
		double a = (double) (arYVals0[nVertices-1]-arYVals0[0]) / (double) (arXVals0[nVertices-1]-arXVals0[0]) ;
		double b = (double) (arYVals0[0]-a*arXVals0[0]);
		int i, j=1;
		arXVals[0] = arXVals0[0];
		arYVals[0] = arYVals0[0];
		for( i = 1 ; i < nVertices - 1; i++ )
		{
			if( pointIsAboveLine(i, a, b) )
			{
				arXVals[j] = arXVals0[i];
				arYVals[j] = arYVals0[i];
				j++;
			}
		}
		arXVals[j] = arXVals0[nVertices-1];
		arYVals[j] = arYVals0[nVertices-1];
		j++;
		for( i = nVertices-2 ; i > 0; i-- )
		{
			if( !pointIsAboveLine(i, a, b) )
			{
				arXVals[j] = arXVals0[i];
				arYVals[j] = arYVals0[i];
				j++;
			}
		}
	}
	
	public void paint(Graphics g)
	{
		Random rn = new Random();
		polygons = new Polygon[nPolygons];
		for( int p = 0 ; p < nPolygons ; p++ )
		{	
			System.out.println("debug1");
			Boolean intersect = true;
			Polygon polytemp;
			nVertices = rn.nextInt(5) + 3;// max vertices count
			arXVals0 = new int[nVertices];
			arYVals0 = new int[nVertices];
			arXVals = new int[nVertices];
			arYVals = new int[nVertices];
			
			if(p == 0) {
				intersect = false;
				drawPoints();
				polygons[0] = new Polygon(arXVals, arYVals, nVertices);
			}
			while(intersect){
				System.out.println("debug2");
				Boolean inside = false;
				
				drawPoints();
				sortByX(0, nVertices-1);
				makeOrder();

				polytemp = new Polygon(arXVals, arYVals, nVertices);
				Boolean br = false;
				for(int i = 0 ; i<p ; i++){
					System.out.println("debug3");
					br = false;

					int[] xpoints = polygons[i].xpoints;
					int[] ypoints = polygons[i].ypoints;
					int npoints = polygons[i].npoints;
					
					Line2D.Float line1firstlast = new Line2D.Float(xpoints[0], ypoints[0], xpoints[npoints-1], ypoints[npoints-1]);
					Line2D.Float line2firstlast = new Line2D.Float(arXVals[0], arYVals[0], arXVals[nVertices-1], arYVals[nVertices-1]);

					if(line1firstlast.intersectsLine(line2firstlast)){
						br = true;
						System.out.println("odrzucony");
					}
					if(br) break;
					for(int k = 1 ; k < npoints ; k++){
						Line2D.Float line1 = new Line2D.Float(xpoints[k], ypoints[k], xpoints[k-1], ypoints[k-1]);
						for(int j = 1 ; j < nVertices ; j++){

							Line2D.Float line2 = new Line2D.Float(arXVals[j], arYVals[j], arXVals[j-1], arYVals[j-1]);

							if(line2.intersectsLine(line1)){
								br = true;
								System.out.println("odrzucony");
								break;
							}
						}
						if(line2firstlast.intersectsLine(line1)){
							br = true;
							System.out.println("odrzucony");
							break;
						}
					}
					if(br) break;
					for(int j = 1 ; j < nVertices ; j++){//checking intersections for firstlast line and existing polygon vertices
						Line2D.Float line2 = new Line2D.Float(arXVals[j], arYVals[j], arXVals[j-1], arYVals[j-1]);
						System.out.println("checking intersection");
						if(line2.intersectsLine(line1firstlast)){
							br = true;
							System.out.println("odrzucony");
							break;
						}
					}
					if(br) break;
					
				}
				if(!br){
					intersect = false;
					polygons[p] = new Polygon(arXVals, arYVals, nVertices);
					System.out.println("przyjety");
				}
			}
			System.out.println("hej");
			g.drawPolygon(arXVals, arYVals, nVertices);
		}
		System.out.println("Polygons:");
		for(int i = 0 ; i < nPolygons ; i++){
			System.out.println("Polygon "+i);
			for(int j = 0 ; j < polygons[i].npoints ; j++){
				System.out.println("point "+j+": ("+polygons[i].xpoints[j]+" , "+polygons[i].ypoints[j]+")");
				
			}
		}
	} // paint()
}