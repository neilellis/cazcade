/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl.xstream;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class LiquidXStreamFactory {
    @Nonnull
    private static final XStream xstream = new XStream(new XppDriver(new XmlFriendlyReplacer("__dollar__", "_")));

    static {
        //        xstream.alias("create-object-request", CreatePoolObjectRequest.class);
        //        xstream.alias("create-pool-request", CreatePoolRequest.class);
        //        xstream.alias("create-user-request", CreateUserRequest.class);
        //        xstream.alias("create-session-request", CreateSessionRequest.class);
        //        xstream.alias("create-alias-request", CreateAliasRequest.class);
        //
        //        xstream.alias("retrieve-object-request", RetrievePoolObjectRequest.class);
        //        xstream.alias("retrieve-pool-request", RetrievePoolRequest.class);
        //        xstream.alias("retrieve-user-request", RetrieveUserRequest.class);
        //        xstream.alias("retrieve-session-request", RetrieveUserRequest.class);
        //        xstream.alias("retrieve-alias-request", RetrieveAliasRequest.class);
        //
        //        xstream.alias("update-object-request", UpdatePoolObjectRequest.class);
        //        xstream.alias("update-pool-request", UpdatePoolRequest.class);
        //        xstream.alias("update-user-request", UpdateUserRequest.class);
        //        xstream.alias("update-session-request", UpdateSessionRequest.class);
        //        xstream.alias("update-alias-request", UpdateAliasRequest.class);
        //
        //        xstream.alias("delete-object-request", DeletePoolObjectRequest.class);
        //        xstream.alias("delete-pool-request", DeletePoolRequest.class);
        //        xstream.alias("delete-session-request", DeleteSessionRequest.class);
        //        xstream.alias("delete-user-request", DeleteUserRequest.class);
        //        xstream.alias("delete-alias-request", DeleteAliasRequest.class);
        //
        //        xstream.alias("authorization-request", AuthorizationRequest.class);
        //        xstream.alias("move-object-request", MovePoolObjectRequest.class);
        //        xstream.alias("rotatexy-object-request", RotateXYPoolObjectRequest.class);
        //        xstream.alias("scale-object-request", ScalePoolObjectRequest.class);
        xstream.aliasType("request", LiquidRequest.class);
        xstream.alias("response", LiquidMessage.class);
        xstream.alias("entity", LSDTransferEntity.class, LSDSimpleEntity.class);

        xstream.registerConverter(new RequestConverter());
        xstream.registerConverter(new LSDEntityConverter());
        xstream.registerConverter(new LiquidURIConverter());
        xstream.registerConverter(new LiquidUUIDConverter());
        xstream.setMode(XStream.NO_REFERENCES);
    }

    @Nonnull
    public static XStream getXstream() {
        return xstream;
    }
}
