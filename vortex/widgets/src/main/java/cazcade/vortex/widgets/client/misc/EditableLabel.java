/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.misc;


/*
 * This code is part of the GWT Widget Library
 */


import cazcade.vortex.common.client.FormatUtil;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.widgets.client.misc.toolbar.RichTextToolbar;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
public class EditableLabel extends Composite implements HasWordWrap, HasText, HasHorizontalAlignment, SourcesClickEvents, SourcesChangeEvents, SourcesMouseEvents {

    @Nonnull
    public static final  String  EDITABLE_LABEL_PLACEHOLDER_STYLE = "editableLabel-placeholder";
    private static final boolean SUPPORT_TOOLBAR                  = false;
    /**
     * Default String value for OK button
     */
    @Nonnull
    private final        String  defaultOkButtonText              = "OK";
    /**
     * Default String value for Cancel button
     */
    @Nonnull
    private final        String  defaultCancelButtonText          = "Cancel";
    ChangeListenerCollection changeListeners;
    /**
     * TextBox element to enable text to be changed if Label is not word wrapped
     */
    private TextBox      changeText;
    /**
     * TextArea element to enable text to be changed if Label is wordwrapped
     */
    private RichTextArea changeTextArea;
    @Nonnull
    private FormatUtil   formatter;
    /**
     * Label element, which is initially is diplayed.
     */
    @Nonnull
    private HTML         text;
    /**
     * String element that contains the original text of a
     * Label prior to it being edited.
     */
    @Nullable
    private String       originalText;
    /**
     * Simple button to confirm changes
     */
    private Widget       confirmChange;
    /**
     * Simple button to cancel changes
     */
    private Widget       cancelChange;
    /**
     * Flag to indicate that Label is in editing mode.
     */
    private boolean      isEditing;
    /**
     * Flag to indicate that label can be edited.
     */
    private boolean editable = true;
    private RichTextToolbar toolbar;
    private FlowPanel       buttonPanel;
    private FlowPanel       instance;
    private Runnable        onEditAction;
    private Runnable        onEditEndAction;
    @Nullable
    private String          plainText;
    private String          okButtonText;
    private String          cancelButtonText;
    private boolean         doubleClick;
    private boolean         showBrief;
    private String placeholder = "Click to edit";
    private String prefix      = "";
    private String color;
    private String fontFamily;
    private String fontSize;
    private int    height;

