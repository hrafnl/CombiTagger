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

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.custom.TableEditor;

/**
 * @author Verena Henrich <verenah08@ru.is>, Timo Reuter <timo08@ru.is>
 * @version 0.9
 */
public class UserInterface {
    private Shell sShell = null;
    private TabFolder tabFolder = null;
    private Canvas canvasPreferences = null;
    private Canvas canvasInput = null;
    private Combo comboInputFile = null;
    private Group groupInputFile = null;
    private Button buttonInputFile = null;
    private Group groupTaggerFiles = null;
    private List listTaggerFiles = null;
    private Button buttonAddTaggerFile = null;
    private Button buttonRemoveAllTaggerFiles = null;
    private Button buttonRemoveTaggerFile = null;
    private Label labelHelpInput = null;
    private Button buttonNext = null;
    private Group groupAlgorithm = null;
    private Label labelHelpPreferences = null;
    private Button buttonPrevious = null;
    private Button buttonAlgorithm = null;
    private Canvas canvasOutput = null;
    private Table tableOutput = null;
    private Combo comboOutput = null;
    private Group groupOutput = null;
    private Button buttonOutput = null;
    private Button checkBoxOutputTable = null;
    private Button checkBoxOutputFile = null;
    private Canvas canvasAlgorithm = null;
    private Canvas canvasResult = null;
    private Group groupTagset = null;
    private Button checkBoxTagset = null;
    private Button checkBoxInputFile = null;
    private Combo comboTagset = null;
    private Button buttonTagset = null;
    private Label labelHelpAlgorithm = null;
    private Label labelResult = null;
    private Group groupAlgorithmSpecifics = null;
    private Combo comboHighlightOutput = null;
    private File[] algorithmScripts;
    private Display display = new Display();
    private GUIListener guiListener = null;
    private GUIListenerCombo guiListenerCombo = null;
    private GUIListenerTable guiListenerTable = null;
    private TableEditor tableEditor = null;
    private File[] taggerOutputFiles = null;
    private Canvas algorithmArea = null;
    private String algorithmScriptsPath = "." + File.separator + "scripts" + File.separator;
    private String tagsetsPath = "." + File.separator + "tagsets" + File.separator;
    private java.util.List<Button> radioButtonAlgorithmScript;
    private java.util.List<TableColumn> tableColumnsOutput;
    private java.util.List<TableItem> tableItemsOutput;
    private boolean outputTabVisible = false;
    private java.util.List<Button> radioButtonTagsets;
    private File[] tagsets;
    private Canvas tagsetArea = null;
    private Label horizLine = null;
    private int width = 850;
    private int height = 575;
    private Combo comboGoldstandardFile = null;
    private Group groupGoldstandardFile = null;
    private Button buttonGoldstandardFile = null;
    private Button checkBoxGoldstandardFile = null;
    private Button buttonSaveResult = null;
    private Button buttonSaveGoldStandard = null;

