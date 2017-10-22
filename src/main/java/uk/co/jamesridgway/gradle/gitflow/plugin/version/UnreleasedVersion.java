package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static freemarker.template.Configuration.VERSION_2_3_23;
import static freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER;

public class UnreleasedVersion implements Version {

    private final int major;
    private final int minor;
    private final String patch;
    private final String detail;

    static Builder build(final ReleaseVersion latestReleasedVersion, final String versionTemplate) {
        return new Builder(latestReleasedVersion, versionTemplate);
    }

    private UnreleasedVersion(final int major, final int minor, final String patch, final String detail) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.detail = detail;
    }

    @Override
    public int getMajor() {
        return major;
    }

    @Override
    public int getMinor() {
        return minor;
    }

    @Override
    public String getPatch() {
        return patch;
    }

    @Override
    public String toString() {
        return detail;
    }

    static class Builder {

        private final ReleaseVersion latestReleasedVersion;
        private final String versionTemplate;

        private final Map<String, Object> variables = new HashMap<>();

        private Builder(final ReleaseVersion latestReleasedVersion, final String versionTemplate) {
            this.latestReleasedVersion = latestReleasedVersion;
            this.versionTemplate = versionTemplate;
            this.variables.put("major", latestReleasedVersion.getMajor());
            this.variables.put("minor", latestReleasedVersion.getMinor());
            this.variables.put("patch", latestReleasedVersion.getPatch());
        }

        public Builder withBranch(final String value) {
            this.variables.put("branch", value);
            return this;
        }

        public Builder withCommitsSinceLastTag(final int commitsSinceLastTag) {
            this.variables.put("commitsSinceLastTag", commitsSinceLastTag);
            return this;
        }

        public Builder withCommitId(final String value) {
            this.variables.put("commitId", value);
            return this;
        }

        public Builder withDirty(final boolean dirty) {
            this.variables.put("dirty", dirty);
            return this;
        }

        public UnreleasedVersion create() {
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate("versionTemplate", versionTemplate);

            Configuration freemarkerConfig = new Configuration(VERSION_2_3_23);
            freemarkerConfig.setDefaultEncoding("UTF-8");
            freemarkerConfig.setTemplateExceptionHandler(RETHROW_HANDLER);
            freemarkerConfig.setTemplateLoader(templateLoader);

            try (final StringWriter writer = new StringWriter()) {
                freemarkerConfig.getTemplate("versionTemplate").process(variables, writer);
                return new UnreleasedVersion(latestReleasedVersion.getMajor(), latestReleasedVersion.getMinor(),
                        latestReleasedVersion.getPatch(), writer.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
