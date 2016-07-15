package tests;

import java.util.Random;

import objects.Thing;

public class Test9 {

	public void foo(){
		Thing x = new Thing();
		
		if(new Random().nextBoolean()){
			x.majig = 1;
		} else {
			x = new Thing();
		}
		
		bar(x);
	}
	
	
	public void bar(Thing x){
		
	}
}
