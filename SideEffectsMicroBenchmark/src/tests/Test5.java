package tests;

import objects.Date;

public class Test5 {

	public static void main(String[] args){
		Test5 test5 = new Test5();
		int value1 = test5.a.hour;
		test5.foo();
		int value2 = test5.a.hour;
		if(value1 != value2){
			System.out.println("test5.a was mutated!");
		}
	}
	
	// polyread
	Date a = new Date();

	public void foo(){
	   // b is readonly
	   // b was mutated
	   Date b = new Date();
	   
	   // a is now b
	   // a is mutated when b was mutated
	   this.a = b;
	   
	   // b is mutated
	   b.hour = 1;
	}
}
