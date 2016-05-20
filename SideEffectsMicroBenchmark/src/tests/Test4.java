package tests;
import objects.Date3;
import objects.Thing;


public class Test4 {

	// polyread
	Date3 y;
	
	public void foo(){
		// x is mutable => thing is polyread => y is polyread
		Thing x = y.thing;
		
		// x is mutable
		x.majig = 2;
	}
	
}