    public UserInterface() {
        guiListener = new GUIListener(this);
        guiListenerCombo = new GUIListenerCombo(this);
        guiListenerTable = new GUIListenerTable(this, guiListener);
        createUserInterface();
        Rectangle r = display.getBounds();
        sShell.setBounds((r.width/2)-(width/2), (r.height/2)-(height/2), width, height);
        sShell.open();

        while (!sShell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    /**
     * initialize UserInterface
     */
    private void createUserInterface() {
        GridData gridData13 = new GridData();
        gridData13.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
        gridData13.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        sShell = new Shell(display, SWT.APPLICATION_MODAL | SWT.SHELL_TRIM);
        sShell.setText("CombiTagger");
        createTabFolder();
        sShell.setLayout(gridLayout);
        sShell.setSize(new Point(722, 358));
        // Define buttons to go previous or next
        buttonPrevious = new Button(sShell, SWT.NONE);
        buttonPrevious.setImage(new Image(Display.getCurrent(), "." + File.separator + "resource" + File.separator + "go-previous.png"));
        buttonPrevious.setText("Previous");
        buttonPrevious.setData("name", "buttonPrevious");
        buttonPrevious.addSelectionListener(guiListener);
        buttonPrevious.setVisible(false);
        buttonNext = new Button(sShell, SWT.NONE);
        buttonNext.setImage(new Image(Display.getCurrent(), "." + File.separator + "resource" + File.separator + "go-next.png"));
        buttonNext.setLocation(-10, 0);
        buttonNext.setLayoutData(gridData13);
        buttonNext.setText("Next");
        buttonNext.setData("name", "buttonNext");
        buttonNext.addSelectionListener(guiListener);
    }

    /**
     * initialize the tab structure
     * 
     */
    private void createTabFolder() {
        GridData gridData = new GridData();
        gridData.verticalSpan = 4;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.horizontalSpan = 2;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        tabFolder = new TabFolder(sShell, SWT.BORDER | SWT.TOP);
        tabFolder.setLayoutData(gridData);
        tabFolder.setData("name", "tabFolder");
        tabFolder.addSelectionListener(guiListener);
        // Call functions to generate the different tab contents
        createCanvasPreferences();
        createCanvasInput();
        createCanvasAlgorithm();
        createCanvasResult();
        createCanvasOutput();
        // Define names and control for the tabs
        TabItem tabItemDataInput = new TabItem(tabFolder, SWT.NONE, 0);
        tabItemDataInput.setText("Data Input");
        tabItemDataInput.setControl(canvasInput);
        TabItem tabItemPreferences = new TabItem(tabFolder, SWT.NONE, 1);
        tabItemPreferences.setText("Preferences");
        tabItemPreferences.setControl(canvasPreferences);
        TabItem tabItemAlgorithm = new TabItem(tabFolder, SWT.NONE, 2);
        tabItemAlgorithm.setText("Algorithm");
        tabItemAlgorithm.setControl(canvasAlgorithm);
        TabItem tabItemResult = new TabItem(tabFolder, SWT.NONE, 3);
        tabItemResult.setText("Result");
        tabItemResult.setControl(canvasResult);
    }

    /**
     * function to show the optional output tab (the table)
     */
    public void showOutputTab() {
        TabItem tabItemOutput = new TabItem(tabFolder, SWT.NONE, 4);
        tabItemOutput.setText("Output Table");
        tabItemOutput.setControl(canvasOutput);
        outputTabVisible = true;
    }

    /**
     * Tab "Data Input" (1st tab)
     */
    private void createCanvasInput() {
        GridData gridData10 = new GridData();
        gridData10.widthHint = 200;
        gridData10.grabExcessVerticalSpace = true;
        gridData10.grabExcessHorizontalSpace = false;
        gridData10.heightHint = 250;
        gridData10.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData10.verticalSpan = 3;
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 2;
        canvasInput = new Canvas(tabFolder, SWT.NONE);
        canvasInput.setLayout(gridLayout2);
        createGroupTaggerFiles();
        labelHelpInput = new Label(canvasInput, SWT.WRAP | SWT.SHADOW_NONE);
        labelHelpInput.setText("You can specify your original text file "
                        + "and your tagger output files which become input to "
                        + "the combined tagging algorithm.");
        labelHelpInput.setLayoutData(gridData10);
        createGroupInputFile();
        createGroupGoldstandardFile();
    }
        
    /**
     * Tab "Data Input" (1st tab)
     *   Group "Tagger Output Files"
     */
    private void createGroupTaggerFiles() {
        GridData gridData9 = new GridData();
        gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData9.widthHint = 100;
        GridData gridData8 = new GridData();
        gridData8.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridData gridData7 = new GridData();
        gridData7.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridLayout gridLayout4 = new GridLayout();
        gridLayout4.numColumns = 2;
        GridData gridData6 = new GridData();
        gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData6.grabExcessVerticalSpace = true;
        gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData6.verticalSpan = 4;
        gridData6.grabExcessHorizontalSpace = true;
        GridData gridData3 = new GridData();
        gridData3.grabExcessHorizontalSpace = true;
        gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData3.verticalSpan = 2;
        gridData3.grabExcessVerticalSpace = true;
        groupTaggerFiles = new Group(canvasInput, SWT.NONE);
        groupTaggerFiles.setText("Tagger Output Files");
        groupTaggerFiles.setLayout(gridLayout4);
        groupTaggerFiles.setLayoutData(gridData3);
        listTaggerFiles = new List(groupTaggerFiles, SWT.BORDER);
        listTaggerFiles.setLayoutData(gridData6);
        buttonAddTaggerFile = new Button(groupTaggerFiles, SWT.NONE);
        buttonAddTaggerFile.setText("Add File...");
        buttonAddTaggerFile.setImage(new Image(Display.getCurrent(), "." + File.separator + "resource" + File.separator + "list-add.png"));
        buttonAddTaggerFile.setLayoutData(gridData8);
        buttonAddTaggerFile.setData("name", "buttonAddTaggerFile");
        buttonAddTaggerFile.addSelectionListener(guiListener);
        buttonRemoveTaggerFile = new Button(groupTaggerFiles, SWT.NONE);
        buttonRemoveTaggerFile.setText("Remove File");
        buttonRemoveTaggerFile.setImage(new Image(Display.getCurrent(), "." + File.separator + "resource" + File.separator + "list-remove.png"));
        buttonRemoveTaggerFile.setLayoutData(gridData7);
        buttonRemoveTaggerFile.setData("name", "buttonRemoveTaggerFile");
        buttonRemoveTaggerFile.addSelectionListener(guiListener);
        buttonRemoveAllTaggerFiles = new Button(groupTaggerFiles, SWT.CENTER);
        buttonRemoveAllTaggerFiles.setText("Remove all");
        buttonRemoveAllTaggerFiles.setLayoutData(gridData9);
        buttonRemoveAllTaggerFiles.setData("name", "buttonRemoveAllTaggerFiles");
        buttonRemoveAllTaggerFiles.addSelectionListener(guiListener);
    }

    /**
     * Tab "Data Input" (1st tab)
     *   Group "Wordlist Input File"
     */
    private void createGroupInputFile() {
        GridLayout gridLayout7 = new GridLayout();
        gridLayout7.numColumns = 2;
        GridData gridData1 = new GridData();
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = false;
        gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        groupInputFile = new Group(canvasInput, SWT.NONE);
        groupInputFile.setLayoutData(gridData1);
        groupInputFile.setLayout(gridLayout7);
        groupInputFile.setText("Wordlist Input File");
        checkBoxInputFile = new Button(groupInputFile, SWT.CHECK);
        checkBoxInputFile.setText("Specify input file");
        checkBoxInputFile.setData("name", "checkBoxInputFile");
        checkBoxInputFile.addSelectionListener(guiListener);
        Label filler4 = new Label(groupInputFile, SWT.NONE);
        createComboInputFile();
        buttonInputFile = new Button(groupInputFile, SWT.NONE);
        buttonInputFile.setImage(new Image(Display.getCurrent(), "." + File.separator + "resource" + File.separator + "document-open.png"));
        buttonInputFile.setData("name", "buttonInputFile");
        buttonInputFile.addSelectionListener(guiListener);
        buttonInputFile.setEnabled(false);
    }
    
    /**
     * Tab "Data Input" (1st tab)
     *   Group "Wordlist Input File"
     *     Combo "Input File"
     */
    private void createComboInputFile() {
        GridData gridData4 = new GridData();
        gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData4.grabExcessHorizontalSpace = true;
        comboInputFile = new Combo(groupInputFile, SWT.SIMPLE);
        comboInputFile.setLayoutData(gridData4);
        comboInputFile.setEnabled(false);
        comboInputFile.setData("name", "comboInputFile");
        comboInputFile.addListener(SWT.FocusOut, guiListenerCombo);
    }

    private void createGroupGoldstandardFile() {
        GridLayout gridLayout7 = new GridLayout();
        gridLayout7.numColumns = 2;
        GridData gridData1 = new GridData();
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = false;
        gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        groupGoldstandardFile = new Group(canvasInput, SWT.NONE);
        groupGoldstandardFile.setLayoutData(gridData1);
        groupGoldstandardFile.setLayout(gridLayout7);
        groupGoldstandardFile.setText("Gold Standard File");
        checkBoxGoldstandardFile = new Button(groupGoldstandardFile, SWT.CHECK);
        checkBoxGoldstandardFile.setText("Specify gold standard file");
        checkBoxGoldstandardFile.setData("name", "checkBoxGoldstandardFile");
        checkBoxGoldstandardFile.addSelectionListener(guiListener);
        Label filler4 = new Label(groupGoldstandardFile, SWT.NONE);
        createComboGoldstandardFile();
        buttonGoldstandardFile = new Button(groupGoldstandardFile, SWT.NONE);
        buttonGoldstandardFile.setImage(new Image(Display.getCurrent(), "." + File.separator + "resource" + File.separator + "document-open.png"));
        buttonGoldstandardFile.setData("name", "buttonGoldstandardFile");
        buttonGoldstandardFile.addSelectionListener(guiListener);
        buttonGoldstandardFile.setEnabled(false);
    }
    
    /**
     * Tab "Data Input" (1st tab)
     *   Group "Gold standard File"
     *     Combo "Gold standard File"
     */
    private void createComboGoldstandardFile() {
        GridData gridData4 = new GridData();
        gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData4.grabExcessHorizontalSpace = true;
        comboGoldstandardFile = new Combo(groupGoldstandardFile, SWT.SIMPLE);
        comboGoldstandardFile.setLayoutData(gridData4);
        comboGoldstandardFile.setEnabled(false);
        comboGoldstandardFile.setData("name", "comboGoldstandardFile");
        comboGoldstandardFile.addListener(SWT.FocusOut, guiListenerCombo);
    }

    /**
     * Tab "Preferences" (2nd tab)
     */
    private void createCanvasPreferences() {
        GridData gridData2 = new GridData();
        gridData2.widthHint = 200;
        gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData2.verticalSpan = 2;
        gridData2.grabExcessVerticalSpace = true;
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 2;
        canvasPreferences = new Canvas(tabFolder, SWT.NONE);
        canvasPreferences.setLayout(gridLayout1);
        canvasPreferences.setSize(new Point(200, 100));
        createGroupTagset();
        labelHelpPreferences = new Label(canvasPreferences, SWT.WRAP | SWT.SHADOW_NONE);
        labelHelpPreferences.setText("Please choose the output format: The table is"
            + " directly shown in the program, the output file is written to disk."
            + " Note that the programm does not check if the file already exists.");
        labelHelpPreferences.setLayoutData(gridData2);
        createGroupOutput();
    }

    /**
     * Tab "Preferences" (2nd tab)
     *   Group "Tagset"
     */
    private void createGroupTagset() {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        groupTagset = new Group(canvasPreferences, SWT.SHADOW_IN);
        groupTagset.setLayout(gridLayout);
        groupTagset.setLayoutData(gridData);
        groupTagset.setText("Tagset");

        tagsetArea = new Canvas(groupTagset, SWT.NONE);
        buttonTagset = new Button(groupTagset, SWT.NONE);
        buttonTagset.setText("Add Tagset...");
        buttonTagset.setImage(new Image(Display.getCurrent(), "." + File.separator + "resource" + File.separator + "list-add.png"));
        buttonTagset.setData("name", "buttonTagset");
        buttonTagset.addSelectionListener(guiListener);
        
        GridData gridBehavior = new GridData();
        gridBehavior.grabExcessVerticalSpace = true;
        Label filler = new Label(groupTagset, SWT.NONE);
        filler.setLayoutData(gridBehavior);
        
        GridData gridData3 = new GridData();
        gridData3.grabExcessVerticalSpace = false;
        gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData3.grabExcessHorizontalSpace = true;
        gridData3.heightHint = 1;
        gridData3.horizontalSpan = 2;
        horizLine = new Label(groupTagset, SWT.SEPARATOR | SWT.HORIZONTAL);
        //horizLine = new Canvas(groupTagset, SWT.BORDER);
        horizLine.setLayoutData(gridData3);
        
        checkBoxTagset = new Button(groupTagset, SWT.CHECK);
        checkBoxTagset.setText("Don't use tagset, use \"Word Space Tag\" format instead");
        checkBoxTagset.setData("name", "checkboxTagset");
        checkBoxTagset.addSelectionListener(guiListener);
        checkBoxTagset.setSelection(true);

        radioButtonTagsets = new ArrayList<Button>();
        updateGroupTagsets();
        /*
        GridLayout gridLayout7 = new GridLayout();
        gridLayout7.numColumns = 2;
        GridData gridData1 = new GridData();
        gridData1.grabExcessHorizontalSpace = true;
        gridData1.grabExcessVerticalSpace = false;
        gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        groupTagset = new Group(canvasPreferences, SWT.NONE);
        groupTagset.setLayoutData(gridData1);
        groupTagset.setLayout(gridLayout7);
        groupTagset.setText("Tagset");
        checkBoxTagset = new Button(groupTagset, SWT.CHECK);
        checkBoxTagset.setText("Specify other tagset than PennTree Bank");
        checkBoxTagset.setData("name", "checkBoxTagset");
        checkBoxTagset.addSelectionListener(guiListener);
        Label filler4 = new Label(groupTagset, SWT.NONE);
        createComboTagset();
        buttonTagset = new Button(groupTagset, SWT.NONE);
        buttonTagset.setImage(new Image(Display.getCurrent(), "./resource/document-open.png"));
        buttonTagset.setData("name", "buttonTagset");
        buttonTagset.addSelectionListener(guiListener);
        buttonTagset.setEnabled(false);*/
    }

    /**
     * Tab "Preferences" (2nd tab)
     *   Group "Tagset"
     *     Combo "Tagset"
     */
    private void createComboTagset() {
        GridData gridData17 = new GridData();
        gridData17.grabExcessHorizontalSpace = true;
        gridData17.verticalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData17.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        comboTagset = new Combo(groupTagset, SWT.NONE);
        comboTagset.setLayoutData(gridData17);
        comboTagset.setEnabled(false);
        comboTagset.setData("name", "comboTagset");
        comboTagset.addListener(SWT.FocusOut, guiListenerCombo);
    }
    
    /**
     * Tab "Preferences" (2nd tab)
     *   Group "Output"
     */
    private void createGroupOutput() {
        GridLayout gridLayout6 = new GridLayout();
        gridLayout6.numColumns = 3;
        GridData gridData15 = new GridData();
        gridData15.grabExcessHorizontalSpace = true;
        gridData15.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData15.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        groupOutput = new Group(canvasPreferences, SWT.NONE);
        groupOutput.setText("Output");
        groupOutput.setLayoutData(gridData15);
        groupOutput.setLayout(gridLayout6);
        checkBoxOutputTable = new Button(groupOutput, SWT.CHECK);
        checkBoxOutputTable.setText("Show output in table (for huge input this may take some time)");
        checkBoxOutputTable.setData("name", "checkBoxOutputTable");
        checkBoxOutputTable.addSelectionListener(guiListener);
        checkBoxOutputTable.setSelection(true);
        Label filler2 = new Label(groupOutput, SWT.NONE);
        Label filler3 = new Label(groupOutput, SWT.NONE);
        checkBoxOutputFile = new Button(groupOutput, SWT.CHECK);
        checkBoxOutputFile.setText("Write output to file");
        checkBoxOutputFile.setData("name", "checkBoxOutputFile");
        checkBoxOutputFile.addSelectionListener(guiListener);
        Label filler = new Label(groupOutput, SWT.NONE);
        Label filler1 = new Label(groupOutput, SWT.NONE);
        createComboOutput();
        buttonOutput = new Button(groupOutput, SWT.NONE);
        buttonOutput.setImage(new Image(Display.getCurrent(), "." + File.separator + "resource" + File.separator + "document-open.png"));
        buttonOutput.setEnabled(false);
        buttonOutput.setData("name", "buttonOutput");
        buttonOutput.addSelectionListener(guiListener);
    }
    
    /**
     * Tab "Preferences" (2nd tab)
     *   Group "Output"
     *     Combo "Output"
     */
    private void createComboOutput() {
        GridData gridData16 = new GridData();
        gridData16.grabExcessHorizontalSpace = true;
        gridData16.verticalAlignment = org.eclipse.swt.layout.GridData.END;
        gridData16.horizontalSpan = 2;
        gridData16.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        comboOutput = new Combo(groupOutput, SWT.NONE);
        comboOutput.setEnabled(false);
        comboOutput.setLayoutData(gridData16);
        comboOutput.setData("name", "comboOutput");
        comboOutput.addListener(SWT.FocusOut, guiListenerCombo);
    }
    
    /**
     * Tab "Algorithm" (3rd tab)
     */
    private void createCanvasAlgorithm() {
        GridData gridData18 = new GridData();
        gridData18.widthHint = 200;
        gridData18.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData18.grabExcessVerticalSpace = true;
        GridLayout gridLayout8 = new GridLayout();
        gridLayout8.numColumns = 2;
        canvasAlgorithm = new Canvas(tabFolder, SWT.NONE);
        createGroupAlgorithm();
        canvasAlgorithm.setLayout(gridLayout8);
        labelHelpAlgorithm = new Label(canvasAlgorithm, SWT.WRAP | SWT.SHADOW_NONE);
        labelHelpAlgorithm.setText("Please choose one of the given algorithms for "
                + "combined tagging and specify corresponding parameters (not "
                + "always necessary). Instead, you can also add your own algorithm "
                + "script.");
        labelHelpAlgorithm.setLayoutData(gridData18);
        createGroupAlgorithmSpecifics();
    }

    /**
     * Tab "Algorithm" (3rd tab)
     *   Group "Combination Algorithm"
     */
    private void createGroupAlgorithm() {
        GridData gridData19 = new GridData();
        gridData19.grabExcessHorizontalSpace = true;
        gridData19.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData19.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData19.grabExcessVerticalSpace = true;
        GridLayout gridLayout5 = new GridLayout();
        gridLayout5.numColumns = 2;
        groupAlgorithm = new Group(canvasAlgorithm, SWT.SHADOW_IN);
        groupAlgorithm.setLayout(gridLayout5);
        groupAlgorithm.setLayoutData(gridData19);
        groupAlgorithm.setText("Combination Algorithm");
        algorithmArea = new Canvas(groupAlgorithm, SWT.NONE);
        buttonAlgorithm = new Button(groupAlgorithm, SWT.NONE);
        buttonAlgorithm.setText("Add Script...");
        buttonAlgorithm.setImage(new Image(Display.getCurrent(), "." + File.separator + "resource" + File.separator + "list-add.png"));
        buttonAlgorithm.setData("name", "buttonAlgorithm");
        buttonAlgorithm.addSelectionListener(guiListener);
        radioButtonAlgorithmScript = new ArrayList<Button>();
        updateGroupAlgorithmScripts();
    }

    /**
     * Tab "Algorithm" (3rd tab)
     *   Group "Algorithm Preferences"
     */
    private void createGroupAlgorithmSpecifics() {
        GridData gridData20 = new GridData();
        gridData20.grabExcessHorizontalSpace = true;
        gridData20.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData20.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        groupAlgorithmSpecifics = new Group(canvasAlgorithm, SWT.NONE);
        groupAlgorithmSpecifics.setLayout(new GridLayout());
        groupAlgorithmSpecifics.setLayoutData(gridData20);
        groupAlgorithmSpecifics.setText("Algorithm Preferences");
    }

    /**
     * Tab "Result" (4th tab)
     */
    private void createCanvasResult() {
        GridData gridData18 = new GridData();
        gridData18.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData18.grabExcessVerticalSpace = true;
        gridData18.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData18.grabExcessHorizontalSpace = true;
        GridLayout gridLayout8 = new GridLayout();
        gridLayout8.numColumns = 2;
        canvasResult = new Canvas(tabFolder, SWT.NONE);
        canvasResult.setLayout(gridLayout8);
        labelResult = new Label(canvasResult, SWT.WRAP | SWT.SHADOW_NONE);
        labelResult.setLayoutData(gridData18);
    }
    
    /**
     * Tab "Output" (5th tab)
     */
    private void createCanvasOutput() {
        GridData gridData12 = new GridData();
        gridData12.grabExcessHorizontalSpace = true;
        gridData12.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData12.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData12.grabExcessVerticalSpace = true;
        gridData12.horizontalSpan = 3;
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 3;
        canvasOutput = new Canvas(tabFolder, SWT.NONE);
        canvasOutput.setLayout(gridLayout1);
        tableOutput = new Table(canvasOutput, SWT.NONE);
        tableOutput.setHeaderVisible(true);
        tableOutput.setLayoutData(gridData12);
        tableOutput.setLinesVisible(true);
        tableEditor = new TableEditor(tableOutput);
        tableEditor.horizontalAlignment = SWT.LEFT;
        tableEditor.grabHorizontal = true;
        tableOutput.addListener (SWT.MouseDown, guiListenerTable);
        tableColumnsOutput = new ArrayList<TableColumn>();
        tableItemsOutput = new ArrayList<TableItem>();
        comboHighlightOutput = new Combo(canvasOutput, SWT.READ_ONLY);
        comboHighlightOutput.setData("name", "comboHighlightOutput");
        comboHighlightOutput.addSelectionListener(guiListener);
        GridData gridData18 = new GridData();
        gridData18.widthHint = 370;
        comboHighlightOutput.setLayoutData(gridData18);
        updateComboSortOutput();
        buttonSaveResult = new Button(canvasOutput, SWT.NONE);
        buttonSaveResult.setText("Save Result Column...");
        buttonSaveResult.setImage(new Image(Display.getCurrent(), "." + File.separator + "resource" + File.separator + "document-open.png"));
        buttonSaveResult.setData("name", "buttonSaveResult");
        buttonSaveResult.addSelectionListener(guiListener);
        buttonSaveGoldStandard = new Button(canvasOutput, SWT.NONE);
        buttonSaveGoldStandard.setText("Save Gold Standard Column...");
        buttonSaveGoldStandard.setImage(new Image(Display.getCurrent(), "." + File.separator + "resource" + File.separator + "document-open.png"));
        buttonSaveGoldStandard.setData("name", "buttonSaveGoldStandard");
        buttonSaveGoldStandard.addSelectionListener(guiListener);
    }
    
    /**
     * Tab "Output" (5th tab)
     *   Combo "Sort method"
     */
    private void updateComboSortOutput() {
        comboHighlightOutput.remove(0, comboHighlightOutput.getItemCount()-1);
        comboHighlightOutput.add("No highlighting");
        comboHighlightOutput.select(0);
        if (guiListener.isGoldStandardSpecified() == true) {
            comboHighlightOutput.add("All taggers, gold std and combined result match");
        } else {
            comboHighlightOutput.add("All taggers and combined tagging result match");
        }
        comboHighlightOutput.add("All " + guiListener.getTaggerOutputFileNames().length + " taggers agree");
        for (int i = guiListener.getTaggerOutputFileNames().length-1; i > guiListener.getTaggerOutputFileNames().length/2; i--) {
            comboHighlightOutput.add(i + " of " + guiListener.getTaggerOutputFileNames().length + " taggers agree");
//            System.out.println(i);
        }
        if (guiListener.isGoldStandardSpecified() == true) {
            comboHighlightOutput.add("One difference to gold standard");
            comboHighlightOutput.add("Only one matches gold standard");
            comboHighlightOutput.add("No match with gold standard");
            comboHighlightOutput.add("Combined tagging result matches gold standard");
            for (int i = 0; i < guiListener.getTaggerOutputFileNames().length; i++) {
                comboHighlightOutput.add(guiListener.getTaggerOutputFileNames()[i] + " matches gold standard");
            }
        }
//        comboHighlightOutput.setSize(200, comboHighlightOutput.getSize().y);
//        Rectangle r = comboHighlightOutput.getBounds();
//        if (checkBoxGoldstandardFile.getSelection() == false) {
//            comboHighlightOutput.remove(2, comboHighlightOutput.getItemCount() - 1);
//        }
//        comboHighlightOutput.setBounds(r);
//        comboHighlightOutput.pack();
    }
    
    /**
     * Function to update the algorithm scripts (add scripts etc.)
     */
    public void updateGroupAlgorithmScripts() {
        System.out.println("UI: updateGroupAlgorithmScripts");
        File[] algorithmScriptsDir = (new File(algorithmScriptsPath)).listFiles();

        // Count number of algorithm files in directory
        int numberOfAlgorithmScripts = 0;
        if (algorithmScriptsDir != null) {
            for (int i = 0; i < algorithmScriptsDir.length; i++) {
                if (algorithmScriptsDir[i].isFile()) {
                    numberOfAlgorithmScripts++;
                }
            }
        }

        // Create array with algorithm script files
        algorithmScripts = new File[numberOfAlgorithmScripts];
        if (algorithmScriptsDir != null) {
            int j = 0;
            for (int i = 0; i < algorithmScriptsDir.length; i++) {
                if (algorithmScriptsDir[i].isFile()) {
                    algorithmScripts[j] = algorithmScriptsDir[i];
                    j++;
                }
            }
        }

        // Sort algorithm script files array
        java.util.Arrays.sort(algorithmScripts);

        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        algorithmArea.setLayout(new GridLayout());
        algorithmArea.setLayoutData(gridData1);
        GridData gridData2 = new GridData();
        gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
        buttonAlgorithm.setLayoutData(gridData2);

        // Create radio button for every algorithm script file
        while (radioButtonAlgorithmScript.size() < algorithmScripts.length) {
            radioButtonAlgorithmScript.add(new Button(algorithmArea, SWT.RADIO));
        }
        for (int i = 0; i < algorithmScripts.length; i++) {
            radioButtonAlgorithmScript.get(i).setText(algorithmScripts[i].getName());
            radioButtonAlgorithmScript.get(i).setData("algorithm", algorithmScripts[i].getName());
            radioButtonAlgorithmScript.get(i).addSelectionListener(guiListener);

            // Select radio button for the first algorithm script file
            if (i == 0) {
                radioButtonAlgorithmScript.get(i).setSelection(true);
                guiListener.setChosenAlgorithm(algorithmScripts[i].getName());
            } else {
                radioButtonAlgorithmScript.get(i).setSelection(false);
            }
        }
        algorithmArea.pack();
    }

    /**
     * Function to update the tagsets
     */
    public void updateGroupTagsets() {
        System.out.println("UI: updateGroupTagsets");
        File[] tagsetsDir = (new File(tagsetsPath)).listFiles();

        // Count number of tagset files in directory
        int numberOfTagsets = 0;
        if (tagsetsDir != null) {
            for (int i = 0; i < tagsetsDir.length; i++) {
                if (tagsetsDir[i].isFile()) {
                    numberOfTagsets++;
                }
            }
        }

        // Create array with tagset files
        tagsets = new File[numberOfTagsets];
        if (tagsetsDir != null) {
            int j = 0;
            for (int i = 0; i < tagsetsDir.length; i++) {
                if (tagsetsDir[i].isFile()) {
                    tagsets[j] = tagsetsDir[i];
                    j++;
                }
            }
        }

        // Sort algorithm script files array
        java.util.Arrays.sort(tagsets);

        GridData gridData1 = new GridData();
        gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData1.grabExcessHorizontalSpace = true;
        tagsetArea.setLayout(new GridLayout());
        tagsetArea.setLayoutData(gridData1);
        GridData gridData2 = new GridData();
        gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
        buttonTagset.setLayoutData(gridData2);

        // Create radio button for every tagsets file
        while (radioButtonTagsets.size() < tagsets.length) {
            radioButtonTagsets.add(new Button(tagsetArea, SWT.RADIO));
        }
        for (int i = 0; i < tagsets.length; i++) {
            radioButtonTagsets.get(i).setText(tagsets[i].getName());
            radioButtonTagsets.get(i).setData("tagset", tagsets[i].getName());
            radioButtonTagsets.get(i).addSelectionListener(guiListener);

            // Select radio button for the first tagset file
            if (i == 0) {
                radioButtonTagsets.get(i).setSelection(true);
                guiListener.setChosenTagset(tagsets[i].getName());
            } else {
                radioButtonTagsets.get(i).setSelection(false);
            }
            
            if (checkBoxTagset != null && checkBoxTagset.getSelection() == true) {
                // Disable all radio buttons for selecting a tagset
                radioButtonTagsets.get(i).setEnabled(false);
            } else if (checkBoxTagset != null) {
                // Enable all radio buttons for selcting a tagset
                radioButtonTagsets.get(i).setEnabled(true);
            }                
        }
        tagsetArea.pack();
    }
    
    /**
     * Update of the output table
     */
    public void updateResultTable() {
        int numberOfColumns = guiListener.getTaggerOutputFileNames().length + 3;
        if (checkBoxGoldstandardFile.getSelection() == true) {
            numberOfColumns++;
        }
        updateComboSortOutput();
        
        if (outputTabVisible == false) {
            showOutputTab();
        }

        while (tableColumnsOutput.size() < numberOfColumns) {
            // Create TableColumn with right alignment
            tableColumnsOutput.add(new TableColumn(tableOutput, SWT.RIGHT));
        }

        // Remove columns if necessary
        while (tableColumnsOutput.size() > numberOfColumns) {
            tableColumnsOutput.get(tableColumnsOutput.size() - 1).dispose();
            tableColumnsOutput.remove(tableColumnsOutput.size() - 1);
        }

        // Write column header for the first column (line numbers)
        tableColumnsOutput.get(0).setText("Line"); // First column
        tableColumnsOutput.get(0).pack();

        // Write column header for the second column (input words)
        tableColumnsOutput.get(1).setText("Input"); // Second column
        tableColumnsOutput.get(1).pack();

        for (int i = 0; i < guiListener.getTaggerOutputFileNames().length; i++) {
            // This text will appear in the column headers of the tagger output columns
            tableColumnsOutput.get(i + 2).setText(guiListener.getTaggerOutputFileNames()[i]);
            tableColumnsOutput.get(i + 2).pack();
        }

        // If gold standard file is given, write column header for the second 
        // last column which shows the gold standard result
        if (guiListener.isGoldStandardSpecified() == true) {
            tableColumnsOutput.get(tableColumnsOutput.size() - 2).setText("Gold Standard (editable)");
            tableColumnsOutput.get(tableColumnsOutput.size() - 2).pack();
        }

        // Write column header for the last column (result column)
        tableColumnsOutput.get(tableColumnsOutput.size() - 1).setText("Combined Tagger (editable)");
        tableColumnsOutput.get(tableColumnsOutput.size() - 1).pack();

        // Create TableItems (rows)
        while (tableItemsOutput.size() < guiListener.getWords().size()) {
            tableItemsOutput.add(new TableItem(tableOutput, SWT.NONE));
        }

        // Remove TableItems (rows) if necessary
        while (tableItemsOutput.size() > guiListener.getWords().size()) {
            tableItemsOutput.get(tableItemsOutput.size() - 1).dispose();
            tableItemsOutput.remove(tableItemsOutput.size() - 1);
        }
        
        tableOutput.deselectAll();
    }

    /**
     * Set text of a specified cell in the output table.
     * 
     * @param rowNumber Row number.
     * @param columnNumber Column number.
     * @param cellContent Text that is written into the specified cell.
     */
//    public void writeOutputTableCell(int rowNumber, int columnNumber, String cellContent) {
//        tableItemsOutput.get(rowNumber).setText(columnNumber, cellContent);
//    }
    
    /**
     * Associate data with a table row (used for statistics).
     * 
     * @param rowNumber Row number.
     * @param key Specified key.
     * @param value Value associated with the key.
     */
//    public void setOutputTableRowData(int rowNumber, String key, String value) {
//        tableItemsOutput.get(rowNumber).setData(key, value);
//    }

    /**
     * Set the background color of a specified table row.
     * 
     * @param rowNumber Row number.
     * @param backgroundColor Background color.
     */
//    public void setOutputTableRowColor(int rowNumber, Color backgroundColor) {
//        tableItemsOutput.get(rowNumber).setBackground(backgroundColor);
//    }
    
//    public void writeTableColumnHeader(int column, String headerText) {
//        for (int i = 0; i < guiListener.getTaggerOutputFileNames().length; i++) {
//            // This text will appear in the column headers of the tagger output
//            // columns
//            tableColumnsOutput.get(i + 1).setText(guiListener.getTaggerOutputFileNames()[i]);
//            tableColumnsOutput.get(i + 1).pack();
//        }
//    }
    
    /**
     * Write string to result label
     * @param text
     */
    public void writeLabelResult(String text) {
        if (text.equals("")) {
            // Clear labelResult contents
            labelResult.setText("");
        } else {
            // Add text argument to labelResult
            labelResult.setText(labelResult.getText() + text);
        }
    }

    /**
     * Resize the shell window (bugfix to redraw)
     */
//    public void resizeShell() {
//        Rectangle r = sShell.getBounds();
//        sShell.setBounds(r.x, r.y, r.width + 1, r.height);
//    }

    /**
     * Delete all children of a certain parent element
     * @param parent
     */
    public void disposeChildren(Composite parent) {
        Control[] children = parent.getChildren();

        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof Composite) {
                disposeChildren((Composite) children[i]);
            }
            children[i].dispose();
        }
    }

