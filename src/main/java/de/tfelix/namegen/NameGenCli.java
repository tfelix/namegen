package de.tfelix.namegen;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Commandline interface.
 * 
 * @author Thomas Felix
 *
 */
public class NameGenCli {

	private final static Logger LOG = LoggerFactory.getLogger(NameGenCli.class);

	public static void main(String[] args) {
		LOG.info("NameGen CLI interface");

		final Options options = setupCli();

		try {
			// parse the commandline
			final CommandLineParser cmdParser = new DefaultParser();
			final CommandLine line = cmdParser.parse(options, args);
			
			// Find input and output file.
			final String outFile = line.getOptionValue("o");
			final String inFile = line.getOptionValue("i");
			
			final NameGenGenerator gen = new NameGenGenerator(3, 0.001f, 0.02f);
			gen.analyze(inFile);
			gen.writeModel(outFile);
			LOG.info("Model was written.");
			
		} catch (ParseException ex) {
			LOG.error("Error while parsing parameter.", ex);
		}
	}

	private static Options setupCli() {
		final Options opts = new Options();

		Option opt = Option.builder("o")
				.argName("outFile")
				.longOpt("out")
				.hasArg()
				.required()
				.desc("Gives the path of the output file.")
				.build();

		opts.addOption(opt);

		opt = Option.builder("i")
				.argName("inFile")
				.longOpt("in")
				.hasArg()
				.required()
				.desc("Gives the path to the input file of the names to be learned.")
				.build();
		opts.addOption(opt);

		return opts;
	}

}
