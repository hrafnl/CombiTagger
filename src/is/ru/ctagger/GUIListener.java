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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author Verena Henrich <verenah08@ru.is>, Timo Reuter <timo08@ru.is>
 * @version 0.9
 */
public class GUIListener implements SelectionListener {
    private UserInterface userInterface = null;
    private String chosenAlgorithm = null;
    private CombiningAlgorithm combiningAlgorithm = null;
    private String[] taggerOutputFileNames = null;
    private String[] resultingTags = null;
    private String[][] foundTags = null;
    private List<String> inputWords = null;
    private List<String> goldStandardTags = null;
    private String chosenTagset = null;
    private POSTagset posTagset = null;
    private int statisticAllMatch = 0; // All match (gold standard, combined tagging result and all taggers)
    private List<Integer> statisticAgreeingTaggers = null; // Number of taggers which agree (order: all agree, all-1 agree, all-2, ..., all-(all/2))
    private int numberOfComparisons = 0; // "Size" of statisticAgreeingTaggers
    private int statisticOneDifferenceToGoldstandard = 0; // One tagger differs from gold standard
    private int statisticOnlyOneMatchWithGoldstandard = 0; // Only one tagger matches gold standard
    private int statisticNoMatchWithGoldstandard = 0; // No tagger matches gold standard
    private int statisticMatchGoldstandardResult = 0; // Combined tagging result matches gold standard
    private List<Integer> statisticMatchGoldstandardTagger = null; // Specific tagger matches gold standard
    private boolean goldStandardSpecified = false;
    FileParser fileParser;
    TaggingOutput taggingOutput;  // Tagging file output


    public List<String> getWords() {
        return inputWords;
    }

    public String[][] getFoundTags() {
        return foundTags;
    }

    public GUIListener(UserInterface mainUserInterface) {
        userInterface = mainUserInterface;
        taggerOutputFileNames = new String[1];
        taggerOutputFileNames[0] = "";
        taggingOutput = new TaggingOutput();

    }

