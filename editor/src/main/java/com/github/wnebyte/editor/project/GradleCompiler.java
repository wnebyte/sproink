package com.github.wnebyte.editor.project;

/*
import java.io.File;
import org.gradle.tooling.*;
import org.gradle.tooling.events.task.TaskFinishEvent;

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
            build.addProgressListener(new ProgressListener() {
                @Override
                public void statusChanged(ProgressEvent event) {
                    if (event instanceof TaskFinishEvent) {
                        System.out.println("Compile complete");
                    }
                }
            });
            build.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

 */
