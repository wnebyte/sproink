package com.github.wnebyte.editor.project;

import java.net.*;
import java.util.HashSet;
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
import com.github.wnebyte.sproink.core.Prefab;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;

public class Context {

    public static Context newInstance(String name, String path) {
        File parent = new File(path);
        assert (parent.exists() && parent.isDirectory()) :
                String.format("Error (Context): Path: '%s' does not exist/is not a directory", parent.getAbsolutePath());
        File root = new File(path + File.separator + name);
        ProjectInitializer init = new ProjectInitializer(root);
        init.mkdirs();
        init.copyTemplates();
        File projectFile = new File(root.getAbsolutePath() + File.separator + "project.xml");
        Context context = new Context(projectFile);
        context.loadProject();
        context.getProject().setName(name);
        context.getProject().setPath(root.getAbsolutePath());
        context.syncProject();
        context.schedule();
        Context.instance = context;
        return get();
    }

    public static Context open(String path) {
        File projectFile = new File(path + File.separator + "project.xml");
        assert projectFile.exists() :
                String.format("Error (Context): ProjectFile: '%s' does not exists", projectFile.getAbsolutePath());
        Context context = new Context(projectFile);
        context.loadProject();
        context.schedule();
        Context.instance = context;
        return get();
    }

    public static Context get() {
        return instance;
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

    private static Context instance;

    static {
        try {
            JAXBContext context = JAXBContext.newInstance(Project.class);
            unmarshaller = context.createUnmarshaller();
            marshaller = context.createMarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private Project project;

    private final File file;

    private final Set<Class<? extends Prefab>> prefabs;

    private final Set<Class<? extends Component>> components;

    private final Set<Class<? extends SceneInitializer>> sceneInitializers;

    private final ScheduledExecutorService executor;

    private Context(File file) {
        this.file = file;
        this.prefabs = new HashSet<>();
        this.components = new HashSet<>();
        this.sceneInitializers = new HashSet<>();
        this.executor = Executors.newScheduledThreadPool(1);
    }

    private void schedule() {
        executor.scheduleAtFixedRate(() -> {
            prefabs.addAll(fetchSubTypesOf(Prefab.class));
            components.addAll(fetchSubTypesOf(Component.class));
            sceneInitializers.addAll(fetchSubTypesOf(SceneInitializer.class));
        }, 0, 60, TimeUnit.SECONDS);
    }

    public void saveProject() {
        Context.marshall(project, file);
    }

    public void loadProject() {
        project = Context.unmarshall(file);
        project.format();
    }

    public void syncProject() {
        saveProject();
        loadProject();
    }

    public Project getProject() {
        return project;
    }

    public Class<? extends Prefab> getPrefab(String canonicalName) {
        return prefabs.stream().filter(cls -> cls.getCanonicalName().equals(canonicalName))
                .findFirst().orElse(null);
    }

    public Class<? extends Component> getComponent(String canonicalName) {
        return components.stream().filter(cls -> cls.getCanonicalName().equals(canonicalName))
                .findFirst().orElse(null);
    }

    public Class<? extends SceneInitializer> getSceneInitializer(String canonicalName) {
        return sceneInitializers.stream().filter(cls -> cls.getCanonicalName().equals(canonicalName))
                .findFirst().orElse(null);
    }

    public Set<Class<? extends Prefab>> getPrefabs() {
        return prefabs;
    }

    public Set<Class<? extends Component>> getComponents() {
        return components;
    }

    public Set<Class<? extends SceneInitializer>> getSceneInitializers() {
        return sceneInitializers;
    }

    private <T> Set<Class<? extends T>> fetchSubTypesOf(Class<T> cls) {
        File file = new File(project.getOutDir());
        try (URLClassLoader child = new URLClassLoader(
                new URL[] { file.toURI().toURL() },
                this.getClass().getClassLoader()
        )) {
            ConfigurationBuilder conf = new ConfigurationBuilder();
            conf.setClassLoaders(new ClassLoader[]{child});
            conf.setUrls(ClasspathHelper.forClassLoader(child));
            conf.setScanners(new SubTypesScanner());
            Set<Class<? extends T>> set = new Reflections(conf).getSubTypesOf(cls);
            return set;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashSet<>();
    }
}
