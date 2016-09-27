package tests;

import annotations.TestCase;
import annotations.immutability.MUTABLE;
import annotations.immutability.POLYREAD;
import annotations.immutability.READONLY;
import objects.Date;

@TestCase(description = "Tests TCALL, specifically the immutability of the receiver \"this\" node on the method")
public class Test6 {

	// polyread because bar2 passes an alias to a to foo2
	// where it is mutated
	@POLYREAD
	Date a = new Date();

	// foo1 mutates first parameter but not second  
	public int foo1(@MUTABLE Date param1, @READONLY Date param2) {
		Date b = param1;
		b.hour = 1;
		return 0;
	}

	// foo2 mutates second parameter but not first
	public int foo2(@READONLY Date param1, @MUTABLE Date param2) {
		Date b = param2;
		b.hour = 1;
		return 0;
	}

	// bar2 calls foo2 with receiver's field as second parameter
	// second parameter is not modified by foo2, 
	// so bar2's "this" node should be READONLY
	public void bar1() {
		Date temp = new Date();
		Date alias = this.a;
		this.foo1(temp, alias);
	}
	
	// bar2 calls foo2 with receiver's field as second parameter
	// second parameter is modified by foo2, 
	// so bar2's "this" node should be MUTABLE
	public void bar2() {
		Date temp = new Date();
		Date alias = this.a;
		this.foo2(temp, alias);
	}
}
