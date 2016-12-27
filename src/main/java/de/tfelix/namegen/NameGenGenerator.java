package de.tfelix.namegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tfelix.namegen.model.MarkovModel;
import de.tfelix.namegen.model.Model;

/**
 * Use this class to initialize a serialized file which can be loaded by the
 * name generator.
 * <p>
 * The file to be analyzed must contain a newline terminated list of names.
 * </p>
 * 
 * @author Thomas Felix
 *
 */
public class NameGenGenerator {

	private final static Logger LOG = LoggerFactory.getLogger(NameGenGenerator.class);

	private final Model model;

	/**
	 * This value is added to each probability in order to smooth out the
	 * distribution and make less likly symbols occure more often which might be
	 * the case for small training samples.
	 */
	private final float prior;

	/**
	 * If we undercut this probability for the next token we will backoff to the
	 * next lower order.
	 */
	private final float katzBackoff;

	public NameGenGenerator(int maxOrder, float prior, float katzBackoff) {
		if (maxOrder < 1 || maxOrder > 10) {
			throw new IllegalArgumentException("Order must be between 1 and 10.");
		}

		if (prior < 0) {
			throw new IllegalArgumentException("Prior value must be bigger than 0.");
		}

		if (katzBackoff < 0) {
			throw new IllegalArgumentException("KatzBackoff must be bigger then 0.");
		}

		this.prior = prior;
		this.katzBackoff = katzBackoff;

		this.model = new MarkovModel(maxOrder, prior);
	}

	public void analyze(String inFile, String outFile) {

		final File inF = new File(inFile);
		final File outF = new File(outFile);

		// Validate the input files.
		if (!inF.exists() || !inF.canRead()) {
			throw new IllegalArgumentException("Can not find or read input file.");
		}

		// Try to create out file.
		if (!outF.exists()) {
			try {
				outF.createNewFile();
			} catch (IOException e) {
				throw new IllegalArgumentException("Can not create outFile.", e);
			}
		}

		try {
			scanFileContent(inFile);
		} catch (IOException e) {
			LOG.error("Could not open inFile", e);
		}
		
		// Write the model to the outfile.
	}

	/**
	 * Scans the file content to create the n-grams and their probability
	 * distribution.
	 * 
	 * @param inFile
	 *            The file to scan.
	 */
	private void scanFileContent(String inFile) throws IOException {

		try (BufferedReader br = new BufferedReader(new FileReader(inFile))) {
			String line = "";
			while ((line = br.readLine()) != null) {
				line = line.trim().toLowerCase();
				// Generate our hash counts.
				model.update(line);
			}
		}
	}

}
