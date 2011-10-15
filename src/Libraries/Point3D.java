/** this calss represent a 3D point */ 
package Libraries;

import java.util.*;
import java.io.*;

public class Point3D implements GeomElement {
// ***** private data *****
    private double _x, _y, _z;
    private int _color; // virtual color
    public Tag _tag; // temp flag for algorithms.
    public final int _DefaultColor = 0;
    // ****** constructors ******
    public Point3D(ajPoint p) {_x=p.x;_y=p.y;_z=p.z;}
    public Point3D (double x1, double y1)   {_x = x1;   _y = y1; _z = 0; _color = _DefaultColor;}
    public Point3D (double x1, double y1, double z1)   {_x = x1;   _y = y1; _z = z1; _color = _DefaultColor;}
    public Point3D (double x1, double y1, double z1, int c)   {_x = x1;   _y = y1; _z = z1; _color = c;}
    
    /** copy constructor */
    public Point3D (Point3D p) { _x = p._x; _y = p._y; _z= p._z; _color = p._color;}
    /** Note: if color is false do not reads the color */
    public Point3D(String s) throws Exception { this(s,false);} // no color 
    public Point3D(String s, boolean color) throws Exception {
	StringTokenizer st = new StringTokenizer(s);
	double d1,d2,d3;
	_x = Const.s2d(st.nextToken());
	_y = Const.s2d(st.nextToken());
	_z = Const.s2d(st.nextToken());
	if(color) _color = Const.s2i(st.nextToken());
    }
    // ***** public methodes *****
    public double x() {return _x;}
    public double y() {return _y;}
    public double z() {return _z;}
    
    public int ix() {return (int)_x;}
    public int iy() {return (int)_y;}
    public int iz() {return (int)_z;}

    public void setColor(int c) {_color = c;}
    public int getColor() {return _color;}
    
    public Tag getTag() {return _tag;}
    /** logical equals
     * @param p othe Point.
     * @return true iff this is logicly equals to p) */
   public boolean equals (Point3D p)
    {return p._x == _x && p._y == _y && p._z==_z;}
    public boolean equalsXY (Point3D p)
    {return p._x == _x && p._y == _y;}
    public boolean smallerXY(Point3D p) {
	if(_x<p._x ||(_x==p._x && _y<p._y)) return true;
	else return false;
    }
    public boolean close2equalsXY(Point3D p)
    {return Math.abs(p._x- _x) < Const._epsilon && Math.abs(p._y -_y) <Const._epsilon;}
    /** key is [x y z] use for hashtables */
    public String key() {return _x+" "+_y+" "+_z;}
    public String keyXY() {return _x+" "+_y;}
    
   /** translate this by the Point p (as it was a vector from 0,0)
    * <br> NOTE! this method return a new Point3D.*/
    public Point3D translate (Point3D p)
    {
		if(p==null) return null;
		return new Point3D(_x+p._x, _y+p._y, _z+p._z);}
    /** @return the L2 3D distanse */
    public double distance (Point3D p) {return this.distance3D(p);}
    
    /** @return the L2 3D distanse */
    public double distance3D (Point3D p)
    {
	double temp = Math.pow (p._x - _x, 2) + Math.pow (p._y - _y, 2) + Math.pow (p._z - _z, 2);
	return Math.sqrt (temp);
    }
   /** @return the L2 2D distance (x,y) */
    public double distance2D (Point3D p)
    {
	double temp = Math.pow (p._x - _x, 2) + Math.pow (p._y - _y, 2);
	return Math.sqrt (temp);
    }
    public Point3D centerPoint() {return this;}
    /** @return a String contains the Point data*/
    public String toString() {return "[" + (int)_x + "," + (int)_y+","+(int)_z+"]";}
    public String toString(boolean all) {
	if(all) return "[" + _x + "," +_y+","+_z+","+_color+"]";
	else return "[" + (int)_x + "," + (int)_y+","+(int)_z+"]";
    } 
    public String toFile()  {return _x+" "+_y+" "+_z+" ";}
    public String toFile1()  {return "Point3D "+_x+" "+_y+" "+_z;}

    ////////////////////////////////////////////////////////////////////////////////////////

public final static int ONSEGMENT = 0,  LEFT = 1, RIGHT = 2, INFRONTOFA = 3, BEHINDB = 4, ERROR = 5;
    /** pointLineTest <br>
	test the following location of a point regards a line segment - all in 2D projection.<br><br>
   
	ONSEGMENT:  �����a----+----b������                              <br> <br>

	           +       +        +                              <br>
	LEFT:	 �����a---------b������                              <br> <br>


	RIGHT:	 �����a---------b������                              <br>
		   +      +        +                              <br> <br>

	INFRONTOFA:  ��+��a---------b������                              <br>
        BEHINDB:  �����a---------b����+�                              <br>
	ERROR: a==b || a==null || b == null;                               <br>
    */

    public int pointLineTest(Point3D a, Point3D b) {

	if(a== null || b==null || a.equalsXY(b)) return ERROR;

	double dx = b._x-a._x;
	double dy = b._y-a._y;
	double res = dy*(_x-a._x)-dx*(_y-a._y);

	if (res < 0) return LEFT;
	if (res > 0) return RIGHT;
	
	if (dx > 0) {
	    if (_x < a._x) return INFRONTOFA;
	    if (b._x < _x) return BEHINDB;
	    return ONSEGMENT;
	}
	if (dx < 0) {
	    if (_x > a._x) return INFRONTOFA;
	    if (b._x > _x) return BEHINDB;
	    return ONSEGMENT;
	}
	if (dy > 0) {
	    if (_y < a._y) return INFRONTOFA;
	    if (b._y < _y) return BEHINDB;
	    return ONSEGMENT;
	}
	if (dy < 0) {
	    if (_y > a._y) return INFRONTOFA;
	    if (b._y > _y) return BEHINDB;
	    return ONSEGMENT;
	}
	return ERROR;
    }
    public int pointLineTest(Line3D l) {
	if (l==null) return ERROR;
	return pointLineTest(l.refPoint1(), l.refPoint2());
    }
}  // class Point3D


