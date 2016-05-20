package tests;
import objects.Date;


public class Test2 {

	// polyread
	Date y;
	
	public void foo(Date x){
		// parameter x cannot be mutable
		// no longer have a reference to parameter x
		x = new Date();
		
		// y is now the local variable x
		this.y = x;
		
		// thing is polyread => y is polyread
		x.thing.majig = 2;	
	}
	
}
