package uk.co.jamesridgway.gradle.gitflow.plugin;

import uk.co.jamesridgway.gradle.gitflow.plugin.version.GitFlowVersionConfig;
import uk.co.jamesridgway.gradle.gitflow.plugin.version.GitFlowVersionProvider;

import java.io.File;

import static com.google.common.base.Preconditions.checkArgument;

public class Main {

    private Main() {
    }

    public static void main(final String[] args) {

        checkArgument(args.length == 1, "Requires 1 arg pointing to project dir");

        File file = new File(args[0]);

        checkArgument(file.exists() && file.isDirectory(), "Arg must be an existing directory");

        GitFlowVersionProvider versionProvider = new GitFlowVersionProvider(GitFlowVersionConfig.DEFAULT);

        System.out.println("Git Flow Version: " + versionProvider.getVersion(file));
    }
}
