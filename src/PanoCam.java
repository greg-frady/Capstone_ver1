import java.awt.image.BufferedImage;
import Libraries.*;

// References:
// http://www.cs.bgu.ac.il/~cg022/code/Geom/api/Point3D.html
// http://www.cs.bgu.ac.il/~cg022/code/Geom/api/Line3D.html
// http://www.cs.bgu.ac.il/~cg022/code/Geom/api/GeomElement.html

public class PanoCam
{

	private BufferedImage panoImage;
    private double MinimumDistToMatteCloud = 10.0;
	private Point3D LocationXYZ;
	//private Orientation theoreticalOrientation;
	//private Orientation camOrientation;
	private boolean calibrated = false;
	protected double aspectRatio;

	public PanoCam(BufferedImage pano, Point3D LocXYZ)//, Orientation tOrientation)
	{
		panoImage = pano;
		LocationXYZ = LocXYZ;
		//theoreticalOrientation = tOrientation;
		//camOrientation = tOrientation;
		aspectRatio = panoImage.getWidth() / panoImage.getHeight();
    }

	public int getWidth()
	{ return panoImage.getWidth(); }

	public int getHeight()
	{ return panoImage.getHeight(); }

	/*
    public void setCalibratedOrientation(Orientation cOrientation)
	{
		camOrientation = cOrientation;
		calibrated = true;
	}
*/

	//stub:
	// Parameter: spherical coordinates for point, (origin at panoCam)
	// Returns: Spherical
	// this will help in aligning the cameras to their registered orientations
	private Spherical panoRotate(Spherical spoint)
	{
		if(false)//!calibrated)
		{
			return new Spherical(spoint.getR(), spoint.getLat(), spoint.getLon());// + camOrientation.getLon()); //!!! check out coordinate type
		}
		else return spoint;
	}


	/* colorAt,
	Parameters:
	Returns: int, sRGB color
	*/
	public int colorAt(Point3D pointXYZ)
	{
		//move origin to camera position
		Point3D newPoint = pointXYZ.translate(new Point3D(-LocationXYZ.x(),-LocationXYZ.y(),-LocationXYZ.z()));//pointXYZ.subtract(LocationXYZ);
		//rotate to align to cam's local orientation
		Spherical newSphereCoord = new Spherical(newPoint);
		double lat = newSphereCoord.getLat();
		double lon = newSphereCoord.getLon();
		//convert spherical to pixel coordinate based on Image info
		int x = (int)(lon / (Math.PI / 2)) * panoImage.getWidth();
		double maxLat = Math.PI / aspectRatio;
		int heightOver2 = panoImage.getHeight() /2;
		//int y = heightOver2 - (heightOver2 * (lat / maxLat));
		int y = (int)(heightOver2 * (1 - (lat / maxLat)));

		//return color at pixel
		return panoImage.getRGB(x,y);
	}


    /*Parameters:
        -Cartesian point,,
    */
    public boolean isVisible(Point3D point, Point3D[] pCloud)
    {
        boolean visible = false;
        //generate line
        Point3D p1 = new Point3D(LocationXYZ.x(), LocationXYZ.y(), LocationXYZ.z());

        //for loop along line from cam through point to inf
        for(int i = 0; i < pCloud.length; i++)
        {
            Line3D line = new Line3D(p1, pCloud[i]);
            if(line.distance(point) < MinimumDistToMatteCloud)
            {
                return false;
            }
        }
        return true;
	}
}