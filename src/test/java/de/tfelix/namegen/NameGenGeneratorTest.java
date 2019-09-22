package de.tfelix.namegen;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import com.ibm.icu.util.ULocale;
import org.junit.Test;

public class NameGenGeneratorTest {

	@Test(expected = IllegalArgumentException.class)
	public void analyze_nullArg_throws() {
		
		NameGenGenerator gen = getGen();
		gen.analyze(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void analyze_filesDoesNotExist_throws() {
		
		NameGenGenerator gen = getGen();
		gen.analyze("C:\bla\blubber\1235358xhsg.dat");
	}

	@Test
	public void analyze_file_ok() throws URISyntaxException {
		
		URL url = getClass().getClassLoader().getResource("morrow_names.txt");
		File f = Paths.get(url.toURI()).toFile();
		NameGenGenerator gen = getGen();
		gen.analyze(f.getAbsolutePath());
	}

	@Test(expected = IllegalArgumentException.class)
	public void analyze_writeNullFile_throws() throws Exception {
		
		URL url = getClass().getClassLoader().getResource("morrow_names.txt");
		File f = Paths.get(url.toURI()).toFile();
		NameGenGenerator gen = getGen();
		gen.analyze(f.getAbsolutePath());
		gen.writeModel(null);
	}

	private NameGenGenerator getGen() {
		
		return new NameGenGenerator(3, 0.01f, 0.03f, ULocale.ENGLISH);
	}
}
