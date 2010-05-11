## convert an image to acceptable format for tesseract
convert -monochrome /home/yang/workspace/webcrawl/thirdparty/tesseract/temp/num.png /home/yang/workspace/webcrawl/thirdparty/tesseract/temp/num.tif

## run ocr on the new tif file
tesseract /home/yang/workspace/webcrawl/thirdparty/tesseract/temp/num.tif num