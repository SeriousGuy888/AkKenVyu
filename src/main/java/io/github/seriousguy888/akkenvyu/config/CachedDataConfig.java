package io.github.seriousguy888.akkenvyu.config;

import io.github.seriousguy888.akkenvyu.AkKenVyu;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.util.UUID;

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


    public UUID getPackUuid() {
        @Nullable String uuidStr = config.getString("resource_pack_uuid");

        if (uuidStr == null) {
            UUID uuid = UUID.randomUUID();
            setPackUuid(uuid);
            return uuid;
        } else {
            return UUID.fromString(uuidStr);
        }
    }

    public void setDownloadUrl(String url) {
        config.set("download_url", url);
    }

    public void setHash(String hash) {
        config.set("sha1_hash", hash);
    }

    public void setPackUuid(UUID uuid) {
        config.set("resource_pack_uuid", uuid.toString());
    }
}