    public EditableLabel() {
        super();
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
    public EditableLabel(final String labelText, final String okText, final String cancelText, final boolean wordWrap, final boolean doubleClick) {
        super();
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
    public EditableLabel(final String labelText, final boolean wordWrap, final boolean doubleClick) {
        super();
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
    public EditableLabel(final String labelText, final String okText, final String cancelText, final boolean doubleClick) {
        super();
        createEditableLabel(labelText, okText, cancelText, doubleClick);
    }

    /**
     * Constructor that uses default text values for buttons.
     *
     * @param labelText The initial text of the label.
     * @param onUpdate  Handler object for performing actions once label is updated.
     */
    public EditableLabel(final String labelText) {
        super();
        createEditableLabel(labelText, defaultOkButtonText, defaultCancelButtonText, false);
    }


    public EditableLabel(final String labelText, final boolean doubleClick) {
        super();
        createEditableLabel(labelText, defaultOkButtonText, defaultCancelButtonText, doubleClick);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Allows the setting of the isEditable flag, marking
     * the label as editable or not.
     *
     * @param flag True or False value depending if the Label is to be editable or not
     */
    public void setEditable(final boolean flag) {
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
            fontFamily = WidgetUtil.getComputedStyle(text.getElement(), "fontFamily");
            fontSize = WidgetUtil.getComputedStyle(text.getElement(), "fontSize");
            color = getColor() != null ? color : WidgetUtil.getComputedStyle(text.getElement(), "color");
            height = getWidget().getOffsetHeight();

            // Change the view from Label to TextBox and Buttons
            text.removeFromParent();

            if (text.getWordWrap()) {
                // If Label word wrapped use the TextArea to edit
                //                instance.add(toolbar);
                instance.add(changeTextArea);
                instance.add(buttonPanel);
                changeTextArea.setHTML(originalText);
                changeTextArea.setFocus(true);
            }
            else {
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
        }
        else {
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
        }
        else {
            // Set the Label to be the text in the Text Box
            setText(changeText.getText());
        }
        // Set the object back to display label rather than TextBox and Buttons
        restoreVisibility();

        // Call the update method provided in the Constructor
        // (this could be anything from alerting the user through to
        // Making an AJAX call to store the data.
        //updater.onChange(this);
        if (changeListeners != null) {
            changeListeners.fireChange(this);
        }
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
                public void onDoubleClick(final DoubleClickEvent event) {
                    if (editable) {
                        changeTextLabel();
                    }
                }
            });
        }
        else {
            text.addClickListener(new ClickListener() {
                public void onClick(final Widget sender) {
                    if (editable) {
                        changeTextLabel();
                    }
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
    private void createEditableLabel(final String labelText, final String okButtonText, final String cancelButtonText, final boolean doubleClick) {
        this.okButtonText = okButtonText;
        this.cancelButtonText = cancelButtonText;
        this.doubleClick = doubleClick;
        // Put everything in a VerticalPanel
        instance = new FlowPanel();
        instance.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        instance.setWidth("100%");
        plainText = labelText;

        // Create the Label element and add a ClickListener to call out Change method when clicked
        if (formatter != null) {
            text = new HTML(formatter.formatRichText(labelText));
        }
        else {
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
            public void onKeyUp(@Nonnull final KeyUpEvent event) {
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
            public void onBlur(final BlurEvent event) {
                if (isEditing) {
                    setTextLabel();
                }
            }
        });

        // Create the TextAre element used for word-wrapped Labels
        // and add a KeyboardListener for Esc key presses (not return in this case)

        changeTextArea = new RichTextArea();
        changeTextArea.addInitializeHandler(new InitializeHandler() {
            public void onInitialize(final InitializeEvent ie) {
                final IFrameElement fe = (IFrameElement) changeTextArea.getElement().cast();
                fe.setFrameBorder(0);
                //                fe.setMarginWidth(10);
                changeTextArea.setHeight(height + "px");
                //                fe.setScrolling("no");
                final Style s = fe.getContentDocument().getBody().getStyle();
                s.setProperty("fontFamily", fontFamily == null || fontFamily.isEmpty()
                                            ? "'Helvetica Neue',Arial,sans-serif"
                                            : fontFamily);
                //                Window.alert(fontFamily);
                //                Window.alert(fontSize);
                s.setProperty("fontSize", fontSize);
                s.setProperty("wordWrap", "break-word");
                s.setOverflow(Style.Overflow.HIDDEN);
                s.setColor(color);
                fe.focus();
            }
        });


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
            public void onKeyUp(@Nonnull final KeyUpEvent event) {
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
            public void onBlur(final BlurEvent event) {
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

        initWidget(instance);


        // Set the widget that this Composite represents


    }


    @Override protected void onAttach() {
        super.onAttach();    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * @param cancelButtonText
     */
    @Nonnull
    protected Widget createCancelButton(final String cancelButtonText) {
        if (text.getWordWrap()) {
            final Button result = new Button();
            result.setStyleName("editableLabel-buttons");
            result.addStyleName("editableLabel-cancel");
            result.setText(cancelButtonText);
            result.addStyleName("btn");
            return result;
        }
        else {

            final Label result = new Label("");
            result.setStyleName("editableLabel-button-images");
            result.addStyleName("editableLabel-cancel-image");
            result.setTitle(cancelButtonText);
            return result;
        }

    }

    /**
     * @param okButtonText
     */
    @Nonnull
    protected Widget createConfirmButton(final String okButtonText) {
        if (text.getWordWrap()) {
            final Button result = new Button();
            result.setStyleName("editableLabel-buttons");
            result.addStyleName("editableLabel-confirm");
            result.addStyleName("btn");
            result.addStyleName("primary");
            result.setText(okButtonText);
            return result;
        }
        else {
            final Label result = new Label("");
            result.setStyleName("editableLabel-button-images");
            result.addStyleName("editableLabel-confirm-image");
            result.setTitle(okButtonText);
            return result;
        }
    }

    /**
     * Return whether the Label is word wrapped or not.
     */
    public boolean getWordWrap() {
        return text.getWordWrap();
    }

    /**
     * Set the word wrapping on the label (if word wrapped then the editable
     * field becomes a TextArea, if not then the editable field is a TextBox.
     *
     * @param b Boolean value, true means Label is word wrapped, false means it is not.
     */
    public void setWordWrap(final boolean b) {
        text.setWordWrap(b);

        if (text.getWordWrap() && confirmChange == null) {
            // Set up Confirmation Button
            confirmChange = createConfirmButton(okButtonText);

            if (!(confirmChange instanceof SourcesClickEvents)) {
                throw new RuntimeException("Confirm change button must allow for click events");
            }

            ((SourcesClickEvents) confirmChange).addClickListener(new ClickListener() {
                public void onClick(final Widget sender) {
                    setTextLabel();
                }
            });

            // Set up Cancel Button
            cancelChange = createCancelButton(cancelButtonText);
            if (!(cancelChange instanceof SourcesClickEvents)) {
                throw new RuntimeException("Cancel change button must allow for click events");
            }

            ((SourcesClickEvents) cancelChange).addClickListener(new ClickListener() {
                public void onClick(final Widget sender) {
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
     * Return the text value of the Label
     */
    @Nonnull
    public String getText() {
        return plainText;
    }

    /**
     * Set the text value of the Label
     */
    @Override
    public void setText(@Nullable final String newText) {
        String displayText = newText;
        if (displayText == null) {
            displayText = "";
        }
        if (formatter != null) {
            plainText = formatter.sanitize(newText);
        }
        else {
            plainText = newText;
        }
        if (editable && displayText.isEmpty() && placeholder != null && !placeholder.isEmpty()) {
            displayText = placeholder;
            text.addStyleName(EDITABLE_LABEL_PLACEHOLDER_STYLE);
        }
        else {
            text.removeStyleName(EDITABLE_LABEL_PLACEHOLDER_STYLE);
        }
        if (showBrief) {
            if (formatter != null) {
                displayText = formatter.formatRichText(displayText).replaceAll("<[^>]*>", " ").replaceAll("\n", " ");
            }
            else {
                displayText = displayText.replaceAll("<[^>]*>", " ").replaceAll("\n", " ");
            }
            text.setHTML(displayText);
        }
        else {
            if (formatter != null) {
                final String formatted = formatter.formatRichText(displayText);
                text.setHTML(prefix + formatted);
            }
            else {
                text.setHTML(prefix + displayText);
            }
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
        }
        else {
            throw new RuntimeException("Editable Label that is not word-wrapped has no number of Visible Lines");
        }
    }

    /**
     * Sets the number of visible lines for a word-wrapped editable label.
     *
     * @param number Number of visible lines.
     * @throws RuntimeException if the editable label is not word-wrapped.
     */
    public void setVisibleLines(final int number) {
        if (text.getWordWrap()) {
            //            changeTextArea.setVisibleLines(number);
        }
        else {
            throw new RuntimeException("Cannnot set number of visible lines for a non word-wrapped Editable Label");
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
        }
        else {
            return changeText.getMaxLength();
        }
    }

    /**
     * Set maximum length of editable area.
     *
     * @param length Length of editable area.
     */
    public void setMaxLength(final int length) {
        if (text.getWordWrap()) {
            //            changeTextArea.setCharacterWidth(length);
        }
        else {
            changeText.setMaxLength(length);
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
        }
        else {
            return changeText.getVisibleLength();
        }
    }

    /**
     * Set the visible length of the editable area.
     *
     * @throws RuntimeException If editable label is word wrapped.
     */
    public void setVisibleLength(final int length) {
        if (text.getWordWrap()) {
            throw new RuntimeException("Cannnot set visible length for a word-wrapped Editable Label");
        }
        else {
            changeText.setVisibleLength(length);
        }
    }

    public HorizontalAlignmentConstant getHorizontalAlignment() {
        return text.getHorizontalAlignment();
    }

    public void setHorizontalAlignment(final HorizontalAlignmentConstant align) {
        text.setHorizontalAlignment(align);
    }

    public void addClickListener(final ClickListener listener) {
        text.addClickListener(listener);
    }

    public void removeClickListener(final ClickListener listener) {
        text.removeClickListener(listener);
    }

    public void addMouseListener(final MouseListener listener) {
        text.addMouseListener(listener);

    }

    public void removeMouseListener(final MouseListener listener) {
        text.removeMouseListener(listener);
    }

    public void addChangeListener(final ChangeListener listener) {
        if (changeListeners == null) {
            changeListeners = new ChangeListenerCollection();
        }
        changeListeners.add(listener);
    }

    public void removeChangeListener(final ChangeListener listener) {
        if (changeListeners != null) {
            changeListeners.remove(listener);
        }
    }

    public void setOnEditAction(final Runnable onEditAction) {
        this.onEditAction = onEditAction;
    }

    public void setOnEditEndAction(final Runnable onEditEndAction) {
        this.onEditEndAction = onEditEndAction;
    }

    public void setFormatter(@Nullable final FormatUtil formatter) {
        this.formatter = formatter;
    }

    public boolean isDoubleClick() {
        return doubleClick;
    }

    public void setDoubleClick(final boolean doubleClick) {
        this.doubleClick = doubleClick;
    }

    public void setShowBrief(final boolean b) {
        showBrief = b;
    }

    public void setInputType(final String type) {
        if (changeText != null) {
            changeText.getElement().setAttribute("type", type);

        }
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(final String placeholder) {
        this.placeholder = placeholder;
    }

    public void startEdit() {
        changeTextLabel();
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
}

