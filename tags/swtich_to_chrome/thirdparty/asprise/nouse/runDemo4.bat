@echo off
		java -classpath .;demo.jar;aspriseOCR.jar com.asprise.util.ocr.demo.Demo sample-images/scanned-text-100dpi.jpg "*** Scanned document at 100 dpi (very low resolution) ***" "The fonts used in the document is very hard to recognize. "  "Normal fonts like 'New Times', 'Arial' should result " "much better performance. "
		pause
		