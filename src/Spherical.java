//package coordinateSystems;

import java.lang.Math;
import Libraries.*;

public class Spherical{

	private double lat,lon,r;

	public Spherical(double r1, double lat1, double lon1)
	{
		r=r1;
		lat=lat1;
		lon=lon1;
	}

    // TODO constructor using Point3D
    public Spherical(Point3D point)
    {
        double r = Math.sqrt(point.x()*point.x() + point.y()*point.y()+ point.z()*point.z());
		double lat = Math.asin(point.z()/r);
		double lon = Math.atan2(point.y(), point.x());
    }

	public double getR(){return r;}
	public void setR(double r1){r=r1;}
	public double getLat(){return lat;}
	public void setLat(double lat1){lat=lat1;}
	public double getLon(){return lon;}
	public void setLon(double lon1){lon=lon1;}

	/*public Cartesian toCartesian()
	{
		double x = r * Math.sin(lat) * Math.cos(lon);
		double y = r * Math.sin(lat) * Math.sin(lon);
		double z = r * Math.cos(lat);
		return new Cartesian(x,y,z);
	} */

    public Point3D toPoint3D()
	{
		double x = r * Math.sin(lat) * Math.cos(lon);
		double y = r * Math.sin(lat) * Math.sin(lon);
		double z = r * Math.cos(lat);
		return new Point3D(x,y,z);
	}

}