package de.tfelix.namegen.model;

import java.io.Serializable;
import java.util.Random;

/**
 * A model containing the descriptive information in order to create new names
 * from it. The model must be serializable so it can be written to a file.
 * 
 * @author Thomas Felix
 *
 */
public interface Model extends Serializable {

	/**
	 * Updates the model with new information which is contained in the given
	 * string.
	 * 
	 * @param line
	 *            A new name to be included into the models description.
	 */
	void update(String line);

    /**
     * Returns a modified Model instance that has been optimized for runtime rather than training. This should be called
     * before any subsequent calls to generate().
     * @return
     */
	Model build();

	/**
	 * Generate a random name from the model which was previously generated.
	 * 
	 * @param rand
	 *            A instance of a random number generator.
	 * @return A generated name from the model.
     * @throws RuntimeException if the model hasn't yet been built
	 */
	String generate(Random rand) throws RuntimeException;

}