    /**
     * Redraw all children of a certain parent element
     * @param parent
     */
//    public void redrawChildren(Composite parent) {
//        System.out.println("redraw child: " + parent.getClass());
//        Control[] children = parent.getChildren();
//
//        for (int i = 0; i < parent.getChildren().length; i++) {
//            children[i].redraw(0, 0, children[i].getBounds().width, children[i].getBounds().height, true);
//            if (children[i] instanceof Composite) {
//                redrawChildren((Composite) children[i]);
//            }
//        }
//    }

    /**
     * Update Group "Algorithm Preferences"
     */
    public void updateGroupAlgorithmSpecifics() {
        GridData gridData20 = new GridData();
        gridData20.grabExcessHorizontalSpace = true;
        gridData20.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData20.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        groupAlgorithmSpecifics.setLayout(new GridLayout());
        groupAlgorithmSpecifics.setLayoutData(gridData20);
    }

    /**
     * Pack Group "Algorithm Preferences"
     */
//    public void packGroupAlgorithmSpecifics() {
//        groupAlgorithmSpecifics.pack();
//    }

    /**
     * Function actually not needed
     * @param algorithmScripts
     * @param taggerOutputs
     */
//    public void updateGroupAlgorithmSpecifics(File[] algorithmScripts,
//            File[] taggerOutputs) {
//        System.out.println("UI - updateAlgorithmSpecifics: "
//                + guiListener.getChosenAlgorithm());
//    }

