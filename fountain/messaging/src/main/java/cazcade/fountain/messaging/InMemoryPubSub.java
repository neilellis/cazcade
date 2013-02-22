/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.messaging;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageHandler;
import cazcade.liquid.api.LiquidRequest;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author neilellis@cazcade.com
 */
public class InMemoryPubSub implements FountainPubSub {

    private static final Logger log = Logger.getLogger(InMemoryPubSub.class);

    private static final Map<Long, KeyedListener> listeners = new HashMap<Long, KeyedListener>();
    private static final AtomicLong               counter   = new AtomicLong();

    @Override
    public void dispatch(final String key, final LiquidRequest request) {

        for (final KeyedListener listener : getListenersCopy()) {
            if (listener.matches(key)) {
                try {
                    listener.getHandler().handle(request);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
    }

    private ArrayList<KeyedListener> getListenersCopy() {
        ArrayList<KeyedListener> keyedListeners;
        synchronized (listeners) {
            keyedListeners = new ArrayList<KeyedListener>(listeners.values());
        }
        return keyedListeners;
    }

    @Override
    public LiquidRequest sendSync(final String key, final LiquidRequest request) throws Exception {
        for (final KeyedListener listener : getListenersCopy()) {
            if (listener.matches(key)) {
                return listener.getHandler().handle(request);
            }
        }
        throw new FountainPubSubException("No listener for synchronous send to " + key);
    }

    @Override
    public synchronized long addListener(final String key, final LiquidMessageHandler<LiquidRequest> requestLiquidMessageHandler) {
        final KeyedListener keyedListener = new KeyedListener(key, requestLiquidMessageHandler);
        final long count = counter.incrementAndGet();
        synchronized (listeners) {
            listeners.put(count, keyedListener);
        }
        return count;
    }

    private static class KeyedListener {

        private final String                              key;
        private final LiquidMessageHandler<LiquidRequest> handler;

        private KeyedListener(final String key, final LiquidMessageHandler<LiquidRequest> handler) {
            //To change body of created methods use File | Settings | File Templates.
            this.key = key;
            this.handler = handler;
        }

        public String getKey() {
            return key;
        }

        public LiquidMessageHandler<LiquidRequest> getHandler() {
            return handler;
        }

        public boolean matches(final String keyToMatch) {
            return (key.endsWith(".*") || key.endsWith(".#")) && key.substring(0, key.length() - 2).equals(keyToMatch)
                   || key.equals(keyToMatch);

        }
    }

    @Override
    public void removeListener(final long listenerId) {
        synchronized (listeners) {
            listeners.remove(listenerId);
        }
    }

    @Override
    public Collector createCollector(final ArrayList<String> queues) {
        final Collector inMemoryCollector = new InMemoryCollector(this);
        for (final String queue : queues) {
            inMemoryCollector.bind(queue);
        }
        return inMemoryCollector;
    }

    @Override
    public Collector createCollector() {
        return new InMemoryCollector(this);
    }

    private static class InMemoryCollector implements Collector, LiquidMessageHandler<LiquidRequest> {
        private Map<String, Long>   listeners = new HashMap<String, Long>();
        private List<LiquidMessage> messages  = new ArrayList<LiquidMessage>();
        private FountainPubSub pubSub;

        public InMemoryCollector(FountainPubSub fountainPubSub) {
            pubSub = fountainPubSub;
        }

        @Override
        public LiquidMessage readSingle() {
            if (messages.size() > 0) {
                return messages.remove(0);
            } else {
                return null;
            }
        }

        @Override
        public synchronized void close() {
            for (Long id : listeners.values()) {
                pubSub.removeListener(id);
            }
            listeners.clear();
        }

        @Override
        public synchronized void unbind(final String key) {
            final Long listenerId = listeners.get(key);
            pubSub.removeListener(listenerId);
            listeners.remove(key);
        }

        @Override
        public synchronized void bind(String key) {
            if (!listeners.containsKey(key)) {
                listeners.put(key, pubSub.addListener(key, this));
            }
        }

        @Override
        public synchronized ArrayList<LiquidMessage> readMany() {
            final ArrayList<LiquidMessage> result = new ArrayList<LiquidMessage>(messages);
            messages.clear();
            return result;
        }

        @Override
        public Set<String> getKeys() {
            return listeners.keySet();
        }

        @Override
        public void bind(ArrayList<String> keys) {
            for (String key : keys) {
                bind(key);
            }
        }

        @Nonnull @Override
        public LiquidRequest handle(LiquidRequest message) throws Exception {
            messages.add(message);
            return message;
        }
    }
}
