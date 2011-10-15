import java.lang.Math;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import Libraries.*;

//import com.sun.xml.internal.bind.v2.TODO;
//import coordinateSystems.*;
//import Point3D.*;
// References:
// http://stackoverflow.com/questions/716496/get-mode-value-in-java



public class PanoCamRig
{
	private int EpipolarSamples = 32;
	private int numPanoCams;
	private PanoCam[] panoCamArray; //array of PanoCam objects
	private PanoCam centralPano;
    private BufferedImage matte;
	private BufferedImage globalDepthMap;
	private ConvergenceMap[][] cMapArray;
	private int colorBucketPrecision = 3; //(2^n)^3 = 8^n divisions, example 8^3 = 512 buckets
    private int rigRadius = 6096; // = 24in
    private Point3D[] pointCloud;
    private double far;


	// 3-param constructor
	public PanoCamRig(BufferedImage central, BufferedImage[] circleImages, BufferedImage matte1, BufferedImage gDepthMap)
	{
		this(central, circleImages, matte1);
		globalDepthMap = gDepthMap;
	}

	// 2-param constructor
	public PanoCamRig(BufferedImage central, BufferedImage[] circleImages, BufferedImage matte1)
	{
		centralPano = new PanoCam(central, new Point3D(0,0,0));

        int l = circleImages.length;
        panoCamArray = new PanoCam[l];
        matte = matte1;
        int height = central.getHeight();
		int width = central.getWidth();

        //
        for(int i = 0; i < l; i++)
        {
            Point3D camLocation = (new Spherical(rigRadius, 0, (i/l)*2*Math.PI)).toPoint3D();
            panoCamArray[i] = new PanoCam(circleImages[i],camLocation);
        }

        double maxdepth=0;
        int pixelcount=0; //number of pixels in selection
        for(int y =0; y < height; y++)
        {
            for(int x=0; x < width; x++)
            {
                if(matte.getRGB(x,y)>0) //if inside matte
                {
                    pixelcount++; //accumulate number of pixels for pointCloud instantiation
                    if(globalDepthMap.getRGB(x,y) > maxdepth){maxdepth = globalDepthMap.getRGB(x,y);} //determine far
                }
            }
        }
        far = maxdepth;

        pointCloud = new Point3D[pixelcount];

        // make pointCloud from gDepthMap
		double aspectRatio = (double)width / height;
		double maxLat = Math.PI / aspectRatio; //latitude ranges from -maxLat to maxLat
        int pixelcounter=0;
        for(int y =0; y < matte.getHeight(); y++)
        {
            double lat = maxLat * ( 1 - (2*y / height));  //image space to latitude
            for(int x=0; x < matte.getWidth(); x++)
            {
                if(matte.getRGB(x,y)>0) //if inside the matte
                {
                    double lon = ((double)x / width) * Math.PI * 2; //image space to longitude
                    int r = globalDepthMap.getRGB(x,y); //depth from depthmap, aka: radius in spherical coordinates
                    pointCloud[pixelcounter++] = new Point3D(
                                                             r * Math.sin(lat) * Math.cos(lon),
                                                             r * Math.sin(lat) * Math.sin(lon),
                                                             r * Math.cos(lat));
                }
            }
        }



        //centralPano = central;
        //TODO possibly clear out cMapArray, so every element is null
    }

	public void setColorBucketPrecision(int cbp){colorBucketPrecision = cbp;}
	public int getColorBucketPrecision(){return colorBucketPrecision;}

	//get number of samples along "pixel ray"
	public int getEpipolarSamples()
	{
		return EpipolarSamples;
	}

	//set number of samples along "pixel ray"
	public void setEpipolarSamples(int numSamples)
	{
		EpipolarSamples = numSamples;
	}



	// derives the depthmap behind the matted foreground
	public BufferedImage generateZDepth(){
        //initially use nested for-loops
        //possibly better to implement quadtree, much faster

        int height = centralPano.getHeight();
        int width = centralPano.getWidth();
        BufferedImage outputZMap = new BufferedImage(width, height,BufferedImage.TYPE_USHORT_GRAY );
        double aspectRatio = (double)width / height;
        double maxLat = Math.PI / aspectRatio; //latitude ranges from -maxLat to maxLat

        for(int row=0; row<height; row++) //along theta-axis (elevation)
        {
            double lat = maxLat * ( 1 - (2*row / height));  //image space to latitude
            for(int col=0; col<width; col++) //along phi-axis (azimuth)
            {
                //if pixel is inside the selection mask
                if(matte.getRGB(col,row)>0)
                {
                    //create new convergence map (data structure)
                    ConvergenceMap cMap = new ConvergenceMap(numPanoCams, EpipolarSamples, globalDepthMap.getRGB(col,row), far);

                    //convert from image pixel space to lat-long spherical space
                    //int y = heightOver2 - (heightOver2 * (lat / maxLat));
                    double lon = ((double)col / width) * Math.PI * 2; //image space to longitude


                    //////double treeDepth = findTreeDepth(col,row);
                    //////double treeDepthRAD = Math.atan(treeDepth);

                    // sample along epipolar line, starting at treeDepth, until infinity
                    // number of samples (numEpiplolarSamples) determines accuracy
                    for(int rIter=0; rIter<EpipolarSamples; rIter++)
                    {
                        //
                        // find distance from center for cam
                        // this code is fucked:    r = treeDepth + Math.tan(Math.atan(treeDepth)+

                        //convert to cartesian coordinate space
                        double zdepth = epiSampleToZDepth(rIter, row, col);

                        Point3D sampleXYZ = (new Spherical(zdepth,lat,lon)).toPoint3D(); //(r,theta,phi)
                        //
                        for(int cam=0; cam<numPanoCams; cam++)
                        {
                            //if sampleXYZ can't be seen by cam
                            //figure out mask issues with orientation in 3D space
                            if(!(panoCamArray[cam].isVisible(
                                    new Point3D(sampleXYZ.x(),sampleXYZ.y(),sampleXYZ.z()),
                                    pointCloud)))
                            {
                                cMap.setColor(cam,rIter,0);
                            }
                            else
                            {
                                //find color from cam pixels
                                cMap.setColor(cam,rIter, panoCamArray[cam].colorAt(sampleXYZ));
                            }
                        }
                    }
                    //get the depth from the convergence map, save it as the pixel in depth map
                    double actualdepth = cMap.getDepth();
                    int depthcolor = (int)(65536*(actualdepth/ far));
                    outputZMap.setRGB(row, col, depthcolor);
                    cMapArray[row][col] = cMap; //store ConvergenceMap for later use (getting output pixel color)
                }
            }
        }
        return outputZMap;
	}


