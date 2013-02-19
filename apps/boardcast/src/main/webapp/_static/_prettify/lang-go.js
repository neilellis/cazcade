/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

PR.registerLangHandler(PR.createSimpleLexer([
    ["pln", /^[\t\n\r \xa0]+/, null, "\t\n\r �\xa0"],
    ["pln", /^(?:"(?:[^"\\]|\\[\S\s])*(?:"|$)|'(?:[^'\\]|\\[\S\s])+(?:'|$)|`[^`]*(?:`|$))/, null, "\"'"]
], [
    ["com", /^(?:\/\/[^\n\r]*|\/\*[\S\s]*?\*\/)/],
    ["pln", /^(?:[^"'/`]|\/(?![*/]))+/]
]), ["go"]);
