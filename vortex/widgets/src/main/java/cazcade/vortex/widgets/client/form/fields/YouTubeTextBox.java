/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class YouTubeTextBox extends VortexTextBox {

    interface RegexTextBoxUiBinder extends UiBinder<HTMLPanel, YouTubeTextBox> {}

    @Nonnull
    public static final  String               YOU_TUBE_URL_REGEX  = "http[s]?://www\\.youtube\\.com/watch\\?(.*)v=([A-Za-z0-9\\-_]+).*";
    @Nonnull
    public static final  String               INVALID_URL_MESSAGE = "Please supply a valid YouTube id or URL (e.g. http://www.youtube.com/watch?v=hfjGRBFd7mQ or hfjGRBFd7mQ)";
    @Nonnull
    private static final String               VIDEO_REGEX         = "[A-Za-z0-9\\-_]+";
    private static final RegexTextBoxUiBinder ourUiBinder         = GWT.create(RegexTextBoxUiBinder.class);
    private              String               oldText             = "";

    public YouTubeTextBox() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        textBox.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(@Nonnull final KeyPressEvent event) {
                if (!event.isAnyModifierKeyDown()) {
                    final int keyCode = event.getUnicodeCharCode();

                    if (keyCode == KeyCodes.KEY_ENTER) {
                        update(true);
                    }

                }
            }
        });

        textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(final ValueChangeEvent<String> stringValueChangeEvent) {
                update(true);
            }
        });


        textBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(@Nonnull final KeyUpEvent event) {
                if (!event.isAnyModifierKeyDown()) {
                    final String text = textBox.getText();
                    if (!oldText.equals(text)) {
                        oldText = text;
                        update(false);
                        if (text.length() > 0) {
                            showValidity();
                        }
                    }
                }
            }
        });

    }

    public void update(final boolean andCallOnChange) {
        String text = textBox.getText();
        if (text.startsWith("http")) {
            if (text.matches(YOU_TUBE_URL_REGEX)) {
                text = text.replaceAll("http[s]?://www\\.youtube\\.com/watch\\?(.*)v=([A-Za-z0-9\\-_]+).*", "$2");
                setValue(text);
                processChange();
                errorMessage.setText("");
            } else {
                errorMessage.setText(INVALID_URL_MESSAGE);
            }
        } else {
            if (text.matches(VIDEO_REGEX)) {
                setValue(text);
                errorMessage.setText("");
                if (andCallOnChange) {
                    processChange();
                }
            } else {
                errorMessage.setText(INVALID_URL_MESSAGE);
            }
        }

    }

    @Override
    public boolean isValid() {
        return textBox.getText().matches(YOU_TUBE_URL_REGEX) || textBox.getText().matches(VIDEO_REGEX);
    }

    @Nonnull @Override
    public TransferEntity getEntityDiff() {
        final TransferEntity newEntity = SimpleEntity.createNewEntity(getEntity().type());
        if (getEntity().hasURI()) { newEntity.$(Dictionary.URI, getEntity().uri().toString()); }
        newEntity.$(Dictionary.EURI, "youtube:" + getValue());
        newEntity.$(Dictionary.SOURCE, "http://www.youtube.com/embed/" + getValue() + "?wmode=transparent");
        newEntity.$(Dictionary.IMAGE_URL, "http://img.youtube.com/vi/" + getValue() + "/hqdefault.jpg");
        newEntity.$(Dictionary.MEDIA_ID, getValue());


        return newEntity;
    }
}