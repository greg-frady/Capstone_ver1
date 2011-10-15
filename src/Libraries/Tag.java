package Libraries;

import java.io.*;
/**
this class represent a simple Tag used by algorithms for marking Graphs,
usualy it is enougth to uses 3 colors - White, Gray, Black, but we allowd
the user to give any positive tag. <br>
Note: many algorithms uses this class as is do not chainge this class behaver   
*/

public class Tag implements Serializable{
    private int _tag;
    public static final int White=0, Gray=1,Black =-1;

    public Tag() {_tag= this.White;}
    public Tag(Tag other) {_tag = other._tag;}
    public int getTag() {return _tag;}
    public void setWhite() {_tag= this.White;}
    public void setGray() {_tag= this.Gray;}
    public void setBlack() {_tag= this.Black;}
    public boolean isWhite() {return _tag==this.White;}
    public boolean isGray() {return _tag==this.Gray;}
    public boolean isBlack() {return _tag==this.Black;}
    public boolean isAnyGray() {return (_tag!=this.Black && _tag!=this.White);}
    public boolean equals(Tag t) {return t!=null && t._tag==this._tag;}
    /** increases tag by one */
    public void nextTag() {_tag++;}  
    /** decreases tag by one */
    public void lastTag() {if(_tag!=White && _tag!=Black) _tag--; else System.out.print("*** Error can not 'last' Black or White tag = "+_tag+" ***");} 
    public String toString() {return ""+_tag;}
    /** NOTE: try not to use this method*/ 
    public void setTag(Tag t) {if(t!=null)_tag = t._tag;}
}
