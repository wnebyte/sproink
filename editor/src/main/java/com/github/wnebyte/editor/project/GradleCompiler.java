package com.github.wnebyte.editor.project;

import java.io.File;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

public class GradleCompiler {

    private final GradleConnector connector;

    public GradleCompiler(File projectDir) {
        this.connector = GradleConnector.newConnector()
                .forProjectDirectory(projectDir);
    }

    public void compile() {
        try (ProjectConnection connection = connector.connect()) {
            BuildLauncher build = connection.newBuild();
            build.forTasks("clean", "build");
            build.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
