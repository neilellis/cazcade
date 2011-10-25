package cazcade.liquid.api.lsd;

/**
 * @author neilelliz@cazcade.com
 */
public interface LSDPropertyTypeValidator {
    boolean validate(LSDPropertyFormatValidator propertyFormatValidator, String nextValidationString, String value);
}
