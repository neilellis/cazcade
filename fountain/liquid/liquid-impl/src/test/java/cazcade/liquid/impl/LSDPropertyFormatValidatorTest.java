package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import junit.framework.TestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDPropertyFormatValidatorTest extends TestCase {
    private ClassPathXmlApplicationContext applicationContext;
    private LSDPropertyFormatValidator validator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        applicationContext = new ClassPathXmlApplicationContext("liquid-spring-config.xml");
        validator = (LSDPropertyFormatValidator) applicationContext.getBean("formatValidator");

    }


    public void test() {
        assertTrue("Number validation failed.", validator.isValidFormat("number:", "0123.456"));
        assertTrue("Number validation failed.", validator.isValidFormat("number:", "-123.456"));
        assertTrue("Number validation failed.", validator.isValidFormat("number:", "0123."));
        assertTrue("Number validation failed.", validator.isValidFormat("number:", ".456"));
        assertTrue("Integer validation failed.", validator.isValidFormat("number:int:", "123"));
        assertTrue("Integer validation failed.", validator.isValidFormat("number:int:", "-123"));
        assertTrue("Integer validation failed.", validator.isValidFormat("number:int:", "+123"));
        assertTrue("UUID validation failed.", validator.isValidFormat("uuid:", UUIDFactory.randomUUID().toString()));
        assertTrue("URL validation failed.", validator.isValidFormat("url:", "http://www.facebook.com/home.php#!/home.php?sk=lf"));
        assertTrue("URI validation failed.", validator.isValidFormat("uri:", "uri:"));
        assertTrue("Nested validation failed.", validator.isValidFormat("text:uri:url:", "http://www.facebook.com/home.php#!/home.php?sk=lf"));
        assertTrue("RegEx validation failed.", validator.isValidFormat("text:regex:[0-1]+", "11101010101010101010101"));
        assertTrue("MimeType validation failed.", validator.isValidFormat("text:mime:", "x-application/text+wibble"));
        assertTrue("Boolean validation failed.", validator.isValidFormat("boolean:", "false"));
        assertTrue("Boolean validation failed.", validator.isValidFormat("boolean:", "true"));
        assertTrue("Short Name validation failed.", validator.isValidFormat("shortname:", "steve"));
        assertTrue("Short Name validation failed.", validator.isValidFormat("shortname:", "steve0"));
        assertTrue("Short Name validation failed.", validator.isValidFormat("shortname:", ".steve0"));
        assertTrue("Email validation failed.", validator.isValidFormat("email:", "neil.ellis@mangala.co.uk"));
        assertTrue("Title validation failed.", validator.isValidFormat("title:", "Who Framed Roger Rabbit?"));
        assertTrue("Type validation failed.", validator.isValidFormat("type:", "Wibbly.Wide.Web(Wobbly.Wibbly.Web,Dibbly.Dabbly.Deb,TeddyBear.Picnic.Woods.Dont.Go.Down)"));

        assertFalse("Integer negative validation failed.", validator.isValidFormat("number:int:", "123A"));
        assertFalse("Integer negative validation failed.", validator.isValidFormat("number:int:", "123.1"));
        assertFalse("Integer negative validation failed.", validator.isValidFormat("number:int:", ".123"));
        assertFalse("Number negative failed.", validator.isValidFormat("number:", "A0123.456"));
        assertFalse("Number negative failed.", validator.isValidFormat("number:", "0.123.456"));
        assertFalse("UUID negative validation failed.", validator.isValidFormat("uuid:", UUIDFactory.randomUUID().toString() + "1"));
        assertFalse("URL negative validation failed.", validator.isValidFormat("url:", "dave:?www.facebook.com/home.php#!/home.php?sk=lf"));
//        assertFalse("URI negative validation failed.", validator.isValidFormat("uri:", "/"));
        assertFalse("Nested URI negative validation failed.", validator.isValidFormat("text:uri:url:", "dave://neil@neil@www.facebook.com/home.php#!/home.php?sk=lf"));
        assertFalse("RegEx negative validation failed.", validator.isValidFormat("text:regex:[0-1]+", "11101010101012101010101"));
        assertFalse("MimeType negative validation failed.", validator.isValidFormat("text:mime:", "/x-application/text+wibble"));
        assertFalse("Boolean negative validation failed.", validator.isValidFormat("boolean:", "TRUE"));
        assertFalse("Boolean negative validation failed.", validator.isValidFormat("boolean:", "1"));
        assertFalse("Boolean negative validation failed.", validator.isValidFormat("boolean:", "yes"));
        assertFalse("Boolean negative validation failed.", validator.isValidFormat("boolean:", "on"));
        assertFalse("Short Name negative validation failed.", validator.isValidFormat("shortname:", "steve-0"));
        assertFalse("Short Name negative validation failed.", validator.isValidFormat("shortname:", "steve-e"));
        assertFalse("Short Name negative validation failed.", validator.isValidFormat("shortname:", "stev#IE"));
        assertFalse("Email negative validation failed.", validator.isValidFormat("email:", "neil.ellis"));
        assertFalse("Email negative validation failed.", validator.isValidFormat("email:", "neil.ellis@com."));
        assertFalse("Email negative validation failed.", validator.isValidFormat("email:", "@mangala.co.uk"));
        assertFalse("Type negative validation failed.", validator.isValidFormat("type:", "Wibbly.Wide(Wobbly.Wibbly.Web,Dibbly.Dabbly.Deb,TeddyBear.Picnic.Woods.Dont.Go.Down)"));
        assertFalse("Type negative validation failed.", validator.isValidFormat("type:", "Wibbly.Wide.Web()"));
        assertFalse("Type negative validation failed.", validator.isValidFormat("type:", "Wibbly.Wide.Web(Wobbly.Wibbly.,Dibbly.Dabbly.Deb,TeddyBear.Picnic.Woods.Dont.Go.Down)"));
        assertFalse("Type negative validation failed.", validator.isValidFormat("type:", "Wibbly.Wide.Web(Wobbly.Wibbly,Dibbly.Dabbly.Deb,TeddyBear.Picnic.Woods.Dont.Go.Down)"));
        assertFalse("Type negative validation failed.", validator.isValidFormat("type:", "Wibbly.Wide.(Wobbly.Wibbly.Web,Dibbly.Dabbly.Deb,TeddyBear.Picnic.Woods.Dont.Go.Down)"));
        assertFalse("Type negative validation failed.", validator.isValidFormat("type:", "Wibbly.Wide.Web(Wobbly.Wibbly.Web,Dibbly.Dabbly.Deb,,TeddyBear.Picnic.Woods.Dont.Go.Down)"));
        assertFalse("Type negative validation failed.", validator.isValidFormat("type:", "Wibbly.Wide.Web(Wobbly.Wibbly.Web,Dibbly.Dabbly.Deb,TeddyBear.Picnic.Woods.Dont.Go.Down"));
    }


}
