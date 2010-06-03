@echo off

PATH=%PATH%;UI-Eval

java -Xms50M -Xmx500M -classpath .;demo.jar;aspriseOCR.jar;UI-Eval/jid.jar;UI-Eval/JTwain.jar com.asprise.util.ocr.demo.DemoUI
pause
		