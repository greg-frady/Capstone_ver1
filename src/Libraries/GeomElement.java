package Libraries;

import java.io.*;
/**
LSRT - cg022, 11/4/2002 <br><br>

This interface represents the common properties of Geometric Elements,
we assume the following: <br>
1. any geometric element implements this class.<br>
2. once a GeomElement was constructed it can NOT be changed -except for
   color and Tag, this has some exceptions: <br>
   - Polygon.add(Point3D) <br>
   - Triangulation3D.insertPoint(Point3D).<br>
   if possible try to avoid using these two methods.<br>
3. all GeomElements are naturly ordered by there center point.
*/

public interface GeomElement extends Serializable {
    /** any geometric element should have a tag used by algorithms, data-structers .. */
    public Tag getTag();

    /** represent the distance between this and the Point3D */
    public double distance(Point3D p); 

    /** represent all the info of the GeomElement needed for saving the it or drawing it */
    public String toFile(); 
 
    /** any geometric element should have a unick key  used by, data-structers (Hashtable).. */
    public String key(); 
    
	/** any geometric element has a center point needed mostly for sorting */
	public Point3D centerPoint();
	
	/** any geometric element has a color - store as an int that can be changed */
	public void setColor(int c);
	/** any geometric element has a color - store as an int that can be read */
    public int getColor();
}
