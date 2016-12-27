package de.tfelix.namegen.model;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;

public class MarkovModelTest {
	
	private Random rand = ThreadLocalRandom.current();
	
	@Test(expected=IllegalArgumentException.class)
	public void ctor_negOrder_throws() {
		new MarkovModel(-1, 0.1f);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void ctor_negPrior_throws() {
		new MarkovModel(3, -0.1f);
	}
	
	/**
	 * If only one name is added model should only generate this name.
	 */
	@Test
	public void ctor_updateAndGenerate_ok() {
		Model model = new MarkovModel(3, 0.001f);
		String name = "thomas";
		model.update(name);
		String n = model.generate(rand);
		Assert.assertEquals(name, n);
	}
}
