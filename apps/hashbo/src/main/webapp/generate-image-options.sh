cd _background
for file in `find . -name "*.jpg"` `find . -name "*.png"`
do
if echo $file | grep -v thumb > /dev/null
then
f="`echo $file | sed -e \"s/\.\///\"`"
echo  "<image:option><image:ImageOption url=\"_background/$f\"  thumbnail=\"_background/thumb/$f\"/> </image:option>"
fi
done


