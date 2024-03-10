package io.github.seriousguy888.akkenvyu.config;

import io.github.seriousguy888.akkenvyu.AkKenVyu;

import java.io.FileNotFoundException;

public class MainConfig extends ConfigReader {
    public MainConfig(AkKenVyu plugin, String name, boolean mustRetainComments) throws FileNotFoundException {
        super(plugin, name, mustRetainComments);
    }

    public String getGithubRepoName() {
        return config.getString("github.repo_name");
    }

    public String getGithubFileName() {
        return config.getString("github.file_name");
    }
}
