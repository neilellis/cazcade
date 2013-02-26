/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.dictionary;

import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.RetrieveDictionaryRequest;
import cazcade.liquid.impl.UUIDFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class DictionaryRestHandler extends AbstractRestHandler {
    private EntityFactory entityFactory;

    @Nonnull
    public LiquidMessage keys() {
        final RetrieveDictionaryRequest request = new RetrieveDictionaryRequest(RetrieveDictionaryRequest.Category.KEYS);
        final TransferEntity dictionary = SimpleEntity.createEmpty();
        dictionary.$(Dictionary.ID, UUIDFactory.randomUUID().toString());
        dictionary.$(Dictionary.NAME, "LiquidKeyDictionary");
        dictionary.$(Dictionary.TYPE, Types.T_LIQUID_KEY_DICTIONARY.getValue());
        final Attribute[] values = Attribute.values();
        final List<Entity> entries = new ArrayList<Entity>();
        for (final Attribute value : values) {
            final Entity dictionaryEntry = SimpleEntity.createEmpty();
            //            dictionaryEntry.$(Attribute.ID, value.id().toString());
            dictionaryEntry.$(Dictionary.NAME, value.getKeyName());
            dictionaryEntry.$(Dictionary.TYPE, Types.T_LIQUID_KEY_DICTIONARY_ENTRY.getValue());
            dictionaryEntry.$(Dictionary.DESCRIPTION, value.getDescription());
            dictionaryEntry.$(Dictionary.VALIDATION_FORMAT_STRING, value.getFormatValidationString());
            entries.add(dictionaryEntry);
        }
        dictionary.children(Dictionary.CHILD_A, entries);
        request.response(dictionary);
        return request;
    }

    public void setLsdFactory(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Nonnull
    public RetrieveDictionaryRequest types() {
        final RetrieveDictionaryRequest request = new RetrieveDictionaryRequest(RetrieveDictionaryRequest.Category.TYPES);
        final TransferEntity dictionary = SimpleEntity.createEmpty();
        dictionary.$(Dictionary.NAME, "LiquidTypeDictionary");
        dictionary.$(Dictionary.TYPE, Types.T_LIQUID_TYPE_DICTIONARY.getValue());
        final Types[] values = Types.values();
        final List<Entity> entries = new ArrayList<Entity>();
        for (final Types value : values) {
            final Entity dictionaryEntry = SimpleEntity.createEmpty();
            dictionaryEntry.$(Dictionary.ID, UUIDFactory.randomUUID().toString());
            dictionaryEntry.$(Dictionary.NAME, value.getValue());
            dictionaryEntry.$(Dictionary.TYPE, Types.T_LIQUID_TYPE_DICTIONARY_ENTRY.getValue());
            entries.add(dictionaryEntry);
        }
        dictionary.children(Dictionary.CHILD_A, entries);
        request.response(dictionary);
        return request;
    }
}
