package de.tfelix.namegen.model;

import com.ibm.icu.util.ULocale;

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
	private final Transition delimiterTransition;
	private final ULocale locale;
	private final Map<String, Transition> transitions = new HashMap<>();

	/**
	 * Creates a new Markov model. The order is how many characters are taken
	 * into account for creating depending probabilities. It should be between 1
	 * and 10 (inclusive). The prior value can be used to make the model not so
	 * depending on the learning data but gets more random. Usually the values
	 * should be quite low, between 0 and maybe 0.1. Can be less if there is
	 * more learning data.
	 * 
	 * @param order
	 *            Order of the Markov model.
	 * @param prior
	 *            The starting probability assigned to each alphabet character, that it will be output. Note that this
     *            probability will be meaningless if it's more than the inverse of the size of the alphabet, as it would
     *            dictate that every alphabet character is equally likely to be output, with no adjustment room left for
     *            the observations.
     * @param locale
     *            The locale to use for generating letters that were not seen in the training set.
	 */
	public MarkovModel(int order, float prior, ULocale locale) {
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
		Transition transition = new Transition(0f, locale);
		transition.update(this.postfix);
		this.delimiterTransition = transition.build();
		this.locale = locale;
	}

	private Transition getTransition(String context) {
		/**
		 * Get a Transition for this context
		 */
		if (!transitions.containsKey(context)) {
			return delimiterTransition;
		}
		return transitions.get(context);
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
			char output = line.charAt(i);  // line[i] should be typically output in this context
			for (int j = 0; j < context.length(); j++) {
				String subContext = context.substring(j);
				if(this.transitions.containsKey(subContext)) {
				    transitions.get(subContext).update(output);
                } else {
				    Transition transition = new Transition(this.prior, locale);
				    transition.update(output);
				    transitions.put(subContext, transition);
                }
			}
		}
	}

	@Override
    public Model build() {
	    MarkovModel runtimeModel = new MarkovModel(this.order, this.prior, this.locale);
	    for(String key: transitions.keySet()) {
	        runtimeModel.transitions.put(key, transitions.get(key).build());
        }
        return runtimeModel;
    }

	/**
	 * The leading text is transformed so it is usable. It gets appended or
	 * shortened so that a categorical exists.
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
		while (!transitions.containsKey(context) && context.length() > 0) {
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
	private char sample(String context, float rand) throws RuntimeException {
		// Check if we need to backoff the context.
		context = backoff(context);
		return getTransition(context).pick(rand);
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
