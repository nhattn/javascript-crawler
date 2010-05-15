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

import com.tj.common.CommonUtil;
import com.tj.common.OSHelper;
import com.zyd.Constants;

public class Ocr {
    private final static boolean isLinux = OSHelper.isLinux();

    public static String ocrImageNumber(String byteString) {
        if (isLinux) {
            return linuxOcrNumber(byteString);
        } else {
            return windowsOcrNumber(byteString);
        }
    }

    private static Object ocrInstance = null;
    private static Method ocrMethod = null;

    private static String windowsOcrNumber(String byteString) {
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
            err.printStackTrace();
            System.err.println("Can not find AspriseOCR under java.library.path, check if you have set up Asprise ocr correctly. Must add AspriseOCR.dll to your windows path. ");
            return null;
        } catch (ClassNotFoundException err) {
            System.err.println("Can not find com.asprise.util.ocr.OCR under your java class path. Check you have added aspriseOCR.jar to your class path or your server's class path");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int TEMP_COUNTER = 10000;

    private static String linuxOcrNumber(String byteString) {
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
            fout = new FileOutputStream(new File(Constants.LINUX_OCR_DIR, fileHandle + ".png"));
            IOUtils.write(bs, fout);
            fout.flush();
            StringBuffer buf = new StringBuffer();
            buf.append("convert " + fileHandle + ".png -type GrayScale -depth 8 " + fileHandle + ".tif");
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
            e.printStackTrace();
            return null;
        } finally {
            CommonUtil.closeStream(fout);
            CommonUtil.closeStream(writer);
        }

    }

