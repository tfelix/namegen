package de.tfelix.namegen.model;

import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.LocaleData;
import com.ibm.icu.util.ULocale;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;


public class TransitionTest {
	

	@Test(expected=IllegalArgumentException.class)
	public void ctor_negPrior_throws() {
		new Transition(-1f, ULocale.FRENCH);
	}
	
	@Test
	public void ctor_posPrior_ok() {
		new Transition(0.4f, ULocale.FRENCH);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void pick_negRand_throws() {
		Transition c = new Transition(0.01f, ULocale.FRENCH);
		c.build();
		c.pick(-0.3f);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void pick_biggerOneRand_throws() {
		Transition c = new Transition(0.01f, ULocale.FRENCH);
		c.build();
		c.pick(1.4f);
	}
	
	@Test
	public void update_char_ok() {
		Map<Character, Integer> histogram = new HashMap<>();
        UnicodeSet alphabet = LocaleData.getExemplarSet(ULocale.FRENCH, LocaleData.ES_STANDARD);
        // The ending character should be part of the histogram too. Sample at the Nyquist rate.
        int sampleResolution = 2 * (alphabet.size() + 1);
        Transition training = new Transition(1f/(7*alphabet.size()), ULocale.FRENCH);
        char favorite = 'c';
        training.update(favorite);
        Transition runtime = training.build();
		for(int i = 0; i < sampleResolution; ++i) {
		    char c = runtime.pick((float)i / sampleResolution);
		    if (histogram.containsKey(c)) {
		        histogram.put(c, histogram.get(c) + 1);
            } else {
		        histogram.put(c, 1);
            }
        }
		Assert.assertTrue(histogram.get(favorite) > 3);  // 1 « 3 « 7
	}

	/**
	 * Apply different prior probabilities and alphabets. Assert that the sum of probabilities across
	 * all letters always adds to 1.0.
	 */
	@Test
	public void check_distribution() {
		// todo
        UnicodeSet alphabet = LocaleData.getExemplarSet(ULocale.FRENCH, LocaleData.ES_STANDARD);
        Transition transition = new Transition(1.0f/(float)(alphabet.size()), ULocale.FRENCH);
        Set<Character> observed = new HashSet<>();
        Transition runtimeTransition = transition.build();
        float step = 1.0f/alphabet.size();
        for(float i = 0f; i < 1.0; i+= step) {
            Character letter = runtimeTransition.pick(i);
            Assert.assertFalse(String.format("Letters were meant to be generated deterministically, so it should " +
                            "not be possible to see %s again after already seeing letters %s", letter,
                    observed.toString()),
                    observed.contains(letter));
            observed.add(letter);
        }
        Assert.assertEquals(String.format("It was expected that all %d possible letters would be generated, but " +
                "actually it was %d.", alphabet.size(), observed.size()), alphabet.size(), observed.size());
	}
}
