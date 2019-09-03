package de.tfelix.namegen.model;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.LocaleData;
import com.ibm.icu.util.ULocale;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

public class MarkovModelTest {
	
	private Random rand = ThreadLocalRandom.current();
	private static final Logger logger = LoggerFactory.getLogger(MarkovModelTest.class);
	
	@Test(expected=IllegalArgumentException.class)
	public void negative_order_should_throw() {
		new MarkovModel(-1, 0.1f, ULocale.ITALIAN);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void negative_prior_should_throw() {
		new MarkovModel(3, -0.1f, ULocale.KOREAN);
	}
	
	/**
	 * If only one name is added, the model should only generate this name.
	 */
	@Test
	public void deterministic_model_should_repeat_training() {
		//TrainableModel trainableModel = new MarkovModel(3, 0.001f);
		TrainableModel trainableModel = new MarkovModel(3, 0f, ULocale.GERMAN);
		String name = "thomas";
		trainableModel.update(name);
		RuntimeModel generator = trainableModel.build();
		String n = generator.apply(rand);
		Assert.assertEquals(name, n);
	}

	@Test
    public void should_generate_new_names() {
        UnicodeSet alphabet = com.ibm.icu.util.LocaleData.getExemplarSet(ULocale.FRENCH, LocaleData.ES_STANDARD);
	    float prior = 0.5f / alphabet.size();  // ~½ of characters should be thanks to the observations
        TrainableModel trainableModel = new MarkovModel(3, prior, ULocale.FRENCH);
        String name = "thomas";
        trainableModel.update(name);
        RuntimeModel generator = trainableModel.build();
        String result1 = generator.apply(rand);
        if(result1.equals(name)) {
            logger.warn("The generated name is the same as the training name! This is very unlikely and is probably " +
                    "an error.");
        }
        rand = ThreadLocalRandom.current();
        String result2 = generator.apply(rand);
        if(result1.equals(result2) && result1.equals(name)) {
            fail("It is too unlikely that two random results would both be exactly the same as the training data.");
        }
    }
}
