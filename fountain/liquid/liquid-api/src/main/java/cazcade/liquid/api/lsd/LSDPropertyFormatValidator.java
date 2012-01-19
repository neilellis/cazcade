package cazcade.liquid.api.lsd;

/**
 * @author neilelliz@cazcade.com
 */
public interface LSDPropertyFormatValidator {
    boolean isValidFormat(String validationString, String value) throws LSDPropertyFormatValidationException;
}
