
public class Test1 {

	// mutable
	Date y;
	
	public void foo(){
		// x is mutable => thing is mutable => y is mutable
		Thing x = y.thing;
		
		// x is mutable
		x.majig = 2;		
	}
	
}