    /*
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected(SelectionEvent arg0) {
    }

    /*
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent event) {
        String callee = (String) event.widget.getData("name");
        
        // Chosen algorithm script has changed
        if ((String) event.widget.getData("algorithm") != null) {
            setChosenAlgorithm((String) event.widget.getData("algorithm"));
        }
        
        if ((String) event.widget.getData("tagset") != null) {
            chosenTagset = (String) event.widget.getData("tagset");
        }
        
        if ("buttonInputFile".equals(callee)) {
            String[] selectedFiles = createFileDialog(SWT.OPEN, "Select Wordlist");
            if (selectedFiles.length > 0) {
                userInterface.getComboInputFile().add(selectedFiles[0]);
                userInterface.getComboInputFile().setText(selectedFiles[0]);
            }
        } else if ("buttonGoldstandardFile".equals(callee)) {
            String[] selectedFiles = createFileDialog(SWT.OPEN, "Select Gold Standard");
            if (selectedFiles.length > 0) {
                userInterface.getComboGoldstandardFile().add(selectedFiles[0]);
                userInterface.getComboGoldstandardFile().setText(selectedFiles[0]);
            }
        } else if ("buttonAddTaggerFile".equals(callee)) {
            String[] selectedFile = createFileDialog(SWT.MULTI, "Add Tagger Output File");
            
            // Add files to list
            for (int i = 0; i < selectedFile.length; i++) {
                // Ask wheather the selected file is already in the list
                if (userInterface.getListTaggerFiles().indexOf(selectedFile[i]) == -1) {
                    userInterface.getListTaggerFiles().add(selectedFile[i]);
                }
            }
            updateTaggerOutputFileNames();
        } else if ("buttonRemoveTaggerFile".equals(callee)) {
            if (userInterface.getListTaggerFiles().getSelectionIndex() >= 0) {
                userInterface.getListTaggerFiles().remove(userInterface.getListTaggerFiles().getSelectionIndex());                
            }
            updateTaggerOutputFileNames();
        } else if ("buttonRemoveAllTaggerFiles".equals(callee)) {
            userInterface.getListTaggerFiles().removeAll();
            updateTaggerOutputFileNames();
        } else if ("checkBoxInputFile".equals(callee)) {
            if (userInterface.getCheckBoxInputFile().getSelection()) {
                userInterface.getButtonInputFile().setEnabled(true);
                userInterface.getComboInputFile().setEnabled(true);
            } else {
                userInterface.getButtonInputFile().setEnabled(false);
                userInterface.getComboInputFile().setEnabled(false);                
            }
        } else if ("checkBoxGoldstandardFile".equals(callee)) {
            if (userInterface.getCheckBoxGoldstandardFile().getSelection()) {
                userInterface.getButtonGoldstandardFile().setEnabled(true);
                userInterface.getComboGoldstandardFile().setEnabled(true);
            } else {
                userInterface.getButtonGoldstandardFile().setEnabled(false);
                userInterface.getComboGoldstandardFile().setEnabled(false);                
            }
        } else if ("checkBoxOutputFile".equals(callee)) {
            if (userInterface.getCheckBoxOutputFile().getSelection()) {
                userInterface.getButtonOutput().setEnabled(true);
                userInterface.getComboOutput().setEnabled(true);
            } else {
                userInterface.getButtonOutput().setEnabled(false);
                userInterface.getComboOutput().setEnabled(false);
            }
        } else if ("buttonTagset".equals(callee)) {
            FileDialog fileDialog = new FileDialog(userInterface.getSShell(), SWT.OPEN);
            fileDialog.setText("Open");
            String selectedFile = fileDialog.open();
            String fileName = fileDialog.getFileName();
            // Save new tagset file
            if (selectedFile != null) {
                File newTagsetFile = new File(userInterface.getTagsetsPath() + fileName);
                if (newTagsetFile.exists()) {
                    if (newTagsetFile.setLastModified(System.currentTimeMillis()))
                        System.out.println("touched " + selectedFile);
                    else
                        System.out.println("touch failed on " + selectedFile);
                } else {
                    try {
                        newTagsetFile.createNewFile();
                        System.out.println("create new file " + selectedFile);
                        
                        InputStream fis = new FileInputStream(selectedFile);
                        OutputStream fos = new FileOutputStream(newTagsetFile);
                        try {
                            byte[] buffer = new byte[0xFFFF];
                            for (int len; (len = fis.read(buffer)) != -1; ) {
                                fos.write( buffer, 0, len );
                            }
                        } finally {
                            if (fis != null) {
                                fis.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        }
                        userInterface.updateGroupTagsets();
                    } catch(IOException e) {
                        System.err.println("GUIListener: tagset not added");
                        newTagsetFile.delete();
                    }
                }
            }
        } else if ("checkboxTagset".equals(callee)) {
            if (userInterface.getCheckBoxTagset().getSelection() == true) {
                // Disable all radio buttons for selecting a tagset
                for (int i = 0; i < userInterface.getRadioButtonTagsets().size(); i++) {
                    userInterface.getRadioButtonTagsets().get(i).setEnabled(false);
                }
            } else {
                // Enable all radio buttons for selcting a tagset
                for (int i = 0; i < userInterface.getRadioButtonTagsets().size(); i++) {
                    userInterface.getRadioButtonTagsets().get(i).setEnabled(true);
                }
            }
        } else if ("buttonOutput".equals(callee)) {
            String[] selectedFiles = createFileDialog(SWT.SAVE, "Specify Output File");
            if (selectedFiles.length > 0) {
                userInterface.getComboOutput().add(selectedFiles[0]);
                userInterface.getComboOutput().setText(selectedFiles[0]);
            }
        } else if ("tabFolder".equals(callee)) {
            changeTabFolder();
        } else if ("buttonAlgorithm".equals(callee)) {
            FileDialog fileDialog = new FileDialog(userInterface.getSShell(), SWT.OPEN);
            fileDialog.setText("Open");
            String[] filterExt = { "*.js", "*.*" };
            fileDialog.setFilterExtensions(filterExt);
            String selectedFile = fileDialog.open();
            String fileName = fileDialog.getFileName();
            // Save new script file
            if (selectedFile != null) {
                File newScriptFile = new File(userInterface.getAlgorithmScriptsPath() + fileName);
                if (newScriptFile.exists()) {
                    if (newScriptFile.setLastModified(System.currentTimeMillis()))
                        System.out.println("touched " + selectedFile);
                    else
                        System.out.println("touch failed on " + selectedFile);
                } else {
                    try {
                        newScriptFile.createNewFile();
                        System.out.println("create new file " + selectedFile);
                        
                        InputStream fis = new FileInputStream(selectedFile);
                        OutputStream fos = new FileOutputStream(newScriptFile);
                        try {
                            byte[] buffer = new byte[0xFFFF];
                            for (int len; (len = fis.read(buffer)) != -1; ) {
                                fos.write( buffer, 0, len );
                            }
                        } finally {
                            if (fis != null) {
                                fis.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        }
                        userInterface.updateGroupAlgorithmScripts();
                    } catch(IOException e) {
                        System.err.println("GUIListener: script not added");
                        newScriptFile.delete();
                    }
                }
            }
        } else if ("comboHighlightOutput".equals(callee)) {
            String highlightCriterion;
            int selectionIndex = userInterface.getComboHighlightOutput().getSelectionIndex();
            
            // Specify highlightCriterion with the aid of the selected index of comboHighlightOutput
            if (selectionIndex == 0) {
                highlightCriterion = "none";
            } else if (selectionIndex == 1) {
                highlightCriterion = "allAreTheSame";
            } else if (selectionIndex > 1 && selectionIndex < 2 + numberOfComparisons) {
                highlightCriterion = taggerOutputFileNames.length - selectionIndex + 2 + "TaggersAgree";
            } else if (selectionIndex == 2 + numberOfComparisons) {
                highlightCriterion = "oneDifferenceToGoldstandard";
            } else if (selectionIndex == 3 + numberOfComparisons) {
                highlightCriterion = "onlyOneMatchWithGoldstandard";
            } else if (selectionIndex == 4 + numberOfComparisons) {
                highlightCriterion = "noMatchWithGoldstandard";
            } else if (selectionIndex == 5 + numberOfComparisons) {
                highlightCriterion = "matchGoldstandardResult";
            } else {
                highlightCriterion = taggerOutputFileNames[selectionIndex - 6 - numberOfComparisons];
            }

            // Go through the whole table
            for (int i = 0; i < inputWords.size(); i++) {
                // Highlight all rows which suffice highlightCriterion
                if ("1".equals(userInterface.getTableItemsOutput().get(i).getData(highlightCriterion))) {
                    userInterface.getTableItemsOutput().get(i).setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION));
                } else {
                    userInterface.getTableItemsOutput().get(i).setBackground(null);
                }
            }
            userInterface.getTableOutput().deselectAll();
        } else if ("buttonNext".equals(callee)) {
            if (userInterface.getTabFolder().getSelectionIndex() == 3) {
                startCombinedTagging();
            } else {
                userInterface.getTabFolder().setSelection(userInterface.getTabFolder().getSelectionIndex() + 1);
            }
            changeTabFolder();
        } else if ("buttonPrevious".equals(callee)) {
            userInterface.getTabFolder().setSelection(userInterface.getTabFolder().getSelectionIndex() - 1);
            changeTabFolder();
        } else if ("buttonSaveResult".equals(callee)) {
            FileDialog fileDialog = new FileDialog(userInterface.getSShell(), SWT.SAVE);
            fileDialog.setText("Save Result");
            String resultFilePath = fileDialog.open();
            
            StringWriter outputStringWriter = new StringWriter();  
            PrintWriter outputPrintWriter = new PrintWriter(outputStringWriter);
            
            // Write results
            for (int i = 0; i < inputWords.size(); i++) {
                //outputPrintWriter.print(inputWords.get(i) + " ");
                outputPrintWriter.print(inputWords.get(i) + "\t");
                outputPrintWriter.println(userInterface.getTableItemsOutput().get(i).getText(userInterface.getTableColumnsOutput().size() - 1));
            }

            // Save result file
            // TODO: Rechtefrage betrachten
            if (resultFilePath != null) {
                File resultFile = new File(resultFilePath);
                if (resultFile.exists()) {
                    if (resultFile.setLastModified(System.currentTimeMillis()))
                        System.out.println("touched " + resultFilePath);
                    else
                        System.out.println("touch failed on " + resultFilePath);
                } else {
                    try {
                        resultFile.createNewFile();
                        System.out.println("create new file " + resultFilePath);
                    } catch( IOException e ) {
                        e.printStackTrace();
                        System.err.println("GUIListener: result file not saved");
                    }
                }
            
                FileWriter outputFileWriter = null;
                try {
                    // TODO: Überlegen, was wir machen, wenn es die Datei schon gibt
                    if (new File(resultFilePath).canWrite()) {
                        outputFileWriter = new FileWriter(resultFilePath);
                        outputFileWriter.write(outputStringWriter.toString());
                        System.out.println("Result file successfully written to " + resultFilePath);
                    } else {
                        System.err.println("Error: No access to write result file.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("GUIListener: result file not written");
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
        } else if ("buttonSaveGoldStandard".equals(callee)) {
            FileDialog fileDialog = new FileDialog(userInterface.getSShell(), SWT.SAVE);
            fileDialog.setText("Save Gold Standard");
            String resultFilePath = fileDialog.open();
            
            StringWriter outputStringWriter = new StringWriter();  
            PrintWriter outputPrintWriter = new PrintWriter(outputStringWriter);
            
            // Write gold standard values
            for (int i = 0; i < inputWords.size(); i++) {
                outputPrintWriter.print(inputWords.get(i) + " ");
                outputPrintWriter.println(userInterface.getTableItemsOutput().get(i).getText(userInterface.getTableColumnsOutput().size() - 2));
            }

            // Save gold standard file
            // TODO: Rechtefrage betrachten
            if (resultFilePath != null) {
                File resultFile = new File(resultFilePath);
                if (resultFile.exists()) {
                    if (resultFile.setLastModified(System.currentTimeMillis()))
                        System.out.println("touched " + resultFilePath);
                    else
                        System.out.println("touch failed on " + resultFilePath);
                } else {
                    try {
                        resultFile.createNewFile();
                        System.out.println("create new file " + resultFilePath);
                    } catch( IOException e ) {
                        e.printStackTrace();
                        System.err.println("GUIListener: gold standard file not saved");
                    }
                }
            
                FileWriter outputFileWriter = null;
                try {
                    // TODO: Überlegen, was wir machen, wenn es die Datei schon gibt
                    if (new File(resultFilePath).canWrite()) {
                        outputFileWriter = new FileWriter(resultFilePath);
                        outputFileWriter.write(outputStringWriter.toString());
                        System.out.println("Gold standard file successfully written to " + resultFilePath);
                    } else {
                        System.err.println("Error: No access to write gold standard file.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("GUIListener: gold standard file not written");
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
    }

    private String[] createFileDialog(int type, String title) {
        FileDialog fileDialog = new FileDialog(userInterface.getSShell(), type);
        fileDialog.setText(title);
        fileDialog.open();
        String filepath = fileDialog.getFilterPath();
        String[] selectedFiles = new String[fileDialog.getFileNames().length];
        String[] fileNames = fileDialog.getFileNames();
        for (int i = 0; i < fileNames.length; i++) {
            selectedFiles[i] = filepath + File.separatorChar + fileNames[i];
        }
        return selectedFiles;
    }

    private void changeTabFolder() {
        if (userInterface.getTabFolder().getSelectionIndex() == 0) {
            if (userInterface.getButtonPrevious() != null) {
                userInterface.getButtonNext().setText("Next");
                userInterface.getButtonPrevious().setVisible(false);
                userInterface.getButtonNext().setVisible(true);
            }
        } else if (userInterface.getTabFolder().getSelectionIndex() == 3) {
            userInterface.getButtonNext().setText("Start");
            userInterface.getButtonPrevious().setVisible(true);
            userInterface.getButtonNext().setVisible(true);
        } else if (userInterface.getTabFolder().getSelectionIndex() == 4) {
            userInterface.getButtonNext().setVisible(false);
            userInterface.getButtonPrevious().setVisible(false);
        } else {
            userInterface.getButtonNext().setText("Next");
            userInterface.getButtonPrevious().setVisible(true);
            userInterface.getButtonNext().setVisible(true);
        }
    }

    private void writeLabelResult(String text, int statistics, int wordCount, NumberFormat numberFormat ) {
         userInterface.writeLabelResult(text + statistics + " of " + wordCount + " words (" + numberFormat.format(((float)statistics)/((float)wordCount)*100) + "%)\n");
    }

    private void writeStatisticalResults(int wordCount) {
        // Write statistical results to result tab
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        userInterface.writeLabelResult("Statistical results:\n");

        if (goldStandardSpecified == true)
            writeLabelResult(" - All taggers, gold standard and combined tagging result match: ", statisticAllMatch, wordCount, numberFormat);
        else
            writeLabelResult(" - All taggers and combined tagging result match: ", statisticAllMatch, wordCount, numberFormat);
        writeLabelResult(" - All " + taggerOutputFileNames.length + " taggers agree: ", statisticAgreeingTaggers.get(0), wordCount, numberFormat);

        for (int i = 1; i < numberOfComparisons; i++)
            writeLabelResult(" - " + (taggerOutputFileNames.length - i) + " of " + taggerOutputFileNames.length + " taggers agree: ", statisticAgreeingTaggers.get(i), wordCount, numberFormat);

        if (goldStandardSpecified == true) {
            writeLabelResult(" - One difference to gold standard: ", statisticOneDifferenceToGoldstandard, wordCount, numberFormat);
            writeLabelResult(" - Only one match with gold standard: ", statisticOnlyOneMatchWithGoldstandard, wordCount, numberFormat);
            writeLabelResult(" - No match with gold standard: ", statisticNoMatchWithGoldstandard, wordCount, numberFormat);
            writeLabelResult(" - Combined tagging result matches gold standard in ", statisticMatchGoldstandardResult, wordCount, numberFormat);
            for (int i = 0; i < taggerOutputFileNames.length; i++)
                writeLabelResult(" - " + taggerOutputFileNames[i] + " matches gold standard: ", statisticMatchGoldstandardTagger.get(i), wordCount, numberFormat);
         }
    }

    // Computes the statistical results and prepares corresponding info in output table
    private void computeStatisticalResults() {
        statisticAllMatch = 0;
        statisticOneDifferenceToGoldstandard = 0;
        statisticOnlyOneMatchWithGoldstandard = 0;
        statisticNoMatchWithGoldstandard = 0;
        statisticMatchGoldstandardResult = 0;
        if (userInterface.getCheckBoxOutputTable().getSelection()) {
            userInterface.updateResultTable();
        }
        // Set text of output table cells and calculate statistics
        String allAreTheSame;
        String oneDifferenceToGoldstandard;
        String onlyOneMatchWithGoldstandard;
        String noMatchWithGoldstandard;
        String matchGoldstandardResult;
        List<String> agreeingTaggers = new ArrayList<String>();
        statisticAgreeingTaggers = new ArrayList<Integer>();
        numberOfComparisons = taggerOutputFileNames.length - taggerOutputFileNames.length/2;
        List<String> matchGoldstandardTagger = new ArrayList<String>();
        statisticMatchGoldstandardTagger = new ArrayList<Integer>();

        for (int i = 0; i < taggerOutputFileNames.length; i++) {
            if (i < numberOfComparisons) {
                agreeingTaggers.add("0");
                statisticAgreeingTaggers.add(0);
            }
            matchGoldstandardTagger.add("0");
            statisticMatchGoldstandardTagger.add(0);
        }

        for (int i = 0; i < inputWords.size(); i++) {
            if (inputWords.get(i).equals("")) {
                continue; // Don't calculate using empty lines
            }
            allAreTheSame = "1";
            // Initialize that all taggers agree (first value is 1 in agreeingTaggers)
            agreeingTaggers.set(0, "1");
            for (int d = 1; d < numberOfComparisons; d++) {
                agreeingTaggers.set(d, "0"); // All others values are initialized with false (0)
            }
            oneDifferenceToGoldstandard = "0";
            onlyOneMatchWithGoldstandard = "0";
            noMatchWithGoldstandard = "0";
            matchGoldstandardResult = "0";
            if (userInterface.getCheckBoxOutputTable().getSelection() == true) {
                // Show line number in first column of output table
                userInterface.getTableItemsOutput().get(i).setText(0, Integer.toString(i + 1));
                // Show input words in second column of output table
                userInterface.getTableItemsOutput().get(i).setText(1, inputWords.get(i));
            }
            for (int c = 0; c < taggerOutputFileNames.length; c++) {
                if (userInterface.getCheckBoxOutputTable().getSelection() == true) {
                    // Show tagger outputs in output table, start in third column
                    userInterface.getTableItemsOutput().get(i).setText(c+2, foundTags[c][i]);
                }
                if (c > 0 && !foundTags[c][i].equals(foundTags[c-1][i])) {
                    allAreTheSame = "0";
                    agreeingTaggers.set(0, "0"); // Not all taggers agree
                }
            }

            // If not all taggers agree
            if (agreeingTaggers.get(0).equals("0")) {
                int agreeings = 1;
                List<String> agreeingTag = new ArrayList<String>();
                agreeingTag.add(foundTags[0][i]);
                List<Integer> agreeingTagCount = new ArrayList<Integer>();
                agreeingTagCount.add(1);
                for (int c = 1; c < taggerOutputFileNames.length; c++) {
                    boolean found = false;
                    for (int d = 0; d < agreeingTag.size(); d++) {
                        if (foundTags[c][i].equals(agreeingTag.get(d))) {
                            agreeingTagCount.set(d, agreeingTagCount.get(d) + 1);
                            found = true;
                        }
                    }
                    if (!found) {
                        agreeingTag.add(foundTags[c][i]);
                        agreeingTagCount.add(1);
                    }
                }

                // Look for the highest count of tagger agreeings
                for (int d = 0; d < agreeingTag.size(); d++) {
                    if (agreeingTagCount.get(d) > agreeings) {
                        agreeings = agreeingTagCount.get(d);
                    }
                }

                if (agreeings > taggerOutputFileNames.length - numberOfComparisons) {
                    agreeingTaggers.set(taggerOutputFileNames.length - agreeings, "1");
                }
            }

            if (goldStandardSpecified == true) {
                if (userInterface.getCheckBoxOutputTable().getSelection() == true) {
                    // Show gold standard in second last column of output table
                    userInterface.getTableItemsOutput().get(i).setText(userInterface.getTableColumnsOutput().size() - 2, goldStandardTags.get(i));
                    userInterface.getTableItemsOutput().get(i).setBackground(userInterface.getTableColumnsOutput().size() - 2, null);
                }

                if (goldStandardTags.get(i).equals(resultingTags[i])) {
                    matchGoldstandardResult = "1";
                    statisticMatchGoldstandardResult++;
                }
                if (!foundTags[0][i].equals(goldStandardTags.get(i))) {
                    allAreTheSame = "0";
                }
                if (userInterface.getCheckBoxOutputTable().getSelection()) {
                    userInterface.getTableItemsOutput().get(i).setData("matchGoldstandardResult", matchGoldstandardResult);
                }
                int differenceCounter = 0;
                for (int c = 0; c < taggerOutputFileNames.length; c++) {
                    matchGoldstandardTagger.set(c, "0");
                    if (foundTags[c][i].equals(goldStandardTags.get(i))) {
                        matchGoldstandardTagger.set(c, "1");
                        statisticMatchGoldstandardTagger.set(c, statisticMatchGoldstandardTagger.get(c) + 1);
                    } else {
                        differenceCounter++;
                    }
                    if (userInterface.getCheckBoxOutputTable().getSelection()) {
                        userInterface.getTableItemsOutput().get(i).setData(taggerOutputFileNames[c], matchGoldstandardTagger.get(c));
                    }
                }
                if (differenceCounter == 1) {
                    oneDifferenceToGoldstandard = "1";
                    statisticOneDifferenceToGoldstandard++;
                } else if (differenceCounter == taggerOutputFileNames.length - 1) {
                    onlyOneMatchWithGoldstandard = "1";
                    statisticOnlyOneMatchWithGoldstandard++;
                } else if (differenceCounter == taggerOutputFileNames.length) {
                    noMatchWithGoldstandard = "1";
                    statisticNoMatchWithGoldstandard++;
                }
                if (userInterface.getCheckBoxOutputTable().getSelection()) {
                    userInterface.getTableItemsOutput().get(i).setData("oneDifferenceToGoldstandard", oneDifferenceToGoldstandard);
                    userInterface.getTableItemsOutput().get(i).setData("onlyOneMatchWithGoldstandard", onlyOneMatchWithGoldstandard);
                    userInterface.getTableItemsOutput().get(i).setData("noMatchWithGoldstandard", noMatchWithGoldstandard);
                }
            }
            if (userInterface.getCheckBoxOutputTable().getSelection()) {
                // Show combined tagging result in last column of output table
                userInterface.getTableItemsOutput().get(i).setText(userInterface.getTableColumnsOutput().size() - 1, resultingTags[i]);
                userInterface.getTableItemsOutput().get(i).setBackground(null);
                userInterface.getTableItemsOutput().get(i).setBackground(userInterface.getTableColumnsOutput().size() - 1, null);
            }
            if (allAreTheSame.equals("1")) {
                statisticAllMatch++;
            }
            for (int j = 0; j < numberOfComparisons; j++) {
                if (agreeingTaggers.get(j).equals("1")) {
                    statisticAgreeingTaggers.set(j, statisticAgreeingTaggers.get(j) + 1);
                }
            }
            if (userInterface.getCheckBoxOutputTable().getSelection()) {
                userInterface.getTableItemsOutput().get(i).setData("allAreTheSame", allAreTheSame);
                for (int j = 0; j < numberOfComparisons; j++) {
                    userInterface.getTableItemsOutput().get(i).setData((taggerOutputFileNames.length - j) + "TaggersAgree", agreeingTaggers.get(j));
                }
            }
        }

        if (userInterface.getCheckBoxOutputTable().getSelection() == true) {
            // Set output table, combo and save buttons on output tab visible
            userInterface.getTableOutput().setVisible(true);
            userInterface.getComboHighlightOutput().setVisible(true);
            userInterface.getButtonSaveResult().setVisible(true);
            if (goldStandardSpecified == true) {
                userInterface.getButtonSaveGoldStandard().setVisible(true);
            } else {
                userInterface.getButtonSaveGoldStandard().setVisible(false);
            }
            userInterface.writeLabelResult("Result table updated.\n");
        } else {
            // Set output table, combo and save buttons on output tab invisible
            userInterface.getTableOutput().setVisible(false);
            userInterface.getComboHighlightOutput().setVisible(false);
            userInterface.getButtonSaveResult().setVisible(false);
            userInterface.getButtonSaveGoldStandard().setVisible(false);
        }
    }

    // Parses the input files and returns the word count
    // If the numbers of words cannot be determined then word count = -1
    private int parseFiles() {
            int wordCount = -1;

            String inputFilePath = "";
            if (userInterface.getCheckBoxInputFile().getSelection() == true) {
                try {
                    inputFilePath = userInterface.getComboInputFile().getText();
                } catch (Exception e) {
                    userInterface.writeLabelResult("Error: Input file not found: " + inputFilePath + "\n");
                }
            } else {
                if (userInterface.getListTaggerFiles().getItemCount() > 0) {
                    inputFilePath = userInterface.getListTaggerFiles().getItem(0);
                }
            }
            if (new File(inputFilePath).exists() == false) {
                if (userInterface.getCheckBoxInputFile().getSelection() == true) {
                    userInterface.writeLabelResult("Error: Input file not found: " + inputFilePath + "\n");
                } else {
                    userInterface.writeLabelResult("Error: Tagger file not found: " + inputFilePath + "\n");
                }
            }
            else {
                File inputFile = new File(inputFilePath);
                inputWords = fileParser.parseFile(inputFile, true);
                // The word count is the number of non-empty lines
                wordCount = fileParser.getNonEmptyLines();
                userInterface.writeLabelResult("Input file: " + inputFilePath + " (used to determine the input words)\n");
                userInterface.writeLabelResult("Number of words: " + wordCount + "\n");

                /*  Not needed any more. parseFile takes care of this
                if (!userInterface.getCheckBoxInputFile().getSelection()) {
                    for (int i = 0; i < inputWords.size(); i++) {
                        inputWords.set(i, inputWords.get(i).split("[ \t]+")[0]);
                    }
                }*/

