/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

var a = null;
PR.registerLangHandler(PR.createSimpleLexer([
    ["pun", /^[:>?|]+/, a, ":|>?"],
    ["dec", /^%(?:YAML|TAG)[^\n\r#]+/, a, "%"],
    ["typ", /^&\S+/, a, "&"],
    ["typ", /^!\S*/, a, "!"],
    ["str", /^"(?:[^"\\]|\\.)*(?:"|$)/, a, '"'],
    ["str", /^'(?:[^']|'')*(?:'|$)/, a, "'"],
    ["com", /^#[^\n\r]*/, a, "#"],
    ["pln", /^\s+/, a, " \t\r\n"]
], [
    ["dec", /^(?:---|\.\.\.)(?:[\n\r]|$)/],
    ["pun", /^-/],
    ["kwd", /^\w+:[\n\r ]/],
    ["pln", /^\w+/]
]), ["yaml", "yml"]);
