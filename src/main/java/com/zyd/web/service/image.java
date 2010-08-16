package com.zyd.web.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zyd.core.imagestore.ImageStore;
import com.zyd.web.ServiceBase;

public class image extends ServiceBase {
    private FileNameMap mimeMap;

    public image() {
        super();
        mimeMap = URLConnection.getFileNameMap();

    }

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String imageName = req.getParameter("name");
        if (imageName == null) {
            return;
        }
        resp.setContentType(mimeMap.getContentTypeFor(imageName));
        OutputStream out = resp.getOutputStream();
        File imageFile = new File(ImageStore.getImagePath(imageName));
        FileInputStream in = new FileInputStream(imageFile);
        resp.setContentLength((int) imageFile.length());
        byte[] buf = new byte[102400];
        int count = 0;
        while (true) {
            count = in.read(buf);
            if (count <= 0) {
                break;
            }
            out.write(buf, 0, count);
        }
        in.close();
        out.close();
    }
}
