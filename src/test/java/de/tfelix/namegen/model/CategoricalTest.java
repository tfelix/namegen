package de.tfelix.namegen.model;

import org.junit.Assert;
import org.junit.Test;


public class CategoricalTest {
	

	@Test(expected=IllegalArgumentException.class)
	public void ctor_negPrior_throws() {
		new Categorical(-1f);
	}
	
	@Test
	public void ctor_posPrior_ok() {
		new Categorical(0.4f);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void pick_negRand_throws() {
		Categorical c = new Categorical(0.01f);
		c.pick(-0.3f);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void pick_biggerOneRand_throws() {
		Categorical c = new Categorical(0.01f);
		c.pick(1.4f);
	}
	
	@Test
	public void update_char_ok() {
		Categorical c = new Categorical(0.01f);
		c.update('c');
		char r = c.pick(0.8f);
		Assert.assertEquals('c', r);
	}
}
