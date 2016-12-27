package de.tfelix.namegen.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The model contains all the data to encode a simple markov graph in order to
 * generate names.
 * 
 * @author Thomas Felix
 *
 */
public class MarkovModel implements Model {

	private static final long serialVersionUID = 1L;

	private final int order;
	private final float prior;
	private final String prefix;
	private final char postfix;
	private final Map<String, Categorical> counts = new HashMap<>();

	/**
	 * Creates a new markov model. The order is how many characters are taken
	 * into account for creating depending probabilities. It should be between 1
	 * and 10 (inclusive). The prior value can be used to make the model not so
	 * depending on the learning data but gets more random. Usually the values
	 * should be quite low, between 0 and maybe 0.1. Can be less if there is
	 * more learning data.
	 * 
	 * @param order
	 *            Order of the markov model.
	 * @param prior
	 *            The prior value for smoothing the contained data.
	 */
	public MarkovModel(int order, float prior) {
		if (order < 1 || order > 10) {
			throw new IllegalArgumentException("Order must be between 1 and 10.");
		}
		if (prior < 0) {
			throw new IllegalArgumentException("Prior must be bigger then 0.");
		}

		this.order = order;
		this.prior = prior;

		this.prefix = SymbolManager.getStartSymbol(order);
		this.postfix = SymbolManager.getEndSymbol();
	}

	private Categorical getCategorical(String context) {
		if (!counts.containsKey(context)) {
			counts.put(context, new Categorical(prior));
		}
		return counts.get(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tfelix.namegen.model.Model#update(java.lang.String)
	 */
	@Override
	public void update(String line) {
		line = prefix + line + postfix;
		for (int i = order; i < line.length(); i++) {
			String context = line.substring(i - order, i);
			char event = line.charAt(i);
			for (int j = 0; j < context.length(); j++) {
				String subCtx = context.substring(j);
				Categorical cat = getCategorical(subCtx);
				cat.update(event);
			}
		}
	}

	/**
	 * The leading text is transformed so it is usable. It gets appended or
	 * shortened so that an categorical exists.
	 * 
	 * @param context
	 *            The context string.
	 * @return A prepared context string.
	 */
	private String backoff(String context) {

		// bring the context to the length of the order.
		if (context.length() > order) {
			context = context.substring(context.length() - order);
		} else if (context.length() < order) {
			context = SymbolManager.getStartSymbol(order - context.length()) + context;
		}

		// Remove length until we find a categorical.
		while (!counts.containsKey(context) && context.length() > 0) {
			context = context.substring(1);
		}

		return context;
	}

	/**
	 * Generates a new random char from the model depending on the prior context
	 * and the random number.
	 * 
	 * @param context
	 *            The leading text.
	 * @param rand
	 *            Random number between 0 and 1.
	 * @return A new char.
	 */
	private char sample(String context, float rand) {
		// Check if we need to backoff the context.
		context = backoff(context);
		return getCategorical(context).pick(rand);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tfelix.namegen.model.Model#generate(java.util.Random)
	 */
	@Override
	public String generate(Random rand) {

		StringBuilder sequence = new StringBuilder();
		sequence.append(prefix);

		sequence.append(sample(sequence.toString(), rand.nextFloat()));

		while (sequence.charAt(sequence.length() - 1) != SymbolManager.getEndSymbol()) {
			sequence.append(sample(sequence.toString(), rand.nextFloat()));
		}

		// Remove end symbol.
		sequence.delete(0, prefix.length());
		sequence.deleteCharAt(sequence.length() - 1);

		return sequence.toString();
	}
}
