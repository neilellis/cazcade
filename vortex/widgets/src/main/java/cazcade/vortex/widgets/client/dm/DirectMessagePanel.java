package cazcade.vortex.widgets.client.dm;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.SendRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.BusFactory;
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
import com.google.gwt.user.client.ui.*;

/**
 * @author neilellis@cazcade.com
 */
public class DirectMessagePanel extends Composite {
    private String recipient;
    private Runnable onFinish;
    private IFrameElement fe;

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setOnFinish(Runnable onFinish) {
        this.onFinish = onFinish;
    }


    interface DirectMessagePopupUiBinder extends UiBinder<HTMLPanel, DirectMessagePanel> {
    }

    private static DirectMessagePopupUiBinder ourUiBinder = GWT.create(DirectMessagePopupUiBinder.class);

    @UiField
    RichTextArea textBox;
    //    @UiField
//    VortexListBox listBox;
    @UiField
    Label cancelButton;
    @UiField
    Label sendButton;

    public DirectMessagePanel() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        initTextBox();
    }



    public void start() {
        fe.focus();
    }


    private void initTextBox() {
//        textBox.setHeight("180px");
        DOM.setStyleAttribute(getWidget().getElement(), "overflow", "hidden");
//        DOM.setStyleAttribute(textBox.getElement(), "width", "492px");
        DOM.setStyleAttribute(textBox.getElement(), "border", "none");
        DOM.setStyleAttribute(textBox.getElement(), "margin", "4px");
        DOM.setStyleAttribute(textBox.getElement(), "marginTop", "7px");
        DOM.setStyleAttribute(textBox.getElement(), "borderRadius", "4px");
        DOM.setStyleAttribute(textBox.getElement(), "borderBottom", "1px solid #eee");
        DOM.setStyleAttribute(textBox.getElement(), "borderRight", "1px solid #eee");
        DOM.setStyleAttribute(textBox.getElement(), "borderTop", "1px solid #ddd");
        DOM.setStyleAttribute(textBox.getElement(), "borderLeft", "1px solid #ddd");
        textBox.addInitializeHandler(new InitializeHandler() {
            public void onInitialize(InitializeEvent ie) {
                fe = (IFrameElement)
                        textBox.getElement().cast();
                fe.setFrameBorder(0);
                fe.setMarginWidth(0);
                fe.setScrolling("no");
                Style s = fe.getContentDocument().getBody().getStyle();
                s.setProperty("fontFamily", "'Helvetica Neue',Arial,sans- serif");
                s.setProperty("fontSize", "1em");
                s.setProperty("width", "100%");
                s.setOverflow(Style.Overflow.HIDDEN);
                s.setBackgroundColor("#f5f5f5");
                s.setMargin(4, Style.Unit.PX);
                s.setColor("#222");
                fe.focus();
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                textBox.setText("");
                onFinish.run();
            }
        });

        sendButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        final String text = SafeHtmlUtils.fromString(textBox.getText()).asString();
                        textBox.setText("");
                        LSDSimpleEntity messageEntity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.TEXT_MESSAGE);
                        messageEntity.setAttribute(LSDAttribute.TEXT_EXTENDED, text);
                        BusFactory.getInstance().send(new SendRequest(messageEntity, recipient), new AbstractResponseCallback<SendRequest>() {
                            @Override
                            public void onSuccess(SendRequest message, SendRequest response) {
                                onFinish.run();
                            }

                            @Override
                            public void onFailure(SendRequest message, SendRequest response) {
                                textBox.setText(text);
                                super.onFailure(message, response);
                            }

                            @Override
                            public void onException(SendRequest message, Throwable error) {
                                textBox.setText(text);
                                super.onException(message, error);
                            }
                        });
                    }
                });
            }
        });
    }

}