                // Parse gold standard file
                goldStandardSpecified = userInterface.getCheckBoxGoldstandardFile().getSelection();
                String goldStandardFilePath = "";
                if (goldStandardSpecified == true) {
                    try {
                        goldStandardFilePath = userInterface.getComboGoldstandardFile().getText();
                        if (new File(goldStandardFilePath).exists() == false) {
                            userInterface.writeLabelResult("Error: Gold standard file not found: " + goldStandardFilePath + "\n");
                            goldStandardSpecified = false;
                        } else {
                            File goldStandardFile = new File(goldStandardFilePath);
                            goldStandardTags = fileParser.parseTaggerInput(goldStandardFile, null);
                            userInterface.writeLabelResult("Gold standard file: " + goldStandardFilePath + "\n");
                            if (inputWords.size() != goldStandardTags.size()) {
                                userInterface.writeLabelResult("Error: Gold standard file does not have the same size than the input file.\n");
                                goldStandardSpecified = false;
                                System.out.println("goldStandardSpecified: " + goldStandardSpecified);
                            }
                        }
                    } catch (Exception e) {
                        userInterface.writeLabelResult("Error: Gold standard file not found: " + goldStandardFilePath + "\n");
                    }
                }

                if (userInterface.getCheckBoxTagset().getSelection() == false) {
                    posTagset = new POSTagset(new File(userInterface.getTagsetsPath() + chosenTagset));
                }
                else {
                    posTagset = null;
                }
            }
            return wordCount;
    }
    
    private void startCombinedTagging() {
        int wordCount;
        fileParser = new FileParser();

        userInterface.writeLabelResult(""); // Clear result label contents

        if (userInterface.getListTaggerFiles().getItemCount() < 1) {
            userInterface.writeLabelResult("You need to specify at least one tagger file as input.");
        } else {
            userInterface.writeLabelResult("Start combined tagging\n");
            // TODO: ProgressBar anzeigen

            // Parse the input files
            wordCount = parseFiles();
            if (wordCount >= 0) {
                // Parse input from taggers
                foundTags = new String[userInterface.getListTaggerFiles().getItemCount()][inputWords.size()];

                boolean errorIncompatibleFileSizes = false;
                for (int i = 0; i < userInterface.getListTaggerFiles().getItemCount(); i++) {
                    List<String> parsingOutput = fileParser.parseTaggerInput(new File(userInterface.getListTaggerFiles().getItem(i)), posTagset);
                    foundTags[i] = (parsingOutput).toArray(new String[parsingOutput.size()]);
                    if (foundTags[i].length != inputWords.size()) {
                        userInterface.writeLabelResult("Error: Tagger file " + userInterface.getListTaggerFiles().getItem(i) + " does not have the same size than the input file.\n");
                        errorIncompatibleFileSizes = true;
                    }
                }
    
                if (errorIncompatibleFileSizes == false) {
                    // Show details on result tab
                    userInterface.writeLabelResult("Tagger output files:\n");
                    for (int i = 0; i < userInterface.getListTaggerFiles().getItemCount(); i++) {
                        userInterface.writeLabelResult(" - " + userInterface.getListTaggerFiles().getItem(i) + "\n");
                    }
                    userInterface.writeLabelResult("Chosen algorithm: " + chosenAlgorithm + "\n");
        
                    combiningAlgorithm.updateDataValues(taggerOutputFileNames, foundTags, taggerOutputFileNames.length, inputWords.size(), userInterface.getGroupAlgorithmSpecifics());
                    resultingTags = combiningAlgorithm.runCombinedTaggingAlgorithm();
                    
                    if (userInterface.getCheckBoxOutputFile().getSelection()) {
                        String outputFile = "";
                        try {
                            outputFile = userInterface.getComboOutput().getText();
                        } catch (Exception e) {
                            e.printStackTrace();
                            // TODO: Fehlermeldung, dass Outputfilename leer ist, anzeigen
                            System.out.println("TODO: Fehlermeldung, ob Outputfilename nicht angegeben wurde, anzeigen");
                            userInterface.writeLabelResult("No output file specified.");
                        }
                        
                        if (!outputFile.equals("")) {
                            //writeOutputFile(outputFile);
                            taggingOutput.writeOutputFile(outputFile, taggerOutputFileNames, foundTags, resultingTags, inputWords, goldStandardSpecified, goldStandardTags, userInterface);
                        }
                    }

                    // Compute and write the statistical results
                    computeStatisticalResults();
                    writeStatisticalResults(wordCount);
                }
            }
        }
    }


    /*
     * Write ComboBox-Entries to file
     */
