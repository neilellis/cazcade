/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

function setupScrolling(objectID, scrollEvent) {
    var containerObj = document.getElementById(objectID);
    if (containerObj) {
        var eventListenerObject = containerObj;
        var isWebkit = false;

        if (navigator && navigator.vendor) {
            isWebkit = navigator.vendor.match("Apple") || navigator.vendor.match("Google");
        }

        // some events will need to point to the containing object tag
        if (isWebkit && containerObj.parentNode.tagName.toLowerCase() == "object") {
            eventListenerObject = containerObj.parentNode;
        }

        var scrollHandler = function (event) {
            var xDelta = 0;
            var yDelta = 0;

            if (!event) // IE special case
                event = window.event;

            if (event.wheelDelta) { // IE/Webkit/Opera
                if (event.wheelDeltaX) { // horizontal scrolling is supported in Webkit
                    // Webkit can scroll two directions simultaneously
                    xDelta = event.wheelDeltaX;
                    yDelta = event.wheelDeltaY;
                } else { // fallback to standard scrolling interface
                    yDelta = event.wheelDelta;
                }

                // you'll have to play with these,
                //browsers on Windows and OS X handle them differently
                xDelta /= 120;
                yDelta /= 120;

                if (window.opera) { // Opera special case
                    yDelta = -yDelta;
                }// Opera doesn't support hscroll; vscroll is also buggy
            } else if (event.detail) { // Firefox (Mozilla)
                yDelta = -event.detail / 1.5;

                if (event.axis) { // hscroll supported in FF3.1+
                    if (event.axis == event.HORIZONTAL_AXIS) {
                        // FF can only scroll one dirction at a time
                        xDelta = yDelta;
                        yDelta = 0;
                    }
                }
            }

            try {
                scrollEvent(xDelta, yDelta);
            } catch (e) {
            }
            ;

            if (event.preventDefault)
                event.preventDefault();
            event.returnValue = false;
        };

        if (window.addEventListener && eventListenerObject) { // not IE
            eventListenerObject.addEventListener('mouseover', function (e) {
                if (isWebkit) {
                    containerObj.onmousewheel = scrollHandler;
                } else {
                    containerObj.addEventListener("DOMMouseScroll", scrollHandler, false);
                }
            }, false);
        } else { // IE
            containerObj.onmouseover = function (e) {
                document.onmousewheel = scrollHandler;
            };
        }
    } else { /* No scroll */
//      window.alert('Could not find scroll element '+objectID);
    }
}