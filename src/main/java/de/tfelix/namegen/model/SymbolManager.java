package de.tfelix.namegen.model;

/**
 * Manages the end and start symbols. This is in a single class because we
 * access it from different locations.
 * 
 * @author Thomas Felix
 *
 */
final class SymbolManager {

	private final static Character DELIMITER = '#';

	/**
	 * Start symbol of the tokens of the given order.
	 * 
	 * @param order
	 *            The order to get the start symbol for.
	 * @return The start symbol.
	 */
	public static String getStartSymbol(int order) {
		if(order <= 0) {
			throw new IllegalArgumentException("Order must be positive.");
		}
		return new String(new char[order]).replace("\0", DELIMITER.toString());
	}

	/**
	 *Gets the end symbol.
	 * @return The end symbol.
	 */
	public static Character getEndSymbol() {
		return DELIMITER;
	}
}