    /**
     * Getter and Setter functions
     * @return
     */
    public Shell getSShell() {
        return sShell;
    }

    public Combo getComboInputFile() {
        return comboInputFile;
    }
    
    public Button getCheckBoxInputFile() {
        return checkBoxInputFile;
    }

    public Button getButtonInputFile() {
        return buttonInputFile;
    }

    public List getListTaggerFiles() {
        return listTaggerFiles;
    }

    public Button getCheckBoxTagset() {
        return checkBoxTagset;
    }

    public Button getButtonTagset() {
        return buttonTagset;
    }

    public Combo getComboTagset() {
        return comboTagset;
    }

    public Button getButtonOutput() {
        return buttonOutput;
    }

    public Combo getComboOutput() {
        return comboOutput;
    }

    public Button getCheckBoxOutputFile() {
        return checkBoxOutputFile;
    }

    public Button getButtonNext() {
        return buttonNext;
    }

    public Button getButtonPrevious() {
        return buttonPrevious;
    }

    public TabFolder getTabFolder() {
        return tabFolder;
    }

    public Group getGroupAlgorithmSpecifics() {
        return groupAlgorithmSpecifics;
    }

    public File[] getTaggerOutputFiles() {
        return taggerOutputFiles;
    }

    public File[] getAlgorithmScripts() {
        return algorithmScripts;
    }

