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
        RetrieveDictionaryRequest request = new RetrieveDictionaryRequest(RetrieveDictionaryRequest.Category.KEYS);
        LSDEntity dictionary = LSDSimpleEntity.createEmpty();
        dictionary.setAttribute(LSDAttribute.ID, UUIDFactory.randomUUID().toString());
        dictionary.setAttribute(LSDAttribute.NAME, "LiquidKeyDictionary");
        dictionary.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.LIQUID_KEY_DICTIONARY.getValue());
        LSDAttribute[] values = LSDAttribute.values();
        List<LSDEntity> entries = new ArrayList<LSDEntity>();
        for (LSDAttribute value : values) {
            LSDEntity dictionaryEntry = LSDSimpleEntity.createEmpty();
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
        RetrieveDictionaryRequest request = new RetrieveDictionaryRequest(RetrieveDictionaryRequest.Category.TYPES);
        LSDEntity dictionary = LSDSimpleEntity.createEmpty();
        dictionary.setAttribute(LSDAttribute.NAME, "LiquidTypeDictionary");
        dictionary.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.LIQUID_TYPE_DICTIONARY.getValue());
        LSDDictionaryTypes[] values = LSDDictionaryTypes.values();
        List<LSDEntity> entries = new ArrayList<LSDEntity>();
        for (LSDDictionaryTypes value : values) {
            LSDEntity dictionaryEntry = LSDSimpleEntity.createEmpty();
            dictionaryEntry.setAttribute(LSDAttribute.ID, UUIDFactory.randomUUID().toString());
            dictionaryEntry.setAttribute(LSDAttribute.NAME, value.getValue());
            dictionaryEntry.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.LIQUID_TYPE_DICTIONARY_ENTRY.getValue());
            entries.add(dictionaryEntry);
        }
        dictionary.addSubEntities(LSDAttribute.CHILD, entries);
        request.setResponse(dictionary);
        return request;
    }

    public void setLsdFactory(LSDEntityFactory lsdEntityFactory) {
        this.lsdEntityFactory = lsdEntityFactory;
    }

}
