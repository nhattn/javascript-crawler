package com.zyd.core.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.asprise.util.ocr.OCR;
import com.tj.common.OSHelper;

public class Ocr {

    public static String ocrImageNumber(String byteString) {
        if (OSHelper.isLinux()) {
            return linuxOcrNumber(byteString);
        } else if (OSHelper.isWindows()) {
            return windowsOcrNumber(byteString);
        } else {
            throw new UnsupportedOperationException("ocr not supported on your platform");
        }
    }

    private static String windowsOcrNumber(String byteString) {
        try {
            Base64 b = new Base64();
            ByteArrayInputStream ins = new ByteArrayInputStream(b.decode(byteString.getBytes()));
            BufferedImage image = ImageIO.read(ins);
            return new OCR().recognizeEverything(image);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String linuxOcrNumber(String byteString) {
        Base64 b = new Base64();
        byte[] bs = b.decode(byteString.getBytes());
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(new File("/home/yang/workspace/webcrawl/thirdparty/tesseract/temp/num.png"));
            IOUtils.write(bs, fout);
            fout.close();
            System.out.println(OSHelper.executeCommandForString("/bin/sh /home/yang/workspace/webcrawl/thirdparty/tesseract/temp/ocr.sh", new File(
                    "/home/yang/workspace/webcrawl/thirdparty/tesseract/temp/")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

    public static void main(String[] args) {
        linuxOcrNumber("iVBORw0KGgoAAAANSUhEUgAAALAAAAAaCAYAAAAXMNbWAAAD3klEQVR4nO1bPZLqMAzea70rvI4TUHMADpA6J0jJESjeUNEys8WmTckwNFvSpsqrzHiFJOvPLMy4ULEbO/6k77Ms2+FjnuelWbN3tQ/u4TDtltVxvfz59/duVNvL7boM027ZnLY/+qyO66Ub++Xz+0sN7nK7PozPYchxaDHk+POxNqftMkw7FW5N3OZ5fvBPYlHYo3nT+g6x7M8HFItYwJgASmD254Mo6N3Yq4LRjb0YgxQHxHC5Xclxclsd16IJoImbV8Be7FG8eXyXYFEJmALBgfn8/noYkHqPNJvBd2oxpLGGaffw//358CP4WMCoQF5uV3R8S9zyviXDCPVij+LN4zuXqJJtTlu9gFfHNUo+BwLOcixDcLMJwwGXRQoDbJeLFM5uiCEtWXmfRDAkh8pG1rhJDAo1x+nFHsGbx3dMvAkTlSxYAaeslf72EoFliVKfPAjY0oL1KbXhnnOBKom/VtwokuH4Edi9vFl9x1YAbc3NbuJqCLgUxLx9amsRMCTWSqQ1BlEC5rJvTRxa3ixjwhXCstGvKmBsGSuBzEuBNKslGGAJkWcEmIm0pwq/KWAu+9bCYeFNOybMvt3Y30uXVBZ1Y1+csKECTo5jRT1Wp0HLhZaTZQlICgoUL7chkLxX2j9CwDALaieeFLuXN4vvsFbGauccN1UqhQuYAlCawdxSKcWAZQ4oaK2IuM1hbQF7s68Uu4c3q+/Y5pwzavI9RcCSmZQ7BMmSYiidi6ZdspQIT/b2ChjGUpt9Ndg9vFl9xxLNMO2Wy+16v9CAz7EJWK0Gzm9VoIhgMCBYCFSCAdtxD9MOFbREiBip0SRyxk3omtg1vHl8l4gTlhUYb9VPIeb58awPLuXYQT11iJ8/T06Xaj3sSprLaFh77VLqiZsn+0Zgl/IWKWDKl1K7pwi4dCTDLV+cJQHDbIERhp05SgWg3cR44wb90WTPCOxS3jy+S7+XeAkBl97jFbAUY6ldpACscbNm32jxevyQ9JFMUskEeoqAPTdCEgzwuSUY8ATDcisUETdL9o3G7uVN4rvkbB62wUqYEAGnq0TsTh0734u+SIC1Glbsc/UcVl5oNmxRArZkXw/2WrxJfedqdWktHyJgahMG+1Li8mKgvsrqxh79zC8nmfqMj/syrJaAtdnXi70Wb1LfMfyb0xY9I6Y2kG4BY+KhzHKRIA1I6RKDylDaA3WpGLV9sKtVreA12GvypvHd8g13qIBTMKiv+tOvAjxLsjYg1K8LsE3NqwjYUvt6sdfiTeu79Vc0IgE3a/bK1gTc7K2tCbjZW1sTcLO3tibgZm9tTcDN3tr+Ay02ZsKz6ivBAAAAAElFTkSuQmCC");
    }
}
