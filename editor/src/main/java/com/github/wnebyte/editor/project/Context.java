package com.github.wnebyte.editor.project;

import java.net.*;
import java.util.Set;
import java.util.HashSet;
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
import com.github.wnebyte.editor.scenes.LevelEditorSceneInitializer;
import com.github.wnebyte.editor.util.Log;
import com.github.wnebyte.sproink.core.Prefab;
import com.github.wnebyte.sproink.core.Component;
import com.github.wnebyte.sproink.core.SceneInitializer;
import com.github.wnebyte.sproink.scenes.LevelSceneInitializer;

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

    private static final String TAG = "Context";

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

    private final ExecutorService executor;

    private final GradleCompiler compiler;

    private Reflections reflections;

    private ClassLoader classLoader;

    private Class<? extends SceneInitializer> editorSceneInitializer
            = LevelEditorSceneInitializer.class;

    private Class<? extends SceneInitializer> sceneInitializer
            = LevelSceneInitializer.class;

    private Context(File projectFile) {
        this.projectFile = projectFile;
        this.project = unmarshall(projectFile);
        this.project.format();
        this.prefabs = new HashSet<>();
        this.components = new HashSet<>();
        this.sceneInitializers = new HashSet<>();
        this.executor = Executors.newSingleThreadExecutor();
        this.compiler = new GradleCompiler(new File(project.getProjectDir()));
        loadClassLoader();
        loadClasses();
    }

    private <T> Set<Class<? extends T>> getSubTypesOf(Class<T> cls) {
        Set<Class<? extends T>> set = reflections.getSubTypesOf(cls);
        return set;
    }

    private void loadClassLoader() {
        try {
            this.classLoader = new URLClassLoader(new URL[] {
                    new File(project.getOutDir()).toURI().toURL()
            }, this.getClass().getClassLoader());
            ConfigurationBuilder conf = new ConfigurationBuilder();
            conf.setClassLoaders(new ClassLoader[]{classLoader});
            conf.setUrls(ClasspathHelper.forClassLoader(classLoader));
            conf.setScanners(new SubTypesScanner());
            this.reflections = new Reflections(conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClasses() {
        prefabs.clear();
        components.clear();
        sceneInitializers.clear();
        prefabs.addAll(getSubTypesOf(Prefab.class));
        components.addAll(getSubTypesOf(Component.class));
        sceneInitializers.addAll(getSubTypesOf(SceneInitializer.class));
        setEditorSceneInitializer(LevelEditorSceneInitializer.class);
        setSceneInitializer(LevelSceneInitializer.class);
    }

    public void compile() {
        executor.submit(() -> {
            compiler.compile();
            loadClassLoader();
            loadClasses();
            Log.log(TAG, "compilation complete");
        });
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

    public synchronized Prefab newPrefab(String canonicalName) {
        try {
            return (Prefab) classLoader.loadClass(canonicalName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized Component newComponent(String canonicalName) {
        try {
            return (Component) classLoader.loadClass(canonicalName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized SceneInitializer newSceneInitializer(String canonicalName) {
        try {
            return (SceneInitializer) classLoader.loadClass(canonicalName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized SceneInitializer newEditorSceneInitializer() {
        return newSceneInitializer(editorSceneInitializer.getCanonicalName());
    }

    public synchronized SceneInitializer newSceneInitializer() {
        return newSceneInitializer(sceneInitializer.getCanonicalName());
    }

    public Class<? extends SceneInitializer> getEditorSceneInitializer() {
        return editorSceneInitializer;
    }

    public void setEditorSceneInitializer(Class<? extends SceneInitializer> cls) {
        editorSceneInitializer = cls;
    }

    public Class<? extends SceneInitializer> getSceneInitializer() {
        return sceneInitializer;
    }

    public void setSceneInitializer(Class<? extends SceneInitializer> cls) {
        sceneInitializer = cls;
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
