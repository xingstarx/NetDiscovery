package com.cv4j.netdiscovery.example.wdj;

import com.cv4j.netdiscovery.core.domain.Page;
import com.cv4j.netdiscovery.core.parser.Parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by tony on 2018/6/12.
 */
public class AppParser implements Parser{

    @Override
    public void process(Page page) {
        String pageHtml = page.getHtml().toString();
        Document document = Jsoup.parse(pageHtml);
        Elements elements = document.select("div[class=app-box] ul[id=j-top-list] li[class=card]");
        page.getResultItems().put("app_elements",elements);
    }
}
