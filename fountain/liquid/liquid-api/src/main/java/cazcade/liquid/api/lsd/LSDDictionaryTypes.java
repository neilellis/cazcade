package cazcade.liquid.api.lsd;


import cazcade.liquid.api.LiquidUUID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum LSDDictionaryTypes implements LSDType {
    NATIVE_SERVER_APPLICATION("InteractiveResource.ServerApplication.NativeApplication"),
    SERVER_WEB_APPLICATION("InteractiveResource.ServerApplication.WebApplication"),
    CLIENT_SERVER_WEB_APPLICATION("InteractiveResource.ClientServerApplication.WebApplication"),
    NATIVE_PLUGIN("InteractiveResource.PluginApplication.NativeApplication"),
    WEB_PLUGIN("InteractiveResource.PluginApplication.WebApplication"),

    POOL("Collection.Pool"),
    POOL2D("Collection.Pool.Pool2D"),
    //    SYSTEM_CLIENT_POOL("Collection.Pool.ListPool.Stream"),
    DOCK_POOL("Collection.Pool.ListPool.Dock"),
    CLIPBOARD_POOL("Collection.Pool.ListPool.Clipboard"),
    //    SYSTEM_SERVER_POOL("Collection.Pool.SystemServerPool"),
    BOARD("Collection.Pool.BoardPool"),
    COVERFLOW_POOL("Collection.Pool.CoverflowPool"),
    GRID_POOL("Collection.Pool.GridPool"),
    LIST_POOL("Collection.Pool.ListPool"),
    SLIDESHOW_POOL("Collection.Pool.SlideshowPool"),
    CUSTOMPOOL("Collection.Pool.CustomPool"),
    FEED_POOL("Collection.Pool.ListPool.FeedPool"),
    TRASH_POOL("Collection.Pool.ListPool.TrashPool"),

    CHECKLIST_POOL("Collection.Pool.ListPool.Checklist"),
    IMAGE("Image"),
    BITMAP_IMAGE_2D("Image.Bitmap.2DBitmap"),
    PHOTO2D("Image.Photo.Photo2D"),

    CUSTOM_OBJECT("InteractiveResource.Object.CustomObject"),
    EVENT_HANDLER("InteractiveResource.Event.EventHandler"),
    ACTIVATE_EVENT_HANDLER("InteractiveResource.Event.EventHandler.Activate"),
    HOVER_START_EVENT_HANDLER("InteractiveResource.Event.EventHandler.HoverStart"),
    HOVER_END_EVENT_HANDLER("InteractiveResource.Event.EventHandler.HoverEnd"),

    TEXT("Text"),
    RICH_TEXT("Text.RichText"),
    STICKY("Text.Hypertext.XHTMLFragment.Sticky"),
    NOTE("Text.Hypertext.XHTMLFragment.Note"),
    CAPTION("Text.Hypertext.XHTMLFragment.Caption"),
    HTML_FRAGMENT("Text.Hypertext.XHTMLFragment"),
    WEBPAGE("Text.Hypertext.Reference"),
    PLAIN_TEXT("Text.Plain.Label"),

    VIDEO("MovingImage.Video"),
    YOUTUBE_MOVIE("MovingImage.Video.2DVideo.YouTube"),
    HTML5_MOVIE("MovingImage.Video.2DVideo.HTML5Compatible"),
    FLASH_MOVIE("MovingImage.Video.2DVideo.Flash"),


    MICROBLOG("Text.Message"),
    MICROBLOG_ENTRY("Text.Message.Web"),
    RSS_MICROBLOG_ENTRY("Text.Message.Web.RSS"),
    ATOM_MICROBLOG_ENTRY("Text.Message.Web.Atom"),
    TWITTER_MICROBLOG_ENTRY("Text.Message.Twitter"),

    CHAT("Text.Message.Cazcade.Chat", "A simple chat, i.e. ephemeral, text message."),
    COMMENT("Text.Message.Cazcade.Comment", "A simple text comment."),
    TEXT_MESSAGE("Text.Message.Cazcade.Directed", "A directed text message that is persistent, like an email."),

    UPDATE("Text.StatusUpdate", "A Twitter like status update."),
    PRESENCE_UPDATE("Text.StatusUpdate.Presence", "A pool object status update."),
    OBJECT_UPDATE("Text.StatusUpdate.Object", "A pool object status update."),
    COMMENT_UPDATE("Text.StatusUpdate.Comment", "A comment based status update."),

    RSS_OR_ATOM_FEED("Collection.Feed.Web"),
    RSS_FEED("Collection.Feed.Web.RSS"),
    ATOM_FEED("Collection.Feed.Web.Atom"),

    FEED("Collection.Feed"),
    MICROBLOG_FEED("Collection.Feed.Message"),
    TWITTER_FEED("Collection.Feed.Message.Twitter"),
    FACEBOOK_FEED("Collection.Feed.Message.Facebook"),

    TWITTER_APPLICATION("InteractiveResource.PluginApplication.NativeApplication.Twitter"),
    FACEBOOK_APPLICATION("InteractiveResource.PluginApplication.NativeApplication.Facebook"),

    PERSON("Identity.Person"),
    USER("Identity.Person.User"),
    ALIAS("Identity.Person.Alias"),
    ALIAS_REF("Identity.Person.Reference.Alias"),

    ADDRESS("Location.Point.Address"),

    LIQUID_KEY_DICTIONARY_ENTRY("Data.Reference.DictionaryEntry.LiquidDictionaryEntry.LiquidKeyDictionaryEntry"),
    LIQUID_KEY_DICTIONARY("Collection.Dictionary.LiquidDictionary.LiquidKeyDictionary"),
    LIQUID_TYPE_DICTIONARY_ENTRY("Data.Reference.DictionaryEntry.LiquidDictionaryEntry.LiquidTypeDictionaryEntry"),
    LIQUID_TYPE_DICTIONARY("Collection.Dictionary.LiquidDictionary.LiquidTypeDictionary"),

    GENERIC_APPLICATION_OPERATIONAL_DATA("Data.Application.Operational"),
    GENERIC_REFERENCE_DATA("Data.Application.Reference"),

    DATA_STORE_REFERENCE_RESULT("System.Server.Result.Reference"),
    DATA_STORE_DEFERRED_RESULT("System.Server.Result.Deferred"),

    AUTHORIZATION_NOT_REQUIRED("System.AuthorizationResponse.NotRequired", "An attempt to authorize an item with no permissions defined was made, this entity includes the details."),
    AUTHORIZATION_INVALID("System.AuthorizationResponse.Invalid", "An invalid authorization request was made, this entity contains the details."),
    AUTHORIZATION_ACCEPTANCE("System.AuthorizationResponse.Acceptance", "The details of an accepted authorization request."),
    AUTHORIZATION_DENIAL("System.AuthorizationResponse.Denial", "The details of a denied authorization request."),

    SOCIAL_NETWORK("Collection.Network.SocialNetwork"),
    SESSION("System.Session.ClientSession"),
    VIEW("System.View.PoolObjectView"),

    EMPTY_RESULT("System.Entity.Empty"),
    EXCEPTION("System.Error.JavaException"),
    RESOURCE_NOT_FOUND("System.Error.ResourceNotFound"),
    DUPLICATE_RESOURCE_ERROR("System.Error.Duplicate"),

    SEARCH_RESULTS("Collection.List.Search"),

    USER_LIST("Collection.List.User"),
    BOARD_LIST("Collection.List.Board"),
    ALIAS_LIST("Collection.List.Alias"),
    ENTITY_LIST("Collection.List.Entity"),
    FEED_LIST("Collection.List.Feed"),
    COMMENT_LIST("Collection.List.Comment");

    public String getGenus() {
        return value.getGenus();
    }

    public String getFamily() {
        return value.getFamily();
    }

    public String getTypeClass() {
        return value.getTypeClass();
    }

    public List<String> getFlavors() {
        return value.getFlavors();
    }

    public String asString() {
        return value.asString();
    }

    public LSDType getClassOnlyType() {
        return value.getClassOnlyType();
    }

    public LSDType getParentType() {
        return value.getParentType();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public boolean isSystemType() {
        return value.isSystemType();
    }

    public boolean isA(LSDDictionaryTypes dictionaryType) {
        return value.isA(dictionaryType);
    }

    public boolean canBe(LSDDictionaryTypes type) {
        return value.canBe(type);
    }

    private LSDType value;
    private LiquidUUID id = null;
    private String description = "";

    public String getDescription() {
        return description;
    }

    public boolean equals(LSDType lsdType) {
        return value.equals(lsdType);
    }


    private static class ReverseLookup {
        private static Map<String, String> map = new HashMap<String, String>();
    }

    private LSDDictionaryTypes(String value) {
        this.value = new LSDTypeImpl(value, false);
        ReverseLookup.map.put(value, name());
    }

    private LSDDictionaryTypes(String value, String description) {
        this.value = new LSDTypeImpl(value, false);
        this.description = description;
        ReverseLookup.map.put(value, name());
    }

    public String getValue() {
        return value.asString();
    }

    public LiquidUUID getId() {
        return id;
    }


    public static String getNameForValue(String value) {
        return ReverseLookup.map.get(value);
    }
}