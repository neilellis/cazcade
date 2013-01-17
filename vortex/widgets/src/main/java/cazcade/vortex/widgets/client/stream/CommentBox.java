/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.request.AddCommentRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RichTextArea;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class CommentBox extends Composite {

    private LiquidURI uri;

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
        DOM.setStyleAttribute(getWidget().getElement(), "overflow", "hidden");
        DOM.setStyleAttribute(textBox.getElement(), "width", "592px");
        DOM.setStyleAttribute(textBox.getElement(), "border", "1px solid #ccc");
        DOM.setStyleAttribute(textBox.getElement(), "margin", "4px");
        DOM.setStyleAttribute(textBox.getElement(), "marginTop", "7px");
        DOM.setStyleAttribute(textBox.getElement(), "borderRadius", "4px");
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
                s.setProperty("fontFamily", "'Helvetica Neue',Arial,sans- serif");
                s.setProperty("fontSize", "0.8em");
                s.setProperty("width", "480x");
                s.setOverflow(Style.Overflow.HIDDEN);
                s.setBackgroundColor("#eee");
                s.setColor("black");
                fe.focus();
            }
        });

        //        style.setInnerText("* { color:white; }");
        //        head.appendChild(style);


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

                BusFactory.getInstance().send(new AddCommentRequest(uri, text), new AbstractResponseCallback<AddCommentRequest>() {
                    @Override
                    public void onSuccess(final AddCommentRequest message, final AddCommentRequest response) {
                        Track.getInstance().trackEvent("Comment", "Comment Added");
                    }

                    @Override
                    public void onFailure(final AddCommentRequest message, @Nonnull final AddCommentRequest response) {
                        textBox.setText(text);
                        super.onFailure(message, response);
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


    public void init(final LiquidURI uri) {
        this.uri = uri;
    }
}