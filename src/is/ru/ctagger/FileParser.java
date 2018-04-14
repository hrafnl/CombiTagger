/**
 * Copyright(C) 2008
 * Verena Henrich <verenah08@ru.is>, Timo Reuter <timo08@ru.is>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  US
 */
package is.ru.ctagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses files.
 * 
 * @author Verena Henrich <verenah08@ru.is>, Timo Reuter <timo08@ru.is>
 * @version 0.9
 */
public class FileParser {
	private String row = null;
	private FileReader fileReader = null;
	private BufferedReader bufferedReader = null;
    private static String tagNotFoundSymbol = "##";
    private int numLines;
    private int nonEmptyLines;

    public FileParser() {
        init();
    }
    private void init() {
        numLines = 0;
        nonEmptyLines = 0;
    }

    public int getNumLines() { return numLines;}
    public int getNonEmptyLines() { return nonEmptyLines;}
    /**
     * This method parses an input file and returns the containing words.
     * 
     * @param textFile Input file which is parsed. This file needs to contain one word 
     *                 per line.
     * @return The words that are contained in the input file are returned.
     */
	public List<String> parseFile(File textFile, boolean onlyFirstCol) {
        System.out.println("parse file: " + textFile);

        init();
		List<String> wordsInFile = new ArrayList<String>();
		try {
			fileReader = new FileReader(textFile);
		
			bufferedReader = new BufferedReader(fileReader);
	
			row = bufferedReader.readLine();
			// Iterate over each row
			while (row != null) {
                numLines++;
				row = row.replaceAll("^[ \t]+", "");
                if (row.length() > 0) {
                    nonEmptyLines++;
                    if (onlyFirstCol) {
                        String[] columns = row.split("[ \t]+");
                        wordsInFile.add(columns[0]);
                    }
                    else
                        wordsInFile.add(row);
                }
                 else // row can be empty
                    wordsInFile.add("");
				row = bufferedReader.readLine();
			}
			
			bufferedReader.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return wordsInFile;
    }

    /**
     * A tagger input file is parsed. Every entry (that corresponds to a word) gets
     * related to one tag out of the given tagset.
     * 
     * @param textFile Input file from a tagger which is parsed. This file need to 
     * contain one "entry" per line. These entries should start with the specified
     * word (optional) followed by the corresponding tag and optional other
     * contents.
     * @param posTagset This is the tagset which specifies the tags that are searched
     * in the input file.
     * @return A list is returned. Each entry includes a tag and 
     * corresponds to one word of the input file.
     */
    public List<String> parseTaggerInput(File textFile, POSTagset posTagset) {
        System.out.println("parse tagger file: " + textFile);
        init();
        List<String> wordsInFile = new ArrayList<String>();
        try {
            fileReader = new FileReader(textFile);
        
            bufferedReader = new BufferedReader(fileReader);
    
            row = bufferedReader.readLine();

            String longestMatch = "";

            
            if (posTagset != null) {
                while (row != null) {
                    numLines++;
    				row = row.replaceAll("^[ \t]+", "");
    				if (row.length() > 2)
                    {
                        nonEmptyLines++;
                        // Search for part-of-speech tags
                        for (String tag : posTagset.getTags()) {
                            if (row.contains(tag) && longestMatch.length() < tag.length()) {
                                longestMatch = tag;
                            }
                        }
                        if (longestMatch == "") {
                            longestMatch = tagNotFoundSymbol;
                        }
                        wordsInFile.add(longestMatch);
                        longestMatch = "";
                    }
                    else // row can be empty
                        wordsInFile.add("");
                    row = bufferedReader.readLine();
                }
            } 
            else 
            {
                while (row != null) 
                {
                    numLines++;
    				row = row.replaceAll("^[ \t]+", "");
    				if (row.length() > 2)
                    {
                        nonEmptyLines++;
                        String[] actualWordTag = row.split("[ \t]+");
                        if (actualWordTag.length < 2) {
                            System.out.println("Missing tag for word: " + actualWordTag[0]);
                            wordsInFile.add(tagNotFoundSymbol);
                        }
                        else
                            wordsInFile.add(actualWordTag[1]);
                    }
                    else // row can be empty
                        wordsInFile.add("");
                    row = bufferedReader.readLine();
                }
            }
            
            bufferedReader.close(); 
        } catch (FileNotFoundException e) {
			e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}

		return wordsInFile;
	}

    public static String getTagNotFoundSymbol() {
        return tagNotFoundSymbol;
    }
}
