package Libraries;

import java.awt.*;
import java.io.*;
import java.util.*;
/**
this class represent a Line segment - it can be thought as a 3D segment or a 3D Line, (3D ray as well), <br>
the 3D line function is usaly ca be thought as f(x) --> y,z  ,  if f is vertical - fx(y) --> z, <>br.
*/
public class Line3D implements GeomElement {
   // private data
    private Point3D _p1;
    private Point3D _p2;
    private int _color = Const.black;   //default value.
    public Tag _tag; // can be used by algorithms for "type marking" 
    
    public Line3D(Point3D p,Point3D q) {
	_p1 = new Point3D(p.x(),p.y(),p.z());
	_p2 = new Point3D(q.x(),q.y(),q.z());
    }
    public Line3D(Line3D ot) { this(ot._p1,ot._p2); }
    public Line3D(String s) throws Exception {this(s,false);}
    public Line3D(String s, boolean color) throws Exception {
	StringTokenizer st = new StringTokenizer(s);
	double d1,d2,d3;
	d1 = Const.s2d(st.nextToken());
	d2 = Const.s2d(st.nextToken());
	d3 = Const.s2d(st.nextToken());
	_p1 = new Point3D(d1,d2,d3);
	st.nextToken();    
	d1 = Const.s2d(st.nextToken());
	d2 = Const.s2d(st.nextToken());
	d3 = Const.s2d(st.nextToken());
	_p2 = new Point3D(d1,d2,d3);
	if(color) _color = Const.s2i(st.nextToken());
    }
	public boolean equals(Line3D l) 
	{
		boolean ans = false;
		if (l!=null) 
		{
			ans = (_p1.equals(l._p1) &&  _p2.equals(l._p2)) ||
					(_p1.equals(l._p2) &&  _p2.equals(l._p1));	
		}
		return ans;
	}
	public boolean equalsXY(Line3D l) 
	{
		boolean ans = false;
		if (l!=null) 
		{
			ans = (_p1.equalsXY(l._p1) &&  _p2.equalsXY(l._p2)) ||
					(_p1.equalsXY(l._p2) &&  _p2.equalsXY(l._p1));	
		}
		return ans;
	}	
	
    public Tag getTag() {return _tag;}
    /** @return a reference to the first Point */
    public Point3D refPoint1() {return _p1;}
    /** @return a reference to the second Point */
    public Point3D refPoint2() {return _p2;}
  
    public Point3D centerPoint() 
    { return new Point3D((_p1.x()+_p2.x())/2,(_p1.y()+_p2.y())/2,(_p1.z()+_p2.z())/2);}
  
    public int getColor() { return _color;}
    public void setColor(int c) { _color = c; }
    /** 
	translate this by the Point p (as it was a vector from 0,0)
	<br> NOTE! this method returns a new Line3D translated by p.*/
    public Line3D translate (Point3D p) {
	if(p!=null) { return new Line3D(_p1.translate(p), _p2.translate(p));}
	return null;
    }
    
    /** key is [_p1 _p2] if _p1<_p2 else [_p2 _p1]  use for hashtables */
    public String key() {
	String s1 = _p1.key();
	String s2 = _p2.key();
	if((_p1.x()<_p2.x()) || 
	   (_p1.x()==_p2.x() && _p1.y()<_p2.y()) ||
	   (_p1.x()==_p2.x() && _p1.y()==_p2.y() &&   _p1.z()<_p2.z()))
	    return s1+" "+s2;
	else  return s2+" "+s1;
    }
    public String keyXY() {
	String s1 = _p1.keyXY();
	String s2 = _p2.keyXY();
	if((_p1.x()<_p2.x()) || 
	   (_p1.x()==_p2.x() && _p1.y()<_p2.y()))
	    return s1+" "+s2;
	else  return s2+" "+s1;
    }
    public String toString() {
	return "Line3D: "+_p1+", "+_p2;} //  +"  y="+this.m()+"X + "+this.k(); }
    
