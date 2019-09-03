package de.tfelix.namegen;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tfelix.namegen.model.RuntimeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Name generator main class. This class has to be used as main entry point for
 * the name generation operation.
 * 
 * @author Thomas Felix
 *
 */
public class NameGen<R extends Random> {

	private final static Logger logger = LoggerFactory.getLogger(NameGen.class);

	private R random;
	private final RuntimeModel generator;

	/**
	 * This will initialize the namegenerator with the data coded inside this file.
	 * 
	 * @param nameFile
	 *            Path to a name file resource.
	 */
	public NameGen(String nameFile) {
		if(nameFile == null || nameFile.isEmpty()) {
			throw new IllegalArgumentException("nameFile can not be null or empty.");
		}

		// We must load the learned model from a file.
		final File file = new File(nameFile);

		if (!file.exists() || !file.canRead()) {
			throw new IllegalArgumentException("Can not read/open file.");
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			generator = objectMapper.readValue(file, RuntimeModel.class);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			throw new IllegalArgumentException("Problematic file %s".format(nameFile));
		}
		this.random =  (R)ThreadLocalRandom.current();
	}

	public NameGen(RuntimeModel generator, R random) {
	    this.generator = generator;
	    this.random = random;
    }

	public void setRandom(R random) {
	    this.random = random;
    }

	/**
	 * Returns a new name, based on the learned model file.
	 * 
	 * @return A new random name.
	 */
	public String getName() throws RuntimeException {
		return generator.apply(random);
	}
}
