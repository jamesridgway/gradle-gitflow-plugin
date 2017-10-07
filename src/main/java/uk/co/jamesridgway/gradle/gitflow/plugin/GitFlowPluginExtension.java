package uk.co.jamesridgway.gradle.gitflow.plugin;

import org.gradle.api.Project;
import org.gradle.api.provider.PropertyState;

import java.util.regex.Pattern;

public class GitFlowPluginExtension {

    private final PropertyState<Pattern> masterPattern;
    private final PropertyState<Pattern> developPattern;
    private final PropertyState<Pattern> featurePattern;
    private final PropertyState<Pattern> releasePattern;
    private final PropertyState<Pattern> hotfixPattern;

    private final PropertyState<String> unreleasedVersionTemplate;

    public GitFlowPluginExtension(final Project project) {
        masterPattern = project.property(Pattern.class);
        developPattern = project.property(Pattern.class);
        featurePattern = project.property(Pattern.class);
        releasePattern = project.property(Pattern.class);
        hotfixPattern = project.property(Pattern.class);
        unreleasedVersionTemplate = project.property(String.class);

        masterPattern.set(Pattern.compile("^master$"));
        developPattern.set(Pattern.compile("^develop$"));
        featurePattern.set(Pattern.compile("^feature/(.+)$"));
        releasePattern.set(Pattern.compile("^release/(.+)$"));
        hotfixPattern.set(Pattern.compile("^hotfix/(.+)$"));
        unreleasedVersionTemplate.set("${major}.${minor}.${patch}-${branch}.${commitsSinceLastTag}+sha.${commitId}"
                + "${dirty?then('.dirty','')}");
    }

    boolean isMasterBranch(final String branchName) {
        return masterPattern.get().asPredicate().test(branchName);
    }

    boolean isDevelopBranch(final String branchName) {
        return developPattern.get().asPredicate().test(branchName);
    }

    boolean isFeatureBranch(final String branchName) {
        return featurePattern.get().asPredicate().test(branchName);
    }

    boolean isReleaseBranch(final String branchName) {
        return releasePattern.get().asPredicate().test(branchName);
    }

    boolean isHotfixBranch(final String branchName) {
        return hotfixPattern.get().asPredicate().test(branchName);
    }


}
