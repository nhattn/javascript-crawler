rm num.tif
rm num.txt
convert num.png -type GrayScale -depth 8 num.tif
tesseract num.tif num
cat num.txt