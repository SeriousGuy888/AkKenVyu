package io.github.seriousguy888.akkenvyu.config;

import io.github.seriousguy888.akkenvyu.AkKenVyu;

import java.io.FileNotFoundException;

public class CachedDataConfig extends ConfigReader {
    public CachedDataConfig(AkKenVyu plugin, String name, boolean mustRetainComments) throws FileNotFoundException {
        super(plugin, name, mustRetainComments);
    }

    public String getDownloadUrl() {
        return config.getString("download_url");
    }

    public String getHash() {
        return config.getString("sha1_hash");
    }

    public void setDownloadUrl(String url) {
        config.set("download_url", url);
    }

    public void setHash(String hash) {
        config.set("sha1_hash", hash);
    }
}
