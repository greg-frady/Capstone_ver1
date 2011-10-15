//import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.lang.Math;

public class ConvergenceMap
{
	//it may be best to save this as a grayscale Image object
	private int cMapArray[][];
	private int cams;
	private int samples;
    protected double near; //in 0.1mm
    protected double far; //in 0.1mm


	public ConvergenceMap(int camera, int sample, double near1, double far1)
	{
		cams = camera;
		samples = sample;
		cMapArray = new int[cams][samples];
        near = near1;
        far = far1;
	}


	public int getColor(int camera, int sample)
	{
		return cMapArray[camera][sample];
	}


	public void setColor(int camera, int sample, int clr)
	{
		cMapArray[camera][sample]= clr;
	}

    //TODO Design how to consider number of voting cams in depth calculation, ie How does 5 agreeing cams beat 1 cam?
	public double getDepth()
	{
		double val = 9999999;
		int depth = 0;

		for (int s=0; s<samples; s++)
		{
			double temp;//=huge value;
			temp = AvgColorDistance(s);
			if ( temp < val)
			{
				val = temp;
				depth = s;
			}
		}
        // +.5 to depth to make center of sample offset
        return near + ((((double)depth +.5) / samples)*(far - near)); //return actual depth
	}


	//returns the average distance between all color
	private double AvgColorDistance(int s)
	{
		double dist=0;
		int count=0;
		for (int i=0; i<(cams-1); i++)
		{
			for (int j=i+1; j<cams; j++)
			{
				if (cMapArray[i][s]==0 && cMapArray[j][s]==0)  //!!!check on how to express bad cams
				{
					dist+= colorDistance(cMapArray[i][s], cMapArray[j][s]);
					count++;
				}
			}
		}
		return (dist / count);
	}

    private int colorDistance(int c1, int c2) {
			int r1 = getRed(c1);
			int g1 = getGreen(c1);
			int b1 = getBlue(c1);

			int r2 = getRed(c2);
			int g2 = getGreen(c2);
			int b2 = getBlue(c2);

			// Determine the distance between the two color points
			int d = (int) Math.sqrt(
				(r2 - r1) * (r2 - r1) +
				(g2 - g1) * (g2 - g1) +
				(b2 - b1) * (b2 - b1));
			return d;
	}
    //takes ARGB, returns 0-255
    private int getRed(int color){return (color&0x00FF0000)>>16;}
    private int getGreen(int color){return (color&0x0000FF00)>>8;}
    private int getBlue(int color){return (color&0x000000FF);}

    //TODO: write getBufferedImage()
    //convert ConvergenceMap to BufferedImage for display to the user.
    //used when user clicks on a pixel
	/*public BufferedImage getBufferedImage()
	{
        return new BufferedImage(...)
	}*/
}