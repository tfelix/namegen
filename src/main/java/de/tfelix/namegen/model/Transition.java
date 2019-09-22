package de.tfelix.namegen.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.icu.text.UnicodeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.LocaleData;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import static com.ibm.icu.util.LocaleData.ES_STANDARD;

/**
 * This holds every following character for an input state. It can be picked at
 * random by providing a float between 0 and 1.
 * 
 * @author Thomas Felix
 *
 *  todo: break off runtime stuff into a separate class
 */
class Transition implements Serializable {
	private static Logger logger = LoggerFactory.getLogger(Transition.class);
	private static final long serialVersionUID = 1L;
	private final ULocale locale;
	private final Map<Character, Integer> observedChars;
	private int observations;
	private float priorProbability;
	private Map<Character, Float> distribution;  // Store the distribution here once the observations are finished.
	/**
	 * Larger alphabets should really have smaller priors, but anyway, to offer some amount of independence between the
	 * prior and the alphabet, we'll scale the selection point by the actual probability distribution (created with the
	 * prior), rather than trying to build a distribution that sums to 1.0. The sum of our assembled probabilities will
	 * be «selectionRange».
	 **/

	/**
	 * A Transition represents the flow of «something» to a random character from an available alphabet.
	 *
	 * @param priorProbability: The default chance of being chosen, applied to each character of the alphabet.
	 */
	public Transition(float priorProbability, ULocale alphabetLocale) {
		if(priorProbability < 0 || priorProbability > 1.0f) {
			throw new IllegalArgumentException("Prior must be ≥ 0.");
		}
		this.locale = alphabetLocale;
		this.priorProbability = priorProbability;
		this.observedChars = new HashMap<>();
	}

	public Map<Character, Float> getDistribution() {
		return distribution;
	}

	@JsonCreator
	public Transition(@JsonProperty("distribution") Map<Character, Float> distribution) {
		// todo: separate interface for runtime transition
		this.distribution = distribution;
		this.locale = null;
		this.observedChars = null;
	}

	/**
	 * Updates the transition with a new output choice. The
	 * internal counts and observations will be updated.
	 * 
	 * @param c
	 *			The character that is being defined as a valid output.
	 */
	public void update(char c) {
		if (!observedChars.containsKey(c)) {
			observedChars.put(c, 0);
		}

		observedChars.put(c, observedChars.get(c) + 1);
		observations++;
	}

	/**
	 * Once we've finished learning, prepare the whole alphabet for generating letters.
	 */
	public Transition build() {
		Transition runtimeTransition = new Transition(this.priorProbability, this.locale);
		// Having a tree allows deterministic traversal
		runtimeTransition.distribution = new TreeMap<>();
		float observationRange = 1.0f;
		if(priorProbability >= Math.ulp(1.0)) {
			// Prior is desired; initialise the alphabet.
			UnicodeSet alphabet = LocaleData.getExemplarSet(this.locale, ES_STANDARD);
			/* Observations need to be scaled so that the probability across the alphabet sums to 1.0 */
			observationRange = (1.0f - priorProbability * alphabet.size());
			if (observationRange < 0.0) {
				logger.warn("The prior probability was meant to be the chance that any available letter would occur. " +
						"By specifying a probability of {} with {} letters in the alphabet means that there's no " +
						"room in the probability distribution to adjust for the letters that are more likely.",
						priorProbability, alphabet.size());
				// Set a default prior − the show must go on
				priorProbability = 1.0f / (2.0f*alphabet.size());
				observationRange = 0.5f;  // Half of our outputs will be influenced by the observations
			}
			Iterator<String> iterator = alphabet.iterator();
			while(iterator.hasNext()) {
				Character letter = iterator.next().charAt(0);
				runtimeTransition.distribution.put(letter, priorProbability);
			}
		}
		for(Entry<Character, Integer> entry: observedChars.entrySet()) {
			// Some observed characters (such as the ending token, hopefully) might not belong to the alphabet
			float probability = observationRange * entry.getValue() / (float)(this.observations);
			if (runtimeTransition.distribution.containsKey(entry.getKey())) {
				Float prior = runtimeTransition.distribution.get(entry.getKey());
				probability += prior;
			}
			runtimeTransition.distribution.put(entry.getKey(), probability);
		}
		return runtimeTransition;
	}

	/**
	 * Deterministically pick a new character from this transition's probability distribution.
	 * 
	 * @param position
	 *			A position in the probability distribution ∈ [0, 1.0].
	 * @return A randomly picked character.
	 */
	public char pick(float position) throws RuntimeException {
		if (position < 0 || position > 1.0) {
			throw new IllegalArgumentException(String.format("Probability %g must be between 0 and 1.0", position));
		}
		if(distribution == null) {
			throw new RuntimeException("A transition was called for sampling before it had been built.");
		}
		float cumulation = 0f;
		for (Entry<Character, Float> entry : distribution.entrySet()) {
			/** By iterating and cumulating, it's easier to query specific letters and see their probability.
			 * Todo: replace this approach with an array of elements, specifying the cumulative sum (for binary search
			 * at runtime).
			 **/
			cumulation += entry.getValue();
			if(cumulation > position) {
				return entry.getKey();
			}
		}
		logger.error("Unable to find a position for {} in Transition ", position);
		return SymbolManager.getEndSymbol();
	}

	@Override
	public String toString() {
		return String.format("Transition: %d sightings ∈ output: [%s]", observations, observedChars.entrySet().toString());
	}
}
