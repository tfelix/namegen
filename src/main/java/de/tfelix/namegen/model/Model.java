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
	 * Generate a random name from the model which was previously generated.
	 * 
	 * @param rand
	 *            A instance of a random number generator.
	 * @return A generated name from the model.
	 */
	String generate(Random rand);

}