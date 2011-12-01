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
    private LSDEntityFactory lsdEntityFactory;

    @Nonnull
    public LiquidMessage keys() {
        final RetrieveDictionaryRequest request = new RetrieveDictionaryRequest(RetrieveDictionaryRequest.Category.KEYS);
        final LSDTransferEntity dictionary = LSDSimpleEntity.createEmpty();
        dictionary.setAttribute(LSDAttribute.ID, UUIDFactory.randomUUID().toString());
        dictionary.setAttribute(LSDAttribute.NAME, "LiquidKeyDictionary");
        dictionary.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.LIQUID_KEY_DICTIONARY.getValue());
        final LSDAttribute[] values = LSDAttribute.values();
        final List<LSDBaseEntity> entries = new ArrayList<LSDBaseEntity>();
        for (final LSDAttribute value : values) {
            final LSDBaseEntity dictionaryEntry = LSDSimpleEntity.createEmpty();
            dictionaryEntry.setAttribute(LSDAttribute.ID, value.getId().toString());
            dictionaryEntry.setAttribute(LSDAttribute.NAME, value.getKeyName());
            dictionaryEntry.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.LIQUID_KEY_DICTIONARY_ENTRY.getValue());
            dictionaryEntry.setAttribute(LSDAttribute.DESCRIPTION, value.getDescription());
            dictionaryEntry.setAttribute(LSDAttribute.VALIDATION_FORMAT_STRING, value.getFormatValidationString());
            entries.add(dictionaryEntry);
        }
        dictionary.addSubEntities(LSDAttribute.CHILD, entries);
        request.setResponse(dictionary);
        return request;

    }


    @Nonnull
    public RetrieveDictionaryRequest types() {
        final RetrieveDictionaryRequest request = new RetrieveDictionaryRequest(RetrieveDictionaryRequest.Category.TYPES);
        final LSDTransferEntity dictionary = LSDSimpleEntity.createEmpty();
        dictionary.setAttribute(LSDAttribute.NAME, "LiquidTypeDictionary");
        dictionary.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.LIQUID_TYPE_DICTIONARY.getValue());
        final LSDDictionaryTypes[] values = LSDDictionaryTypes.values();
        final List<LSDBaseEntity> entries = new ArrayList<LSDBaseEntity>();
        for (final LSDDictionaryTypes value : values) {
            final LSDBaseEntity dictionaryEntry = LSDSimpleEntity.createEmpty();
            dictionaryEntry.setAttribute(LSDAttribute.ID, UUIDFactory.randomUUID().toString());
            dictionaryEntry.setAttribute(LSDAttribute.NAME, value.getValue());
            dictionaryEntry.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.LIQUID_TYPE_DICTIONARY_ENTRY.getValue());
            entries.add(dictionaryEntry);
        }
        dictionary.addSubEntities(LSDAttribute.CHILD, entries);
        request.setResponse(dictionary);
        return request;
    }

    public void setLsdFactory(final LSDEntityFactory lsdEntityFactory) {
        this.lsdEntityFactory = lsdEntityFactory;
    }

}
