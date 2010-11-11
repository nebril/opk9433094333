import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
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
	Random rn;

	/**
	 * @param args
	 */

	public void init()
	{
		setSize(nMaxX+2, nMaxY);
		rn = new Random();
		//nPolygons = rn.nextInt(25) + 5;
		nPolygons = 1;
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
		for( int p = 0 ; p < nPolygons ; p++ )
		{	
			nVertices = rn.nextInt(7) + 3;
			arXVals0 = new int[nVertices];
			arYVals0 = new int[nVertices];
			arXVals = new int[nVertices];
			arYVals = new int[nVertices];
			drawPoints();
			sortByX(0, nVertices-1);
			System.out.println("Po sortowaniu:");
			for( int i = 0; i < nVertices; i++ )
			{
				System.out.println("Punkt " + (i+1) + " (" + arXVals0[i] + ", " + arYVals0[i] + ")");
			}
			makeOrder();
			System.out.println("Po ustawieniu:");
			for( int i = 0; i < nVertices; i++ )
			{
				System.out.println("Punkt " + (i+1) + " (" + arXVals[i] + ", " + arYVals[i] + ")");
			}
			g.drawPolygon(arXVals, arYVals, nVertices);
		}
	} // paint()
}