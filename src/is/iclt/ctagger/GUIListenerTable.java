package is.iclt.ctagger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class GUIListenerTable implements Listener {
    private UserInterface userInterface = null;
    private GUIListener guiListener = null;

    public GUIListenerTable(UserInterface mainUserInterface, GUIListener listener) {
        userInterface = mainUserInterface;
        guiListener = listener;
    }

    public void handleEvent(Event event) {
        Rectangle rectangleClientArea = userInterface.getTableOutput().getClientArea();
        Point clickedPoint = new Point(event.x, event.y);
        int rowNumber = userInterface.getTableOutput().getTopIndex();
        while (rowNumber < userInterface.getTableOutput().getItemCount()) {
            boolean visible = false;
            final TableItem tableItem = userInterface.getTableOutput().getItem(rowNumber);
            int i = 0;
            if (guiListener.isGoldStandardSpecified() == true) {
                // Make column "gold standard" and "combined tagging result" editable
                i = userInterface.getTableOutput().getColumnCount() - 2;
            } else {
                // Make column "combined tagging result" editable
                i = userInterface.getTableOutput().getColumnCount() - 1;
            }

            for (; i < userInterface.getTableOutput().getColumnCount(); i++) {
                Rectangle rectangleRow = tableItem.getBounds(i);
                if (rectangleRow.contains(clickedPoint)) {
                    final int columnNumber = i;
                    final Text newText = new Text(userInterface.getTableOutput(), SWT.NONE);
                    final String oldText = tableItem.getText(i);
                    Listener textListener = new Listener() {
                        public void handleEvent(final Event event) {
                            Color color = new Color(Display.getCurrent(), 220, 70, 70);
                            boolean textIsValid = false;
                            // If tagset is specified
                            if (guiListener.getPosTagset() != null) {
                                // Search for equivalence with a part-of-speech tag
                                for (String tag : guiListener.getPosTagset().getTags()) {
                                    // The new text is valid, if it equals the old text, one tag of
                                    // the used tagset or the specified tag for "tag not found"
                                    if (newText.getText().equals(tag) || newText.getText().equals(FileParser.getTagNotFoundSymbol()) || newText.getText().equals(oldText) && !tableItem.getBackground(columnNumber).equals(color)) {
                                        textIsValid = true;
                                        break;
                                    }
                                }
                            } else {
                                textIsValid = true;
                            }
                            switch (event.type) {
                            case SWT.FocusOut:
                                tableItem.setText(columnNumber, newText.getText());
                                newText.dispose();
                                if (textIsValid == false) {
                                    tableItem.setBackground(columnNumber, color);
                                } else {
                                    tableItem.setBackground(columnNumber, null);
                                }
                                break;
                            case SWT.Traverse:
                                switch (event.detail) {
                                case SWT.TRAVERSE_RETURN:
                                    tableItem.setText(columnNumber, newText.getText());
                                    if (textIsValid == false) {
                                        tableItem.setBackground(columnNumber, color);
                                    } else {
                                        tableItem.setBackground(columnNumber, null);
                                    }
                                    // Fall through
                                case SWT.TRAVERSE_ESCAPE:
                                    newText.dispose();
                                    event.doit = false;
                                }
                                break;
                            }
                            color.dispose();
                        }
                    };
                    newText.addListener(SWT.FocusOut, textListener);
                    newText.addListener(SWT.Traverse, textListener);
                    userInterface.getTableEditor().setEditor(newText, tableItem, i);
                    newText.setText(tableItem.getText(i));
                    newText.selectAll();
                    newText.setFocus();
                    return;
                }
                if (!visible && rectangleRow.intersects(rectangleClientArea)) {
                    visible = true;
                }
            }
            if (!visible) {
                return;
            }
            rowNumber++;
        }
    }
}
