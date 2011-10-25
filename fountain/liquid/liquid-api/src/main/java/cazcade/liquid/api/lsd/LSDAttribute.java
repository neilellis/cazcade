package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidUUID;

import java.util.HashMap;
import java.util.Map;


public final class LSDAttribute {
    public static final LSDAttribute TYPE = LSDAttribute.create("type", "type:", "The type of an entity as defined in http://wiki.cazcade.com/display/LIQUID/Liquid+Dictionary#LiquidDictionary-Type", true, false, false, false, false, true, false);


    public static final LSDAttribute ID = LSDAttribute.create("id", "uuid:", "A unique identitifer for this entity based on the Java UUID http://java.sun.com/j2se/1.5.0/docs/api/java/util/UUID.html . ", true, false, false, false, false, true, false);
    public static final LSDAttribute URI = LSDAttribute.create("uri", "uri:", "A universally unique identifier for this resource that is human readable following internal URI schemes.", true, true, false, false, false, true, false);
    public static final LSDAttribute EURI = LSDAttribute.create("euri", "uri:", "A universally unique identifier for this resource that is human readable based around an external URI schema.", false, false, false, false, false, true, false);
    public static final LSDAttribute VERSION = LSDAttribute.create("version", "text:", "The version of the entity", true, true, false, false, false, true, true);
    public static final LSDAttribute NAME = LSDAttribute.create("name", "text:regex:[a-z0-9A-Z._-]+", "A short name for this entity, this is what is used to uniquely identify it within a given scope  = LSDAttributes.create(does not need to be universally unique).", false, false, false, false, false, true, false);
    public static final LSDAttribute INTERNAL_URI_PATH = LSDAttribute.create("_path", "text:", "The internal path location of the object.", false, false, false, false, false, false, false);
    public static final LSDAttribute TITLE = LSDAttribute.create("title", "text:title:", "The title of an entity, e.g. picture name or a formatted fullname.", false, false, false, false, false, false, false);
    public static final LSDAttribute FULL_NAME = LSDAttribute.create("fn", "text:title:", "The fullname as defined in the VCARD format: http://www.ietf.org/rfc/rfc2426.txt .", false, false, false, false, false, false, false);
    public static final LSDAttribute EMAIL_ADDRESS = LSDAttribute.create("email", "text:email:", "The email address of a person.", false, false, false, false, false, false, false);
    public static final LSDAttribute NETWORK = LSDAttribute.create("network", "text:shortname:", "A short name for a social network.", false, false, false, false, false, false, false);
    public static final LSDAttribute DELETED = LSDAttribute.create("_deleted", "", "Marked as logical deletion.", true, true, false, false, true, true, true);
    public static final LSDAttribute VIEWABLE = LSDAttribute.create("viewable", "boolean:", "Simple boolean flag to say if the current user can view the entity.", true, true, false, false, false, false, true);
    public static final LSDAttribute EDITABLE = LSDAttribute.create("editable", "boolean:", "Simple boolean flag to say if the current user can edit the entity.", true, true, false, false, false, false, true);
    public static final LSDAttribute MODIFIABLE = LSDAttribute.create("modifiable", "boolean:", "Simple boolean flag to say if the current user can modify the entity.", true, true, false, false, false, false, true);
    public static final LSDAttribute DELETABLE = LSDAttribute.create("deletable", "boolean:", "Simple boolean flag to say if the current user can delete the entity.", true, true, false, false, false, false, true);
    public static final LSDAttribute ADMINISTERABLE = LSDAttribute.create("administerable", "boolean:", "Simple boolean flag to say if the current user can administer the entity.", true, true, false, false, false, false, true);
    public static final LSDAttribute SELECTED = LSDAttribute.create("selected", "boolean:", "Simple boolean flag to say if the entity has been selected in some manner.", false, false, false, false, false, false, false);
    public static final LSDAttribute CHECKED = LSDAttribute.create("selected", "boolean:", "Simple boolean flag to say if the entity has been 'ticked' in some manner.", false, false, false, false, false, false, false);
    public static final LSDAttribute PINNED = LSDAttribute.create("pinned", "boolean:", "Simple boolean flag to say if the entity has been pinned, i.e. made immovable by the user.", false, false, false, false, false, false, false);
    public static final LSDAttribute ACTIVE = LSDAttribute.create("active", "boolean:", "Simple boolean flag to say if the entity is in some manner active.", false, false, false, false, false, false, false);
    public static final LSDAttribute HAS_FOCUS = LSDAttribute.create("focused", "boolean:", "Simple boolean flag to say if the entity should have focus.", true, true, false, false, false, false, true);


