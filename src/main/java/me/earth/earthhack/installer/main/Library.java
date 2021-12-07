package me.earth.earthhack.installer.main;

import java.net.URL;

public class Library {
    private final boolean download;
    private final URL url;
    private final URL web;

    public Library(URL url, URL web, boolean download) {
        this.download = download;
        this.url = url;
        this.web = web;
    }

    public URL getUrl() {
        return this.url;
    }

    public URL getWeb() {
        return this.web;
    }

    public boolean needsDownload() {
        return this.download;
    }
}