//    private void writeComboBoxRememberFile(String outputFilePath, String type, String[] comboElements) {
//        System.out.println("GUIListener: writeComboBoxRememberFile");
//        
//        StringWriter outputStringWriter = new StringWriter();  
//        PrintWriter outputPrintWriter = new PrintWriter(outputStringWriter);
//        outputPrintWriter.println("[" + type +"]");
//        for (int i = 0; i < comboElements.length; i++) {
//            outputPrintWriter.println(comboElements[i]);
//        }
//
//        FileWriter outputFileWriter = null;
//        try {
//            outputFileWriter = new FileWriter(outputFilePath, true);
//            outputFileWriter.write(outputStringWriter.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (outputFileWriter != null) {
//                    outputFileWriter.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void updateTaggerOutputFileNames() {
        if (userInterface.getListTaggerFiles().getItemCount() > 0) {
            taggerOutputFileNames = new String[userInterface.getListTaggerFiles().getItemCount()];
            for (int i = 0; i < userInterface.getListTaggerFiles().getItemCount(); i++) {
                String[] filePathArray = userInterface.getListTaggerFiles().getItem(i).split(File.separator);
                taggerOutputFileNames[i] = filePathArray[filePathArray.length-1];
            }
        } else {
            taggerOutputFileNames = new String[1];
            taggerOutputFileNames[0] = "";
        }
        
        // Sort tagger output file names array