    /**
     * Profile information *
     */
    public static final LSDAttribute HONORIFIC = LSDAttribute.create("honorific", "text:", "The title of the person, e.g. Mr, Mrs, Dr.", false, false, false, false, false, false, false);
    public static final LSDAttribute GENDER = LSDAttribute.create("gender", "text:regex:((male)|(female)|(indeterminate))", "The gender of the person.", false, false, false, false, false, false, false);
    public static final LSDAttribute DATE_OF_BIRTH = LSDAttribute.create("dob", "number:", "The date of birth stored as milliseconds since 1970.", false, false, false, false, false, false, false);
    public static final LSDAttribute JOB_TITLE = LSDAttribute.create("job.title", "text:", "The title of the person's job.", false, false, false, false, false, false, false);
    public static final LSDAttribute JOB_TYPE = LSDAttribute.create("job.type", "text:", "The type of the person's job.", false, false, false, false, false, false, false);

    /*
      * boards
     */

    public static final LSDAttribute LISTED = LSDAttribute.create("listed", "boolean:", "Simple boolean flag to say if the board should be publicly listed.", false, false, false, false, false, false, false);


    /*
     *  Style
     */

    public static final LSDAttribute THEME = LSDAttribute.create("style.theme", "text:", "A variation on the standard appearance of this item.", false, false, false, false, false, false, false);
    public static final LSDAttribute SIZE = LSDAttribute.create("style.size", "text:", "A textual enumeration of the size, suitable for passing on to a CSS stylesheet etc.", false, false, false, false, false, false, false);


    /**
     * Gamification *
     */
    public static final LSDAttribute ROLE_TITLE = LSDAttribute.create("role.title", "text:", "The role within the system as human readable text e.g. admin, ambassador, newbie.", false, false, false, false, false, false, false);


    /**
     * Friendship information *
     */
    public static final LSDAttribute FOLLOWING = LSDAttribute.create("following", "boolean:", "Is the current user following this person/resource.", true, true, false, false, false, false, true);
    public static final LSDAttribute FOLLOWS_ALIAS_COUNT = LSDAttribute.create("follows_alias.total", "int:", "How many aliases does this alias follow.", false, false, false, false, false, false, false);
    public static final LSDAttribute FOLLOWS_RESOURCES_COUNT = LSDAttribute.create("follows_resources.total", "int:", "How many aliases does this alias follow.", true, true, false, false, false, false, true);
    public static final LSDAttribute FOLLOWERS_COUNT = LSDAttribute.create("followers.total", "int:", "How many aliases follow this.", false, false, false, false, false, true, false);

    /**
     * Organisational information.
     */
    public static final LSDAttribute PROFILE_ENTITY_TYPE = LSDAttribute.create("org.entity.type", "text:regex:((individual)|(corporate)|(group))", "The type of entity this user is.", false, false, false, false, false, false, false);
    public static final LSDAttribute COMPANY = LSDAttribute.create("company", "text:", "The name of the person's employer.", false, false, true, false, false, false, false);
    public static final LSDAttribute DEPARTMENT_NAME = LSDAttribute.create("department.title", "text:", "The name of a department of some organisation..", false, false, false, false, false, false, false);
    public static final LSDAttribute INDUSTRY_SECTOR = LSDAttribute.create("industry.sector", "text:", "The sector of an industry.", false, false, false, false, false, false, false);

