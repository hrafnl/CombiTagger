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
package is.iclt.ctagger;

import java.io.*;
import java.util.List;


public class TaggingOutput {

public void writeOutputFile(String outputFilePath, String[] taggerOutputFileNames, String[][] taggerTags, String[] resultingTags, List<String> inputWords, boolean goldStandardSpecified, List<String> goldStandardTags, UserInterface userInterface) {
        StringWriter outputStringWriter = new StringWriter();
        PrintWriter outputPrintWriter = new PrintWriter(outputStringWriter);
        // Write header line
        outputPrintWriter.print("Input\t\t");
        for (int j = 0; j < taggerOutputFileNames.length; j++) {
            outputPrintWriter.print(j+1 + "\t\t");
        }
        if (goldStandardSpecified) {
            outputPrintWriter.print("Gold Standard\t");
        }
        outputPrintWriter.println("Combined Tagger");

        // Write results
        for (int i = 0; i < inputWords.size(); i++) {
            if (inputWords.get(i) == "")    // if empty line
                outputPrintWriter.println();
            else {
                outputPrintWriter.print(inputWords.get(i) + "\t\t");
                for (int j = 0; j < taggerOutputFileNames.length; j++) {
                    outputPrintWriter.print(taggerTags[j][i] + "\t\t");
                }
                if (goldStandardSpecified) {
                    outputPrintWriter.print(goldStandardTags.get(i) + "\t\t");
                }

                outputPrintWriter.println(resultingTags[i]);
            }
        }

        // Save output file
        if (outputFilePath != null) {
            File resultFile = new File(outputFilePath);
            if (resultFile.exists()) {
                if (resultFile.setLastModified(System.currentTimeMillis()))
                    System.out.println("touched " + outputFilePath);
                else
                    System.out.println("touch failed on " + outputFilePath);
            } else {
                try {
                    resultFile.createNewFile();
                    System.out.println("create new file " + outputFilePath);
                } catch( IOException e ) {
                    e.printStackTrace();
                    System.err.println("GUIListener: result file not saved");
                }
            }
        }

        FileWriter outputFileWriter = null;
        try {
            // TODO: Überlegen, was wir machen, wenn es die Datei schon gibt
            if (new File(outputFilePath).canWrite()) {
                outputFileWriter = new FileWriter(outputFilePath);
                outputFileWriter.write(outputStringWriter.toString());
                if (userInterface != null)
                    userInterface.writeLabelResult("Output file successfully written to " + outputFilePath + "\n");
            } else {
                if (userInterface != null)
                    userInterface.writeLabelResult("Error: No access to write output file.\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("GUIListener: writeOutputfile not written");
            if (userInterface != null)
                userInterface.writeLabelResult("Error: Output file not written.\n");
        } finally {
            try {
                if (outputFileWriter != null) {
                    outputFileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}