    public String toFile() {return toFile(false);}
    public String toFile(boolean color) {
	String ans = _p1.toFile()+" "+_p2.toFile();
	if(color) ans+=" "+_color;
	return ans;
    }
    /** this method calculate the APPROXIMATE Segment containment */
    public boolean appSegmentContains(Point3D p) {
	boolean ans = false;
	double d1 = _p1.distance(_p2);
	double d2 = p.distance(_p1)+ p.distance(_p2);
	ans = (d2 - d1) < Const._epsilon;  
	return ans;
    }
    /** this method checks if the Point3D p falls on the this (Line3D) */
    public boolean line3DContains(Point3D p) {
	boolean ans = false;
	if(p!=null) {
	    if(p.equals(_p1) || p.equals(_p2)) return true;
	    if(!this.vertical()) {if(p.equals(this.function(p.x()))) return true;}
	    if(p.x() == _p1.x() && p.equals(this.functionY(p.y()))) return true;}
	return ans;
    }
    /** not in use in the GIS application */
    public Line3D inner(double dist) {
	double dy = _p1.y()-_p2.y();
	double dx = _p1.x()-_p2.x();
	double angle = Math.atan2(dy,dx);
	angle = angle - Math.PI/2;
	Point3D vec = new Point3D(dist*Math.cos(angle), dist*Math.sin(angle));
	Line3D ans = new Line3D(_p1,_p2);
	ans._p1.translate(vec);
	ans._p2.translate(vec);
	return ans;
    }

    /** this methode compare between this and Line3D in terms of same SEGMENT! */
    public boolean sameSegment3D(Line3D l) {
	boolean ans = false;
	if(l!=null) 
	    if(_p1.equals(l._p1) && _p2.equals(l._p2)) ans = true;
	    else if (_p2.equals(l._p1) && _p1.equals(l._p2)) ans = true;
	return ans;
    }
     /** this methode compare between this and Line3D in terms of same 2D (XY) SEGMENT! */
    public boolean sameSegment2D(Line3D l) {
	boolean ans = false;
	if(l!=null) 
	    if(_p1.equalsXY(l._p1) && _p2.equalsXY(l._p2)) ans = true;
	    else if (_p2.equalsXY(l._p1) && _p1.equalsXY(l._p2)) ans = true;
	return ans;
    }
    /** this methode compare between this and Line3D in terms of same 2D (XY) Line! */
    public boolean sameLine2D(Line3D l) {
	boolean ans = false;
	if(l!=null) 
	    if(!this.vertical() && this.m() == l.m() && this.k() == l.k()) ans = true;
	    else if (l.vertical() && this._p1.x() == l._p1.x()) ans = true;
	return ans;
    }
    /** this methode compare between this and Line3D in terms of same 3D Line! */
    public boolean sameLine3D(Line3D l) {
	boolean ans = false;
	if(l!=null) if(this.line3DContains(l._p1) && this.line3DContains(l._p2)) ans = true;
	return ans;
    }
  

    ///////////// 2D XY only Line segment methods ////////////////////
    /** checks is this (as a segment) crosses l. <BR>
	Note: checks 2d corssing!!, (projection on XY plane!!!)*/
    public boolean crosses(Line3D l) {
	return (this.intersection(l) != null);}

    /** compute the 3D (XYZ) intersection Point of the 2D (XY) projection. <BR>
	@return the 3D Point with THIS z coordinate iff the intersection point fall ON BOTH Line3D */   
    public Point3D intersection(Line3D l) {
	Point3D cross = cross(l);
	if(cross == null) return null; 
	double a1= _p1.distance2D(cross), a2 =_p2.distance2D(cross);
	double b1= l._p1.distance2D(cross), b2 =l._p2.distance2D(cross); 
	double d1 = _p1.distance2D(_p2), d2=l._p1.distance2D(l._p2);
	if(Math.max(a1,a2) > d1 || Math.max(b1,b2) > d2) return null; 
	else return cross;
    }

