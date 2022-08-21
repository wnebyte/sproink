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

import com.github.wnebyte.sproink.core.Prefab;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import com.github.wnebyte.sproink.core.ecs.Component;

public class Context {

    public static Context newInstance(String name, String path) {
        File root = new File(path + File.separator + name);
        ProjectInitializer init = new ProjectInitializer(root);
        init.mkdirs();
        init.copy();
        File file = new File(root.getAbsolutePath() + File.separator + "project.xml");
        context = new Context(file);
        context.loadProject();
        context.getProject().setName(name);
        context.getProject().setPath(root.getAbsolutePath());
        context.syncProject();
        context.schedule();
        return context;
    }

    public static Context open(String path) {
        File file = new File(path + File.separator + "project.xml");
        context = new Context(file);
        context.loadProject();
        context.schedule();
        return context;
    }

    public static Context get() {
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

    private static Context context;

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

    private Set<Class<? extends Prefab>> prefabs;

    private Set<Class<? extends Component>> components;

    private final ScheduledExecutorService executor;

    private Context(File file) {
        this.file = file;
        this.executor = Executors.newScheduledThreadPool(1);
    }

    public Project getProject() {
        return project;
    }

    private void schedule() {
        executor.scheduleAtFixedRate(() -> {
            loadPrefabs();
            loadComponents();
        }, 0, 60, TimeUnit.SECONDS);
    }

    public void saveProject() {
        Context.marshall(project, file);
    }

    public void loadProject() {
        project = Context.unmarshall(file);
    }

    public void loadPrefabs() {
        prefabs = fetchPrefabs();
    }

    public void loadComponents() {
        components = fetchComponents();
    }

    public void syncProject() {
        saveProject();
        loadProject();
    }

    public Class<? extends Prefab> getPrefab(String className) {
        return null;
    }

    public Class<? extends Component> getComponent(String className) {
        return components.stream().filter(cls -> cls.getName().equals(className))
                .findFirst().orElse(null);
    }

    public Set<Class<? extends Prefab>> getPrefabs() {
        return prefabs;
    }

    public Set<Class<? extends Component>> getComponents() {
        return components;
    }

    private Set<Class<? extends Prefab>> fetchPrefabs() {
        return fetchSubTypesOf(Prefab.class);
    }

    private Set<Class<? extends Component>> fetchComponents() {
        return fetchSubTypesOf(Component.class);
    }

    private <T> Set<Class<? extends T>> fetchSubTypesOf(Class<T> cls) {
        File file = new File(project.getPath() + File.separator + project.getOutDir());
        try (URLClassLoader child = new URLClassLoader(
                new URL[] { file.toURI().toURL() },
                this.getClass().getClassLoader()
        )) {
            ConfigurationBuilder conf = new ConfigurationBuilder();
            conf.setClassLoaders(new ClassLoader[]{child});
            conf.setUrls(ClasspathHelper.forClassLoader(child));
            conf.setScanners(new SubTypesScanner());
            Set<Class<? extends T>> set = new Reflections(conf)
                    .getSubTypesOf(cls);
            return set;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