    public static final LSDAttribute CONTACT_NAME = LSDAttribute.create("contact.name", "text:", "The name of the contact.", false, false, false, false, false, false, false);
    public static final LSDAttribute CONTACT_POSITION = LSDAttribute.create("contact.job.title", "text:", "The name job title of a contact.", false, false, false, false, false, false, false);
    public static final LSDAttribute CONTACT_EMAIL = LSDAttribute.create("contact.email", "text:", "The email address of a contact.", false, false, false, false, false, false, false);
    public static final LSDAttribute CONTACT_TEL = LSDAttribute.create("contact.tel", "text:", "The telephone number of a contact.", false, false, false, false, false, false, false);

    /**
     * Convenience attributes for companies.
     */
    public static final LSDAttribute COMPANY_INDUSTRY = LSDAttribute.create("company.industry.sector", "text:", "The sector of the industry the person's employer is in.", false, false, false, false, false, false, false);
    public static final LSDAttribute COMPANY_NAME = LSDAttribute.create("company.title", "text:", "The name of the person's employer.", false, false, false, false, false, false, false);
    public static final LSDAttribute COMPANY_DEPARTMENT_NAME = LSDAttribute.create("company.department.title", "text:", "The department the person work's in.", false, false, false, false, false, false, false);
    public static final LSDAttribute COMPANY_DESCRIPTION = LSDAttribute.create("company.description", "text:", "The description of the company.", false, false, false, false, false, false, false);
    public static final LSDAttribute COMPANY_CONTACT_NAME = LSDAttribute.create("company.contact.name", "text:", "The name of the contact for a company.", false, false, false, false, false, false, false);
    public static final LSDAttribute COMPANY_CONTACT_POSITION = LSDAttribute.create("company.contact.job.title", "text:", "The name job title of a contact for a company.", false, false, false, false, false, false, false);
    public static final LSDAttribute COMPANY_CONTACT_EMAIL = LSDAttribute.create("company.contact.email", "text:", "The email address of a contact for a company.", false, false, false, false, false, false, false);
    public static final LSDAttribute COMPANY_CONTACT_TEL = LSDAttribute.create("company.contact.tel", "text:", "The telephone number of a contact for a company.", false, false, false, false, false, false, false);


