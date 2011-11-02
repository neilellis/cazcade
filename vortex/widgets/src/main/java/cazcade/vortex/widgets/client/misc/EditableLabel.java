package cazcade.vortex.widgets.client.misc;


/*
 * Copyright 2006 Robert Hanson <iamroberthanson AT gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This code is part of the GWT Widget Library
 */


import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.widgets.client.misc.toolbar.RichTextToolbar;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;

/**
 * Editable Label class, funcionality displays a Label UI
 * Element until clicked on, then if element is set to be
 * editable (default) then an editable area and Buttons are
 * displayed instead.
 * <p/>
 * If the Label is not set to be word wrapped (default) then
 * the editable area is a Text Box and clicking the OK button
 * or hitting return key in the TextBox will display the Label with
 * the updated text.
 * <p/>
 * If the Label is set to be word wrapped, using the setWordWrap(boolean)
 * method, then the editable area is a Text Area and clicking the OK
 * button will display the Label with the updated text.
 * <p/>
 * In both cases, clicking Cancel button or hitting Escape key in the
 * TextBox/TextArea then the Label is displayed with original text.
 *
 * @author Adam Tacy
 * @version 0.0.2
 *          <p/>
 *          Changes since version 0.0.1
 *          + made cancelLabelChange public [ref request id: 1518134]
 *          + made originalText have default value of empty string [to support ref request id: 1518134]
 *          *End*
 */
