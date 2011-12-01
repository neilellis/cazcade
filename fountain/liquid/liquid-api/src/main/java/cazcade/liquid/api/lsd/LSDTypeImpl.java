package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDTypeImpl implements LSDType {
    @Nullable
    private String genus;
    @Nullable
    private String family;
    @Nullable
    private String typeClass;
    @Nonnull
    private final List<String> flavors = new ArrayList<String>();
    @Nullable
    private String typeString;

    public LSDTypeImpl() {
    }

    public LSDTypeImpl(@Nonnull String typeString) {
        this(typeString, true);
    }

    protected LSDTypeImpl(@Nonnull String typeString, boolean fullType) {
        this.typeString = typeString;
        String[] types = typeString.split("\\.");
        if (fullType && types.length < 3) {
            throw new LSDTypeException("Types must be at least Genus.Family.Class");
        }
        genus = types[0];
        if (types.length > 1) {
            family = types[1];
        }
        if (types.length > 2) {
            typeClass = types[2];
        }
        if (types.length > 3) {
            flavors.addAll(Arrays.asList(types).subList(3, types.length));
        }
    }

    public LSDTypeImpl(@Nullable String genus, @Nullable String family, @Nullable String typeClass) {

        if (genus == null || family == null || typeClass == null) {
            throw new LSDTypeException("Types must be at least Genus.Family.Class");
        }
        this.genus = genus;
        this.family = family;
        this.typeClass = typeClass;
        typeString = genus + "." + family + "." + typeClass;
    }

    @Nullable
    public String getGenus() {
        return genus;
    }

    @Nullable
    public String getFamily() {
        return family;
    }

    @Nullable
    public String getTypeClass() {
        return typeClass;
    }

    @Nonnull
    public List<String> getFlavors() {
        return flavors;
    }

    @Nullable
    public String asString() {
        return typeString;
    }

    @Nullable
    @Override
    public String toString() {
        return asString();
    }

    public boolean isSystemType() {
        return typeString.startsWith("System");
    }

    public boolean isA(@Nonnull LSDDictionaryTypes dictionaryType) {
        return typeString.equals(dictionaryType.getValue());
    }

    public boolean canBe(@Nonnull LSDDictionaryTypes type) {
        return isA(type) || typeString.startsWith(type.getValue() + ".");
    }

    @Nonnull
    public LSDType getClassOnlyType() {
        return new LSDTypeImpl(genus, family, typeClass);
    }

    @Nonnull
    public LSDType getParentType() {
        int index = typeString.lastIndexOf('.');
        if (index < 0) {
            throw new LSDTypeException("Cannot get the parent of a Genus only.");
        }
        return new LSDTypeImpl(typeString.substring(0, index));
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LSDTypeImpl lsdType = (LSDTypeImpl) o;

        if (!typeString.equals(lsdType.typeString)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return typeString.hashCode();
    }
}
