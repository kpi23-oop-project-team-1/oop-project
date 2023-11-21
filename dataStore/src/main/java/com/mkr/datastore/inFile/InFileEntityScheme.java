package com.mkr.datastore.inFile;

import com.mkr.datastore.EntityScheme;
import com.mkr.datastore.EntitySchemeKey;
import java.util.Arrays;

public class InFileEntityScheme {
    public static final String INT_CODE = "int";
    public static final String STRING_CODE = "str";

    private final InFileEntitySchemeKey[] keys;

    public InFileEntityScheme(InFileEntitySchemeKey[] keys) {
        this.keys = keys;
    }

    public InFileEntityScheme(EntityScheme<?> entityScheme) {
        EntitySchemeKey[] schemeKeys = entityScheme.getDirectKeys();

        this.keys = new InFileEntitySchemeKey[schemeKeys.length];
        Arrays.setAll(this.keys, i -> createInFileEntitySchemeKey(schemeKeys[i]));
    }

    public InFileEntitySchemeKey[] getKeys() {
        return keys;
    }

    private InFileEntitySchemeKey createInFileEntitySchemeKey(EntitySchemeKey key) {
        Class<?> valueTypeClass = key.valueType();
        boolean isArray = valueTypeClass.isArray();

        return new InFileEntitySchemeKey(
                key.name(),
                getValueTypeCode(isArray ? valueTypeClass.getComponentType() : valueTypeClass),
                isArray
        );
    }

    private String getValueTypeCode(Class<?> valueType) {
        if (valueType.equals(Integer.class)) {
            return INT_CODE;
        } else if (valueType.equals(String.class)) {
            return STRING_CODE;
        }

        throw new UnsupportedValueTypeException(valueType);
    }
}
