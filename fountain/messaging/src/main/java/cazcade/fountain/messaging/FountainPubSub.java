package cazcade.fountain.messaging;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageHandler;
import cazcade.liquid.api.LiquidRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainPubSub {
    void dispatch(String key, LiquidRequest request);

    LiquidRequest sendSync(String key, LiquidRequest request) throws Exception;

    long addListener(String key, LiquidMessageHandler<LiquidRequest> requestLiquidMessageHandler);

    void removeListener(long listenerId);

    Collector createCollector(ArrayList<String> queues);

    Collector createCollector();

    public interface Collector {

        LiquidMessage readSingle();

        void close();

        void unbind(String key);

        void bind(String key);

        List<LiquidMessage> readMany();

        Set<String> getKeys();

        void bind(ArrayList<String> keys);
    }
}
