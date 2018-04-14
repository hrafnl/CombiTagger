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

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Main class for combined tagging. Includes the main function of the program.
 * 
 * @author Verena Henrich <verenah08@ru.is>, Timo Reuter <timo08@ru.is>
 * @version 0.9
 */
public class CombinedTagging {
    private CombiningAlgorithm combiningAlgorithm = null;
    private String[] taggerFiles;          // The individual tagger files
    private String outputFile=null;        // The output file
    private List<String> inputWords = null;// The words
    private int numTaggers=0;              // Number of taggers
    private String[][] taggerTags;         // The tags of the individual taggers
    private String[] resultingTags = null; // Results of the combined tagging
    private String algorithmScriptsPath = "." + File.separator + "scripts" + File.separator;
    private String chosenAlgorithm="simpleVoting.js";
    private TaggingOutput taggingOutput;

    private void printHeader()
    {
        System.out.println("************************************************");
		System.out.println("* CombiTagger - A program for combined tagging *");
        System.out.println("*   Version 1.0                                *");
        System.out.println("* Copyright (C) 2008-2010                      *");
        System.out.println("*    V. Henrich, T. Reuter, H. Loftsson        *");
        System.out.println("************************************************");
    }

    protected void showParametersExit()
	{
		System.out.println( "Arguments: " );
        System.out.println( "-help (shows this info)" );
        System.out.println( "or: " );
        System.out.println( "-t <taggerFile1> <taggerFile2> ... <taggerFileN>" );
        //System.out.println( "-g <gold standard>" );
		System.out.println( "-o <output file>");
        System.out.println();
        System.out.println("In command line mode, CombiTagger runs simple voting");

        System.exit(0);
    }

    private void processArguments(String[] args)
    {
        boolean tagParam=false;
        boolean outParam=false;
        ArrayList<String> tlist = new ArrayList();

        for( int i = 0; i <= args.length - 1; i++ )
		{
            if( args[i].equals( "-help" ) ) {
                showParametersExit();
            }
            else if (tagParam) {
                if (!args[i].equals("-o"))
				    tlist.add(args[i]);
                else
                    tagParam=false;
            }
            else if (outParam) {
                outputFile = args[i];
                outParam = false;
            }

            if (args[i].equals( "-t" )) {
                tagParam = true;
                outParam = false;
            }
            if (args[i].equals( "-o" )) {
                outParam = true;
                tagParam = false;
            }
        }
        numTaggers = tlist.size();
        taggerFiles = (String[])tlist.toArray(new String[tlist.size()]);
    }

    private void quit(String msg) {
        System.out.println(msg);
        System.exit(0);
    }

    private void parseFiles()
    {
        FileParser fileParser = new FileParser();

        if (numTaggers > 0) {
            // Use the first tagger file to get the words
            String taggerFile = taggerFiles[0];
            File inputFile = new File(taggerFile);
            if (!inputFile.exists())
                quit("File " + taggerFile + " not found - exiting!");
            inputWords = fileParser.parseFile(inputFile, true);

            // taggerTags contains the PoS tags for each tagger
            taggerTags = new String[numTaggers][inputWords.size()];
            // Now get the PoS tags from the taggesr
            for (int i=0; i<numTaggers; i++)
            {
                taggerFile = taggerFiles[i];
                inputFile = new File(taggerFile);
                if (!inputFile.exists())
                    quit("File " + taggerFile + " not found - exiting!");

                List<String> parsingOutput = fileParser.parseTaggerInput(inputFile, null);
                taggerTags[i] = (parsingOutput).toArray(new String[parsingOutput.size()]);
                if (taggerTags[i].length != inputWords.size())
                    quit("File " + taggerFile + ": " + "Inconsistent line count - exiting!");
            }
        }
    }

    private void runCombination()
    {
        combiningAlgorithm = new CombiningAlgorithm(algorithmScriptsPath + chosenAlgorithm);
        combiningAlgorithm.updateDataValues(taggerFiles, taggerTags, numTaggers, inputWords.size(), null);
        resultingTags = combiningAlgorithm.runCombinedTaggingAlgorithm();
    }

    private void writeResults() {
        taggingOutput = new TaggingOutput();
        taggingOutput.writeOutputFile(outputFile, taggerFiles, taggerTags, resultingTags, inputWords, false, null, null);
    }
	/**
     * Main function to start the combined tagging program.
     * 
	 * @param args Command line parameters.
	 */
	public static void main(String[] args) {
        // Command line functionality added by H. Loftsson (January 2010)
        if (args.length > 0)  {
            CombinedTagging app = new CombinedTagging();
            app.printHeader();
            app.processArguments(args);
            if (app.numTaggers > 0) {
                app.parseFiles();
                app.runCombination();
                app.writeResults();
            }
        }
        else
            new UserInterface();
    }
}
