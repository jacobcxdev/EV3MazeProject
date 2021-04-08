package utilities;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lejos.hardware.lcd.LCD;

/**
 * A class used to house utilities relating to the EV3's LCD.
 * 
 * @author Jacob Clayden (<a href="https://github.com/jacobcxdev">@jacobcxdev</a>)
 */
public class LCDUtilities {
	// Public Static Methods
	
	/**
	 * Draws a string on the EV3's LCD display, automatically inserting line breaks.
	 * 
	 * @param str The {@code String} to draw on the LCD display. It may be worth noting that the LCD screen has a size of 17 × 8 characters, so this string will need to be less than that to fit on the screen (not including hyphens added with line breaks).
	 */
	public static void drawLongString(String str) {
		List<String> words = new ArrayList<>(Arrays.asList(str.split(" "))); // Split `str` into a list of words.
		String[] lines = new String[LCD.DISPLAY_CHAR_DEPTH]; // Create a string array to hold strings for each line of the display.
		for (int i = 0; i < lines.length; i++) {
			lines[i] = "";
		}
		for (int i = 0; i < lines.length && !words.isEmpty(); i++) {
			String line = lines[i]; // The current line.
			String word = words.get(0); // The next word to be displayed.
			int lineLength = line.length();
			int wordLength = word.length();
			int overflow = lineLength + wordLength - LCD.DISPLAY_CHAR_WIDTH; // The number of characters the next word to be displayed would overflow the current line.
			int truncatedWordLength = wordLength - overflow - 1; // The number of characters of the current word which can fit on the current line (excluding the line break hyphen).

			if (overflow >= wordLength) { // If no more characters will fit on the current line.
				continue;
			} else if (overflow > 0) { // If the next word to be displayed overflows onto the next line.
				lines[i] = line + word.substring(0, truncatedWordLength) + (truncatedWordLength > 0 ? "-" : ""); // Truncate the next word to be displayed and add it to the current line with a hyphen before the line break.
				words.set(0, word.substring(truncatedWordLength, wordLength)); // Update the next word to be displayed to only include the remaining characters.
			} else { // If the word fully fits onto the current line.
				lines[i] = line + word + " "; // Add the next word to be displayed to the current line with a space.
				words.remove(0); // Remove this word from the list of words.
				i--; // Iterate over the current line again until it is full.
			}
		}
		for (int i = 0; i < lines.length; i++) {
			LCD.drawString(lines[i], 0, i); // Draw the lines on the EV3's LCD display.
		}
	}

}
