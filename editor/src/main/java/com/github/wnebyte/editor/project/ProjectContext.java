package com.github.wnebyte.editor.project;

import java.net.*;
import java.util.Set;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.concurrent.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import com.github.wnebyte.sproink.core.ecs.Component;

public class ProjectContext {

    public static ProjectContext newInstance(String name, String path) {
        File root = new File(path + File.separator + name);
        ProjectInitializer init = new ProjectInitializer(root);
        init.mkdirs();
        init.copy();
        File file = new File(root.getAbsolutePath() + File.separator + "project.xml");
        context = new ProjectContext(file);
        context.load();
        context.getProject().setName(name);
        context.getProject().setPath(root.getAbsolutePath());
        context.sync();
        context.schedule();
        return context;
    }

    public static ProjectContext open(String path) {
        File file = new File(path + File.separator + "project.xml");
        context = new ProjectContext(file);
        context.load();
        context.schedule();
        return context;
    }

    public static ProjectContext get() {
        return context;
    }

    private static Project unmarshall(File file) {
        try {
            return (Project) unmarshaller.unmarshal(new FileReader(file));
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void marshall(Project project, File file) {
        try {
            marshaller.marshal(project, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private static final Unmarshaller unmarshaller;

    private static final Marshaller marshaller;

    private static ProjectContext context;

    static {
        try {
            JAXBContext context = JAXBContext.newInstance(Project.class);
            unmarshaller = context.createUnmarshaller();
            marshaller = context.createMarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private final File file;

    private Project project;

    private Set<Class<? extends Component>> componentSubTypes;

    private final ScheduledExecutorService executor;

    private ProjectContext(File file) {
        this.file = file;
        this.executor = Executors.newScheduledThreadPool(1);
    }

    public Project getProject() {
        return project;
    }

    public void schedule() {
        executor.scheduleAtFixedRate(this::loadComponents, 0, 60, TimeUnit.SECONDS);
    }

    public void save() {
        marshall(project, file);
    }

    public void load() {
        project = unmarshall(file);
    }

    public void loadComponents() {
        componentSubTypes = fetchComponentSubTypes();
    }

    public void sync() {
        save();
        load();
    }

    public Set<Class<? extends Component>> getComponentSubTypes() {
        return componentSubTypes;
    }

    private Set<Class<? extends Component>> fetchComponentSubTypes() {
        File file = new File(project.getPath() + File.separator + project.getOutDir());
        try (URLClassLoader child = new URLClassLoader(
                new URL[] { file.toURI().toURL() },
                this.getClass().getClassLoader()
        )) {
            ConfigurationBuilder conf = new ConfigurationBuilder();
            conf.setClassLoaders(new ClassLoader[]{child});
            conf.setUrls(ClasspathHelper.forClassLoader(child));
            conf.setScanners(new SubTypesScanner());
            Set<Class<? extends Component>> c = new Reflections(conf)
                    .getSubTypesOf(Component.class);
            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
