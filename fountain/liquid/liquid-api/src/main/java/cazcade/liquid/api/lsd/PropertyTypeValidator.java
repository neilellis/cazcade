/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

/**
 * @author neilelliz@cazcade.com
 */
public interface PropertyTypeValidator {
    boolean validate(PropertyFormatValidator propertyFormatValidator, String nextValidationString, String value);
}
