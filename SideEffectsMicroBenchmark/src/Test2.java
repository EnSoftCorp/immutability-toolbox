
public class Test2 {

	// mutable
	Date y;
	
	public void foo(Date x){
		// parameter x cannot be mutable
		// no longer have a reference to parameter x
		x = new Date();
		
		// y is now the local variable x
		y = x;
		
		// thing is mutable => y is mutable
		x.thing.majig = 2;	
	}
	
}
