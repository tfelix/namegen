package de.tfelix.namegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.ibm.icu.util.ULocale;
import de.tfelix.namegen.model.MarkovModel;
import de.tfelix.namegen.model.RuntimeModel;
import de.tfelix.namegen.model.TrainableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this class to train a model based on a simple text file containing a list
 * of new-line-terminated names. It will initialize a serialized file which can
 * be loaded by the name generator later and used to generate the names.
 * <p>
 * The file to be analyzed must contain a newline terminated list of names.
 * </p>
 * 
 * @author Thomas Felix
 *
 */
public class NameGenGenerator {

	private final static Logger LOG = LoggerFactory.getLogger(NameGenGenerator.class);

	private final TrainableModel trainableModel;


	/**
	 * Ctor.
	 * The prior value is added to each probability in order to smooth out the
	 * distribution and make less likely symbols occur more often which might be
	 * the case for small training samples.
	 * 
	 * @param maxOrder
	 *            Maximum order of the Markov model. 3 is a good default value.
	 * @param prior
	 *            The higher the prior value is, the more random the trainableModel will
	 *            be. Must be higher if there is not enough training data.
	 *            Usually a value between 0.01 and 0.05 is a good start.
	 * @param katzBackoff
	 *            If the probability for choosing a new terminal for the name is
	 *            under this threshold we will fall back to the lower order
	 *            trainableModel of the markov chain. 0.05 is a reasonable default value.
     * @param locale
     *            The locale to use for generating letters that were not seen in
     *            the training set.
	 */
	public NameGenGenerator(int maxOrder, float prior, float katzBackoff, ULocale locale) {
		if (maxOrder < 1 || maxOrder > 10) {
			throw new IllegalArgumentException("Order must be between 1 and 10.");
		}

		if (prior < 0) {
			throw new IllegalArgumentException("Prior value must be bigger than 0.");
		}

		if (katzBackoff < 0) {
			throw new IllegalArgumentException("KatzBackoff must be bigger then 0.");
		}

		this.trainableModel = new MarkovModel(maxOrder, prior, locale);
	}

	/**
	 * Reads the file and feeds it into the trainableModel. The file must contain newline
	 * terminated names.
	 * 
	 * @param inFile
	 *            The file to be read.
	 */
	public void analyze(String inFile) {
		if(inFile == null || inFile.isEmpty()) {
			throw new IllegalArgumentException("inFile can not be null or empty.");
		}

		final long startTime = System.currentTimeMillis();
		final File inF = new File(inFile);

		// Validate the input files.
		if (!inF.exists() || !inF.canRead()) {
			throw new IllegalArgumentException("Can not find or read input file.");
		}

		try {
			try (BufferedReader br = new BufferedReader(new FileReader(inFile))) {
				String line = "";
				while ((line = br.readLine()) != null) {
					line = line.trim().toLowerCase();
					// Generate our hash counts.
					trainableModel.update(line);
				}
			}
		} catch (IOException e) {
			LOG.error("Could not open inFile", e);
		}

		LOG.info("File {} analyzed in {} ms.", inF.getName(), System.currentTimeMillis() - startTime);
	}

	RuntimeModel build() {
	    return this.trainableModel.build();
    }

	/**
	 * Writes the trainableModel serialized to a file to load it later.
	 * 
	 * @param outFile
	 *            The file to write.
	 *            todo: write as text instead of a binary blob.
	 */
	public void writeModel(String outFile) {
		if(outFile == null || outFile.isEmpty()) {
			throw new IllegalArgumentException("inFile can not be null or empty.");
		}
		
		final File outF = new File(outFile);

		// Try to create out file.
		if (!outF.exists()) {
			try {
				outF.createNewFile();
			} catch (IOException e) {
				throw new IllegalArgumentException("Can not create outFile.", e);
			}
		}
		RuntimeModel generator = this.trainableModel.build();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(outF, generator);
		} catch(JsonMappingException me) {
			LOG.error(me.getLocalizedMessage());
		} catch (JsonGenerationException ge) {
            LOG.error(ge.getLocalizedMessage());
        } catch (IOException ioe) {
            LOG.error("Could not write to {}", outFile);
        }
	}

}
