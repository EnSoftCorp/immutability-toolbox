
public class Test4 {

	// mutable
	Date3 y;
	
	public void foo(){
		// x is mutable => thing is mutable => y is mutable
		Thing x = y.thing;
		
		// x is mutable
		x.majig = 2;
	}
	
}
