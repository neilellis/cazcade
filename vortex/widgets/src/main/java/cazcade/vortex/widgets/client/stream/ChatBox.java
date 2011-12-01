package cazcade.vortex.widgets.client.stream;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.request.ChatRequest;
import cazcade.vortex.bus.client.AbstractResponseCallback;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.widgets.client.stream.chat.ChatParser;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RichTextArea;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ChatBox extends Composite {

    @Nonnull
    private final Bus bus = BusFactory.getInstance();
    private LiquidURI uri;
    @Nonnull
    private final ChatParser chatParser = new ChatParser();

    interface VortexAddCommentBoxUiBinder extends UiBinder<HTMLPanel, ChatBox> {
    }

    private static final VortexAddCommentBoxUiBinder ourUiBinder = GWT.create(VortexAddCommentBoxUiBinder.class);
    @UiField
    RichTextArea textBox;

    public ChatBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTextBox();


    }

    private void initTextBox() {
        textBox.setHeight("65px");
        DOM.setStyleAttribute(getWidget().getElement(), "overflow", "hidden");
        DOM.setStyleAttribute(textBox.getElement(), "width", "592px");
//        DOM.setStyleAttribute(textBox.getElement(), "border", "1px solid #ccc");
        DOM.setStyleAttribute(textBox.getElement(), "margin", "4px");
        DOM.setStyleAttribute(textBox.getElement(), "marginTop", "7px");
        DOM.setStyleAttribute(textBox.getElement(), "borderRadius", "4px");
        textBox.addInitializeHandler(new InitializeHandler() {
            public void onInitialize(InitializeEvent ie) {
                IFrameElement fe = (IFrameElement)
                        textBox.getElement().cast();
                fe.setFrameBorder(0);
                fe.setMarginWidth(0);
                fe.setScrolling("no");
                Style s = fe.getContentDocument().getBody().getStyle();
                s.setProperty("fontFamily", "'Helvetica Neue',Arial,sans- serif");
                s.setProperty("fontSize", "0.8em");
                s.setProperty("width", "480x");
                s.setOverflow(Style.Overflow.HIDDEN);
                s.setBackgroundColor("#fff");
                s.setColor("black");
                fe.focus();
            }
        });

        //        style.setInnerText("* { color:white; }");
//        head.appendChild(style);
        textBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(@Nonnull KeyUpEvent event) {
                if (uri != null) {
                    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && !textBox.getText().isEmpty()) {
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                final String text = SafeHtmlUtils.fromString(textBox.getText()).asString();
                                //Oh my god what a hack!!! But they behave so badly, best just remove and re-add.
                                if (text.startsWith("/")) {
                                    if (chatParser.parse(text)) {
                                        rebuildTextBox();
                                    } else {
                                        Window.alert("Chat commands starts with / like /direct - but that command was not recognized");
                                    }
                                } else {
                                    sendMessage(text);
                                }
                            }

                            private void sendMessage(final String text) {
                                rebuildTextBox();

                                bus.send(new ChatRequest(uri, text), new AbstractResponseCallback<ChatRequest>() {
                                    @Override
                                    public void onSuccess(ChatRequest message, ChatRequest response) {
                                    }

                                    @Override
                                    public void onFailure(ChatRequest message, @Nonnull ChatRequest response) {
                                        textBox.setText(text);
                                        super.onFailure(message, response);
                                    }

                                    @Override
                                    public void onException(@Nonnull ChatRequest message, @Nonnull Throwable error) {
                                        textBox.setText(text);
                                        super.onException(message, error);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }


    private void rebuildTextBox() {
        RichTextArea oldTextBox = textBox;
        textBox = new RichTextArea();
        textBox.setVisible(false);
        initTextBox();
        ((HTMLPanel) getWidget()).add(textBox);
        oldTextBox.removeFromParent();
        textBox.setVisible(true);
    }

    public void init(final LiquidURI uri) {
        this.uri = uri;
    }
}