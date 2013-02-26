/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

/**
 * @author neilelliz@cazcade.com
 */
public interface PropertyFormatValidator {
    boolean isValidFormat(String validationString, String value) throws PropertyFormatValidationException;
}
