
package Libraries;

import java.io.*;
class ajSegment implements Serializable  {
    ajPoint a,b;
    
    public ajSegment() {}
    
    public ajSegment( ajPoint A, ajPoint B ) {
	a=A;
	b=B;
	//    color=segmentColor;
    }
    
    public String toString() {
	return(new String(" ajSg["+ a.toString() + "|" + b.toString() + "]"));
    }
    
}
