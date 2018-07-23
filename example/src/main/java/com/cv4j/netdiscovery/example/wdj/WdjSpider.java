package com.cv4j.netdiscovery.example.wdj;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.selenium.Browser;
import com.cv4j.netdiscovery.selenium.action.SeleniumAction;
import com.cv4j.netdiscovery.selenium.downloader.SeleniumDownloader;
import com.cv4j.netdiscovery.selenium.pool.WebDriverPool;
import com.cv4j.netdiscovery.selenium.pool.WebDriverPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 2018/6/12.
 * 爬取豌豆荚的http://www.wandoujia.com/top/app页面
 */
public class WdjSpider {

    public static void main(String[] args) {

        WebDriverPoolConfig config = new WebDriverPoolConfig("example/chromedriver",Browser.CHROME); //设置浏览器的驱动程序和浏览器的类型，浏览器的驱动程序要跟操作系统匹配。
        WebDriverPool.init(config); // 需要先使用init，才能使用WebDriverPool

        List<SeleniumAction> actions = new ArrayList<>();
//        actions.add(new BrowserAction());
//        actions.add(new SearchAction());
//        actions.add(new SortAction());

        SeleniumDownloader seleniumDownloader = new SeleniumDownloader(actions);

        String url = "http://www.wandoujia.com/top/app";

        Spider.create()
                .name("wandoujia")
                .url(url)
                .downloader(seleniumDownloader)
                .parser(new AppParser())
                .pipeline(new AppPipeline())
                .run();
    }
}
