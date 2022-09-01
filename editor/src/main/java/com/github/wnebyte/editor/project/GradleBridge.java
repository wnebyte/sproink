package com.github.wnebyte.editor.project;

import java.io.File;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.ResultHandler;

public class GradleBridge {

    private final GradleConnector connector;

    public GradleBridge(String projectDir) {
        this.connector = GradleConnector.newConnector()
                .forProjectDirectory(new File(projectDir));
    }

    public void compile() {
        forTasks("clean", "build");
    }

    public void forTasks(String... tasks) {
        forTasks(tasks, null);
    }

    public void forTasks(String[] tasks, ResultHandler<Void> handler) {
        try (ProjectConnection connection = connector.connect()) {
            BuildLauncher build = connection.newBuild();
            build.forTasks(tasks);
            if (handler != null) {
                build.run(handler);
            } else {
                build.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
