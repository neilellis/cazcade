<?php
require_once('less.inc.php');

$less        = (string) 'boardcast-v2.less';
$less_cache      = (string) $less.'.cache';
$cache       = (string) (file_exists($less_cache)) ? unserialize(file_get_contents($less_cache)) : $less;
$new_cache   = lessc::cexecute($cache);

if(!is_array($cache) || $new_cache['updated'] > $cache['updated']) { file_put_contents($less_cache, serialize($new_cache)); }

header('Content-type: text/css');
echo (string) $new_cache['compiled'];
?>