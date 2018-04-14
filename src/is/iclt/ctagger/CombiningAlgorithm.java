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

import javax.script.*;

import org.eclipse.swt.widgets.Group;

/**
 * This class represents a combined algorithm method, i.e. simple voting.
 * The algorithm is initialized by a java script file and ran with a script 
 * engine. It can specify optional additional gui elements to input algorithm 
 * parameters (method updateAlgorithmSpecificGUI). The proper algorithm 
 * runs by invoking method runCombinedTaggingAlgorithm.
 * 
 * @author Verena Henrich <verenah08@ru.is>, Timo Reuter <timo08@ru.is>
 * @version 0.9
 */
public class CombiningAlgorithm {
    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private ScriptEngineManager scriptEngineManager;
    private ScriptEngine scriptEngine;
    private String[] resultingTags;
    private String fileContent;
    
    /**
     * Class constructor initializes the script engine to invoke the corresponding
     * java script.
     * 
     * @param filepath The java script file which represents the algorithm.
     */
    public CombiningAlgorithm(String filepath) {
        System.out.println("CombiningAlgorithm: " + filepath);
        scriptEngineManager = new ScriptEngineManager();
        scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
        try {
            fileReader = new FileReader(filepath);

            bufferedReader = new BufferedReader(fileReader);
            String row = bufferedReader.readLine();
            fileContent = "";
            while (row != null) {
                if (row.split("//").length > 0) {
                    row = row.split("//")[0];
                }
                fileContent = fileContent.concat(row + " ");
                row = bufferedReader.readLine();
            }
            scriptEngine.eval(fileContent);
        } catch (ScriptException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Necessary java objects are made available to the java script.
     * 
     * @param taggerOutputFileNames Contains the tagger output file names.
     * @param tagArray Two dimensional string array which contains for every 
     * tagger output file the corresponding found tags.
     * @param numberOfTaggerOutputs The number of tagger output files specified. 
     * First dimension of the tag array.
     * @param numberOfWords The number of words in the input text. Second dimension
     * of the tag array.
     * @param algorithmSpecificsGroup The java gui object which can optionally
     * be modified to input algorithm specific parameters.
     */
    public void updateDataValues(String[] taggerOutputFileNames, String[][] tagArray, int numberOfTaggerOutputs, int numberOfWords, Group algorithmSpecificsGroup) {
        System.out.println("CombiningAlgorithm: updateDataValues()");
        resultingTags = new String[numberOfWords];
        
        scriptEngine.put("tagArray", tagArray);
        scriptEngine.put("numberOfTaggerOutputs", numberOfTaggerOutputs);
        scriptEngine.put("numberOfWords", numberOfWords);
        scriptEngine.put("resultingTags", resultingTags);
        scriptEngine.put("taggerOutputFileNames", taggerOutputFileNames);
        scriptEngine.put("algorithmSpecificsGroup", algorithmSpecificsGroup);
    }
    
    /**
     * This method invokes the java script function createAlgorithmSpecificGUI which
     * can optionally modify the gui of the program i.e. to allow algorithm specific
     * parameter inputs.
     * 
     * @param taggerOutputFileNames Contains the tagger output file names.
     * @param numberOfTaggerOutputs The number of tagger output files specified. 
     * First dimension of the tag array.
     * @param algorithmSpecificsGroup The java gui object which can optionally
     * be modified to input algorithm specific parameters.
     */
    public void updateAlgorithmSpecificGUI(String[] taggerOutputFileNames, int numberOfTaggerOutputs, Group algorithmSpecificsGroup) {
        scriptEngine.put("taggerOutputFileNames", taggerOutputFileNames);
        scriptEngine.put("numberOfTaggerOutputs", numberOfTaggerOutputs);
        scriptEngine.put("algorithmSpecificsGroup", algorithmSpecificsGroup);
        System.out.println("CombiningAlgorithm: updateAlgorithmSpecificGUI");
        try {
            Invocable invocableEngine = (Invocable) scriptEngine;
            invocableEngine.invokeFunction("createAlgorithmSpecificGUI");
        } catch (ScriptException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method invokes the java script function runCombinedTaggingAlgorithm
     * which implements the proper combined tagging algorithm. This method represents
     * an algorithm which compares the tags of several tagger outputs and computes
     * new tags in the way specified by the algorithm i.e. simple voting.
     * 
     * @return The resulting tags are returned.
     */
    public String[] runCombinedTaggingAlgorithm() {
        System.out.println("CombiningAlgorithm: runAlgorithm");
        try {
            Invocable invocableEngine = (Invocable) scriptEngine;
            invocableEngine.invokeFunction("runCombinedTaggingAlgorithm");
        } catch (ScriptException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        return resultingTags;
    }
    
    /**
     * This method behaves like a destructor. It closes the open stream which was used
     * to read the java script file.
     *
     */
    public void closeStream() {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
