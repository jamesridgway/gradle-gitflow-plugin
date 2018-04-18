package uk.co.jamesridgway.gradle.gitflow.plugin.version;

public interface GitFlowVersionConfig {

    GitFlowVersionConfig DEFAULT = () ->
            "${major}.${minor}.${patch}.${commitsSinceLastTag}-${branch?replace('/', '_')}"
                    + "+sha.${commitId?substring(0,7)}${dirty?then('.dirty','')}";

    String getUnreleaseVersionTemplate();

}
