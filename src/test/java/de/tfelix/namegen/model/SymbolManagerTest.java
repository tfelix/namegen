package de.tfelix.namegen.model;

import org.junit.Assert;
import org.junit.Test;


public class SymbolManagerTest {
	
	@Test(expected=IllegalArgumentException.class)
	public void getStartSymbol_negative_throws() {
		SymbolManager.getStartSymbol(-2);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getStartSymbol_0_throws() {
		SymbolManager.getStartSymbol(0);
	}
	
	@Test
	public void getStartSymbol_number_ok() {
		Assert.assertEquals(3, SymbolManager.getStartSymbol(3).length());
	}

}
