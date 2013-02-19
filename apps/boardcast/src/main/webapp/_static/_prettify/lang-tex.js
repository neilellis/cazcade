/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

PR.registerLangHandler(PR.createSimpleLexer([
    ["pln", /^[\t\n\r \xa0]+/, null, "\t\n\r �\xa0"],
    ["com", /^%[^\n\r]*/, null, "%"]
], [
    ["kwd", /^\\[@-Za-z]+/],
    ["kwd", /^\\./],
    ["typ", /^[$&]/],
    ["lit", /[+-]?(?:\.\d+|\d+(?:\.\d*)?)(cm|em|ex|in|pc|pt|bp|mm)/i],
    ["pun", /^[()=[\]{}]+/]
]), ["latex", "tex"]);
