package com.zyd.core.imagestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.zyd.Constants;

public class ImageStore {
    private static Logger logger = Logger.getLogger(ImageStore.class);
    static {
        createRootDirectory();
    }

    private static void createRootDirectory() {
        if (Constants.IMAGE_FILE_STORE_ROOT.endsWith("/") == false) {
            Constants.IMAGE_FILE_STORE_ROOT = Constants.IMAGE_FILE_STORE_ROOT + "/";
        }
        File file = new File(Constants.IMAGE_FILE_STORE_ROOT);
        if (file.isFile() == false) {
            if (file.isDirectory() == false) {
                if (file.mkdirs() == false) {
                    logger.fatal("Failed to create directory for ImageStore: " + Constants.IMAGE_FILE_STORE_ROOT);
                } else {
                    logger.info("Created directory for ImageStore: " + Constants.IMAGE_FILE_STORE_ROOT);
                }
            }
        } else {
            logger.fatal("!!! Directory for image store already exist and is a file, can not create a directoy for it:");
            logger.fatal(Constants.IMAGE_FILE_STORE_ROOT);
        }
    }

    public String storeImage(byte[] imageData, String type) throws IOException {
        String fileName = UUID.randomUUID().toString().replace('-', 'o') + "." + type;
        String filePath = getImagePath(fileName);
        File imageFile = new File(filePath);
        File parentDir = imageFile.getParentFile();
        if (parentDir.exists() == false) {
            parentDir.mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
            out.write(imageData);
        } finally {
            if (out != null)
                out.close();
        }
        return fileName;
    }

    public String storeImage(String base64Encoded, String type) throws IOException {
        return storeImage(Base64.decodeBase64(base64Encoded), type);
    }

    public static String getImagePath(String imageName) {
        int a = imageName.indexOf('.');
        if (a <= 0)
            return null;

        String uuid = imageName.substring(0, a);
        StringBuffer path = new StringBuffer(Constants.IMAGE_FILE_STORE_ROOT);
        for (int len = uuid.length(), i = len - 6; i < len; i++) {
            path.append(uuid.charAt(i));
            path.append('/');
        }
        path.append(imageName);
        return path.toString();
    }

    public static boolean deleteImageByName(String name) {
        String fullPath = getImagePath(name);
        if (fullPath == null) {
            return false;
        }
        File imageFile = new File(fullPath);
        if (imageFile.isFile() == false) {
            return false;
        }
        return imageFile.delete();
    }

    public static void main(String[] args) {
        //        String uuid = UUID.randomUUID().toString().replace('-', 'o');
        //        System.out.println(uuid);
        //        System.out.println(getImagePath(uuid + ".name"));
    }
}
