package de.tfelix.namegen;

import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.LocaleData;
import com.ibm.icu.util.ULocale;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Commandline interface.
 * 
 * @author Thomas Felix
 *
 */
public class NameGenCli {

	private final static Logger LOG = LoggerFactory.getLogger(NameGenCli.class);
	private final static NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);
	public final static int DEFAULT_ORDER = 3;
	private final static String ORDER_ARG = "order";
	private final static String MODE_ARG = "mode";
	private final static String LOCALE_ARG = "locale";

	public static void main(String[] args) {
		LOG.info("NameGen CLI interface");

		final Options options = setupCli();

		try {
			final CommandLineParser cmdParser = new DefaultParser();
			final CommandLine line = cmdParser.parse(options, args);
			final String outputFile = line.getOptionValue("o");
			final String inputFile = line.getOptionValue("i");
			final String mode = line.getOptionValue(MODE_ARG, "build");
			final String locale = line.getOptionValue(LOCALE_ARG, Locale.ENGLISH.getLanguage());
			final ULocale icuLocale = new ULocale(locale);
			int count = 5;  // Default
			if (line.hasOption("n")) {
				count = NUMBER_FORMAT.parse(line.getOptionValue("n")).intValue();
			}
			if(mode.equalsIgnoreCase("build")) {
				int order = DEFAULT_ORDER;
				if (line.hasOption(ORDER_ARG)) {
					order = NUMBER_FORMAT.parse(line.getOptionValue(ORDER_ARG)).intValue();
				}
				UnicodeSet alphabet = LocaleData.getExemplarSet(icuLocale, LocaleData.ES_STANDARD);
				float prior = 1f/(30f*alphabet.size());  // ~1/30 chance that generated letters will be unseen
				final NameGenGenerator gen = new NameGenGenerator(order, prior, 0.02f, icuLocale);
				gen.analyze(inputFile);
				gen.writeModel(outputFile);
				LOG.info("Trainable model was written.");
			} else if (mode.equalsIgnoreCase("generate")) {
				final NameGen gen = new NameGen(inputFile);
				Set<String> generatedNames = new HashSet<>(count);
				for(int i = 0; i < count; ++i) {
					String randomName = gen.getName();
					if(generatedNames.contains(randomName)) {
						i-= 1;
					} else {
						generatedNames.add(randomName);
					}
				}
				LOG.info(generatedNames.toString());
			} else {
				LOG.info("It was unclear which desired mode should be used for execution.");
			}
			
		} catch (ParseException ex) {
			LOG.error("Error while parsing parameter.", ex);
		} catch (java.text.ParseException ei) {
			LOG.error("Error parsing n (number of desired words).", ei);
		}
	}

	private static Options setupCli() {
		final Options opts = new Options();

		Option opt = Option.builder("o")
				.argName("outputFile")
				.longOpt("output")
				.hasArg()
				.required(false)
				.desc("Gives the path of the output file.")
				.build();

		opts.addOption(opt);

		opt = Option.builder("i")
				.argName("inputFile")
				.longOpt("input")
				.hasArg()
				.required()
				.desc("Gives the path to the input file of the names to be learned.")
				.build();
		opts.addOption(opt);

		Option mode_option = Option.builder(MODE_ARG)
				.argName("mode")
				.longOpt("mode")
				.hasArg(true)
				.required(true)
				.desc("Specify the running mode.")
				.build();
		opts.addOption(mode_option);

		Option count_option = Option.builder("n")
				.argName("count")
				.longOpt("count")
				.hasArg()
				.required(false)
				.desc("Specify the number of names you'd like to generate.")
				.type(Integer.TYPE)
				.build();
		opts.addOption(count_option);

		Option order_option = Option.builder(ORDER_ARG)
				.argName("order")
				.longOpt("order")
				.hasArg()
				.required(false)
				.desc(String.format("Order of the Markov chain (how many letters to consider). Default is %d.",
						DEFAULT_ORDER))
				.type(Integer.TYPE)
				.build();
		opts.addOption(order_option);

		Option locale = Option.builder(LOCALE_ARG)
				.argName("locale")
				.longOpt("locale")
				.hasArg(true)
				.required(false)
				.desc("The locale to use for sometimes generating letters that were not seen in the traning data.")
				.build();
		opts.addOption(locale);

		return opts;
	}

}
