/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;

import static cazcade.liquid.api.lsd.Attribute.create;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public interface Dictionary {
    @Nonnull
             Attribute TYPE                          = create("type", "type:", "The type of an entity as defined in http://wiki.cazcade.com/display/LIQUID/Liquid+Dictionary#LiquidDictionary-Type", true, false, false, false, false, true, false);
    @Nonnull
             Attribute ID                            = create("id", "uuid:", "A unique identitifer for this entity based on the Java UUID http://java.sun.com/j2se/1.5.0/docs/api/java/util/UUID.html . ", true, false, false, false, false, true, false);
    @Nonnull
             Attribute URI                           = create("uri", "uri:", "A universally unique identifier for this resource that is human readable following internal URI schemes.", true, true, false, false, false, true, false);
    @Nonnull
             Attribute EURI                          = create("euri", "uri:", "A universally unique identifier for this resource that is human readable based around an external URI schema.", false, false, false, false, false, true, false);
    @Nonnull
             Attribute VERSION                       = create("version", "text:", "The version of the entity", true, true, false, false, false, true, true);
    @Nonnull
             Attribute NAME                          = create("name", "text:regex:[a-z0-9A-Z._-]+", "A short name for this entity, this is what is used to uniquely identify it within a given scope (does not need to be universally unique).", false, false, false, false, false, true, false);
    //    @Nonnull public static final Attribute INTERNAL_URI_PATH = Attribute.create("_path", "text:", "The internal path location of the object.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute TITLE                         = create("title", "text:title:", "The title of an entity, e.g. picture name or a formatted fullname.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute FULL_NAME                     = create("fn", "text:title:", "The fullname as defined in the VCARD format: http://www.ietf.org/rfc/rfc2426.txt .", false, false, false, false, false, false, false);
    @Nonnull
             Attribute EMAIL_ADDRESS                 = create("email", "text:email:", "The email address of a person.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute NETWORK                       = create("network", "text:shortname:", "A short name for a social network.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute DELETED                       = create("_deleted", "", "Marked as logical deletion.", true, true, false, false, true, true, true);
    @Nonnull
             Attribute VIEWABLE                      = create("viewable", "boolean:", "Simple boolean flag to say if the current user can view the entity.", true, true, false, false, false, false, true);
    @Nonnull
             Attribute EDITABLE                      = create("editable", "boolean:", "Simple boolean flag to say if the current user can edit the entity.", true, true, false, false, false, false, true);
    @Nonnull
             Attribute MODIFIABLE                    = create("modifiable", "boolean:", "Simple boolean flag to say if the current user can modify the entity.", true, true, false, false, false, false, true);
    @Nonnull
             Attribute DELETABLE                     = create("deletable", "boolean:", "Simple boolean flag to say if the current user can delete the entity.", true, true, false, false, false, false, true);
    @Nonnull
             Attribute ADMINISTERABLE                = create("administerable", "boolean:", "Simple boolean flag to say if the current user can administer the entity.", true, true, false, false, false, false, true);
    @Nonnull
             Attribute SELECTED                      = create("selected", "boolean:", "Simple boolean flag to say if the entity has been selected in some manner.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CHECKED                       = create("checked", "boolean:", "Simple boolean flag to say if the entity has been 'ticked' in some manner.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute PINNED                        = create("pinned", "boolean:", "Simple boolean flag to say if the entity has been pinned, i.e. made immovable by the user.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute ACTIVE                        = create("active", "boolean:", "Simple boolean flag to say if the entity is in some manner active.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute HAS_FOCUS                     = create("focused", "boolean:", "Simple boolean flag to say if the entity should have focus.", true, true, false, false, false, false, true);
    /*
         * Requests
         */
    @Nonnull
             Attribute DICTIONARY_CATEGORY           = create("dictionary.category", "text:", "Dictionary category.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute FOLLOW                        = create("follow", "boolean:", "A request to follow (true) or unfollow (false)", false, false, false, false, false, false, false);
    @Nonnull
             Attribute RECIPIENT                     = create("recipient.name", "text:shortname:", "The recipient of a message.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute FROM                          = create("request.resource.from.id", "uuid:", "Location to move a resource from.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute TO                            = create("request.resource.to.id", "uuid:", "Location to move a resource from.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute UNLINK                        = create("request.resource.unlink", "boolean:", "Unlink the original resource in a link request.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CREATE_OR_UPDATE              = create("request.coru", "boolean:", "Create the resource if it doesn't exist, update if it does.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CLIENT_APPLICATION_IDENTIFIER = create("request.cai", "boolean:", "Client application identifier", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_ORIGIN                = create("request.origin", "text:", "From where the request originated.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_STATE                 = create("request.state", "text:", "The current state of the request.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_CACHING_SCOPE         = create("request.cache.scope", "text:", "The scope of the caching for this request.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_SESSION_ID            = create("request.sid", "text:", "Session identifier.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute INTERNAL_REQUEST              = create("request.internal", "boolean:", "Is this request internal.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute HISTORICAL_REQUEST            = create("request.historical", "boolean:", "Is this request historical.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_EXPLICIT_RPC          = create("request.rpc", "boolean:", "Is this request synchronous (RPC).", false, false, false, false, false, false, false);
    @Nonnull
             Attribute IS_ME                         = create("request.self", "boolean:", "Request relates to me.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute IS_CLAIM                      = create("request.claim", "boolean:", "Claiming resource.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_RESULT                = create("request.result", "", "The result entity for this request.", false, false, true, false, false, false, false);
    @Nonnull
             Attribute REQUEST_ENTITY                = create("request.resource.entity", "", "The entity to use for this request.", false, false, true, false, false, false, false);
    @Nonnull
             Attribute REQUEST_URI                   = create("request.resource.uri", "uri:", "Request URI.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_UUID                  = create("request.resource.uuid", "uuid:", "Request UUID.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_POOL_UUID             = create("request.resource.pool.uuid", "uuid:", "Request Pool UUID.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_POOL_URI              = create("request.resource.pool.uri", "uri:", "Request Pool URI.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_PREVIOUS_POOL_URI     = create("request.resource.previous.pool.uri", "uri:", "Previous Pool URI.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_OBJECT_UUID           = create("request.resource.object.uuid", "text:", "Request Object UUID.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_ALIAS                 = create("request.resource.alias", "uri:", "Request Alias.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_AUTHOR                = create("request.resource.author", "uuid:", "Request Author.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_RESOURCE_TYPE         = create("request.resource.type", "type:", "Request resource type.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REQUEST_PARENT_URI            = create("request.resource.parent.uri", "uri:", "Parent resource.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute QUERY_DETAIL                  = create("query.detail", "text:", "Level of detail to return.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute SINCE                         = create("query.since", "number:", "Time from which results should be returned.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CONTENTS                      = create("query.contents", "boolean:", "Include contents in result.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute SORT_BY                       = create("query.sort.by", "text:", "Sort criteria.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute BOARD_QUERY_TYPE              = create("query.board.type", "text:", "Board query type", false, false, false, false, false, false, false);
    @Nonnull
             Attribute QUERY_START_OFFSET            = create("query.start.offset", "text:", "The offset to return results from.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute QUERY_MAX                     = create("query.max", "text:", "The maximum number of results to return.", false, false, false, false, false, false, false);
    /**
     * Profile information *
     */
    @Nonnull
             Attribute HONORIFIC                     = create("honorific", "text:", "The title of the person, e.g. Mr, Mrs, Dr.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute GENDER                        = create("gender", "text:regex:((male)|(female)|(indeterminate))", "The gender of the person.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute DATE_OF_BIRTH                 = create("dob", "number:", "The date of birth stored as milliseconds since 1970.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute JOB_TITLE                     = create("job.title", "text:", "The title of the person's job.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute JOB_TYPE                      = create("job.type", "text:", "The type of the person's job.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute EMAIL_UPDATE_FREQUENCY        = create("pref.email.updates", "text:", "How often the user would like to receive email updates.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute LISTED                        = create("listed", "boolean:", "Simple boolean flag to say if the board should be publicly listed.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute THEME                         = create("style.theme", "text:", "A variation on the standard appearance of this item.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute SIZE                          = create("style.size", "text:", "A textual enumeration of the size, suitable for passing on to a CSS stylesheet etc.", false, false, false, false, false, false, false);
    /**
     * Gamification *
     */
    @Nonnull
             Attribute ROLE_TITLE                    = create("role.title", "text:", "The role within the system as human readable text e.g. admin, ambassador, newbie.", false, false, false, false, false, false, false);
    /**
     * Friendship information *
     */
    @Nonnull
             Attribute FOLLOWING                     = create("following", "boolean:", "Is the current user following this person/resource.", true, true, false, false, false, false, true);
    @Nonnull
             Attribute FOLLOWS_ALIAS_COUNT           = create("follows_alias.total", "int:", "How many aliases does this alias follow.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute FOLLOWS_RESOURCES_COUNT       = create("follows_resources.total", "int:", "How many aliases does this alias follow.", true, true, false, false, false, false, true);
    @Nonnull
             Attribute FOLLOWERS_COUNT               = create("followers.total", "int:", "How many aliases follow this.", false, false, false, false, false, true, false);
    /**
     * Organisational information.
     */
    @Nonnull
             Attribute PROFILE_ENTITY_TYPE           = create("org.entity.type", "text:regex:((individual)|(corporate)|(group))", "The type of entity this user is.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute COMPANY                       = create("company", "text:", "The name of the person's employer.", false, false, true, false, false, false, false);
    @Nonnull
             Attribute DEPARTMENT_NAME               = create("department.title", "text:", "The name of a department of some organisation..", false, false, false, false, false, false, false);
    @Nonnull
             Attribute INDUSTRY_SECTOR               = create("industry.sector", "text:", "The sector of an industry.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CONTACT_NAME                  = create("contact.name", "text:", "The name of the contact.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CONTACT_POSITION              = create("contact.job.title", "text:", "The name job title of a contact.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CONTACT_EMAIL                 = create("contact.email", "text:", "The email address of a contact.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CONTACT_TEL                   = create("contact.tel", "text:", "The telephone number of a contact.", false, false, false, false, false, false, false);
    /**
     * Convenience attributes for companies.
     */
    @Nonnull
             Attribute COMPANY_INDUSTRY              = create("company.industry.sector", "text:", "The sector of the industry the person's employer is in.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute COMPANY_NAME                  = create("company.title", "text:", "The name of the person's employer.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute COMPANY_DEPARTMENT_NAME       = create("company.department.title", "text:", "The department the person work's in.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute COMPANY_DESCRIPTION           = create("company.description", "text:", "The description of the company.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute COMPANY_CONTACT_NAME          = create("company.contact.name", "text:", "The name of the contact for a company.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute COMPANY_CONTACT_POSITION      = create("company.contact.job.title", "text:", "The name job title of a contact for a company.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute COMPANY_CONTACT_EMAIL         = create("company.contact.email", "text:", "The email address of a contact for a company.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute COMPANY_CONTACT_TEL           = create("company.contact.tel", "text:", "The telephone number of a contact for a company.", false, false, false, false, false, false, false);
    /**
     * Location
     */
    @Nonnull
             Attribute LOCATION_LONG                 = create("location.geo.long", "number:", "The physical global longitude of the entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute LOCATION_LAT                  = create("location.geo.lat", "number:", "The physical global lattitude of the entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute LOCATION_NAME                 = create("location.geo.fn", "text:", "Human readable name of the location of the entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute ADDRESS_FIRST_LINE            = create("location.address.first_line", "text:", "The first line of an address.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute ADDRESS_SECOND_LINE           = create("location.address.second_line", "text:", "The first line of an address.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute ADDRESS_CITY                  = create("location.address.city", "text:", "The city of the location.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute ADDRESS_STATE                 = create("location.address.state", "text:", "The state of the location.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute ADDRESS_POSTALCODE            = create("location.address.postal_code", "text:", "The postal code of the location.", false, false, false, false, false, false, false);
    // see http://www.textfixer.com/resources/dropdowns/country-list-iso-codes.txt
    @Nonnull
             Attribute ADDRESS_COUNTRY               = create("location.address.country", "text:", "The country of the location.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute STRENGTH_AMOUNT               = create("strength.amount", "number:", "The calculated strength in relation to the current session.", true, false, false, false, false, false, false);
    @Nonnull
             Attribute STRENGTH_HALFLIFE             = create("strength.halflife", "int:", "A halflife in seconds associated with this entity.", true, false, false, false, false, false, false);
    @Nonnull
             Attribute STRENGTH_THRESHOLD            = create("strength.threshold", "number:", "A threshold value, below which events will not be propagated.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute LINK_INTERNAL_URI             = create("link.internal.uri", "uri:", "An internal URI of the object that this entity links to.", true, false, false, false, false, false, false);
    @Nonnull
             Attribute LINK_INTERNAL_REL             = create("link.internal.rel", "shortname:", "The relationship to the internal URI.", true, false, false, false, false, false, false);
    @Nonnull
             Attribute LINK_EXTERNAL_URL             = create("link.external.url", "url:", "An external URL of the object that this entity links to.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute LINK_EXTERNAL_REL             = create("link.external.rel", "shortname:", "The relationship to the external URL of the object that this entity links to.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute SOURCE                        = create("source", "uri:", "This identifies where the data for this entity originally came from  = LSDAttributes.create(for example the data for a video, picture, file etc.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute FORMAT                        = create("format", "mime:", "The mime format of the data described by the 'source' property.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute DESCRIPTION                   = create("description", "text:", "A textual description of the entity, suitable for display in a tooltip etc.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute COMMENT                       = create("comment", "", "A comment placed on the entity.", false, false, true, false, false, true, false);
    @Nonnull
             Attribute COMMENT_TEXT                  = create("comment.text", "text:", "A short comment added by the user.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute COMMENT_UPDATED               = create("comment.updated", "text:", "A short comment added by the user.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute COMMENT_COUNT                 = create("comments.count", "int:", "The number of comments added to an entity.", false, false, false, false, false, true, false);
    //Metrics
    @Nonnull
             Attribute POPULARITY_METRIC             = create("metric.popularity", "int:", "A simple measure of the popularity of something.", true, true, false, false, false, true, true);
    @Nonnull
             Attribute VISITS_METRIC                 = create("metric.visits", "int:", "The total number of visits to this resource.", true, true, false, false, false, true, true);
    @Nonnull
             Attribute REGISTERED_VISITORS_METRIC    = create("metric.visitors", "int:", "The total number of registered visitors to this resource.", true, true, false, false, false, true, true);
    @Nonnull
             Attribute MOTIVE_TEXT                   = create("motive.text", "text:", "A short comment added by the user, usually to explain why it was added to a pool.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MOTIVE_UPDATED                = create("motive.updated", "text:", "A short comment added by the user, usually to explain why it was added to a pool.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute TEXT                          = create("text", "", "Free text content.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute TEXT_BRIEF                    = create("text.brief", "text:", "140 visible character limit.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute TEXT_EXTENDED                 = create("text.extended", "text:", "Free text content.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute RIGHTS                        = create("rights", "text:", "Copyright assertion statement.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute LOCALE_LANGUAGE               = create("locale.lang", "text:", "Language locale for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute LOCALE_TIMEZONE               = create("locale.time.zone", "text:", "Timezone locale for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute LOCALE_UTC_OFFSET             = create("locale.time.offset", "number:", "UTC offset for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute KEYWORDS                      = create("keywords", "text:regex:[a-zA-Z0-9 _\\-]*", "Space delimited hyphen joined list of keywords that describe this entity.", false, false, false, false, false, false, false);
    @Nonnull Attribute UPDATED = create("updated", "number:int:", "The date this entity was last edited in milliseconds since "
                                                                  + "1970", false, false, false, false, false, false, false);
    Attribute MODIFIED = create("modified", "number:int:", "The date this entity was last modified (i.e. a child was "
                                                           + "changed) in milliseconds since 1970. ", false, false, false, false, false, false, false);
    @Nonnull
             Attribute PUBLISHED                  = create("published", "number:int:", "The date this entity was first created in milliseconds since 1970. ", false, false, false, false, false, false, false);
    @Nonnull
             Attribute HASHED_AND_SALTED_PASSWORD = create("security.password.hashed", "text:", "A hashed and salted password stored using Jasypt see http://www.jasypt.org/howtoencryptuserpasswords.html.", false, false, false, false, true, false, false);
    @Nonnull
             Attribute CHANGE_PASSWORD_ON_LOGIN   = create("security.password.change", "boolean:", "If set to true then on next login the client should force the user to change their password and then the client should set this value to false.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute SECURITY_BLOCKED           = create("security.access.blocked", "boolean:", "If set to true the user cannot login the informational text should be displayed to the user.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute SECURITY_CONFIRMATION_HASH = create("security.confirm.hash", "text:", "An MD5 Hash of the current password, user UUID and a salt, used to pre-auth actions.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute SECURITY_TOKEN             = create("security.oauth.token", "text:", "Token for ouath token based logins, for example Twitter.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute SECURITY_SECRET            = create("security.oauth.secret", "text:", "Secret for oauth token based logins, for example Twitter.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute SECURITY_RESTRICTED        = create("security.access.restricted", "boolean:", "If set to true the user cannot login   the informational text should be displayed to the user.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute SECURITY_STATUS            = create("security.access.text", "text:", "Informational text relating to the status of the user's account, this should be displayed if the account is blocked.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute PERMISSIONS                = create("security.permissions", "text:", "The numeric value of the permission set for this entity, note that you cannot change this property by changing the entity, an appropriate request must be made.", true, true, false, false, false, true, false);
    @Nonnull
             Attribute PERMISSION_CHANGE          = create("security.alter.permission", "text:", "A request to change the permissions of a resource.", false, false, false, false, false, false, true);
    @Nonnull
             Attribute PLAIN_PASSWORD             = create("security.password.plain", "text:", "Plain text password, will not be stored.", true, false, false, false, true, false, true);
    @Nonnull
             Attribute VIEW_ENTITY                = create("view", "", "The nested view object", false, false, true, true, false, false, false);
    @Nonnull
             Attribute VIEW_HEIGHT                = create("view2d.height", "number:", "The height in pixels when rendering on a 2D pixel based display.", true, false, false, false, false, false, false);
    @Nonnull
             Attribute VIEW_WIDTH                 = create("view2d.width", "number:", "The width in pixels when rendering on a 2D pixel based display.", true, false, false, false, false, false, false);
    @Nonnull
             Attribute VIEW_X                     = create("view2d.x", "number:", "The x position in pixels when rendering on a 2D pixel based display.", true, false, false, false, false, false, false);
    @Nonnull
             Attribute VIEW_Y                     = create("view2d.y", "number:", "The y position in pixels when rendering on a 2D pixel based display.", true, false, false, false, false, false, false);
    @Nonnull
             Attribute VIEW_Z                     = create("view2d.z", "number:", "The z position (i.e plane) in pixels when rendering on a 2D pixel based display.", true, false, false, false, false, false, false);
    @Nonnull
             Attribute VIEW_RADIUS                = create("view2d.radius", "number:", "The distance from the center (caculated from X and Y).", true, true, false, false, false, false, false);
    @Nonnull
             Attribute VIEW_ROTATE_XY             = create("view2d.rotatexy", "number:", "The rotation in degress in the XY plain of the entity.", true, false, false, false, false, false, false);
    @Nonnull
             Attribute ICON_URL                   = create("icon.url", "url:", "The URL of an image suitable for rendering as an icon for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute ICON_HEIGHT                = create("icon.height", "number:int:", "The height of an image suitable for rendering as an icon for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute ICON_WIDTH                 = create("icon.width", "number:int:", "The width of an image suitable for rendering as an icon for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute IMAGE_URL                  = create("image.url", "url:", "The URL of an image suitable for rendering as a fullsize image on a 2D display for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute IMAGE_HEIGHT               = create("image.height", "number:", "The height of an image suitable for rendering as a fullsize image on a 2D display for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute IMAGE_WIDTH                = create("image.width", "number:", "The width of an image suitable for rendering as a fullsize image on a 2D display for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute IMAGE_REFRESH              = create("image.refresh", "number:int:", "The time in milliseconds beyond the published value that the image should be refreshed.", true, false, false, false, false, false, false);
    @Nonnull
             Attribute BACKGROUND_URL             = create("background.url", "url:", "The URL of an image suitable for rendering as the background image on a 2D display for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CATEGORY_TERM              = create("category.term", "text:shortname:", "An array of terms for the category this entity belongs to see http://www.atomenabled.org/developers/syndication/#category .", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_SOURCE               = create("media.content.source", "uri:", "The source of the  file, video, image, anything really associated with the entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_ID                   = create("media.content.id", "text:", "An identifier for the media, local to the media provider not universally unique", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_SIZE                 = create("media.content.filesize", "number:int:", "The size in bytes of the media (file, video, image, anything really) associated with the entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_BITRATE              = create("media.content.bitrate", "number:", "The bitrate of the media (in this case likely to be video or audio) associated with the entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_FRAMERATE            = create("media.content.framerate", "number:", "The framerate of the media (in this case likely to be video) associated with the entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_SAMPLINGRATE         = create("media.content.samplingrate", "number:", "The sampling rate of the media  (in this case likely to be video or audio) associated with the entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_CHANNELS             = create("media.content.channels", "number:int:", "The number of audio channels of the media (in this case likely to be video or audio) associated with the entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_DURATION             = create("media.content.duration", "number:", "The duration of the media (in this case likely to be video or audio) associated with the entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_WIDTH                = create("media.content.width", "number:int:", "The original width of the media (probably video) associated with the entity, only supplied if not an image itself.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_HEIGHT               = create("media.content.height", "number:int:", "The original width of the media (probably video) associated with the entity, only supplied if not an image itself.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_HASH                 = create("media.hash", "text:", "A hash of the media.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute MEDIA_HASH_ALGO            = create("media.hash.algo", "text:shortname:", "The algorithm used to create the hash of the media associated with the entity .", false, false, false, false, false, false, false);
    @Nonnull
             Attribute EVENT_HANDLER              = create("handler", "", "An event handler.", false, false, true, false, false, false, false);
    @Nonnull
             Attribute CLIENT_SCRIPT              = create("script.client.js", "", "A script to be executed.", false, false, true, false, false, false, false);
    @Nonnull
             Attribute SERVER_SCRIPT              = create("script.server.liquid", "", "A script to be executed.", false, false, true, false, false, false, false);
    @Nonnull
             Attribute NAVIGATION_URL             = create("href", "", "A hyperlink to move to.", false, false, true, false, false, false, false);
    @Nonnull
             Attribute WEBSITE_RESOUCE            = create("resource.website", "url:", "A website associated with this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute VERIFICATION_SOURCE        = create("verification.source", "text:", "The source of the verification score for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute VERIFICATION_SCORE         = create("verification.score", "text:", "The verification score for this entity.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REPLY_URI                  = create("reply.message.uri", "uri:", "URI of the message this entity is a reply to.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REPLY_AUTHOR_URI           = create("reply.author.uri", "uri:", "Author of the message this entity is a reply to.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute REPLY_AUTHOR_FN            = create("reply.author.fn", "uri:", "Full name of the author of the message this entity is a reply to.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CHILD_A                    = create("child", "", "An array of sub entities that this entity contains.", false, false, true, false, false, false, false);
    @Nonnull
             Attribute HISTORY_A                  = create("history", "", "An historical version of the entity.", true, true, true, true, false, true, true);
    @Nonnull
             Attribute AUTHOR_A                   = create("author", "", "The author of the entity.", true, false, true, true, false, false, true);
    @Nonnull
             Attribute A_OWNER                    = create("owner", "", "The owner of the entity.", true, false, true, true, false, false, true);
    @Nonnull
             Attribute EDITOR_A                   = create("editor", "", "The last editor of the entity.", true, false, true, true, false, false, true);
    @Nonnull
             Attribute VISITOR_A                  = create("visitor", "", "The visitor of the pool.", true, false, true, false, false, false, true);
    @Nonnull
             Attribute CORRELATION_ID             = create("correlation.id", "uuid:", "Used for asynchronous communication.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CLIENT_APPLICATION_NAME    = create("client.application.name", "text:", "A textual description of the client application the user is using for this session.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CLIENT_APPLICATION_KEY     = create("client.application.key", "text:", "The client key of the client application being used for this session.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute CLIENT_HOST                = create("client.host.description", "text:", "A textual description of the host platform the user is using for this session.", false, false, false, false, false, false, false);
    @Nonnull
             Attribute VALIDATION_FORMAT_STRING   = create("validation.format", "uri:", "The type of validation that should be used to validate a single valued entity", false, false, false, false, false, false, false);
    @Nonnull
             Attribute INTERNAL_MIN_IMAGE_RADIUS  = create("internal.minimageradius", "number:", "The closest position to the center.", true, true, false, false, false, false, false);
    @Nonnull
             Attribute LAST_FORK_VERSION          = create("fork.version.last", "number:", "For internal use only, for version management", true, true, false, false, true, true, false);
    @Nonnull
             Attribute TEST_COUNTER               = create("x.test.counter", "A counter used for testing purposes only.", false, false, false, false, false, false, false);


}