    /** it is too late at night for real Geometry: so this method calculates the approximated 3D distance to p 
     <br> Never been tested!!
     */
    public double appDist3D(Point3D p) {
        return appDist3D(p,_p1,_p2);}

    private static double appDist3D(Point3D p, Point3D p1, Point3D p2) {
	for(int i=0;i<10; i++) {
	    if(p1.distance(p2) < Const._epsilon) return Math.min(p.distance(p1), p.distance(p2));
            Point3D pm = new Point3D((p1.x()+p2.x())/2, (p1.y()+p2.y())/2, (p1.z()+p2.z())/2);
            if(p.distance(p1) < p.distance(p2)) p2 = pm; else  p1 = pm;
        }
	return Math.min(p.distance(p1), p.distance(p2));
    }
    private static double appDist3Dr(int i,Point3D p,Point3D p1, Point3D p2) {
        if(i>10 || p1.distance(p2) < Const._epsilon) return Math.min(p.distance(p1), p.distance(p2));
        else {
            Point3D pm = new Point3D((p1.x()+p2.x())/2, (p1.y()+p2.y())/2, (p1.z()+p2.z())/2);
            if(p.distance(p1) < p.distance(p2)) return appDist3Dr(i+1,p,p1,pm);
            else  return appDist3Dr(i+1,p,p2,pm);
        }
    }
 /** it is too late at night for real Geometry: so this method calculates the approximated 3D distance to p
     <br> Never been tested!!*/
   public double appDist3D(Line3D l) {
        return appDist3D(l,_p1,_p2);}
    private static double appDist3D(Line3D l,Point3D p1, Point3D p2) {
	for(int i=0;i<10; i++) {
	    if(p1.distance(p2) < Const._epsilon) return Math.min(l.appDist3D(p1), l.appDist3D(p2));
	    Point3D pm = new Point3D((p1.x()+p2.x())/2, (p1.y()+p2.y())/2, (p1.z()+p2.z())/2);
            if(l.appDist3D(p1) < l.appDist3D(p2)) p2=pm; else p1=pm;
        }
	 return Math.min(l.appDist3D(p1), l.appDist3D(p2));
    }  

    private static double appDist3Dr(int i,Line3D l,Point3D p1, Point3D p2) {
        if(i>10 || p1.distance(p2) < Const._epsilon) return Math.min(l.appDist3D(p1), l.appDist3D(p2));
        else {
            Point3D pm = new Point3D((p1.x()+p2.x())/2, (p1.y()+p2.y())/2, (p1.z()+p2.z())/2);
            if(l.appDist3D(p1) < l.appDist3D(p2)) return appDist3Dr(i+1,l,p1,pm);
            else  return appDist3Dr(i+1,l,p2,pm);
        }
    }  
   /** Note 2D distance only XY, for 3d distance (approximate uses app3Ddist(Point3D)) */
    public double distance(Point3D p) {return this.distance2D(p);}
    /** Note 2D distance only XY, for 3d distance (approximate uses app3Ddist(Point3D)) */
    public double distance2D(Point3D p) {
	double ans = -1;
	if(p!=null) {
	    if(this.vertical()) {
		if((p.y()-this._p1.y())*(p.y()-this._p2.y()) <0)
		    ans = Math.abs(p.x()-_p1.x());
		else ans = Math.min(p.distance2D(_p1), p.distance2D(_p2));
	    }
	    else if(this.horisontle()) {
		if((p.x()-this._p1.x())*(p.x()-this._p2.x()) <0)
		    ans = Math.abs(p.y()-_p1.y());
		else ans = Math.min(p.distance2D(_p1), p.distance2D(_p2));
	    }
	    else {
		double m = (-1.0)/this.m();
		Point3D p0 = new Point3D(p.x()+1,p.y()+m);
		Line3D l0= new Line3D(p,p0);
		ans = Math.min(p.distance2D(_p1),p.distance2D(_p2));
		if(this.crosses(l0)) {
		    Point3D p3 = this.cross(l0);
		    ans = Math.min(p.distance2D(p3),ans);
		}
	    }
	}
	return ans;
    }
    /** Not yet has been proven correct, assume 2D (XY projection)  !!!! */
    public double distance(Line3D l) {
	double ans = -1;
	if(l!=null) {
	    if(this.crosses(l)) ans=0;
	    else {
		double d1 = Math.min(l.distance(this._p1), l.distance(this._p2));
		double d2 = Math.min(this.distance(l._p1), this.distance(l._p2));
		ans = Math.min(d1,d2);
	    }
	}
	return ans;
    }
    
