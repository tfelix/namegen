package de.tfelix.namegen;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import com.ibm.icu.util.ULocale;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NameGenTest {

	private static final Logger logger = LoggerFactory.getLogger(NameGenTest.class);

	@Test(expected = IllegalArgumentException.class)
	public void ctor_nameFileNull_throws() {
		
		new NameGen(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void analyze_filesDoesNotExist_throws() {
		
		new NameGen("C:\bla\blubber\1235358xhsg.dat");
	}

	@Test
	public void getName_ok() throws URISyntaxException {
		NameGenGenerator trainer = new NameGenGenerator(3, 0f, 0.02f, ULocale.ENGLISH);
		String training_input = "morrow_names.txt";
		URL morrowNames = getClass().getClassLoader().getResource(training_input);
		trainer.analyze(morrowNames.getFile());
		String trainedData = "test_model.dat";
		URL url = getClass().getClassLoader().getResource(trainedData);
		String modelFile = Paths.get(url.toURI()).toString();
		trainer.writeModel(modelFile);
		
		NameGen gen = new NameGen(modelFile);
		String name = gen.getName();
		Assert.assertNotNull(name);
		Assert.assertTrue(name.length() > 0);
		logger.info("Successfully generated {}, reminiscent of the names in {}.", name, training_input);
	}
}
