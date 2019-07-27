# NameGen

This program is able to create random names based upon an input file. The names are analyzed and converted into a serialized file which can be loaded and used to get the random name.
It contains also unit tests.

## Working

Its basically a generator which consists of two function: with the NameGenGenerator one can create the probability distribution files which are used by the NameGen in order to generate the names. The files can be stored and loaded for later usage.
The generator uses a Markov process to generate the name and employs a Katz Backoff technique for avoiding some shortcomes of this algorithm. It pretty sticks to the description provided here: [www.roguebasin.com](http://www.roguebasin.com/index.php?title=Names_from_a_high_order_Markov_Process_and_a_simplified_Katz_back-off_scheme)

It uses Slf4J as a logging façade framework.

## Usage

Currently only the Markov chain based model is used. Maybe the framework will be extended later to use different techniques.

### Generating the Models

First a model file must be created. In order to do so ones need a file with each name on its own line.
An example file is given inside [src/test/resources/morrow\_names.txt](./src/test/resources/morrow_names.txt), which holds some names from [Morrowind](https://de.wikipedia.org/wiki/The_Elder_Scrolls_III:_Morrowind).

One can use the API to generate models programatically − please see the docs of [NameGenGenerator.java](./src/main/java/de/tfelix/namegen/NameGenGenerator.java) or one can use the command-line with the executable `jar` of this repository.

```bash
echo "Building the jar…"
mvn clean install --file pom.xml --activate-profiles shaded
echo "Using the jar to build a name generator…"
java -jar target/namegen-1.0.1.jar --locale EN --input src/test/resources/morrow_names.txt --output EN_morrow_model.json --mode build
```

Currently the `katzbackoff` probability and the `priorProbability` are set to fixed default values.

### Running the Models

Once the name generator has been built, we can generate names:
```bash
echo "Using the trained name generator to generate some new names…"
java -jar target/namegen-1.0.1.jar --locale EN --input EN_morrow_model.json --count 7 --mode generate
```

## Requirements and Dependencies

* [Java](https://docs.oracle.com/javase/10/install/toc.htm) 1.8 or greater
* Be sure to set `JAVA_HOME`

## License

The library is licensed under the MIT license. See [LICENSE](./LICENSE).