    /**
     * Location
     */
    public static final LSDAttribute LOCATION_LONG = LSDAttribute.create("location.geo.long", "number:", "The physical global longitude of the entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute LOCATION_LAT = LSDAttribute.create("location.geo.lat", "number:", "The physical global lattitude of the entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute LOCATION_NAME = LSDAttribute.create("location.geo.fn", "text:", "Human readable name of the location of the entity.", false, false, false, false, false, false, false);

    public static final LSDAttribute ADDRESS_FIRST_LINE = LSDAttribute.create("location.address.first_line", "text:", "The first line of an address.", false, false, false, false, false, false, false);
    public static final LSDAttribute ADDRESS_SECOND_LINE = LSDAttribute.create("location.address.second_line", "text:", "The first line of an address.", false, false, false, false, false, false, false);
    public static final LSDAttribute ADDRESS_CITY = LSDAttribute.create("location.address.city", "text:", "The city of the location.", false, false, false, false, false, false, false);
    public static final LSDAttribute ADDRESS_STATE = LSDAttribute.create("location.address.state", "text:", "The state of the location.", false, false, false, false, false, false, false);
    public static final LSDAttribute ADDRESS_POSTALCODE = LSDAttribute.create("location.address.postal_code", "text:", "The postal code of the location.", false, false, false, false, false, false, false);
    // see http://www.textfixer.com/resources/dropdowns/country-list-iso-codes.txt
    public static final LSDAttribute ADDRESS_COUNTRY = LSDAttribute.create("location.address.country", "text:", "The country of the location.", false, false, false, false, false, false, false);


    public static final LSDAttribute STRENGTH_AMOUNT = LSDAttribute.create("strength.amount", "number:", "The calculated strength in relation to the current session.", true, false, false, false, false, false, false);
    public static final LSDAttribute STRENGTH_HALFLIFE = LSDAttribute.create("strength.halflife", "int:", "A halflife in seconds associated with this entity.", true, false, false, false, false, false, false);
    public static final LSDAttribute STRENGTH_THRESHOLD = LSDAttribute.create("strength.threshold", "number:", "A threshold value, below which events will not be propagated.", false, false, false, false, false, false, false);
    public static final LSDAttribute LINK_INTERNAL_URI = LSDAttribute.create("link.internal.uri", "uri:", "An internal URI of the object that this entity links to.", true, false, false, false, false, false, false);
    public static final LSDAttribute LINK_INTERNAL_REL = LSDAttribute.create("link.internal.rel", "shortname:", "The relationship to the internal URI.", true, false, false, false, false, false, false);
    public static final LSDAttribute LINK_EXTERNAL_URL = LSDAttribute.create("link.external.url", "url:", "An external URL of the object that this entity links to.", false, false, false, false, false, false, false);
    public static final LSDAttribute LINK_EXTERNAL_REL = LSDAttribute.create("link.external.rel", "shortname:", "The relationship to the external URL of the object that this entity links to.", false, false, false, false, false, false, false);

    public static final LSDAttribute SOURCE = LSDAttribute.create("source", "uri:", "This identifies where the data for this entity originally came from  = LSDAttributes.create(for example the data for a video, picture, file etc.", false, false, false, false, false, false, false);
    public static final LSDAttribute FORMAT = LSDAttribute.create("format", "mime:", "The mime format of the data described by the 'source' property.", false, false, false, false, false, false, false);
    public static final LSDAttribute DESCRIPTION = LSDAttribute.create("description", "text:", "A textual description of the entity, suitable for display in a tooltip etc.", false, false, false, false, false, false, false);
    public static final LSDAttribute COMMENT = LSDAttribute.create("comment", "", "A comment placed on the entity.", false, false, true, false, false, true, false);
    public static final LSDAttribute COMMENT_TEXT = LSDAttribute.create("comment.text", "text:", "A short comment added by the user.", false, false, false, false, false, false, false);
    public static final LSDAttribute COMMENT_UPDATED = LSDAttribute.create("comment.updated", "text:", "A short comment added by the user.", false, false, false, false, false, false, false);
    public static final LSDAttribute COMMENT_COUNT = LSDAttribute.create("comments.count", "int:", "The number of comments added to an entity.", false, false, false, false, false, true, false);

    public static final LSDAttribute POPULARITY_METRIC = LSDAttribute.create("metric.popularity", "int:", "A simple measure of the popularity of something.", true, true, false, false, false, true, true);


    public static final LSDAttribute MOTIVE_TEXT = LSDAttribute.create("motive.text", "text:", "A short comment added by the user, usually to explain why it was added to a pool.", false, false, false, false, false, false, false);
    public static final LSDAttribute MOTIVE_UPDATED = LSDAttribute.create("motive.updated", "text:", "A short comment added by the user, usually to explain why it was added to a pool.", false, false, false, false, false, false, false);

    public static final LSDAttribute TEXT = LSDAttribute.create("text", "", "Free text content.", false, false, false, false, false, false, false);
    public static final LSDAttribute TEXT_BRIEF = LSDAttribute.create("text.brief", "text:", "140 visible character limit.", false, false, false, false, false, false, false);
    public static final LSDAttribute TEXT_EXTENDED = LSDAttribute.create("text.extended", "text:", "Free text content.", false, false, false, false, false, false, false);
    public static final LSDAttribute RIGHTS = LSDAttribute.create("rights", "text:", "Copyright assertion statement.", false, false, false, false, false, false, false);
    public static final LSDAttribute LOCALE_LANGUAGE = LSDAttribute.create("locale.lang", "text:", "Language locale for this entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute LOCALE_TIMEZONE = LSDAttribute.create("locale.time.zone", "text:", "Timezone locale for this entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute LOCALE_UTC_OFFSET = LSDAttribute.create("locale.time.offset", "number:", "UTC offset for this entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute KEYWORDS = LSDAttribute.create("keywords", "text:regex:[a-zA-Z0-9 _\\-]*", "Space delimited hyphen joined list of keywords that describe this entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute UPDATED = LSDAttribute.create("updated", "number:int:", "The date this entity was last modified in milliseconds since 1970. ", false, false, false, false, false, false, false);
    public static final LSDAttribute PUBLISHED = LSDAttribute.create("published", "number:int:", "The date this entity was first created in milliseconds since 1970. ", false, false, false, false, false, false, false);

    public static final LSDAttribute HASHED_AND_SALTED_PASSWORD = LSDAttribute.create("security.password.hashed", "text:", "A hashed and salted password stored using Jasypt see http://www.jasypt.org/howtoencryptuserpasswords.html.", false, false, false, false, true, false, false);
    public static final LSDAttribute CHANGE_PASSWORD_ON_LOGIN = LSDAttribute.create("security.password.change", "boolean:", "If set to true then on next login the client should force the user to change their password and then the client should set this value to false.", false, false, false, false, false, false, false);
    public static final LSDAttribute SECURITY_BLOCKED = LSDAttribute.create("security.access.blocked", "boolean:", "If set to true the user cannot login  = LSDAttributes.create(enforced by server); the informational text should be displayed to the user.", false, false, false, false, false, false, false);
    public static final LSDAttribute SECURITY_TOKEN = LSDAttribute.create("security.oauth.token", "text:", "Token for ouath token based logins, for example Twitter.", false, false, false, false, false, false, false);
    public static final LSDAttribute SECURITY_SECRET = LSDAttribute.create("security.oauth.secret", "text:", "Secret for oauth token based logins, for example Twitter.", false, false, false, false, false, false, false);
    public static final LSDAttribute SECURITY_RESTRICTED = LSDAttribute.create("security.access.restricted", "boolean:", "If set to true the user cannot login  = LSDAttributes.create(enforced by client); the informational text should be displayed to the user.", false, false, false, false, false, false, false);
    public static final LSDAttribute SECURITY_STATUS = LSDAttribute.create("security.access.text", "text:", "Informational text relating to the status of the user's account, this should be displayed if the account is blocked.", false, false, false, false, false, false, false);
    public static final LSDAttribute PERMISSIONS = LSDAttribute.create("security.permissions", "text:", "The numeric value of the permission set for this entity, note that you cannot change this property by changing the entity, an appropriate request must be made.", true, true, false, false, false, true, false);
    public static final LSDAttribute PLAIN_PASSWORD = LSDAttribute.create("security.password.plain", "text:", "Plain text password, will not be stored.", true, false, false, false, true, false, true);

    public static final LSDAttribute VIEW = LSDAttribute.create("view", "", "The nested view object", false, false, true, true, false, false, false);
    public static final LSDAttribute VIEW_HEIGHT = LSDAttribute.create("view2d.height", "number:", "The height in pixels when rendering on a 2D pixel based display.", true, false, false, false, false, false, false);
    public static final LSDAttribute VIEW_WIDTH = LSDAttribute.create("view2d.width", "number:", "The width in pixels when rendering on a 2D pixel based display.", true, false, false, false, false, false, false);
    public static final LSDAttribute VIEW_X = LSDAttribute.create("view2d.x", "number:", "The x position in pixels when rendering on a 2D pixel based display.", true, false, false, false, false, false, false);
    public static final LSDAttribute VIEW_Y = LSDAttribute.create("view2d.y", "number:", "The y position in pixels when rendering on a 2D pixel based display.", true, false, false, false, false, false, false);
    public static final LSDAttribute VIEW_Z = LSDAttribute.create("view2d.z", "number:", "The z position  = LSDAttributes.create(i.e plane) in pixels when rendering on a 2D pixel based display.", true, false, false, false, false, false, false);
    public static final LSDAttribute VIEW_RADIUS = LSDAttribute.create("view2d.radius", "number:", "The distance from the center  = LSDAttributes.create(caculated from X and Y).", true, true, false, false, false, false, false);
    public static final LSDAttribute VIEW_ROTATE_XY = LSDAttribute.create("view2d.rotatexy", "number:", "The rotation in degress in the XY plain of the entity.", true, false, false, false, false, false, false);
    public static final LSDAttribute ICON_URL = LSDAttribute.create("icon.url", "url:", "The URL of an image suitable for rendering as an icon for this entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute ICON_HEIGHT = LSDAttribute.create("icon.height", "number:int:", "The height of an image suitable for rendering as an icon for this entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute ICON_WIDTH = LSDAttribute.create("icon.width", "number:int:", "The width of an image suitable for rendering as an icon for this entity.", false, false, false, false, false, false, false);

    public static final LSDAttribute IMAGE_URL = LSDAttribute.create("image.url", "url:", "The URL of an image suitable for rendering as a fullsize image on a 2D display for this entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute IMAGE_HEIGHT = LSDAttribute.create("image.height", "number:", "The height of an image suitable for rendering as a fullsize image on a 2D display for this entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute IMAGE_WIDTH = LSDAttribute.create("image.width", "number:", "The width of an image suitable for rendering as a fullsize image on a 2D display for this entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute IMAGE_REFRESH = LSDAttribute.create("image.refresh", "number:int:", "The time in milliseconds beyond the published value that the image should be refreshed.", true, false, false, false, false, false, false);

    public static final LSDAttribute CATEGORY_TERM = LSDAttribute.create("category.term", "text:shortname:", "An array of terms for the category this entity belongs to see http://www.atomenabled.org/developers/syndication/#category .", false, false, false, false, false, false, false);

    public static final LSDAttribute MEDIA_SOURCE = LSDAttribute.create("media.content.source", "uri:", "The source of the  = LSDAttributes.create(file, video, image, anything really) associated with the entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute MEDIA_SIZE = LSDAttribute.create("media.content.filesize", "number:int:", "The size in bytes of the media  = LSDAttributes.create(file, video, image, anything really) associated with the entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute MEDIA_BITRATE = LSDAttribute.create("media.content.bitrate", "number:", "The bitrate of the media  = LSDAttributes.create(in this case likely to be video or audio) associated with the entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute MEDIA_FRAMERATE = LSDAttribute.create("media.content.framerate", "number:", "The framerate of the media  = LSDAttributes.create(in this case likely to be video) associated with the entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute MEDIA_SAMPLINGRATE = LSDAttribute.create("media.content.samplingrate", "number:", "The sampling rate of the media  = LSDAttributes.create(in this case likely to be video or audio) associated with the entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute MEDIA_CHANNELS = LSDAttribute.create("media.content.channels", "number:int:", "The number of audio channels of the media  = LSDAttributes.create(in this case likely to be video or audio) associated with the entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute MEDIA_DURATION = LSDAttribute.create("media.content.duration", "number:", "The duration of the media  = LSDAttributes.create(in this case likely to be video or audio) associated with the entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute MEDIA_WIDTH = LSDAttribute.create("media.content.width", "number:int:", "The original width of the media  = LSDAttributes.create(probably video) associated with the entity, only supplied if not an image itself.", false, false, false, false, false, false, false);
    public static final LSDAttribute MEDIA_HEIGHT = LSDAttribute.create("media.content.height", "number:int:", "The original width of the media  = LSDAttributes.create(probably video) associated with the entity, only supplied if not an image itself.", false, false, false, false, false, false, false);
    public static final LSDAttribute MEDIA_HASH = LSDAttribute.create("media.hash", "text:", "A hash of the media.", false, false, false, false, false, false, false);
    public static final LSDAttribute MEDIA_HASH_ALGO = LSDAttribute.create("media.hash.algo", "text:shortname:", "The algorithm used to create the hash of the media associated with the entity .", false, false, false, false, false, false, false);


    public static final LSDAttribute EVENT_HANDLER = LSDAttribute.create("handler", "", "An event handler.", false, false, true, false, false, false, false);
    public static final LSDAttribute CLIENT_SCRIPT = LSDAttribute.create("script.client.js", "", "A script to be executed.", false, false, true, false, false, false, false);
    public static final LSDAttribute SERVER_SCRIPT = LSDAttribute.create("script.server.liquid", "", "A script to be executed.", false, false, true, false, false, false, false);
    public static final LSDAttribute NAVIGATION_URL = LSDAttribute.create("href", "", "A hyperlink to move to.", false, false, true, false, false, false, false);


    public static final LSDAttribute WEBSITE_RESOUCE = LSDAttribute.create("resource.website", "url:", "A website associated with this entity.", false, false, false, false, false, false, false);

    public static final LSDAttribute VERIFICATION_SOURCE = LSDAttribute.create("verification.source", "text:", "The source of the verification score for this entity.", false, false, false, false, false, false, false);
    public static final LSDAttribute VERIFICATION_SCORE = LSDAttribute.create("verification.score", "text:", "The verification score for this entity.", false, false, false, false, false, false, false);

    public static final LSDAttribute REPLY_URI = LSDAttribute.create("reply.message.uri", "uri:", "URI of the message this entity is a reply to.", false, false, false, false, false, false, false);
    public static final LSDAttribute REPLY_AUTHOR_URI = LSDAttribute.create("reply.author.uri", "uri:", "Author of the message this entity is a reply to.", false, false, false, false, false, false, false);
    public static final LSDAttribute REPLY_AUTHOR_FN = LSDAttribute.create("reply.author.fn", "uri:", "Full name of the author of the message this entity is a reply to.", false, false, false, false, false, false, false);

    public static final LSDAttribute CHILD = LSDAttribute.create("child", "", "An array of sub entities that this entity contains.", false, false, true, false, false, false, false);
    public static final LSDAttribute HISTORY = LSDAttribute.create("history", "", "An historical version of the entity.", true, true, true, true, false, true, true);
    public static final LSDAttribute AUTHOR = LSDAttribute.create("author", "", "The author of the entity.", true, false, true, true, false, false, true);
    public static final LSDAttribute OWNER = LSDAttribute.create("owner", "", "The owner of the entity.", true, false, true, true, false, false, true);
    public static final LSDAttribute EDITOR = LSDAttribute.create("editor", "", "The last editor of the entity.", true, false, true, true, false, false, true);
    public static final LSDAttribute VISITOR = LSDAttribute.create("visitor", "", "The visitor of the pool.", true, false, true, false, false, false, true);

    public static final LSDAttribute CORRELATION_ID = LSDAttribute.create("correlation.id", "uuid:", "Used for asynchronous communication.", false, false, false, false, false, false, false);

    public static final LSDAttribute CLIENT_APPLICATION_NAME = LSDAttribute.create("client.application.name", "text:", "A textual description of the client application the user is using for this session.", false, false, false, false, false, false, false);
    public static final LSDAttribute CLIENT_APPLICATION_KEY = LSDAttribute.create("client.application.key", "text:", "The client key of the client application being used for this session.", false, false, false, false, false, false, false);
    public static final LSDAttribute CLIENT_HOST = LSDAttribute.create("client.host.description", "text:", "A textual description of the host platform the user is using for this session.", false, false, false, false, false, false, false);

    public static final LSDAttribute VALIDATION_FORMAT_STRING = LSDAttribute.create("validation.format", "uri:", "The type of validation that should be used to validate a single valued entity", false, false, false, false, false, false, false);
    public static final LSDAttribute INTERNAL_MIN_IMAGE_RADIUS = LSDAttribute.create("internal.minimageradius", "number:", "The closest position to the center.", true, true, false, false, false, false, false);

    public static final LSDAttribute LAST_FORK_VERSION = LSDAttribute.create("fork.version.last", "number:", "For internal use only, for version management", true, true, false, false, true, true, false);

    public static final LSDAttribute TEST_COUNTER = LSDAttribute.create("x.test.counter", "A counter used for testing purposes only.", false, false, false, false, false, false, false);

    private final String key;
    private final String validationString;
    private final String description;
    private final boolean nonupdateable;
    private boolean nonPersistable;

    private final boolean systemGenerated;
    private final boolean subEntity;
    private final boolean hidden;
    private final boolean common;
    private final boolean topLevelEntity;
    private boolean isTransient;


    public static LSDAttribute valueOf(String key) {
        if (key.startsWith("x.")) {
            return new LSDAttribute(key);
        }
        return ReverseLookup.map.get(key);
    }

    private LSDAttribute(String key) {
        this(key, "", "", false, false, false, false, false, false, false);
        if (!key.startsWith("x.")) {
            throw new IllegalArgumentException("Attempted to create an attribute from a non 'x.' key.");
        }
    }

    private static LSDAttribute create(String key, String description, boolean nonupdateable, boolean systemGenerated, boolean subEntity, boolean topLevel, boolean hidden, boolean common, boolean isTransient) {
        final LSDAttribute attribute = new LSDAttribute(key, "", description, nonupdateable, systemGenerated, subEntity, topLevel, hidden, common, isTransient);
        ReverseLookup.map.put(key, attribute);
        return attribute;
    }

    private static LSDAttribute create(String key, String validationString, String description, boolean nonupdateable, boolean systemGenerated, boolean subEntity, boolean topLevel, boolean hidden, boolean common, boolean isTransient) {
        final LSDAttribute attribute = new LSDAttribute(key, validationString, description, nonupdateable, systemGenerated, subEntity, topLevel, hidden, common, isTransient);
        ReverseLookup.map.put(key, attribute);
        return attribute;
    }


    private LSDAttribute(String key, String description, boolean nonupdateable, boolean systemGenerated, boolean subEntity, boolean hidden, boolean common, boolean isTransient) {
        this(key, "", description, nonupdateable, systemGenerated, subEntity, false, hidden, common, isTransient);
    }

    private LSDAttribute(String key, String validationString, String description, boolean nonupdateable, boolean systemGenerated, boolean subEntity, boolean topLevel, boolean hidden, boolean common, boolean isTransient) {
        this.key = key;
        this.validationString = validationString;
        this.description = description;
        this.nonupdateable = nonupdateable;
        this.systemGenerated = systemGenerated;
        this.subEntity = subEntity;
        this.topLevelEntity = topLevel;
        this.hidden = hidden;
        this.common = common;
        this.nonPersistable = isTransient;
        if (!nonupdateable && systemGenerated) {
            throw new IllegalArgumentException("Can't be writeable and system generated.");
        }
    }

    public String getDescription() {
        return description;
    }

    public LiquidUUID getId() {
        return null;
    }

    public boolean isUpdateable() {
        return !nonupdateable;
    }

    public String getFormatValidationString() {
        return validationString;
    }

    public String getKeyName() {
        return key;
    }

    public boolean isSystemGenerated() {
        return systemGenerated;
    }

    public boolean isSubEntity() {
        return subEntity;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isCommon() {
        return common;
    }

    public boolean isPersistable() {
        return !nonPersistable;
    }

    public static LSDAttribute[] values() {
        return ReverseLookup.map.values().toArray(new LSDAttribute[ReverseLookup.map.size()]);
    }

    public String name() {
        return getKeyName().replaceAll("\\.", "_").toUpperCase();
    }

    public boolean isTopLevelEntity() {
        return topLevelEntity;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public boolean isFreeTexSearchable() {
        return true;
    }

    private static class ReverseLookup {
        private static Map<String, LSDAttribute> map = new HashMap<String, LSDAttribute>();


    }
}