public class EditableLabel extends Composite implements HasWordWrap,
        HasText,
        HasHorizontalAlignment,
        SourcesClickEvents,
        SourcesChangeEvents,
        SourcesMouseEvents {

    public static final String EDITABLE_LABEL_PLACEHOLDER_STYLE = "editableLabel-placeholder";
    /**
     * TextBox element to enable text to be changed if Label is not word wrapped
     */
    private TextBox changeText;

    /**
     * TextArea element to enable text to be changed if Label is wordwrapped
     */
    private RichTextArea changeTextArea;

    private FormatUtil formatter;


    /**
     * Label element, which is initially is diplayed.
     */
    private HTML text;

    /**
     * String element that contains the original text of a
     * Label prior to it being edited.
     */
    private String originalText;

    /**
     * Simple button to confirm changes
     */
    private Widget confirmChange;

    /**
     * Simple button to cancel changes
     */
    private Widget cancelChange;

    /**
     * Flag to indicate that Label is in editing mode.
     */
    private boolean isEditing = false;

    /**
     * Flag to indicate that label can be edited.
     */
    private boolean editable = true;

    ChangeListenerCollection changeListeners;

    /**
     * Default String value for OK button
     */
    private String defaultOkButtonText = "OK";

    /**
     * Default String value for Cancel button
     */
    private String defaultCancelButtonText = "Cancel";
    private RichTextToolbar toolbar;
    private FlowPanel buttonPanel;
    private FlowPanel instance;
    private Runnable onEditAction;
    private Runnable onEditEndAction;
    private String plainText;
    private String okButtonText;
    private String cancelButtonText;
    private boolean doubleClick;
    private boolean showBrief;
    private String placeholder = "Click to edit";
    private static final boolean SUPPORT_TOOLBAR = false;
    private String prefix = "";

    /**
     * Allows the setting of the isEditable flag, marking
     * the label as editable or not.
     *
     * @param flag True or False value depending if the Label is to be editable or not
     */
    public void setEditable(boolean flag) {
        editable = flag;
    }

    /**
     * Returns the value of the isEditable flag.
     *
     * @return
     */
    public boolean isFieldEditable() {
        return editable;
    }

    /**
     * Returns the value of the isEditing flag, allowing outside
     * users to see if the Label is being edited or not.
     *
     * @return
     */
    public boolean isInEditingMode() {
        return isEditing;
    }


    /**
     * Change the displayed label to be a TextBox and copy label
     * text into the TextBox.
     */
    private void changeTextLabel() {
        if (editable) {
            // Set up the TextBox
            originalText = plainText;

            // Change the view from Label to TextBox and Buttons
            text.removeFromParent();

            if (text.getWordWrap()) {
                // If Label word wrapped use the TextArea to edit
//                instance.add(toolbar);
                instance.add(changeTextArea);
                instance.add(buttonPanel);
                changeTextArea.setHTML(originalText);
                changeTextArea.setFocus(true);
            } else {
                // Otherwise use the TextBox to edit.
                instance.add(changeText);
                changeText.setText(originalText);
                changeText.setFocus(true);
            }

            //Set instance as being in editing mode.
            isEditing = true;
            if (onEditAction != null) {
                onEditAction.run();
            }
        }
    }

    /**
     * Restores visibility of Label and hides the TextBox and Buttons
     */
    private void restoreVisibility() {
        // Change appropriate visibilities
        instance.add(text);
        if (text.getWordWrap()) {
            // If Label is word wrapped hide the TextArea
            changeTextArea.removeFromParent();
            if (toolbar != null) {
                toolbar.removeFromParent();
            }
            if (buttonPanel != null) {
                buttonPanel.removeFromParent();
            }
        } else {
            // Otherwise hide the TextBox
            changeText.removeFromParent();
            if (buttonPanel != null) {
                buttonPanel.removeFromParent();
            }
        }
        // Set isEditing flag to false as we are no longer editing
        isEditing = false;
        if (onEditEndAction != null) {
            onEditEndAction.run();
        }

    }

    /**
     * Sets the Label text to the new value, restores the
     * display and calls the update method.
     */
    private void setTextLabel() {
        if (text.getWordWrap()) {
            // Set the Label to be the text in the Text Box
            setText(changeTextArea.getHTML());
        } else {
            // Set the Label to be the text in the Text Box
            setText(changeText.getText());
        }
        // Set the object back to display label rather than TextBox and Buttons
        restoreVisibility();

        // Call the update method provided in the Constructor
        // (this could be anything from alerting the user through to
        // Making an AJAX call to store the data.
        //updater.onChange(this);
        if (changeListeners != null) changeListeners.fireChange(this);
    }

    /**
     * Sets the Label text to the original value, restores the display.
     */
    public void cancelLabelChange() {
        // Set the Label text back to what it was originally
        setText(originalText);
        // Set the object back to display Label rather than TextBox and Buttons
        restoreVisibility();
    }


    @Override
    protected void onLoad() {
        super.onLoad();
        if (doubleClick) {
            text.addDoubleClickHandler(new DoubleClickHandler() {
                public void onDoubleClick(DoubleClickEvent event) {
                    changeTextLabel();
                }
            });
        } else {
            text.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    changeTextLabel();
                }
            });
        }
    }

    /**
     * Creates the Label, the TextBox and Buttons.  Also associates
     * the update method provided in the constructor with this instance.
     *
     * @param labelText        The value of the initial Label.
     * @param okButtonText     The text diplayed in the OK button.
     * @param cancelButtonText The text displayed in the Cancel button.
     * @param doubleClick
     */
    private void createEditableLabel(String labelText,
                                     String okButtonText, String cancelButtonText, boolean doubleClick) {
        this.okButtonText = okButtonText;
        this.cancelButtonText = cancelButtonText;
        this.doubleClick = doubleClick;
        // Put everything in a VerticalPanel
        instance = new FlowPanel();
        instance.getElement().getStyle().setDisplay(Style.Display.INLINE);

        plainText = labelText;

        // Create the Label element and add a ClickListener to call out Change method when clicked
        if (formatter != null) {
            text = new HTML(formatter.formatRichText(labelText));
        } else {
            text = new HTML(labelText);
        }

        text.setStyleName("editableLabel-label");


        // Create the TextBox element used for non word wrapped Labels
        // and add a KeyboardListener for Return and Esc key presses
        changeText = new TextBox();
        changeText.setStyleName("editableLabel-textBox");
        changeText.sinkEvents(Event.KEYEVENTS);
        changeText.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_ENTER:
                        setTextLabel();
                        break;
                    case KeyCodes.KEY_ESCAPE:
                        cancelLabelChange();
                        break;
                }
            }
        });

        changeText.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                if (isEditing) {
                    setTextLabel();
                }
            }
        });

        // Create the TextAre element used for word-wrapped Labels
        // and add a KeyboardListener for Esc key presses (not return in this case)

        changeTextArea = new RichTextArea();

        if (SUPPORT_TOOLBAR) {
            toolbar = new RichTextToolbar(changeTextArea, false);
            toolbar.setWidth("100%");
        }

