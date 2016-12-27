# NameGen

This program is able to create random names based upon an input file. The names are analyzed and converted into a serialized file which can be loaded and used to get the random name.
It contains also unit tests.

## Working

Its basically a generator which consists of two function: with the NameGenInitilizer one can create the probability distribution files which are used by the NameGen in order to generate the names.
The generator uses a markov process to generate the name and imploys a Katz Backoff technique for avoiding some shortcomes of this algorithm. It pretty sticks to the description provided here: [www.roguebasin.com](http://www.roguebasin.com/index.php?title=Names_from_a_high_order_Markov_Process_and_a_simplified_Katz_back-off_scheme)

It uses Slf4J as a logging facade framework.

## Usage

TBD

## Requirements and Dependencies

* Java 1.8 or greater

## License

The library is licensed under the MIT license. See LICENSE.