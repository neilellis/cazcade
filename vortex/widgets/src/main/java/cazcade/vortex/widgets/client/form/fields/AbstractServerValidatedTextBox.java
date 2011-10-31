package cazcade.vortex.widgets.client.form.fields;

import cazcade.vortex.widgets.client.Resources;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.*;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractServerValidatedTextBox extends VortexTextBox {
    public static final int MAX_USERNAME_LENGTH = 40;
    protected boolean acceptable;
    protected String oldText = "";
    protected boolean showAvailability;

    protected abstract void checkAvailable();

    protected void init() {
        textBox.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (!event.isAnyModifierKeyDown()) {
//
                    final int keyCode = event.getUnicodeCharCode();
//
                    if (keyCode == KeyCodes.KEY_ENTER && isValid()) {
                        event.preventDefault();
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                String text = cleanUpText(textBox.getText());
                                textBox.setText(text);
                                onChangeAction.run();
                            }
                        });
                    }
                }

            }
        });

        textBox.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                errorMessage.setText("");
            }
        });


        textBox.addKeyUpHandler(new CleanUpKeyUpHandler());

    }

    @Override
    protected String cleanUpText(String text) {
        if (text.length() == 0) {
            return text;
        }
        if ("!@".indexOf(text.charAt(0)) >= 0) {
            text = text.charAt(0) + text.substring(1).replaceAll("[^@a-zA-Z0-9_.-]", "");
        } else {
            text = text.replaceAll("[^@a-zA-Z0-9_.-]", "");
        }
        return text;
    }


    public boolean isShowAvailability() {
        return showAvailability;
    }

    public void setShowAvailability(boolean showAvailability) {
        if (!showAvailability) {
            acceptable = true;
        }
        this.showAvailability = showAvailability;
    }

    @Override
    public boolean isValid() {
        return acceptable && textBox.getText().matches("[a-zA-Z][a-zA-Z0-9_.-]*");
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
