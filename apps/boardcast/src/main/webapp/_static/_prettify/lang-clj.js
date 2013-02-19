/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */
var a = null;
PR.registerLangHandler(PR.createSimpleLexer([
    ["opn", /^[([{]+/, a, "([{"],
    ["clo", /^[)\]}]+/, a, ")]}"],
    ["com", /^;[^\n\r]*/, a, ";"],
    ["pln", /^[\t\n\r \xa0]+/, a, "\t\n\r \xa0"],
    ["str", /^"(?:[^"\\]|\\[\S\s])*(?:"|$)/, a, '"']
], [
    ["kwd", /^(?:def|if|do|let|quote|var|fn|loop|recur|throw|try|monitor-enter|monitor-exit|defmacro|defn|defn-|macroexpand|macroexpand-1|for|doseq|dosync|dotimes|and|or|when|not|assert|doto|proxy|defstruct|first|rest|cons|defprotocol|deftype|defrecord|reify|defmulti|defmethod|meta|with-meta|ns|in-ns|create-ns|import|intern|refer|alias|namespace|resolve|ref|deref|refset|new|set!|memfn|to-array|into-array|aset|gen-class|reduce|map|filter|find|nil?|empty?|hash-map|hash-set|vec|vector|seq|flatten|reverse|assoc|dissoc|list|list?|disj|get|union|difference|intersection|extend|extend-type|extend-protocol|prn)\b/, a],
    ["typ", /^:[\dA-Za-z-]+/]
]), ["clj"]);
