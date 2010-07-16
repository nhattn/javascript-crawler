package com.zyd.core.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.tj.common.CommonUtil;
import com.tj.common.OSHelper;
import com.zyd.Constants;

public class Ocr {
    private static Logger logger = Logger.getLogger(Ocr.class);
    private final static boolean isLinux = OSHelper.isLinux();

    /**
     * 
     * @param byteString the image data in byte, encoded with base64. 
     * @param format the suffix of the image, png, jpg etc
     * @return
     */
    public static String ocrImageNumber(String byteString, String format) {
        String n = "";
        if (isLinux) {
            n = linuxOcrNumber(byteString, format);
        } else {
            n = windowsOcrNumber(byteString, format);
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0, len = n.length(); i < len; i++) {
            char c = n.charAt(i);
            if (Character.isWhitespace(c) == true || c == '\'' || c == '"') {
                continue;
            }
            if (c == 'o' || c == 'O') {
                buf.append('0');
            } else if (c == 'l' || c == 'L' || c == 'I' || c == 'i') {
                buf.append('1');
            } else if (c == 'z' || c == 'Z') {
                buf.append('2');
            } else if (c == 'g') {
                buf.append('9');
            } else if (c == 'S' || c == 's' || c == 'B') {
                buf.append('8');
            } else if (c == 'b') {
                buf.append('6');
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    private static Object ocrInstance = null;
    private static Method ocrMethod = null;

    private static String windowsOcrNumber(String byteString, String format) {
        try {
            if (ocrMethod == null) {
                synchronized (Ocr.class) {
                    ocrInstance = Class.forName("com.asprise.util.ocr.OCR").newInstance();
                    ocrMethod = ocrInstance.getClass().getMethod("recognizeEverything", java.awt.image.RenderedImage.class);
                }
            }
            Base64 b = new Base64();
            ByteArrayInputStream ins = new ByteArrayInputStream(b.decode(byteString.getBytes()));
            BufferedImage image = ImageIO.read(ins);
            String r = (String) ocrMethod.invoke(ocrInstance, image);
            return r.trim();
        } catch (UnsatisfiedLinkError err) {
            logger.error("Can not find AspriseOCR under java.library.path, check if you have set up Asprise ocr correctly. Must add AspriseOCR.dll to your windows path. ");
            logger.error(err);
            return null;
        } catch (ClassNotFoundException err) {
            logger.error("Can not find com.asprise.util.ocr.OCR under your java class path. Check you have added aspriseOCR.jar to your class path or your server's class path");
            logger.error(err);
            return null;
        } catch (Exception e) {
            logger.error("Can not peform ocr for input.");
            logger.debug("detailed info is: ");
            logger.debug(byteString);
            logger.debug(format);
            return null;
        }
    }

    private static int TEMP_COUNTER = 10000;

    private static String linuxOcrNumber(String byteString, String format) {
        int suffix = 0;
        synchronized (Ocr.class) {
            suffix = TEMP_COUNTER++;
        }
        String fileHandle = "img" + suffix;
        Base64 b = new Base64();
        byte[] bs = b.decode(byteString.getBytes());
        FileOutputStream fout = null;
        FileWriter writer = null;
        try {
            fout = new FileOutputStream(new File(Constants.LINUX_OCR_DIR, fileHandle + "." + format));
            IOUtils.write(bs, fout);
            fout.flush();
            StringBuffer buf = new StringBuffer();
            buf.append("convert " + fileHandle + "." + format + " -type GrayScale -depth 8 " + fileHandle + ".tif");
            buf.append("\n");
            buf.append("tesseract " + fileHandle + ".tif " + fileHandle);
            buf.append("\n");
            buf.append("cat " + fileHandle + ".txt");
            buf.append("\n");
            buf.append("rm " + fileHandle + ".*");
            buf.append("\n");
            writer = new FileWriter(new File(Constants.LINUX_OCR_DIR, fileHandle + ".sh"));
            IOUtils.write(buf.toString(), writer);
            writer.flush();
            String txt = OSHelper.executeCommandForString("/bin/bash " + fileHandle + ".sh", new File(Constants.LINUX_OCR_DIR));
            return txt.trim();
        } catch (Exception e) {
            logger.error("Can not peform ocr for input.");
            logger.debug("detailed info is: ");
            logger.debug(byteString);
            logger.debug(format);
            return null;
        } finally {
            CommonUtil.closeStream(fout);
            CommonUtil.closeStream(writer);
        }
    }
}
