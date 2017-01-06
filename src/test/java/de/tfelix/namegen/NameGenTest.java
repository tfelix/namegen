package de.tfelix.namegen;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;


public class NameGenTest {

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
		
		URL url = getClass().getClassLoader().getResource("test_model.dat");
		String modelFile = Paths.get(url.toURI()).toString();
		
		NameGen gen = new NameGen(modelFile);
		String name = gen.getName();
		Assert.assertNotNull(name);
		Assert.assertTrue(name.length() > 0);
	}
}