    public static void main(String[] args) {
        System.out
                .println(linuxOcrNumber("iVBORw0KGgoAAAANSUhEUgAAALAAAAAaCAYAAAAXMNbWAAAD3klEQVR4nO1bPZLqMAzea70rvI4TUHMADpA6J0jJESjeUNEys8WmTckwNFvSpsqrzHiFJOvPLMy4ULEbO/6k77Ms2+FjnuelWbN3tQ/u4TDtltVxvfz59/duVNvL7boM027ZnLY/+qyO66Ub++Xz+0sN7nK7PozPYchxaDHk+POxNqftMkw7FW5N3OZ5fvBPYlHYo3nT+g6x7M8HFItYwJgASmD254Mo6N3Yq4LRjb0YgxQHxHC5Xclxclsd16IJoImbV8Be7FG8eXyXYFEJmALBgfn8/noYkHqPNJvBd2oxpLGGaffw//358CP4WMCoQF5uV3R8S9zyviXDCPVij+LN4zuXqJJtTlu9gFfHNUo+BwLOcixDcLMJwwGXRQoDbJeLFM5uiCEtWXmfRDAkh8pG1rhJDAo1x+nFHsGbx3dMvAkTlSxYAaeslf72EoFliVKfPAjY0oL1KbXhnnOBKom/VtwokuH4Edi9vFl9x1YAbc3NbuJqCLgUxLx9amsRMCTWSqQ1BlEC5rJvTRxa3ixjwhXCstGvKmBsGSuBzEuBNKslGGAJkWcEmIm0pwq/KWAu+9bCYeFNOybMvt3Y30uXVBZ1Y1+csKECTo5jRT1Wp0HLhZaTZQlICgoUL7chkLxX2j9CwDALaieeFLuXN4vvsFbGauccN1UqhQuYAlCawdxSKcWAZQ4oaK2IuM1hbQF7s68Uu4c3q+/Y5pwzavI9RcCSmZQ7BMmSYiidi6ZdspQIT/b2ChjGUpt9Ndg9vFl9xxLNMO2Wy+16v9CAz7EJWK0Gzm9VoIhgMCBYCFSCAdtxD9MOFbREiBip0SRyxk3omtg1vHl8l4gTlhUYb9VPIeb58awPLuXYQT11iJ8/T06Xaj3sSprLaFh77VLqiZsn+0Zgl/IWKWDKl1K7pwi4dCTDLV+cJQHDbIERhp05SgWg3cR44wb90WTPCOxS3jy+S7+XeAkBl97jFbAUY6ldpACscbNm32jxevyQ9JFMUskEeoqAPTdCEgzwuSUY8ATDcisUETdL9o3G7uVN4rvkbB62wUqYEAGnq0TsTh0734u+SIC1Glbsc/UcVl5oNmxRArZkXw/2WrxJfedqdWktHyJgahMG+1Li8mKgvsrqxh79zC8nmfqMj/syrJaAtdnXi70Wb1LfMfyb0xY9I6Y2kG4BY+KhzHKRIA1I6RKDylDaA3WpGLV9sKtVreA12GvypvHd8g13qIBTMKiv+tOvAjxLsjYg1K8LsE3NqwjYUvt6sdfiTeu79Vc0IgE3a/bK1gTc7K2tCbjZW1sTcLO3tibgZm9tTcDN3tr+Ay02ZsKz6ivBAAAAAElFTkSuQmCC"));
        // ocrImageNumber("iVBORw0KGgoAAAANSUhEUgAAALAAAAAaCAYAAAAXMNbWAAAD3klEQVR4nO1bPZLqMAzea70rvI4TUHMADpA6J0jJESjeUNEys8WmTckwNFvSpsqrzHiFJOvPLMy4ULEbO/6k77Ms2+FjnuelWbN3tQ/u4TDtltVxvfz59/duVNvL7boM027ZnLY/+qyO66Ub++Xz+0sN7nK7PozPYchxaDHk+POxNqftMkw7FW5N3OZ5fvBPYlHYo3nT+g6x7M8HFItYwJgASmD254Mo6N3Yq4LRjb0YgxQHxHC5Xclxclsd16IJoImbV8Be7FG8eXyXYFEJmALBgfn8/noYkHqPNJvBd2oxpLGGaffw//358CP4WMCoQF5uV3R8S9zyviXDCPVij+LN4zuXqJJtTlu9gFfHNUo+BwLOcixDcLMJwwGXRQoDbJeLFM5uiCEtWXmfRDAkh8pG1rhJDAo1x+nFHsGbx3dMvAkTlSxYAaeslf72EoFliVKfPAjY0oL1KbXhnnOBKom/VtwokuH4Edi9vFl9x1YAbc3NbuJqCLgUxLx9amsRMCTWSqQ1BlEC5rJvTRxa3ixjwhXCstGvKmBsGSuBzEuBNKslGGAJkWcEmIm0pwq/KWAu+9bCYeFNOybMvt3Y30uXVBZ1Y1+csKECTo5jRT1Wp0HLhZaTZQlICgoUL7chkLxX2j9CwDALaieeFLuXN4vvsFbGauccN1UqhQuYAlCawdxSKcWAZQ4oaK2IuM1hbQF7s68Uu4c3q+/Y5pwzavI9RcCSmZQ7BMmSYiidi6ZdspQIT/b2ChjGUpt9Ndg9vFl9xxLNMO2Wy+16v9CAz7EJWK0Gzm9VoIhgMCBYCFSCAdtxD9MOFbREiBip0SRyxk3omtg1vHl8l4gTlhUYb9VPIeb58awPLuXYQT11iJ8/T06Xaj3sSprLaFh77VLqiZsn+0Zgl/IWKWDKl1K7pwi4dCTDLV+cJQHDbIERhp05SgWg3cR44wb90WTPCOxS3jy+S7+XeAkBl97jFbAUY6ldpACscbNm32jxevyQ9JFMUskEeoqAPTdCEgzwuSUY8ATDcisUETdL9o3G7uVN4rvkbB62wUqYEAGnq0TsTh0734u+SIC1Glbsc/UcVl5oNmxRArZkXw/2WrxJfedqdWktHyJgahMG+1Li8mKgvsrqxh79zC8nmfqMj/syrJaAtdnXi70Wb1LfMfyb0xY9I6Y2kG4BY+KhzHKRIA1I6RKDylDaA3WpGLV9sKtVreA12GvypvHd8g13qIBTMKiv+tOvAjxLsjYg1K8LsE3NqwjYUvt6sdfiTeu79Vc0IgE3a/bK1gTc7K2tCbjZW1sTcLO3tibgZm9tTcDN3tr+Ay02ZsKz6ivBAAAAAElFTkSuQmCC");
    }
}
