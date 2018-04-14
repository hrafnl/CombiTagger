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

function createAlgorithmSpecificGUI()
{
    println("function test: createAlgorithmSpecificGUI");
}

function runCombinedTaggingAlgorithm()
{
    println("function test: run algorithm");
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
    				currentTagCount[l]++;
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
    			currentTagCount[k] = 1;
			//tagNotFound = false;
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