    public String getAlgorithmScriptsPath() {
        return algorithmScriptsPath;
    }

    public Display getDisplay() {
        return display;
    }

    public Canvas getCanvasAlgorithm() {
        return canvasAlgorithm;
    }

    public Button getCheckBoxOutputTable() {
        return checkBoxOutputTable;
    }

    public Table getTableOutput() {
        return tableOutput;
    }

    public String getTagsetsPath() {
        return tagsetsPath;
    }

    public java.util.List<TableItem> getTableItemsOutput() {
        return tableItemsOutput;
    }

    public Combo getComboGoldstandardFile() {
        return comboGoldstandardFile;
    }

    public Button getButtonGoldstandardFile() {
        return buttonGoldstandardFile;
    }

    public Button getCheckBoxGoldstandardFile() {
        return checkBoxGoldstandardFile;
    }

    public java.util.List<Button> getRadioButtonTagsets() {
        return radioButtonTagsets;
    }

    public java.util.List<TableColumn> getTableColumnsOutput() {
        return tableColumnsOutput;
    }

    public Combo getComboHighlightOutput() {
        return comboHighlightOutput;
    }

    public TableEditor getTableEditor() {
        return tableEditor;
    }

    public Button getButtonSaveGoldStandard() {
        return buttonSaveGoldStandard;
    }

    public Button getButtonSaveResult() {
        return buttonSaveResult;
    }
}
