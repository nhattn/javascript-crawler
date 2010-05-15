@echo off
		java -classpath .;demo.jar;aspriseOCR.jar com.asprise.util.ocr.demo.Demo sample-images/scanned-text-200dpi.jpg "*** Scanned document at 200 dpi ***" "The fonts used in the document is very hard to recognize."  "Normal fonts like 'New Times', 'Arial' should result " "much better performance. "
		pause
		