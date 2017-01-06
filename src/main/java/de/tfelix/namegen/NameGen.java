package de.tfelix.namegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tfelix.namegen.model.Model;

/**
 * Name generator main class. This class has to be used as main entry point for
 * the name generation operation.
 * 
 * @author Thomas Felix
 *
 */
public class NameGen {

	private final static Logger LOG = LoggerFactory.getLogger(NameGen.class);

	private final Random rand = ThreadLocalRandom.current();
	private final Model model;

	/**
	 * Ctor. This will initialize the namegenerator with the data coded inside
	 * this file.
	 * 
	 * @param nameFile
	 *            Path to a name file resource.
	 */
	public NameGen(String nameFile) {
		if(nameFile == null || nameFile.isEmpty()) {
			throw new IllegalArgumentException("nameFile can not be null or empty.");
		}

		// We must load the model from file.
		final File file = new File(nameFile);

		if (!file.exists() || !file.canRead()) {
			throw new IllegalArgumentException("Can not read/open file.");
		}

		try (ObjectInputStream ooin = new ObjectInputStream(new FileInputStream(file))) {

			model = (Model) ooin.readObject();

		} catch (ClassNotFoundException | IOException ex) {
			LOG.error("Can not read or re-establish a object from file.", ex);
			throw new IllegalArgumentException("Can not read model from file.");
		}
	}

	/**
	 * Returns a new name, based on the learned model file.
	 * 
	 * @return A new random name.
	 */
	public String getName() {
		return model.generate(rand);
	}
}
