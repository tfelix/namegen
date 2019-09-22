package de.tfelix.namegen.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.icu.util.ULocale;

import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class RuntimeModel<R extends Random> implements Function<R, String> {
    /**
     * JSON serialization requires properties to be public :(
     */
    public final int order;
    public String language_code;
    private final String prefix;
    public final Map<String, Transition> transitions;
    public final Transition delimiterTransition;

    public RuntimeModel(
            int order,
            ULocale locale,
            Map<String, Transition> transitions,
            Transition delimiterTransition
    ) {
        this.language_code = locale.toString();
        this.order = order;
        this.transitions = transitions;
        this.prefix = SymbolManager.getStartSymbol(order);
        this.delimiterTransition = delimiterTransition;
    }

    @JsonCreator
    public RuntimeModel(@JsonProperty("order") int order,
                        @JsonProperty("language_code") String language_code,
                        @JsonProperty("transitions") Map<String, Transition> transitions,
                        @JsonProperty("delimiterTransition") Transition delimiterTransition) {
        this(order, new ULocale(language_code), transitions, delimiterTransition);
    }

    /**
     * The leading text is transformed so it is usable. It gets appended or
     * shortened so that a categorical exists.
     *
     * @param context The context string.
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

    private Transition getTransition(String context) {
        /**
         * Get a Transition for this context
         */
        if (!transitions.containsKey(context)) {
            return delimiterTransition;
        }
        return transitions.get(context);
    }

    /**
     * Generates a new random char from the model depending on the prior context
     * and the random number.
     *
     * @param context The leading text.
     * @param rand    Random number between 0 and 1.
     * @return A new char.
     */
    private char sample(String context, float rand) throws RuntimeException {
        // Check if we need to backoff the context.
        context = backoff(context);
        return getTransition(context).pick(rand);
    }

    /**
     * Generate a random name from the model which was previously generated.
     *
     * @param rand A instance of a random number generator.
     * @return A generated name from the model.
     * @throws RuntimeException if the model hasn't yet been built
     */
    public String apply(R rand) throws RuntimeException {
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