    /**  calculate the crossing point between two Line3D <br>
     Note: the z value of the return point is the calculate on THIS (not on l)*/
    public Point3D cross(Line3D l){
	Point3D ans = null;
	if(!vertical() && !l.vertical() && m()!=l.m()) {
	    double x = (k()-l.k())/(l.m()-m());
	    double y = m()*x+k();
	    double z = mz()*x+kz();
	    ans = new Point3D(x,y,z);
	}
	else if(!this.vertical() && l.vertical()) ans = this.function(l._p1.x());
	else if(!l.vertical() && this.vertical())   {
	    Point3D cros = l.cross(this);
	    ans = this.functionY(cros.y());
	}
	return ans;
    }
    /** assume y = m*x + k (as a 2D XY !! linear function) */
    private double m() {
	double ans=0;
	double dx = _p2.x()-_p1.x(), dy = _p2.y()-_p1.y();
	if(dx != 0) ans = dy/dx;
	return ans;
    }
  private double k() {
	double k = _p1.y() - m()*_p1.x();
	return k;    
    }   
 /** assume z = m*x + k (as a 2D XZ!! linear function) */
    private double mz() {
	double ans=0;
	double dx = _p2.x()-_p1.x(), dz = _p2.z()-_p1.z();
	if(dx != 0) ans = dz/dx;
	return ans;
    }
  private double kz() {
	double k = _p1.z() - mz()*_p1.x();
	return k;    
    }   
 /** assume z = m*y + k (as a 2D YZ!! linear function) */
    private double myz() {
	double ans=0;
	double dy = _p2.y()-_p1.y(), dz = _p2.z()-_p1.z();
	if(dy != 0) ans = dz/dy;
	return ans;
    }
  private double kyz() {
	double k = _p1.z() - myz()*_p1.y();
	return k;    
    }   

    /** "fixed x": function f(y) = z,x (x is fixed).*/
    public boolean vertical() { return (_p2.x()-_p1.x() == 0);}
    /** "fixed y": function f(x) = z,y (y is fixed).*/
    public boolean horisontle() { return (_p2.y()-_p1.y() == 0);}
    /** "fixed z": function f(x) = y,z (z is fixed).*/
    public boolean z_only() { return (_p2.z()-_p1.z() == 0);}
   
  
    /** assume 3D linear function y = m*x +k */
    public Point3D function(double x) {
	Point3D ans = null;
	if(!this.vertical()){
	    double y = this.m()*x+this.k();
	    double z = this.mz()*x+this.kz();
	    ans = new Point3D(x,y,z); 
	}
	else {
	    System.out.println("*** Error can not Vertical line can NOT be used as a function over x!! **");
	}
	return ans;
    }
    
    /** assume 3D linear function z = mz*y +kz <br>
     this function should be used ONLY if this is vertical!!!*/
    public Point3D functionY(double y) {
	Point3D ans = null;
	if(this.vertical() && !this.horisontle()){
	    double z = this.myz()*y+this.kyz();
	    ans = new Point3D(this._p1.x(),y,z); 
	}
	else {
	    System.out.println("*** Error: must get Vertical (non Horisontal) line - else can NOT be used as a function over y!! **");
	}
	return ans;
    }
    /////////////////////////////// debug only ////////////////////////////
  public String toFile1() {return "Line3D "+_p1.toFile1()+" "+_p2.toFile1()+" "+_color; }

}
