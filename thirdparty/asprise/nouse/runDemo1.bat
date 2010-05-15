@echo off
		java -classpath .;demo.jar;aspriseOCR.jar com.asprise.util.ocr.demo.Demo sample-images/ocr.gif "A general sample with both characters and barcodes"
		pause
		