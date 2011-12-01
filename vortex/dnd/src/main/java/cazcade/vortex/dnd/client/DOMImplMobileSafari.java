package cazcade.vortex.dnd.client;

import com.google.gwt.user.client.DOM;

/**
 * Native implementation associated with {@link DOM}.
 *
 * @deprecated this is now implemented in GWT 2.3 :-)
 */
public class DOMImplMobileSafari {
//
//    @SuppressWarnings("unused")
//    private static JavaScriptObject captureElem;
//
//    @SuppressWarnings("unused")
//    private static JavaScriptObject dispatchCapturedEvent;
//
//    @SuppressWarnings("unused")
//    private static JavaScriptObject dispatchCapturedMouseEvent;
//
//    @SuppressWarnings("unused")
//    private static JavaScriptObject dispatchEvent;
//
//    public DOMImplMobileSafari() {
//        ClientLog.log("Mobile Safari DOM in use.", null);
//    }
//
//    public native int eventGetTypeInt(String eventType) /*-{
//    switch (eventType) {
//    case "blur": return 0x01000;
//    case "change": return 0x00400;
//    case "click": return 0x00001;
//    case "dblclick": return 0x00002;
//    case "focus": return 0x00800;
//    case "keydown": return 0x00080;
//    case "keypress": return 0x00100;
//    case "keyup": return 0x00200;
//    case "load": return 0x08000;
//    case "losecapture": return 0x02000;
//    case "mousedown": return 0x00004;
//    case "mousemove": return 0x00040;
//    case "mouseout": return 0x00020;
//    case "mouseover": return 0x00010;
//    case "mouseup": return 0x00008;
//    case "scroll": return 0x04000;
//    case "error": return 0x10000;
//    case "mousewheel": return 0x20000;
//    case "DOMMouseScroll": return 0x20000;
//    case "contextmenu": return 0x40000;
//    case "paste": return 0x80000;
//
//    case "touchstart": return 0x100000;
//    case "touchmove": return 0x200000;
//    case "touchcancel": return 0x400000;
//    case "touchend": return 0x800000;
//
//    case "gesturestart": return 0x1000000;
//    case "gestureend": return 0x2000000;
//    case "gesturechange": return 0x4000000;
//    case "orientationchange": return 0x8000000;
//
////    case "vtxdrag": return 0x10000000;
////    case "vtxenddrag": return 0x20000000;
////    case "vtxholddrag": return 0x40000000;
////    case "vtxflick": return 0x80000000;
////
////    case "vtxtap": return 0x100000000;
////    case "vtxdoubletap": return 0x200000000;
////    case "vtxshortpress": return 0x400000000;
////    case "vtxlongpress": return 0x800000000;
////
////    case "vtxgesturestart": return 0x8000000000;
//
//
//
//    }
//  }-*/;
//
//
//    @Override
//    protected native void initEventSystem() /*-{
//
//       @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent = $entry(function(evt) {
//         if ((@cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent)(evt)) {
//           var cap = @cazcade.vortex.dnd.client.DOMImplMobileSafari::captureElem;
//           if (cap && cap.__listener) {
//             if (@cazcade.vortex.dnd.client.DOMImplMobileSafari::isMyListener(Ljava/lang/Object;)(cap.__listener)) {
//               @com.google.gwt.user.client.DOM::dispatchEvent(Lcom/google/gwt/user/client/Event;Lcom/google/gwt/user/client/Element;Lcom/google/gwt/user/client/EventListener;)(evt, cap, cap.__listener);
//               evt.stopPropagation();
//             }
//           }
//         }
//       });
//
//       @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent = $entry(function(evt) {
//         if (!@com.google.gwt.user.client.DOM::previewEvent(Lcom/google/gwt/user/client/Event;)(evt)) {
//           evt.stopPropagation();
//           evt.preventDefault();
//           return false;
//         }
//
//         return true;
//       });
//
//       @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent = $entry(function(evt) {
//         var listener, curElem = this;
//         while (curElem && !(listener = curElem.__listener)) {
//           curElem = curElem.parentNode;
//         }
//
//         if (curElem && curElem.nodeType != 1) {
//           curElem = null;
//         }
//
//         if (listener) {
//           if (@com.google.gwt.user.client.impl.DOMImpl::isMyListener(Ljava/lang/Object;)(listener)) {
//             @com.google.gwt.user.client.DOM::dispatchEvent(Lcom/google/gwt/user/client/Event;Lcom/google/gwt/user/client/Element;Lcom/google/gwt/user/client/EventListener;)(evt, curElem, listener);
//           }
//         }
//       });
//
//       $wnd.addEventListener('click', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('dblclick', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('mousedown', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('mouseup', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('mousemove', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('mouseover', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, true);
//       $wnd.addEventListener('mouseout', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('mousewheel', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('keydown', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, true);
//       $wnd.addEventListener('keyup', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, true);
//       $wnd.addEventListener('keypress', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, true);
//
//       $wnd.addEventListener('touchstart', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('touchend', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('touchmove', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('touchcancel', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//
//       $wnd.addEventListener('gesturestart', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('gestureend', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('gesturechange', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedMouseEvent, false);
//       $wnd.addEventListener('orientationchange', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, false);
//
//
////       $wnd.addEventListener('vtxdrag', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, true);
////       $wnd.addEventListener('vtxenddrag', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, true);
////       $wnd.addEventListener('vtxholddrag', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, true);
////       $wnd.addEventListener('vtxflick', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, true);
////       $wnd.addEventListener('vtxtap', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, true);
////       $wnd.addEventListener('vtxdoubletap', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, true);
////       $wnd.addEventListener('vtxlongpress', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, true);
////       $wnd.addEventListener('vtxgesturestart', @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchCapturedEvent, true);
//
//
//
//
//     }-*/;
//
//    protected native void sinkEventsImpl(Element elem, int bits) /*-{
//
////       window.alert("Bits ="+bits);
//
//       var chMask = (elem.__eventBits || 0) ^ bits;
//       elem.__eventBits = bits;
//       if (!chMask) return;
//
//       if (chMask & 0x00001) elem.onclick       = (bits & 0x00001) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x00002) elem.ondblclick    = (bits & 0x00002) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x00004) elem.onmousedown   = (bits & 0x00004) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x00008) elem.onmouseup     = (bits & 0x00008) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x00010) elem.onmouseover   = (bits & 0x00010) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x00020) elem.onmouseout    = (bits & 0x00020) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x00040) elem.onmousemove   = (bits & 0x00040) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x00080) elem.onkeydown     = (bits & 0x00080) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x00100) elem.onkeypress    = (bits & 0x00100) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x00200) elem.onkeyup       = (bits & 0x00200) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x00400) elem.onchange      = (bits & 0x00400) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x00800) elem.onfocus       = (bits & 0x00800) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x01000) elem.onblur        = (bits & 0x01000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x02000) elem.onlosecapture = (bits & 0x02000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x04000) elem.onscroll      = (bits & 0x04000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x08000) elem.onload        = (bits & 0x08000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x10000) elem.onerror       = (bits & 0x10000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x20000) elem.onmousewheel  = (bits & 0x20000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x40000) elem.oncontextmenu = (bits & 0x40000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x80000) elem.onpaste       = (bits & 0x80000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//
//       if (chMask & 0x100000) elem.ontouchstart       = (bits & 0x100000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x200000) elem.ontouchmove       = (bits & 0x200000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x400000) elem.ontouchcancel       = (bits & 0x400000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x800000) elem.ontouchend       = (bits & 0x800000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//
//       if (chMask & 0x1000000) elem.ongesturestart       = (bits & 0x1000000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x2000000) elem.ongesturechange       = (bits & 0x2000000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//       if (chMask & 0x4000000) elem.ongestureend       = (bits & 0x4000000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//
//       if (chMask & 0x8000000) elem.onorientationchange       = (bits & 0x8000000) ?
//           @cazcade.vortex.dnd.client.DOMImplMobileSafari::dispatchEvent : null;
//
//     }-*/;
//
//    @Override
//    public Element eventGetFromElement(Event evt) {
//        if (evt.getType().equals("mouseover")) {
//            return evt.getRelatedTarget().cast();
//        }
//
//        if (evt.getType().equals("mouseout")) {
//            return evt.getTarget().cast();
//        }
//
//        return null;
//    }
//
//    @Override
//    public Element eventGetToElement(Event evt) {
//        if (evt.getType().equals("mouseover")) {
//            return evt.getTarget().cast();
//        }
//
//        if (evt.getType().equals("mouseout")) {
//            return evt.getRelatedTarget().cast();
//        }
//
//        return null;
//    }
//
//    @Override
//    public native Element getChild(Element elem, int index) /*-{
//    var count = 0, child = elem.firstChild;
//    while (child) {
//      var next = child.nextSibling;
//      if (child.nodeType == 1) {
//        if (index == count)
//          return child;
//        ++count;
//      }
//      child = next;
//    }
//
//    return null;
//  }-*/;
//
//    @Override
//    public native int getChildCount(Element elem) /*-{
//    var count = 0, child = elem.firstChild;
//    while (child) {
//      if (child.nodeType == 1)
//        ++count;
//      child = child.nextSibling;
//    }
//    return count;
//  }-*/;
//
//    @Override
//    public native int getChildIndex(Element parent, Element toFind) /*-{
//    var count = 0, child = parent.firstChild;
//    while (child) {
//      if (child === toFind) {
//        return count;
//      }
//      if (child.nodeType == 1) {
//        ++count;
//      }
//      child = child.nextSibling;
//    }
//    return -1;
//  }-*/;
//
//    @Override
//    public native void insertChild(Element parent, Element toAdd, int index) /*-{
//    var count = 0, child = parent.firstChild, before = null;
//    while (child) {
//      if (child.nodeType == 1) {
//        if (count == index) {
//          before = child;
//          break;
//        }
//        ++count;
//      }
//      child = child.nextSibling;
//    }
//
//    parent.insertBefore(toAdd, before);
//  }-*/;
//
//    @Override
//    public void releaseCapture(Element elem) {
//        maybeInitializeEventSystem();
//        releaseCaptureImpl(elem);
//    }
//
//    @Override
//    public void setCapture(Element elem) {
//        maybeInitializeEventSystem();
//        setCaptureImpl(elem);
//    }
//
//    @Override
//    public void sinkBitlessEvent(Element elem, String eventTypeName) {
//      maybeInitializeEventSystem();
//      sinkBitlessEventImpl(elem, eventTypeName);
//    }
//
//
//    @Override
//    public void sinkEvents(Element elem, int bits) {
//        maybeInitializeEventSystem();
//        sinkEventsImpl(elem, bits);
//    }
//
//
//    private native void releaseCaptureImpl(Element elem) /*-{
//    if (elem === @cazcade.vortex.dnd.client.DOMImplMobileSafari::captureElem) {
//      @cazcade.vortex.dnd.client.DOMImplMobileSafari::captureElem = null;
//    }
//  }-*/;
//
//    private native void setCaptureImpl(Element elem) /*-{
//    @cazcade.vortex.dnd.client.DOMImplMobileSafari::captureElem = elem;
//  }-*/;
}
