package sample.model.archivation;


import archivation.Archivation;
import archivation.ArchivationType;
import javafx.util.Pair;
import sample.model.ClassHandler;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArchivationHandler {
    private static final String PLUGINS_DIR_NAME = "plugins";
    private static final String FILE_EXT = ".jar";
    private static final String FULL_ARCHIVATION_CLASS_NAME = "archivationImpl.ArchivationPlugin";

    private ArrayList<URLClassLoader> classLoaders;
    private Map<ArchivationType, Archivation> archivationImplementations;

    public ArchivationHandler() {
        classLoaders = new ArrayList<>();
        archivationImplementations = new HashMap<>();

        //loadPlugins();
    }

    public void loadPlugins() {
        File[] pluginFiles = getPluginFiles(PLUGINS_DIR_NAME);
        loadClasses(pluginFiles);
    }

    public void unloadPlugins() {
        for (URLClassLoader urlClassLoader : classLoaders) {
            try {
                urlClassLoader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        classLoaders.clear();
        archivationImplementations.clear();
    }

    private File[] getPluginFiles(String pluginsDirName) {
        File pluginDir = new File(pluginsDirName);

        File[] pluginFiles = pluginDir.listFiles(file -> file.isFile() && file.getName().endsWith(FILE_EXT));
        return pluginFiles;
    }

    private void loadClasses(File[] pluginFiles) {
        for (File pluginFile : pluginFiles) {
            try {
                URL jarURL = pluginFile.toURI().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{jarURL});
                classLoaders.add(classLoader);
                Class pluginClass = classLoader.loadClass(FULL_ARCHIVATION_CLASS_NAME);
                Archivation archivation = (Archivation) pluginClass.newInstance();
                archivationImplementations.put(archivation.getArchivationType(), archivation);
            } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public Archivation getArchivationByType(ArchivationType archivationType) {
        return archivationImplementations.get(archivationType);
    }

    public ArrayList<Pair> getExtensionPairs() {
        ArrayList<Pair> archivationExtensions = new ArrayList<>();
        for (Map.Entry<ArchivationType, Archivation> entry: archivationImplementations.entrySet()) {
            archivationExtensions.add(new Pair<>(entry.getValue().getFileExt(),
                    entry.getValue().getFileExtDescription()));
        }
        return archivationExtensions;
    }

    public ArchivationType getSerializationTypeByExt(String ext) {
        for (Map.Entry<ArchivationType, Archivation> entry: archivationImplementations.entrySet()) {
            if (ext.equals(entry.getValue().getFileExt()))
                return entry.getValue().getArchivationType();
        }
        return null;
    }
}
