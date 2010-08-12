package com.zyd.linkmanager;

import java.util.ArrayList;
import java.util.HashSet;

import junit.framework.TestCase;

import com.zyd.ATestUtil;
import com.zyd.Constants;

/**
 * Many threads, different domains, stress testing
 * 
 *
 */
public class TestLinkManagerPerformance extends TestCase {
    public static HashSet<String> watchedList;

    @Override
    protected void setUp() throws Exception {
        assertTrue(ATestUtil.clearServerData("Link"));
        watchedList = new HashSet<String>();
        for (Link link : Constants.WATCH_LIST) {
            watchedList.add(link.getUrl());
        }
    }

    public void testCreateLinkFromManyDomain() throws Exception {
        int totalThread = 5;
        long time = System.currentTimeMillis();
        ArrayList<SimpleProducerThread> threads = new ArrayList<SimpleProducerThread>();
        for (int i = 0; i < totalThread; i++) {
            SimpleProducerThread thread = new SimpleProducerThread("www.domain" + i + ".com", 10000);
            threads.add(thread);
            (thread).start();
        }

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }

        ArrayList<SimpleConsumerThread> cthreads = new ArrayList<SimpleConsumerThread>();
        for (int i = 0; i < totalThread; i++) {
            SimpleConsumerThread thread = new SimpleConsumerThread();
            cthreads.add(thread);
            (thread).start();
        }

        int lastProduce = -1, lastConsume = -1;
        for (int i = 0; i < 60; i++) {
            int totalProduce = 0;
            for (SimpleProducerThread t : threads) {
                totalProduce = totalProduce + t.counter;
            }

            int totalConsume = 0;
            for (SimpleConsumerThread t : cthreads) {
                totalConsume = totalConsume + t.total;
            }

            if (totalConsume == lastConsume && totalProduce == lastProduce) {
                break;
            } else {
                lastProduce = totalProduce;
                lastConsume = totalConsume;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // TODO: handle exception
            }
            System.out.println("total produce " + totalProduce + ", " + totalProduce / ((System.currentTimeMillis() - time + 1) / 1000) + "per second");
        }

        int totalProduce = 0;
        for (SimpleProducerThread t : threads) {
            totalProduce = totalProduce + t.counter;
        }

        int totalConsume = 0;
        HashSet<String> allLinks = new HashSet<String>();
        int dup = 0;
        for (SimpleConsumerThread t : cthreads) {
            totalConsume = totalConsume + t.total;
            for (String s : t.links) {
                if (allLinks.add(s) == false) {
                    System.out.println(s);
                    dup++;
                }
            }
        }
        assertEquals(0, dup);
        assertEquals(totalProduce, totalConsume);
    }

    class SimpleProducerThread extends Thread {
        String domain;
        int maxLink;

        public int counter = 0;

        SimpleProducerThread(String domain, int maxLink) {
            this.domain = domain;
            this.maxLink = maxLink;
        }

        @Override
        public void run() {
            int i = 0;
            while (i < 1000) {
                try {
                    assertTrue(ATestUtil.createLink("http://" + domain + "/link_" + i++));
                    counter++;
                } catch (Exception e) {
                    System.err.println("failed to create link: " + ("http://" + domain + "/link_" + i));
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    class SimpleConsumerThread extends Thread {
        public int total = 0;
        public HashSet<String> links = new HashSet<String>();

        @Override
        public void run() {
            while (true) {
                try {
                    String link = ATestUtil.getNextLink();
                    if (link == null || watchedList.contains(link)) {
                        continue;
                    } else {
                        total++;
                        assertTrue(links.add(link));
                    }
                } catch (Exception e1) {
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
