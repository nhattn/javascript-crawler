package com.zyd.web.filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.zyd.Constants;

public class ServiceEncodingFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        res.setCharacterEncoding(Constants.ENCODING_OUT_GOING_CONTENT);
        if (req.getCharacterEncoding() == null) {
            req.setCharacterEncoding(Constants.ENCODING_INCOMING_CONTENT);
        }
        chain.doFilter(req, res);
    }

    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    public static void main(String[] args) throws Exception {
        ServerSocket sock = new ServerSocket(8080);
        while (true) {
            Socket socket = sock.accept();
            System.out.println("got");
            new MThread(socket).start();
            break;
        }
    }

    static class MThread extends Thread {
        Socket sock;

        public MThread(Socket sock) {
            this.sock = sock;
        }

        @Override
        public void run() {
            try {
                System.out.println("start reading");
                InputStream ins = sock.getInputStream();
                int r = 0;
                byte[] bs = new byte[3000];
                System.out.println(r + "--------------");
                ins.read(bs);
                System.out.println(new String(bs));
                System.out.println(new String(bs, "GBK"));
                System.out.println(new String(bs, "UTF-8"));
                System.out.println(new String(bs, "ISO-8859-1"));
                ins.close();
                System.out.println("------ exiting");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
