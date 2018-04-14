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
import java.util.List;

/**
 * This class represents a part-of-speech tagset.
 * 
 * @author Verena Henrich <verenah08@ru.is>, Timo Reuter <timo08@ru.is>
 * @version 0.9
 */
public class POSTagset {
    private List<String> tags;

    /**
     * Class constructor initializes the tagset. It reads always the first string of
     * characters of an input file and writes these into the tag array. This array
     * represents the tags of the tagset.
     * 
     * @param textFile Input file that contains the tagset.
     */
	public POSTagset(File textFile) {
		FileParser fileParser = new FileParser();
        tags = fileParser.parseFile(textFile, true);
		int i = 0;
		for (String tag : tags) {
			tags.set(i, tag.split(" ")[0]);
			i++;
		}
	}

    /**
     * Getter method for tags.
     * 
     * @return List of tags.
     */
    public List<String> getTags() {
        return tags;
    }
}
