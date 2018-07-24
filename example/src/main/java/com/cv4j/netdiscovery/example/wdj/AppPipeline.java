package com.cv4j.netdiscovery.example.wdj;

import com.cv4j.netdiscovery.core.Spider;
import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.ResultItems;
import com.cv4j.netdiscovery.core.downloader.file.LargeFileDownloader;
import com.cv4j.netdiscovery.core.pipeline.Pipeline;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by tony on 2018/6/12.
 */
@Slf4j
public class AppPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems) {
        Elements elements = resultItems.get("app_elements");
        List<AppName> appNames = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Element element = elements.get(i);
            String appName = element.select("div[class=app-desc] a").first().text();
            String packageName = element.select("a[class=i-source install-btn]").first().attr("data-app-pname");
            String downloadUrl = element.select("a[class=i-source install-btn]").first().attr("href");
            appNames.add(new AppName(appName, packageName, downloadUrl));
            log.info(" appName == " + appName + " , downloadUrl == " + downloadUrl);
        }

        downloadApk(appNames);
    }

    private void downloadApk(List<AppName> appNames) {
//        http://www.wandoujia.com/apps/com.tencent.mobileqq/download?pos=detail-ndownload-com.tencent.mobileqq
//        http://www.wandoujia.com/apps/com.tencent.mobileqq/binding?source=web_direct_binded

        String downloadUrl = appNames.get(0).downloadUrl;
        String packageName = appNames.get(0).packageName;
        String appName = appNames.get(0).appName;
        String newDownloadUrl = downloadUrl.split("binding")[0] + "download?pos=detail-ndownload-" + packageName;

        System.out.println("newDownloadUrl == " + newDownloadUrl);
        Request request = new Request(newDownloadUrl);
        request.putExtra(LargeFileDownloader.ARG_FILENAME, appName + "-" + System.currentTimeMillis() + ".apk"); // 在使用FileDownloader时，可以使用AfterRequest或者Pipeline对文件进行保存等处理。这里使用FileDownloadAfterRequest
        Spider.create().name("wandoujia_download")
                .request(request)
                .downloader(new LargeFileDownloader("test")) // 文件的下载需要使用FileDownloader
                .run();
    }


    class AppName {
        String appName;
        String packageName;
        String downloadUrl;

        public AppName(String appName, String packageName, String downloadUrl) {
            this.appName = appName;
            this.packageName = packageName;
            this.downloadUrl = downloadUrl;
        }
    }
}
