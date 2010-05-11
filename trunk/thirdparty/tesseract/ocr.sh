## convert an image to acceptable format for tesseract
convert -monochrome yourfilename.png.gif.or.whatever.suffix out.tif

## run ocr on the new tif file
tesseract out.tif newfilename