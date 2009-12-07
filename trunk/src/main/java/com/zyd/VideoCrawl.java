package com.zyd;

public class VideoCrawl {

	static {
		init();
	}

	private static String[] urls;
	private static int counter = 0;

	public static void init() {
		urls = new String[] { "http://academicearth.org/lectures/funding-choices", "http://academicearth.org/lectures/how-do-you-find-evangelists",
				"http://academicearth.org/lectures/make-meaning-in-your-company", "http://academicearth.org/lectures/get-up-and-get-going",
				"http://academicearth.org/lectures/weave-mat-and-outline-your-priorities", "http://academicearth.org/lectures/know-thyself-and-niche-thyself",
				"http://academicearth.org/lectures/make-great-pitch", "http://academicearth.org/lectures/lower-barriers-to-adoption",
				"http://academicearth.org/lectures/seed-clouds-and-watch-sales-grow" };

	}

	public static String nextUrl() {
		String r = urls[counter];
		counter = (counter + 1) % urls.length;
		System.out.println("Returning next url " + r);
		return r;
	}

	public static void urlFinished(String url) {
		System.out.println("Finished crawlling of url " + url);
	}

	public static void startCrawl(String url) {
		System.out.println("Starting crawlling of url " + url);
	}
}
