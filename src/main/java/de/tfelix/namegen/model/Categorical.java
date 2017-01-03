package de.tfelix.namegen.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This holds every following character for a category. It can be picked at
 * random by providing a float between 0 and 1.
 * 
 * @author Thomas Felix
 *
 */
class Categorical implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<Character, Integer> tokens = new HashMap<>();
	private int totalCount;
	private final float prior;

	public Categorical(float prior) {
		if(prior < 0) {
			throw new IllegalArgumentException("Prior must be bigger or equal then 0.");
		}
		
		this.prior = prior;
	}

	/**
	 * Updates the category with a new character which can follow it. The
	 * internal counts and possibilities will be updated.
	 * 
	 * @param c
	 *            The new character.
	 */
	public void update(char c) {
		if (!tokens.containsKey(c)) {
			tokens.put(c, 0);
		}

		tokens.put(c, tokens.get(c) + 1);
		totalCount++;
	}

	/**
	 * Picks a new character at random from this category.
	 * 
	 * @param prob
	 *            A probability between 0 and 1.0 (inclusive).
	 * @return A randomly picked character.
	 */
	public char pick(float prob) {
		if (prob < 0 || prob > 1.0) {
			throw new IllegalArgumentException("Prob must be between 0 and 1.0");
		}

		float sample = prior + (prob * totalCount);
		for (Entry<Character, Integer> e : tokens.entrySet()) {
			sample -= e.getValue();
			if (sample <= 0) {
				return e.getKey();
			}
		}

		// Should not happen.
		return SymbolManager.getEndSymbol();
	}

	@Override
	public String toString() {
		return String.format("Cat[tokens: %s, count: %d]", tokens.entrySet().toString(), totalCount);
	}
}