	//takes
	public BufferedImage ImageFromDepth(BufferedImage filteredZDepth)
	{
		int height = filteredZDepth.getHeight();
		int width = filteredZDepth.getWidth();
		BufferedImage cImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //color image

		for(int row=0; row<height; row++) //along theta-axis (elevation)
		{
			for(int col=0; col<width; col++) //along phi-axis (azimuth)
			{
				//if pixel is inside the selection mask
				if(matte.getRGB(col,row)>0) //if(filteredZDepth[row][col] != null) //use if wipe cMapArray in constructor
				{
					//set pixel

					int clr = getColorByBucket(cMapArray[row][col], zDepthToEpiSample(filteredZDepth.getRGB(col,row), row, col) , colorBucketPrecision);
					cImage.setRGB(col, row, clr); //depth??????
				}
			}
		}
		return cImage;
	}


	private int zDepthToEpiSample(double depth, int row, int col) //Figure out our depths and their conversions to depthmaps
	{
        //inverse from ConvergenceMap: actualdepth =  near + ((far - near)*((double)depth +.5) / samples);
        // return numEpiSamples(depth - near) / (far-near)
        double numerator = EpipolarSamples * (depth - cMapArray[row][col].near);
        double denominator = cMapArray[row][col].far - cMapArray[row][col].near;
        return (int)(numerator / denominator);
    }

    private double epiSampleToZDepth(int sample, int row, int col)
    {
        return cMapArray[row][col].near + ((((double)sample +.5) / EpipolarSamples)*(far - cMapArray[row][col].near));
    }



	// Hash into buckets, by bitDepth most significant bits in each channel
	// Identify bucket with largest population (mode of hashed keys)
	// return mean of most populated bucket
	private int getColorByBucket(ConvergenceMap cMap, int depth, int bitDepth)
	{
		//convert bitDepth to hex int
		int bitMask = ((255 >>> (8-bitDepth)) << (8-bitDepth)); // for bitDepth=2, bitMask = 0b11000000
		bitMask = bitMask | (bitMask <<8) | (bitMask <<16);//copy bitMask into other channels

		//determine appropriate length of bucketList array
        int count=0;
		for(int i=0; i<numPanoCams; i++)
		{
			if(cMap.getColor(i, depth)!=0){count++;}
		}

		int[] bucketList = new int[count];
		int j=0;
		for(int i=0; i<numPanoCams; i++)
		{
			//add bucket hash to bucketList
			if(cMap.getColor(i,depth)!=0){bucketList[j++] = (cMap.getColor(i,depth) & bitMask);}
		}

		int mode = getMode(bucketList);

		//get mean of most populated bucket
		int accum=0;
		int counter=0;
		for(int i=0; i<count; j++)
		{
			if((cMap.getColor(i,depth) & bitMask) == mode)
			{
				accum += cMap.getColor(i,depth);
				counter++;
			}
		}

		return accum/counter;
	}

	//private int intToBlue(int color){return (color & 0x0000FF);}
	//private int intToGreen(int color){return ((color & 0x00FF00)>>>8);}
	//private int intToRed(int color){return ((color & 0xFF0000)>>>16);}

	public static int getMode(int[] values) {
		HashMap<Integer,Integer> freqs = new HashMap<Integer,Integer>();

		for (int val : values) {
			Integer freq = freqs.get(val);
			freqs.put(val, (freq == null ? 1 : freq+1));
	  	}

		int mode = 0;
	    int maxFreq = 0;

	    for (Map.Entry<Integer,Integer> entry : freqs.entrySet()) {
	   		int freq = entry.getValue();
	    	if (freq > maxFreq) {
	      		maxFreq = freq;
	      		mode = entry.getKey();
	    	}
	  	}

		return mode;
	}


}