/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import java.util.Arrays;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public interface LSDTypeGroups {
    List<LSDDictionaryTypes> CORE_POOL_OBJECT_TYPES = Arrays.asList(LSDDictionaryTypes.VIDEO, LSDDictionaryTypes.FEED, LSDDictionaryTypes.TEXT, LSDDictionaryTypes.IMAGE, LSDDictionaryTypes.PERSON);
}
