package de.tfelix.namegen;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	/**
	 * Ctor. This will initialize the namegenerator with the data coded inside
	 * this file.
	 * 
	 * @param nameFile
	 *            Path to a name file resource.
	 */
	public NameGen(String nameFile) {

	}
}