//        // Add the components to a panel
//        grid = new Grid(2, 1);
//        grid.setStyleName("editableLabel-RichText");
//        grid.setWidget(0, 0, toolbar);
//        grid.setWidget(1, 0, changeTextArea);

        changeTextArea.setStyleName("editableLabel-textArea");
        changeText.sinkEvents(Event.KEYEVENTS);

        changeText.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_ENTER:
                        if (event.isAnyModifierKeyDown()) {
                            setTextLabel();
                        }
                        break;
                    case KeyCodes.KEY_ESCAPE:
                        cancelLabelChange();
                        break;
                }
            }
        });


        changeTextArea.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                if (isEditing) {
                    setTextLabel();
                }
            }
        });


        // Add panels/widgets to the widget panel
        instance.add(text);

        // Set initial visibilities.  This needs to be after
        // adding the widgets to the panel because the FlowPanel
        // will mess them up when added.
        text.setVisible(true);

        // Assume that this is a non word wrapped Label unless explicitly set otherwise
        text.setWordWrap(false);

        // Set the widget that this Composite represents
        initWidget(instance);
    }

    /**
     * @param cancelButtonText
     */
    protected Widget createCancelButton(String cancelButtonText) {
        if (text.getWordWrap()) {
            Button result = new Button();
            result.setStyleName("editableLabel-buttons");
            result.addStyleName("editableLabel-cancel");
            result.setText(cancelButtonText);
            result.addStyleName("btn");
            return result;
        } else {

            Label result = new Label("");
            result.setStyleName("editableLabel-button-images");
            result.addStyleName("editableLabel-cancel-image");
            result.setTitle(cancelButtonText);
            return result;
        }

    }

    /**
     * @param okButtonText
     */
    protected Widget createConfirmButton(String okButtonText) {
        if (text.getWordWrap()) {
            Button result = new Button();
            result.setStyleName("editableLabel-buttons");
            result.addStyleName("editableLabel-confirm");
            result.addStyleName("btn");
            result.addStyleName("primary");
            result.setText(okButtonText);
            return result;
        } else {
            Label result = new Label("");
            result.setStyleName("editableLabel-button-images");
            result.addStyleName("editableLabel-confirm-image");
            result.setTitle(okButtonText);
            return result;
        }
    }


    /**
     * Set the word wrapping on the label (if word wrapped then the editable
     * field becomes a TextArea, if not then the editable field is a TextBox.
     *
     * @param b Boolean value, true means Label is word wrapped, false means it is not.
     */
    public void setWordWrap(boolean b) {
        text.setWordWrap(b);

        if (text.getWordWrap() && confirmChange == null) {
            // Set up Confirmation Button
            confirmChange = createConfirmButton(okButtonText);

            if (!(confirmChange instanceof SourcesClickEvents)) {
                throw new RuntimeException("Confirm change button must allow for click events");
            }

            ((SourcesClickEvents) confirmChange).addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    setTextLabel();
                }
            });

            // Set up Cancel Button
            cancelChange = createCancelButton(cancelButtonText);
            if (!(cancelChange instanceof SourcesClickEvents)) {
                throw new RuntimeException("Cancel change button must allow for click events");
            }

            ((SourcesClickEvents) cancelChange).addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    cancelLabelChange();
                }
            });

            // Put the buttons in a panel
            buttonPanel = new FlowPanel();
            buttonPanel.setStyleName("editableLabel-buttonPanel");
            buttonPanel.add(confirmChange);
            buttonPanel.add(cancelChange);
        }
    }

    /**
     * Return whether the Label is word wrapped or not.
     */
    public boolean getWordWrap() {
        return text.getWordWrap();
    }

    /**
     * Return the text value of the Label
     */
    public String getText() {
        return plainText;
    }

    /**
     * Set the text value of the Label
     */
    public void setText(String newText) {
        if (formatter != null) {
            this.plainText = formatter.sanitize(newText);
        } else {
            this.plainText = newText;
        }
        String displayText = newText;
        if (editable && (displayText == null || displayText.isEmpty()) && placeholder != null && !placeholder.isEmpty()) {
            displayText = placeholder;
            text.addStyleName(EDITABLE_LABEL_PLACEHOLDER_STYLE);
        } else {
            if (displayText == null) {
                displayText = "";
            }
            text.removeStyleName(EDITABLE_LABEL_PLACEHOLDER_STYLE);
        }
        if (showBrief) {
            if (formatter != null) {
                displayText = formatter.formatRichText(displayText).replaceAll("<[^>]*>", " ").replaceAll("\n", " ");
            } else {
                displayText = displayText.replaceAll("<[^>]*>", " ").replaceAll("\n", " ");
            }
            text.setHTML(displayText);
        } else {
            if (formatter != null) {
                final String formatted = formatter.formatRichText(displayText);
                text.setHTML(prefix + formatted);
            } else {
                text.setHTML(prefix + displayText);
            }
        }
    }


    /**
     * Sets the number of visible lines for a word-wrapped editable label.
     *
     * @param number Number of visible lines.
     * @throws RuntimeException if the editable label is not word-wrapped.
     */
    public void setVisibleLines(int number) {
        if (text.getWordWrap()) {
//            changeTextArea.setVisibleLines(number);
        } else {
            throw new RuntimeException("Cannnot set number of visible lines for a non word-wrapped Editable Label");
        }
    }

    /**
     * Get the number of Visible Lines of editable area of a word-wrapped editable Label.
     *
     * @return Number of Visible Lines.
     * @throws RuntimeException If the Label is not word-wrapped.
     */
    public int getVisibleLines() {
        if (text.getWordWrap()) {
//            return changeTextArea.getVisibleLines();
            return 1;
        } else {
            throw new RuntimeException("Editable Label that is not word-wrapped has no number of Visible Lines");
        }
    }

    /**
     * Set maximum length of editable area.
     *
     * @param length Length of editable area.
     */
    public void setMaxLength(int length) {
        if (text.getWordWrap()) {
//            changeTextArea.setCharacterWidth(length);
        } else {
            changeText.setMaxLength(length);
        }
    }

    /**
     * Get maximum length of editable area.
     *
     * @return maximum length of editable area.
     */
    public int getMaxLength() {
        if (text.getWordWrap()) {
//            return changeTextArea.getCharacterWidth();
            return 0;
        } else {
            return changeText.getMaxLength();
        }
    }

    /**
     * Set the visible length of the editable area.
     *
     * @throws RuntimeExcpetion If editable label is word wrapped.
     */
    public void setVisibleLength(int length) {
        if (text.getWordWrap()) {
            throw new RuntimeException("Cannnot set visible length for a word-wrapped Editable Label");
        } else {
            changeText.setVisibleLength(length);
        }
    }

    /**
     * Get the visible length of the editable area.
     *
     * @return Visible length of editable area if not a word wrapped label.
     * @throws RuntimeException If editable label is word wrapped.
     */
    public int getVisibleLength() {
        if (text.getWordWrap()) {
            throw new RuntimeException("Cannnot get visible length for a word-wrapped Editable Label");
        } else {
            return changeText.getVisibleLength();
        }
    }


    public EditableLabel() {
        createEditableLabel("", defaultOkButtonText, defaultCancelButtonText, false);
    }

    /**
     * Constructor that changes default text for buttons and allows the setting of the wordwrap property directly.
     *
     * @param labelText   The initial text of the label.
     * @param okText      Text for use in overiding the default OK button text.
     * @param cancelText  Text for use in overiding the default CANCEL button text.
     * @param wordWrap    Boolean representing if the label should be word wrapped or not
     * @param doubleClick
     */
    public EditableLabel(String labelText, String okText,
                         String cancelText, boolean wordWrap, boolean doubleClick) {
        createEditableLabel(labelText, okText, cancelText, doubleClick);
        text.setWordWrap(wordWrap);
    }

    /**
     * Constructor that uses default text values for buttons and sets the word wrap property.
     *
     * @param labelText   The initial text of the label.
     * @param wordWrap    Boolean representing if the label should be word wrapped or not
     * @param doubleClick
     */
    public EditableLabel(String labelText, boolean wordWrap, boolean doubleClick) {
        createEditableLabel(labelText, defaultOkButtonText, defaultCancelButtonText, doubleClick);
        text.setWordWrap(wordWrap);
    }


    /**
     * Constructor that changes default button text.
     *
     * @param labelText   The initial text of the label.
     * @param okText      Text for use in overiding the default OK button text.
     * @param cancelText  Text for use in overiding the default CANCEL button text.
     * @param doubleClick
     */
    public EditableLabel(String labelText, String okText,
                         String cancelText, boolean doubleClick) {
        createEditableLabel(labelText, okText, cancelText, doubleClick);
    }

    /**
     * Constructor that uses default text values for buttons.
     *
     * @param labelText The initial text of the label.
     * @param onUpdate  Handler object for performing actions once label is updated.
     */
    public EditableLabel(String labelText) {
        createEditableLabel(labelText, defaultOkButtonText, defaultCancelButtonText, false);
    }

    public EditableLabel(String labelText, boolean doubleClick) {
        createEditableLabel(labelText, defaultOkButtonText, defaultCancelButtonText, doubleClick);
    }

    public HorizontalAlignmentConstant getHorizontalAlignment() {
        return text.getHorizontalAlignment();
    }

    public void setHorizontalAlignment(HorizontalAlignmentConstant align) {
        text.setHorizontalAlignment(align);
    }

    public void addClickListener(ClickListener listener) {
        this.text.addClickListener(listener);
    }

    public void removeClickListener(ClickListener listener) {
        this.text.removeClickListener(listener);
    }

    public void addMouseListener(MouseListener listener) {
        this.text.addMouseListener(listener);

    }

    public void removeMouseListener(MouseListener listener) {
        this.text.removeMouseListener(listener);
    }

    public void addChangeListener(ChangeListener listener) {
        if (changeListeners == null) {
            changeListeners = new ChangeListenerCollection();
        }
        changeListeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        if (changeListeners != null) {
            changeListeners.remove(listener);
            ;
        }
    }

    public void setOnEditAction(Runnable onEditAction) {
        this.onEditAction = onEditAction;
    }


    public void setOnEditEndAction(Runnable onEditEndAction) {
        this.onEditEndAction = onEditEndAction;
    }

    public void setFormatter(FormatUtil formatter) {
        this.formatter = formatter;
    }

    public void setDoubleClick(boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    public boolean isDoubleClick() {
        return doubleClick;
    }

    public void setShowBrief(boolean b) {
        showBrief = b;
    }

    public void setInputType(String type) {
        if (changeText != null) {
            changeText.getElement().setAttribute("type", type);

        }
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void startEdit() {
        changeTextLabel();
    }


    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

