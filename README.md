# NameGen

This program is able to create random names based upon an input file. The names are analyzed and converted into a serialized file which can be loaded and used to get the random name.
It contains also unit tests.

## Working

Its basically a generator which consists of two function: with the NameGenGenerator one can create the probability distribution files which are used by the NameGen in order to generate the names. The files can be stored and loaded for later usage.
The generator uses a markov process to generate the name and employs a Katz Backoff technique for avoiding some shortcomes of this algorithm. It pretty sticks to the description provided here: [www.roguebasin.com](http://www.roguebasin.com/index.php?title=Names_from_a_high_order_Markov_Process_and_a_simplified_Katz_back-off_scheme)

It uses Slf4J as a logging facade framework.

## Usage

Currently only the markov chain based model is used. Maybe the framework is extended later to use different techniques.

### Generating the models

First a model file must be created. In order to do so ones need a file with names provided as a single word separated by a newline (\n).
An example file is given inside src/test/resources/morrow\_names.txt which holds some names from (Morrowind)[https://de.wikipedia.org/wiki/The_Elder_Scrolls_III:_Morrowind].

One can use the API to generate the models programatically, please see the docs of NameGenGenerator.java or one can use the commandline with the executable jar of this repository.

java -jar namegen.jar /path/to/infile /path/to/outfile [-o=3 -p=0.001]

The -o gives the order of the markov model, the default of 3 is quite okay.

-p gives the prior probability and should be higher the fewer the training data is. For 50+ names used for training a value of 0.001 is okay and the default.

## Requirements and Dependencies

* Java 1.8 or greater

## License

The library is licensed under the MIT license. See LICENSE.