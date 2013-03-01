/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.request.AddCommentRequest;
import cazcade.vortex.bus.client.AbstractMessageCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.gwt.util.client.WidgetUtil;
import cazcade.vortex.gwt.util.client.analytics.Track;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RichTextArea;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CommentBox extends Composite {

    private LURI uri;

    interface VortexAddCommentBoxUiBinder extends UiBinder<HTMLPanel, CommentBox> {}

    private static final VortexAddCommentBoxUiBinder ourUiBinder = GWT.create(VortexAddCommentBoxUiBinder.class);
    @UiField RichTextArea textBox;
    @UiField Button       sendButton;

    public CommentBox() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        initTextBox();


    }

    private void initTextBox() {
        textBox.setHeight("65px");
        //todo: update this to proper style manipulation
        getWidget().getElement().getStyle().setProperty("overflow", "hidden");
        Style style = textBox.getElement().getStyle();
        style.setProperty("width", "592px");
        style.setProperty("border", "1px solid #ccc");
        style.setProperty("margin", "4px");
        style.setProperty("marginTop", "7px");
        style.setProperty("borderRadius", "4px");

        textBox.addKeyDownHandler(new KeyDownHandler() {
            @Override public void onKeyDown(final KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    performSubmitAction();
                }
            }
        });
        textBox.addInitializeHandler(new InitializeHandler() {
            public void onInitialize(final InitializeEvent ie) {
                final IFrameElement fe = (IFrameElement) textBox.getElement().cast();
                fe.setFrameBorder(0);
                fe.setMarginWidth(0);
                fe.setScrolling("no");
                final Style s = fe.getContentDocument().getBody().getStyle();
                s.setProperty("fontFamily", WidgetUtil.getComputedStyle(getWidget().getElement(), "fontFamily"));
                s.setProperty("width", "480x");
                s.setProperty("padding", "0.6em");
                s.setOverflow(Style.Overflow.HIDDEN);
                s.setBackgroundColor("#eee");
                s.setColor("black");
                fe.focus();
            }
        });

        sendButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                performSubmitAction();
            }


        });


    }

    private void performSubmitAction() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                final String text = SafeHtmlUtils.fromString(textBox.getText()).asString();
                //Oh my god what a hack!!! But they behave so badly, best just remove and re-add.
                sendMessage(text);
            }

            private void sendMessage(final String text) {
                textBox.setText("");

                Bus.get().send(new AddCommentRequest(uri, text), new AbstractMessageCallback<AddCommentRequest>() {
                    @Override
                    public void onSuccess(final AddCommentRequest original, final AddCommentRequest message) {
                        Track.getInstance().trackEvent("Comment", "Comment Added");
                    }

                    @Override
                    public void onFailure(final AddCommentRequest original, @Nonnull final AddCommentRequest message) {
                        textBox.setText(text);
                        super.onFailure(original, message);
                    }

                    @Override
                    public void onException(@Nonnull final AddCommentRequest message, @Nonnull final Throwable error) {
                        textBox.setText(text);
                        super.onException(message, error);
                    }
                });
            }
        });
    }


    public void init(final LURI uri) {
        this.uri = uri;
    }
}