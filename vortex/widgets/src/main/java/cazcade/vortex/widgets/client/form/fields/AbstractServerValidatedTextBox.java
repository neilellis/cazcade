/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.vortex.widgets.client.Resources;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractServerValidatedTextBox extends VortexTextBox {
    public static final int MAX_USERNAME_LENGTH = 40;
    protected boolean acceptable;
    @Nonnull
    protected String oldText = "";
    protected boolean showAvailability;

    protected abstract void checkAvailable();

    @Override public void clear() {
        super.clear();
        oldText = "";
    }

    protected void init() {
        textBox.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(@Nonnull final KeyPressEvent event) {
                if (!event.isAnyModifierKeyDown()) {
                    //
                    final int keyCode = event.getUnicodeCharCode();
                    //
                    if (keyCode == KeyCodes.KEY_ENTER && isValid()) {
                        event.preventDefault();
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                final String text = cleanUpText(textBox.getText());
                                textBox.setText(text);
                                ValueChangeEvent.fire(AbstractServerValidatedTextBox.this, text);
                            }
                        });
                    }
                }

            }
        });

        textBox.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(final BlurEvent event) {
                errorMessage.setText("");
            }
        });


        textBox.addKeyUpHandler(new CleanUpKeyUpHandler() {
            @Override
            public void onKeyUp(@Nonnull final KeyUpEvent event) {
                super.onKeyUp(event);
                if (!isValidName()) {
                    showInvalidName();
                }
                else {
                    checkAvailable();
                }
            }
        });

    }

    @Nonnull @Override
    protected String cleanUpText(@Nonnull String text) {
        if (text.length() == 0) {
            return text;
        }
        if ("!@".indexOf(text.charAt(0)) >= 0) {
            text = text.charAt(0) + text.substring(1).replaceAll("[^@a-zA-Z0-9_-]", "");
        }
        else {
            text = text.replaceAll("[^@a-zA-Z0-9_-]", "");
        }
        return text;
    }

    public boolean isShowAvailability() {
        return showAvailability;
    }

    public void setShowAvailability(final boolean showAvailability) {
        if (!showAvailability) {
            acceptable = true;
        }
        this.showAvailability = showAvailability;
    }

    @Override
    public boolean isValid() {
        return acceptable && isValidName();
    }

    public boolean isValidName() {
        return textBox.getText().matches("[a-zA-Z][a-zA-Z0-9_-]*");
    }

    protected void showInvalidName() {
        validityImage.setResource(Resources.INSTANCE.userNotAvailable());
        errorMessage.setText("Invalid name");
        errorMessage.removeStyleName("success");
    }

    protected void showTaken() {
        validityImage.setResource(Resources.INSTANCE.userNotAvailable());
        errorMessage.setText("taken");
        errorMessage.removeStyleName("success");
    }

    protected void showAvailable() {
        validityImage.setResource(Resources.INSTANCE.userAvailable());
        errorMessage.setText("available");
        errorMessage.addStyleName("success");
    }
}
