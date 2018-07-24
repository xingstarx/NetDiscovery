package com.cv4j.netdiscovery.core.downloader.file;

import com.cv4j.netdiscovery.core.domain.Request;
import com.cv4j.netdiscovery.core.domain.Response;
import com.cv4j.netdiscovery.core.downloader.Downloader;
import com.safframework.tony.common.utils.FileUtils;
import com.safframework.tony.common.utils.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.functions.Function;

/**
 * 在使用FileDownloader时，可以使用AfterRequest或者Pipeline对文件进行保存等处理。
 * 不建议在Parser中处理文件下载，因为Parser的主要功能是解析html、json等
 * Created by tony on 2018/3/11.
 */
public class LargeFileDownloader implements Downloader {

    public static final String ARG_FILENAME = "arg_filename";

    private URL url = null;
    private HttpURLConnection httpUrlConnection = null;
    private String filePath;
    private Request request;

    public LargeFileDownloader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Maybe<Response> download(final Request request) {
        this.request = request;
        try {
            url = new URL(request.getUrl());
            // 将url以open方法返回的urlConnection连接强转为HttpURLConnection连接(标识一个url所引用的远程对象连接)
            // 此时cnnection只是为一个连接对象,待连接中
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            // 设置是否要从 URL连接读取数据,默认为true
            httpUrlConnection.setDoInput(true);
            // 建立连接
            // (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            httpUrlConnection.connect();
            // 连接发起请求,处理服务器响应 (从连接获取到输入流)

            return Maybe.create(new MaybeOnSubscribe<InputStream>() {

                @Override
                public void subscribe(MaybeEmitter emitter) throws Exception {

                    emitter.onSuccess(httpUrlConnection.getInputStream());
                }
            }).map(new Function<InputStream, Response>() {
                @Override
                public Response apply(InputStream inputStream) throws Exception {
                    downloadInternal(inputStream);
                    Response response = new Response();
                    response.setIs(inputStream);
                    response.setStatusCode(httpUrlConnection.getResponseCode());
                    response.setContentType(httpUrlConnection.getContentType());
                    return response;
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void downloadInternal(InputStream inputStream) {
        if (inputStream != null) {
            try {
                // 创建保存文件的目录
                File savePath = new File(filePath);
                if (!savePath.exists()) {
                    savePath.mkdir();
                }
                // 创建保存的文件
                String fileName = request.getExtra(ARG_FILENAME).toString();
                File file = new File(savePath + "/" + fileName);
                if (FileUtils.exists(file)) {
                    file.createNewFile();
                }
                IOUtils.writeToFile(inputStream, file);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    @Override
    public void close() {
        if (httpUrlConnection != null) {
            httpUrlConnection.disconnect();
        }
    }
}
