package de.tfelix.namegen.model;

import com.ibm.icu.util.ULocale;

import java.util.HashMap;
import java.util.Map;

/**
 * The model contains all the data to encode a simple markov graph in order to
 * generate names.
 * 
 * @author Thomas Felix
 *
 */
public class MarkovModel implements TrainableModel {

    private static final long serialVersionUID = 1L;

    private final int order;
    private final float prior;
    private final String prefix;
    private final char postfix;
    private final ULocale locale;
    private final Map<String, Transition> transitions;

    /**
     * Creates a new Markov model. The order is how many characters are taken
     * into account for creating depending probabilities. It should be between 1
     * and 10 (inclusive). The prior value can be used to make the model not so
     * depending on the learning data but gets more random. Usually the values
     * should be quite low, between 0 and maybe 0.1. Can be less if there is
     * more learning data.
     *
     * @param order  Order of the Markov model.
     * @param prior  The starting probability assigned to each alphabet character, that it will be output. Note that this
     *               probability will be meaningless if it's more than the inverse of the size of the alphabet, as it would
     *               dictate that every alphabet character is equally likely to be output, with no adjustment room left for
     *               the observations.
     * @param locale The locale to use for generating letters that were not seen in the training set.
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
        this.locale = locale;
        this.transitions = new HashMap<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.tfelix.namegen.model.TrainableModel#update(java.lang.String)
     */
    @Override
    public void update(String line) {
        line = prefix + line + postfix;
        for (int i = order; i < line.length(); i++) {
            String context = line.substring(i - order, i);
            char output = line.charAt(i);  // line[i] should be typically output in this context
            for (int j = 0; j < context.length(); j++) {
                String subContext = context.substring(j);
                if (this.transitions.containsKey(subContext)) {
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
    public RuntimeModel build() {
        Map<String, Transition> builtTransitions = new HashMap<>();
        for (String key : transitions.keySet()) {
            builtTransitions.put(key, transitions.get(key).build());
        }
        return new RuntimeModel(this.order, this.locale, builtTransitions);
    }
}
