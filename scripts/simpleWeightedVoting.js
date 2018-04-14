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
 
/**
 * @short Simple Weighted Voting
 * @author Verena Henrich <verenah08@ru.is>, Timo Reuter <timo08@ru.is>
 */

importPackage(org.eclipse.swt);
importPackage(org.eclipse.swt.widgets);
importPackage(org.eclipse.swt.layout);
importPackage(org.eclipse.swt.custom);

var taggerWeights = new Array();

function createAlgorithmSpecificGUI()
{
    //println(numberOfTaggerOutputs);
    if (numberOfTaggerOutputs > 1)
    {
        var taggerDescription = new Array();
        var layout = new GridLayout();
    	layout.numColumns = numberOfTaggerOutputs;
    	algorithmSpecificsGroup.setLayout(layout);
    	
    	var gridBehaviorDesc = new GridData();
    	gridBehaviorDesc.horizontalSpan = numberOfTaggerOutputs;
    	
    	description = new Label(algorithmSpecificsGroup, SWT.WRAP | SWT.SHADOW_NONE);
    	description.setText("Please specify weights");
    	description.setLayoutData(gridBehaviorDesc);
    	
    	// defines use of space for entries
    	var gridBehavior = new GridData();

    	gridBehavior.widthHint = 100;
    	gridBehavior.grabExcessHorizontalSpace = true;
    	
    	var gridBehaviorTextInput = new GridData();
    	gridBehaviorTextInput.widthHint = 80;
    	
        for (i = 0; i < numberOfTaggerOutputs; i++)
        {
    	    taggerDescription[i] = new CLabel(algorithmSpecificsGroup, SWT.SHADOW_NONE);
    	    taggerOutputName = taggerOutputFileNames[i].split("[.]");
    		taggerDescription[i].setText(taggerOutputName[0]);
    		taggerDescription[i].setLayoutData(gridBehavior);
    	}
        for (i = 0; i < numberOfTaggerOutputs; i++)
        {
    		taggerWeights[i] = new Text(algorithmSpecificsGroup, SWT.BORDER);
    		taggerWeights[i].setLayoutData(gridBehaviorTextInput);
    	}
    }
    else
    {
        // There should be a message, to ensure that there are at least 2 tagger outputs 
    }
}

function runCombinedTaggingAlgorithm()
{
    //println("function test: run algorithm");
	var taggerWeight = new Array();
	for (i = 0; i < numberOfTaggerOutputs; i++)
	{
		taggerWeight[i] = parseFloat(taggerWeights[i].getText());
	}
	
	for (i = 0; i < numberOfWords; i++)
	{
		var currentTag = new Array(), currentTagCount = new Array();
		k = -1;
		tagNotFound = true;
		for (j = 0; j < numberOfTaggerOutputs; j++)
		{
			for (l = 0; l <= k; l++)
			{
				if (String(tagArray[j][i]) == String(currentTag[l]))
				{
					currentTagCount[l] += taggerWeight[j];
					tagNotFound = false;
				}
				else
				{
					tagNotFound = true;
				}
			}
			if (tagNotFound)
			{
				k++;
				currentTag[k] = tagArray[j][i];
				currentTagCount[k] = taggerWeight[j];
			}
		}
		
		tempHighestValue = 0;
		
		for (l = 0; l < currentTag.length; l++)
		{
			if (tempHighestValue < currentTagCount[l])
			{
				tempHighestValue = currentTagCount[l];
				resultingTags[i] = currentTag[l];
			}
		}
	}
}

