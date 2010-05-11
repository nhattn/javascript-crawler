## convert an image to acceptable format for tesseract
convert -monochrome num.png num.tif

## run ocr on the new tif file
tesseract num.tif num