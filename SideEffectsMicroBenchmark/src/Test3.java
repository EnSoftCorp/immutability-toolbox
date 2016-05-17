
public class Test3 {

	// mutable
	Date2 y;
	
	public void foo(){
		// x is mutable => thing is mutable => y is mutable
		Thing x = y.thing;
		
		// x is mutable
		x.majig = 2;
	}
	
}
