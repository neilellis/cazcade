/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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

    @Nonnull
    public static final String YOU_TUBE_URL_REGEX = "http[s]?://www\\.youtube\\.com/watch\\?(.*)v=([A-Za-z0-9\\-_]+).*";
    private             String oldText            = "";

    @Nonnull
    private static final String VIDEO_REGEX = "[A-Za-z0-9\\-_]+";


    @Nonnull
    public static final String INVALID_URL_MESSAGE = "Please supply a valid YouTube id or URL (e.g. http://www.youtube.com/watch?v=hfjGRBFd7mQ or hfjGRBFd7mQ)";


    interface RegexTextBoxUiBinder extends UiBinder<HTMLPanel, YouTubeTextBox> {}

    private static final RegexTextBoxUiBinder ourUiBinder = GWT.create(RegexTextBoxUiBinder.class);

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
                onChange();
                errorMessage.setText("");
            }
            else {
                errorMessage.setText(INVALID_URL_MESSAGE);
            }
        }
        else {
            if (text.matches(VIDEO_REGEX)) {
                setValue(text);
                errorMessage.setText("");
                if (andCallOnChange) {
                    onChange();
                }
            }
            else {
                errorMessage.setText(INVALID_URL_MESSAGE);
            }
        }

    }


    @Override
    public boolean isValid() {
        return textBox.getText().matches(YOU_TUBE_URL_REGEX) || textBox.getText().matches(VIDEO_REGEX);
    }

    @Nonnull @Override
    public LSDTransferEntity getEntityDiff() {
        final LSDTransferEntity newEntity = LSDSimpleEntity.createNewEntity(getEntity().getTypeDef());
        if (getEntity().hasURI()) { newEntity.setAttribute(LSDAttribute.URI, getEntity().getURI().toString()); }
        newEntity.setAttribute(LSDAttribute.EURI, "youtube:" + getValue());
        newEntity.setAttribute(LSDAttribute.SOURCE, "http://www.youtube.com/embed/" + getValue() + "?wmode=transparent");
        newEntity.setAttribute(LSDAttribute.IMAGE_URL, "http://img.youtube.com/vi/" + getValue() + "/hqdefault.jpg");
        newEntity.setAttribute(LSDAttribute.MEDIA_ID, getValue());


        return newEntity;
    }
}