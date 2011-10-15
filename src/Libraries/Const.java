package Libraries;

import java.awt.*;
import java.io.*;
/** this class is a simple static info class wich contains some parameters and static functions */
public class Const implements Serializable{
    
    public static final double PI = Math.PI;
    public static String APPLICATION_TITLE = "LSRT: Gis Data Creator ";
    public static final double _epsilon = 0.001, _smallEpsilon= 0.000000001;
	public static final Point3D _epsilonPoint1 = new Point3D(_epsilon/13,_epsilon/19);
	public static final Point3D _epsilonPoint2 = new Point3D(_epsilon/7,_epsilon/11);
	
    /** Note if U are usen Linux this falg MUST be true!! */
    public static final boolean _linux = true;
	
    public final static int black=0,red=1,blue=2,green=3,pink=4,gray=5,yellow=6,Default  = -1,selected = pink;
	/** 
	the color table - converts int to Color.
	*/
    public static Color color(int i) {
	Color c = Color.black;
	if(i==green) c = Color.green;
	else if(i==red) c = Color.red;
	else if(i==blue) c = Color.blue;
	else if(i==pink) c = Color.pink;
	else if(i==gray) c = Color.gray;
	return c;
    }

    public static int s2i(String s) throws Exception {
        Integer i=new Integer(s);
        return i.intValue();
    }
    public static double s2d(String s) throws Exception {
        Double i=new Double(s);
        return i.doubleValue();
    }// const
}
