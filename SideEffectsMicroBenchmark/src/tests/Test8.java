package tests;

public class Test8 {
	public void m2(Date p2){
		Date z = p2;
		this.m1(z);
		
		Date z2 = new Date();
		int x = this.m1(z2);
	}
	
	public int m1(Date p1){
		p1.hour = 1;
		m3(p1);
		return 1;
	}
	
	public void m3(Date p3){}
	
	public static void m4(Date p4){}
	
}

class Date {
	public int hour;
}