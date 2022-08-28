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

import com.github.wnebyte.editor.scenes.LevelEditorSceneInitializer;
import com.github.wnebyte.sproink.scenes.LevelSceneInitializer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import com.github.wnebyte.sproink.core.Prefab;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;

public class Context {

    public static Context newProject(String name, String path) {
        File parent = new File(path);
        assert (parent.exists() && parent.isDirectory()) :
                String.format("Error (Context): Path: '%s' does not exist/is not a directory", parent.getAbsolutePath());
        File root = new File(path + File.separator + name);
        ProjectInitializer init = new ProjectInitializer(root);
        init.mkdirs();
        init.copyTemplates();
        File projectFile = new File(root.getAbsolutePath() + File.separator + "project.xml");
        Context context = new Context(projectFile);
        context.getProject().setName(name);
        context.getProject().setProjectDir(root.getAbsolutePath());
        context.syncProject();
        Context.instance = context;
        return get();
    }

    public static Context openProject(String path) {
        File projectFile = new File(path + File.separator + "project.xml");
        assert projectFile.exists() :
                String.format("Error (Context): ProjectFile: '%s' does not exists", projectFile.getAbsolutePath());
        Context context = new Context(projectFile);
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

    private final File projectFile;

    private final Set<Class<? extends Prefab>> prefabs;

    private final Set<Class<? extends Component>> components;

    private final Set<Class<? extends SceneInitializer>> sceneInitializers;

    private final ScheduledExecutorService executor;

   // private final GradleCompiler compiler;

    private final Reflections reflections;

    private ClassLoader classLoader;

    private Context(File projectFile) {
        this.projectFile = projectFile;
        this.project = unmarshall(projectFile);
        this.project.format();
        this.prefabs = new HashSet<>();
        this.components = new HashSet<>();
        this.sceneInitializers = new HashSet<>();
        this.executor = Executors.newScheduledThreadPool(1);
        try {
            this.classLoader = new URLClassLoader(new URL[] {
                    new File(project.getOutDir()).toURI().toURL()
            }, this.getClass().getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConfigurationBuilder conf = new ConfigurationBuilder();
        conf.setClassLoaders(new ClassLoader[]{classLoader});
        conf.setUrls(ClasspathHelper.forClassLoader(classLoader));
        conf.setScanners(new SubTypesScanner());
        this.reflections = new Reflections(conf);
        this.prefabs.addAll(getSubTypesOf(Prefab.class));
        this.components.addAll(getSubTypesOf(Component.class));
        this.sceneInitializers.addAll(getSubTypesOf(SceneInitializer.class));
      //  this.compiler = new GradleCompiler(new File(project.getProjectDir()));
    }

    private <T> Set<Class<? extends T>> getSubTypesOf(Class<T> cls) {
        Set<Class<? extends T>> set = reflections.getSubTypesOf(cls);
        return set;
    }

    /*
    public void compile() {
        compiler.compile();
    }
     */

    private void schedule() {
        /*
        executor.scheduleAtFixedRate(() -> {
            prefabs.addAll(fetchSubTypesOf(Prefab.class));
            components.addAll(fetchSubTypesOf(Component.class));
            sceneInitializers.addAll(fetchSubTypesOf(SceneInitializer.class));
        }, 0, 60, TimeUnit.SECONDS);

         */
    }

    public void saveProject() {
        Context.marshall(project, projectFile);
    }

    public void loadProject() {
        project = Context.unmarshall(projectFile);
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

    public Prefab newPrefab(String canonicalName) {
        try {
            return (Prefab) classLoader.loadClass(canonicalName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Component newComponent(String canonicalName) {
        try {
            return (Component) classLoader.loadClass(canonicalName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SceneInitializer newSceneInitializer(String canonicalName) {
        try {
            return (SceneInitializer) classLoader.loadClass(canonicalName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Class<? extends SceneInitializer> editorSceneInitializer
            = LevelEditorSceneInitializer.class;

    private Class<? extends SceneInitializer> sceneInitializer
            = LevelSceneInitializer.class;

    public Class<? extends SceneInitializer> getEditorSceneInitializer() {
        return editorSceneInitializer;
    }

    public void setEditorSceneInitializer(Class<? extends SceneInitializer> cls) {
        editorSceneInitializer = cls;
    }

    public SceneInitializer newEditorSceneInitializer() {
        return newSceneInitializer(editorSceneInitializer.getCanonicalName());
    }

    public Class<? extends SceneInitializer> getSceneInitializer() {
        return sceneInitializer;
    }

    public void setSceneInitializer(Class<? extends SceneInitializer> cls) {
        sceneInitializer = cls;
    }

    public SceneInitializer newSceneInitializer() {
        return newSceneInitializer(sceneInitializer.getCanonicalName());
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

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
