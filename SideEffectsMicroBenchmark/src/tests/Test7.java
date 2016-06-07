package tests;

import objects.Date;

public class Test7 {

	Date date;
	
	public void foo(){
		Test7 test = new Test7();
		test.mutate();
		this.mutate();
		Date date = read();
		date.hour = 1;
	}
	
	public Date read(){
		return date;
	}
	
	public void mutate(){
		date.hour = 1;
	}
	
}
