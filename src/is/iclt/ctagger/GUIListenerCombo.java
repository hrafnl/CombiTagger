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

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Verena Henrich <verenah08@ru.is>, Timo Reuter <timo08@ru.is>
 * @version 0.9
 */
public class GUIListenerCombo implements Listener {
    private UserInterface userInterface = null;
    
    public GUIListenerCombo(UserInterface mainUserInterface) {
        userInterface = mainUserInterface;
    }

    public void handleEvent(Event e) {
        String callee = (String) e.widget.getData("name");
        
        // If a Combo loses focus, store the text string into its list
        if ("comboOutput".equals(callee)) {
            if (userInterface.getComboOutput().indexOf(userInterface.getComboOutput().getText()) == -1) {
                userInterface.getComboOutput().add(userInterface.getComboOutput().getText());
            }
        } else if ("comboInputFile".equals(callee)) {
            if (userInterface.getComboInputFile().indexOf(userInterface.getComboInputFile().getText()) == -1) {
                userInterface.getComboInputFile().add(userInterface.getComboInputFile().getText());
            }
        } else if ("comboTagset".equals(callee)) {
            if (userInterface.getComboTagset().indexOf(userInterface.getComboTagset().getText()) == -1) {
                userInterface.getComboTagset().add(userInterface.getComboTagset().getText());
            }
        }
    }
}
