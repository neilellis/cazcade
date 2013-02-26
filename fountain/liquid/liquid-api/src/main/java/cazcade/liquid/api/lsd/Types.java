/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;


import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum Types implements Type {
    T_NATIVE_SERVER_APPLICATION("InteractiveResource.ServerApplication.NativeApplication"),
    T_SERVER_WEB_APPLICATION("InteractiveResource.ServerApplication.WebApplication"),
    T_CLIENT_SERVER_WEB_APPLICATION("InteractiveResource.ClientServerApplication.WebApplication"),
    T_NATIVE_PLUGIN("InteractiveResource.PluginApplication.NativeApplication"),
    T_WEB_PLUGIN("InteractiveResource.PluginApplication.WebApplication"),
    T_POOL("Collection.Pool"),
    T_POOL2D("Collection.Pool.Pool2D"),
    T_DOCK_POOL("Collection.Pool.ListPool.Dock"),
    T_CLIPBOARD_POOL("Collection.Pool.ListPool.Clipboard"),
    T_BOARD("Collection.Pool.BoardPool"),
    T_COVERFLOW_POOL("Collection.Pool.CoverflowPool"),
    T_GRID_POOL("Collection.Pool.GridPool"),
    T_LIST_POOL("Collection.Pool.ListPool"),
    T_SLIDESHOW_POOL("Collection.Pool.SlideshowPool"),
    T_T_CUSTOMPOOL("Collection.Pool.CustomPool"),
    T_FEED_POOL("Collection.Pool.ListPool.FeedPool"),
    T_TRASH_POOL("Collection.Pool.ListPool.TrashPool"),
    T_CHECKLIST_POOL("Collection.Pool.ListPool.Checklist"),
    T_IMAGE("Image"),
    T_BITMAP_IMAGE_2D("Image.Bitmap.2DBitmap"),
    T_PHOTO2D("Image.Photo.Photo2D"),
    T_CUSTOM_OBJECT("InteractiveResource.Object.CustomObject"),
    T_EVENT_HANDLER("InteractiveResource.Event.EventHandler"),
    T_ACTIVATE_EVENT_HANDLER("InteractiveResource.Event.EventHandler.Activate"),
    T_HOVER_START_EVENT_HANDLER("InteractiveResource.Event.EventHandler.HoverStart"),
    T_HOVER_END_EVENT_HANDLER("InteractiveResource.Event.EventHandler.HoverEnd"),
    T_TEXT("Text"),
    T_RICH_TEXT("Text.RichText"),
    T_STICKY("Text.Hypertext.XHTMLFragment.Sticky"),
    T_NOTE("Text.Hypertext.XHTMLFragment.Note"),
    T_CAPTION("Text.Hypertext.XHTMLFragment.Caption"),
    T_HTML_FRAGMENT("Text.Hypertext.XHTMLFragment"),
    T_WEBPAGE("Text.Hypertext.Reference"),
    T_PLAIN_TEXT("Text.Plain.Label"),
    T_VIDEO("MovingImage.Video"),
    T_YOUTUBE_MOVIE("MovingImage.Video.2DVideo.YouTube"),
    T_HTML5_MOVIE("MovingImage.Video.2DVideo.HTML5Compatible"),
    T_FLASH_MOVIE("MovingImage.Video.2DVideo.Flash"),
    T_MICROBLOG("Text.Message"),
    T_MICROBLOG_ENTRY("Text.Message.Web"),
    T_RSS_MICROBLOG_ENTRY("Text.Message.Web.RSS"),
    T_ATOM_MICROBLOG_ENTRY("Text.Message.Web.Atom"),
    T_TWITTER_MICROBLOG_ENTRY("Text.Message.Twitter"),
    T_CHAT("Text.Message.Cazcade.Chat", "A simple chat, i.e. ephemeral, text message."),
    T_COMMENT("Text.Message.Cazcade.Comment", "A simple text comment."),
    T_TEXT_MESSAGE("Text.Message.Cazcade.Directed", "A directed text message that is persistent, like an email."),
    T_UPDATE("Text.StatusUpdate", "A Twitter like status update."),
    T_PRESENCE_UPDATE("Text.StatusUpdate.Presence", "A pool object status update."),
    T_OBJECT_UPDATE("Text.StatusUpdate.Object", "A pool object status update."),
    T_COMMENT_UPDATE("Text.StatusUpdate.Comment", "A comment based status update."),
    T_RSS_OR_ATOM_FEED("Collection.Feed.Web"),
    T_RSS_FEED("Collection.Feed.Web.RSS"),
    T_ATOM_FEED("Collection.Feed.Web.Atom"),
    T_FEED("Collection.Feed"),
    T_MICROBLOG_FEED("Collection.Feed.Message"),
    T_TWITTER_FEED("Collection.Feed.Message.Twitter"),
    T_FACEBOOK_FEED("Collection.Feed.Message.Facebook"),
    T_TWITTER_APPLICATION("InteractiveResource.PluginApplication.NativeApplication.Twitter"),
    T_FACEBOOK_APPLICATION("InteractiveResource.PluginApplication.NativeApplication.Facebook"),
    T_PERSON("Identity.Person"),
    T_USER("Identity.Person.User"),
    T_ALIAS("Identity.Person.Alias"),
    T_ALIAS_REF("Identity.Person.Reference.Alias"),
    T_ADDRESS("Location.Point.Address"),
    T_LIQUID_KEY_DICTIONARY_ENTRY("Data.Reference.DictionaryEntry.LiquidDictionaryEntry.LiquidKeyDictionaryEntry"),
    T_LIQUID_KEY_DICTIONARY("Collection.Dictionary.LiquidDictionary.LiquidKeyDictionary"),
    T_LIQUID_TYPE_DICTIONARY_ENTRY("Data.Reference.DictionaryEntry.LiquidDictionaryEntry.LiquidTypeDictionaryEntry"),
    T_LIQUID_TYPE_DICTIONARY("Collection.Dictionary.LiquidDictionary.LiquidTypeDictionary"),
    T_GENERIC_APPLICATION_OPERATIONAL_DATA("Data.Application.Operational"),
    T_GENERIC_REFERENCE_DATA("Data.Application.Reference"),
    T_DATA_STORE_REFERENCE_RESULT("System.Server.Result.Reference"),
    T_DATA_STORE_DEFERRED_RESULT("System.Server.Result.Deferred"),
    T_AUTHORIZATION_NOT_REQUIRED("System.AuthorizationResponse.NotRequired", "An attempt to authorize an item with no permissions defined was made, this entity includes the details."),
    T_AUTHORIZATION_INVALID("System.AuthorizationResponse.Invalid", "An invalid authorization request was made, this entity contains the details."),
    T_AUTHORIZATION_ACCEPTANCE("System.AuthorizationResponse.Acceptance", "The details of an accepted authorization request."),
    T_AUTHORIZATION_DENIAL("System.AuthorizationResponse.Denial", "The details of a denied authorization request."),
    T_SOCIAL_NETWORK("Collection.Network.SocialNetwork"),
    T_SESSION("System.Session.ClientSession"),
    T_VIEW("System.View.PoolObjectView"),
    T_REQUEST("System.Request"),
    T_EMPTY_RESULT("System.Entity.Empty"),
    T_EXCEPTION("System.Error.JavaException"),
    T_RESOURCE_NOT_FOUND("System.Error.ResourceNotFound"),
    T_DUPLICATE_RESOURCE_ERROR("System.Error.Duplicate"),
    T_SEARCH_RESULTS("Collection.List.Search"),
    T_USER_LIST("Collection.List.User"),
    T_BOARD_LIST("Collection.List.Board"),
    T_ALIAS_LIST("Collection.List.Alias"),
    T_ENTITY_LIST("Collection.List.Entity"),
    T_FEED_LIST("Collection.List.Feed"),
    T_COMMENT_LIST("Collection.List.Comment");

    //    SYSTEM_SERVER_POOL("Collection.Pool.SystemServerPool"),
    //    SYSTEM_CLIENT_POOL("Collection.Pool.ListPool.Stream"),


    @Nonnull
    private final Type value;
    @Nullable
    private final LiquidUUID id          = null;
    private       String     description = "";


    public static String getNameForValue(final String value) {
        return ReverseLookup.map.get(value);
    }

    Types(@Nonnull final String value, final String description) {
        this.value = new TypeImpl(value, false);
        this.description = description;
        ReverseLookup.map.put(value, name());
    }

    Types(@Nonnull final String value) {
        this.value = new TypeImpl(value, false);
        ReverseLookup.map.put(value, name());
    }

    @Nonnull
    public String asString() {
        return value.asString();
    }

    public boolean canBe(final Types type) {
        return value.canBe(type);
    }

    public boolean equals(final Type type) {
        return value.equals(type);
    }

    public Type getClassOnlyType() {
        return value.getClassOnlyType();
    }

    public String getFamily() {
        return value.getFamily();
    }

    public List<String> getFlavors() {
        return value.getFlavors();
    }

    @Nonnull
    public String getGenus() {
        return value.getGenus();
    }

    public Type getParentType() {
        return value.getParentType();
    }

    public String getTypeClass() {
        return value.getTypeClass();
    }

    @Nonnull
    public String getValue() {
        return value.asString();
    }

    public boolean isA(final Types dictionaryType) {
        return value.isA(dictionaryType);
    }

    public boolean isSystemType() {
        return value.isSystemType();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public String getDescription() {
        return description;
    }

    @Nullable
    public LiquidUUID getId() {
        return id;
    }

    private static class ReverseLookup {
        @Nonnull
        private static final Map<String, String> map = new HashMap<String, String>();
    }
}