/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.http.client.*;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class UrlTextBox extends RegexTextBox {

    private boolean validUrl;

    public UrlTextBox() {
        super();
        setRegex("https?://.*");
    }

    @Override protected void showValidity() {
        if (super.isValid()) {
            final RequestBuilder builder = new RequestBuilder(RequestBuilder.HEAD, "./_validate-url?url=" + URL.encode(getValue()));

            try {
                builder.sendRequest(null, new RequestCallback() {
                    @Override public void onError(final Request request, final Throwable exception) {
                        // Couldn't connect to server (could be timeout, SOP violation, etc.)
                        UrlTextBox.super.showValidity();
                        ClientLog.log(exception);
                    }

                    @Override public void onResponseReceived(final Request request, final Response response) {
                        validUrl = response.getStatusCode() < 400;
                        UrlTextBox.super.showValidity();
                    }
                });
            } catch (RequestException e) {
                ClientLog.log(e);
                validUrl = false;
                UrlTextBox.super.showValidity();
            }
        }
        else {
            UrlTextBox.super.showValidity();
        }
    }

    @Override protected String cleanUpText(final String text) {

        if ((text.startsWith("http://") || text.startsWith("https://")) || text.indexOf('.') < 0) {
            return text;
        }
        else {
            return "http://" + text;
        }
    }

    @Override public boolean isValid() {
        return validUrl && super.isValid();
    }
}