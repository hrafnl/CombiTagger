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
import java.util.*;

import javax.script.*;

/**
 * @author Verena Henrich <verenah08@ru.is>, Timo Reuter <timo08@ru.is>
 * @version 0.9
 */
public class ScriptTest {
    private FileReader fileReader;
    private BufferedReader bufferedReader;

    public void runScriptTest() {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");

        List<String> namesList = new ArrayList<String>();
        namesList.add("Jill");
        namesList.add("Bob");
        namesList.add("Laureen");
        namesList.add("Ed");

        jsEngine.put("namesListKey", namesList);
        try {
            fileReader = new FileReader("./scripts/test.js");            
            bufferedReader = new BufferedReader(fileReader);
            String row = bufferedReader.readLine();
            while (row != null) {
                jsEngine.eval(row);
                row = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (ScriptException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        System.out.println();
        // list of available engines installed to the Java platform
        List<ScriptEngineFactory> factories = mgr.getEngineFactories();

        for (ScriptEngineFactory factory: factories) {
            System.out.println("ScriptEngineFactory Info");
            String engName = factory.getEngineName();
            String engVersion = factory.getEngineVersion();
            String langName = factory.getLanguageName();
            String langVersion = factory.getLanguageVersion();
            System.out.printf("\tScript Engine: %s (%s)\n", 
                    engName, engVersion);
            List<String> engNames = factory.getNames();
            for(String name: engNames) {
                System.out.printf("\tEngine Alias: %s\n", name);
            }
            System.out.printf("\tLanguage: %s (%s)\n", 
                    langName, langVersion);
        }
         */
    }

}