//        java.util.Arrays.sort(taggerOutputFileNames);
        
        updateAlgorithmSpecificGui();
    }

    public void setChosenAlgorithm(String algorithmName) {
        // Close data stream from previous algorithm
        if (combiningAlgorithm != null) {
            combiningAlgorithm.closeStream();
        }
                
        chosenAlgorithm = algorithmName;
        combiningAlgorithm = new CombiningAlgorithm(userInterface.getAlgorithmScriptsPath() + chosenAlgorithm);
        updateAlgorithmSpecificGui();
    }
    
    public void updateAlgorithmSpecificGui() {
        // Delete old contents of algorithm specifics group
        if (userInterface.getGroupAlgorithmSpecifics() != null) {
            userInterface.disposeChildren(userInterface.getGroupAlgorithmSpecifics());
        }
        
        combiningAlgorithm.updateAlgorithmSpecificGUI(taggerOutputFileNames, taggerOutputFileNames.length, userInterface.getGroupAlgorithmSpecifics());
        if (userInterface.getGroupAlgorithmSpecifics() != null) {
            Rectangle r = userInterface.getCanvasAlgorithm().getBounds();
            userInterface.getCanvasAlgorithm().setBounds(r.x, r.y, r.width+1, r.height);
            r = userInterface.getCanvasAlgorithm().getBounds();
            userInterface.getCanvasAlgorithm().setBounds(r.x, r.y, r.width-1, r.height);
        }
    }

    public String[] getTaggerOutputFileNames() {
        return taggerOutputFileNames;
    }
    
    public String[] getResultingTags() {
        return resultingTags;
    }    

    public String getChosenAlgorithm() {
        return chosenAlgorithm;
    }

    public void setChosenTagset(String name) {
        chosenTagset = name;
    }

    public boolean isGoldStandardSpecified() {
        return goldStandardSpecified;
    }

    public POSTagset getPosTagset() {
        return posTagset;
    }
}
