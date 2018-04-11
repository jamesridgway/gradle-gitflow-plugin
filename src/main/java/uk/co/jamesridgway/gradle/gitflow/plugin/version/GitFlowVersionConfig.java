package uk.co.jamesridgway.gradle.gitflow.plugin.version;

public interface GitFlowVersionConfig {

    GitFlowVersionConfig DEFAULT = () ->
            "${major}.${minor}.${patch}-${branch?replace('/', '_')}."
                    + "${commitsSinceLastTag}+sha.${commitId?substring(0,7)}${dirty?then('.dirty','')}";

    String getUnreleaseVersionTemplate();

}
