rm -f /y/workspace/webcrawl/thirdparty/firefox/crawler-linux-profile.gz
tar -cf /y/workspace/webcrawl/thirdparty/firefox/crawler-linux-profile.tar -C /y/work/firefox  zyd start.sh
gzip -c /y/workspace/webcrawl/thirdparty/firefox/crawler-linux-profile.tar > /y/workspace/webcrawl/thirdparty/firefox/crawler-linux-profile.gz
rm -f /y/workspace/webcrawl/thirdparty/firefox/crawler-linux-profile.tar