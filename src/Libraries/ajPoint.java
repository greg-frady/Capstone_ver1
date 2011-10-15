//---------------------------------------------------------------------------
//
// ajPoint.java
//
// Klasse f�r Punkte
//
// LM 7.8.1996
//
//---------------------------------------------------------------------------
 
// import java.awt.*;

package Libraries;

import java.io.*;
class ajPoint implements Serializable // extends ajElement {
{
    float x,y,z=0;
    
    public ajPoint(){}
    
    public ajPoint(float x, float y){
	this.x=x;   this.y=y;
    }
    
    public ajPoint(int x, int y){
	this.x=x;   this.y=y;
    }
     public ajPoint(int x, int y, int z){
	 this.x=x;   this.y=y; this.z=z;
    }  

    public ajPoint(double x, double y){
	Float F = new Float(x);
	this.x = F.floatValue();
	F = new Float(y);
	this.y = F.floatValue();
    }
    public ajPoint(double x, double y,double z){
	Float F = new Float(x);
	this.x = F.floatValue();
	F = new Float(y);
	this.y = F.floatValue();
	F = new Float(z);
	this.z = F.floatValue();
    }

	public ajPoint( ajPoint p ) {
		x = p.x;
		y = p.y;
		z = p.z;
	}


	public float distance2(ajPoint p){
		return (p.x-x)*(p.x-x)+(p.y-y)*(p.y-y);
	}
	
	public float distance2(float px, float py){
		return (px-x)*(px-x)+(py-y)*(py-y);
	}

// Darstellung als d�nne/dicke Punkte ein/ausschalten

	protected static boolean smallPoints;
	protected static float distClose; // distance2-Wert f�r nahe zusammenliegende Punkte

	public static void SetSmallPoints( boolean st ) {
		smallPoints = st;
		if (smallPoints)
			distClose = 20;
		else
			distClose = 57;
	}

// Initialisierung der Punktdarstellungsoption
	static { SetSmallPoints( false ); } 

//
// Match mit Koordinaten
//

	public boolean match(int x, int y) {
          return( distance2((float) x, (float) y) <= distClose );
        }

//
// bewegen auf neue Koordinaten
//

	public void move(int xx, int yy) {
		x = (float) xx;
		y = (float) yy;
	}


// Lexikographische Ordnung von Punkten: isLess, isGreater, isEqual

	public boolean isLess( ajPoint p ) {
	  return ( x<p.x ) || ( (x==p.x) && (y<p.y) );
	}

	public boolean isGreater( ajPoint p ) {
	  return (x>p.x) || ( (x==p.x) && (y>p.y) );
	}

	public boolean isEqual( ajPoint p ) {
	  return (x==p.x) && (y==p.y);
	}


//
// toString...
//

	public String toString() {
		return(new String(" ajPt[" + x + "|" + y + "]"));
	}


// pointLineTest
// ===============
// Liegt ein Punkt LINKS oder RECHTS von einer gerichteten Geraden
// durch die Punkte a und b, oder liegt er genau auf dieser Geraden
// und VOR, HINTER oder AUF dem Segment von a nach b.

// Ergebniswerte f�r die Methode pointLineTest.
// In der Skizze ist + eine m�gliche Position des aktuellen Punkts (this),
// a und b sind die Parameter von pointLineTest,

	public final static int ONSEGMENT = 0;	// �����a----+----b������

						//         +
	public final static int LEFT = 1;	// �����a---------b������


	public final static int RIGHT = 2;	// �����a---------b������
						//     +

	public final static int INFRONTOFA = 3;	// ��+��a---------b������

	public final static int BEHINDB = 4;	// �����a---------b����+�

	public final static int ERROR = 5;


	public int pointLineTest(ajPoint a, ajPoint b) {

		float dx = b.x-a.x;
		float dy = b.y-a.y;
		float res = dy*(x-a.x)-dx*(y-a.y);

		if (res < 0) return LEFT;
		if (res > 0) return RIGHT;
	
		if (dx > 0) {
			if (x < a.x) return INFRONTOFA;
			if (b.x < x) return BEHINDB;
			return ONSEGMENT;
		}
		if (dx < 0) {
			if (x > a.x) return INFRONTOFA;
			if (b.x > x) return BEHINDB;
			return ONSEGMENT;
		}
		if (dy > 0) {
			if (y < a.y) return INFRONTOFA;
			if (b.y < y) return BEHINDB;
			return ONSEGMENT;
		}
		if (dy < 0) {
			if (y > a.y) return INFRONTOFA;
			if (b.y > y) return BEHINDB;
			return ONSEGMENT;
		}
		System.out.println("Error, pointLineTest with a=b");
		return ERROR;
	}


// sind drei Punkte kollinear?

  public boolean areCollinear(ajPoint a, ajPoint b) {
    float dx = b.x-a.x;
    float dy = b.y-a.y;
    float res = dy*(x-a.x)-dx*(y-a.y);
    return res==0;
  }


// mittelsenkrechte Gerade

  public ajSegment Bisector( ajPoint b) {

    float sx = (x+b.x)/2;
    float sy = (y+b.y)/2;
    float dx = b.x-x;
    float dy = b.y-y;
    ajPoint p1 = new ajPoint(sx-dy,sy+dx);
    ajPoint p2 = new ajPoint(sx+dy,sy-dx);
    return new ajSegment( p1,p2 );
  }


// Umkreismittelpunkt von drei Punkten

  public ajPoint circumcenter( ajPoint a, ajPoint b) {

    float u = ((a.x-b.x)*(a.x+b.x) + (a.y-b.y)*(a.y+b.y)) / 2.0f;
    float v = ((b.x-x)*(b.x+x) + (b.y-y)*(b.y+y)) / 2.0f;
    float den = (a.x-b.x)*(b.y-y) - (b.x-x)*(a.y-b.y);
    if ( den==0 ) // oops
      System.out.println( "circumcenter, degenerate case" );
    return new ajPoint((u*(b.y-y)   - v*(a.y-b.y)) / den,
                       (v*(a.x-b.x) - u*(b.x-x)) / den);
  }


}
