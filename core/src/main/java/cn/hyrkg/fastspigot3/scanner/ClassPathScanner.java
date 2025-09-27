package cn.hyrkg.fastspigot3.scanner;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassPathScanner implements Scanner {
   @Override
   public List<Class<?>> scan(String path) {
      return doScan(path, null, null);
   }

   public List<Class<?>> scan(String path, Class<?> anchorClass) {
      ClassLoader preferred = anchorClass != null ? anchorClass.getClassLoader() : null;
      return doScan(path, preferred, anchorClass);
   }

   public List<Class<?>> scan(String path, ClassLoader preferredLoader) {
      return doScan(path, preferredLoader, null);
   }

   private List<Class<?>> doScan(String path, ClassLoader preferredLoader, Class<?> anchorClass) {
      List<Class<?>> classes = new ArrayList<>();
      Set<String> collectedClassNames = new LinkedHashSet<>();
      if (path == null || path.isEmpty()) {
         return classes;
      }

      String packageDirName = path.replace('.', '/');

      ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
      ClassLoader selfLoader = ClassPathScanner.class.getClassLoader();
      ClassLoader primaryLoader = preferredLoader != null ? preferredLoader : (selfLoader != null ? selfLoader : contextLoader);

      try {
         List<URL> resources = new ArrayList<>();
         Set<String> seenUrlStrings = new LinkedHashSet<>();
         // search with primary first, then context (dedup by URL string)
         Enumeration<URL> res1 = primaryLoader != null ? primaryLoader.getResources(packageDirName) : null;
         if (res1 != null) {
            while (res1.hasMoreElements()) {
               URL u = res1.nextElement();
               String key = String.valueOf(u);
               if (seenUrlStrings.add(key)) resources.add(u);
            }
         }
         Enumeration<URL> res2 = contextLoader != null ? contextLoader.getResources(packageDirName) : null;
         if (res2 != null) {
            while (res2.hasMoreElements()) {
               URL u = res2.nextElement();
               String key = String.valueOf(u);
               if (seenUrlStrings.add(key)) resources.add(u);
            }
         }

         for (URL url : resources) {
            String protocol = url.getProtocol();

            if ("file".equals(protocol)) {
               String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
               findAndAddClassesInPackageByFile(path, filePath, classes, collectedClassNames, primaryLoader, contextLoader);
            } else if ("jar".equals(protocol)) {
               try {
                  JarURLConnection jarConn = (JarURLConnection) url.openConnection();
                  JarFile jar = jarConn.getJarFile();
                  Enumeration<JarEntry> entries = jar.entries();
                  while (entries.hasMoreElements()) {
                     JarEntry entry = entries.nextElement();
                     String name = entry.getName();
                     if (name.startsWith(packageDirName) && name.endsWith(".class") && !entry.isDirectory()) {
                        String className = name.substring(0, name.length() - 6).replace('/', '.');
                        tryLoadAndCollect(className, classes, collectedClassNames, primaryLoader, contextLoader);
                     }
                  }
               } catch (IOException ignore) {
                  // ignore jar access issues
               }
            }
         }
      } catch (IOException ignore) {
         // ignore discovery issues
      }

      // Fallback: 在某些类加载器（如 Spigot 插件）下，getResources 可能拿不到包资源。
      // 先尝试 anchorClass 的代码来源（如果提供），再尝试当前类的代码来源。
      if (classes.isEmpty()) {
         try {
            URL anchorLocation = null;
            if (anchorClass != null && anchorClass.getProtectionDomain() != null && anchorClass.getProtectionDomain().getCodeSource() != null) {
               anchorLocation = anchorClass.getProtectionDomain().getCodeSource().getLocation();
            }
            if (anchorLocation != null) {
               scanBySingleLocation(anchorLocation, packageDirName, path, classes, collectedClassNames, primaryLoader, contextLoader);
            }

            URL location = ClassPathScanner.class.getProtectionDomain() != null
                    && ClassPathScanner.class.getProtectionDomain().getCodeSource() != null
                    ? ClassPathScanner.class.getProtectionDomain().getCodeSource().getLocation()
                    : null;
            if (location != null) {
               scanBySingleLocation(location, packageDirName, path, classes, collectedClassNames, primaryLoader, contextLoader);
            }
         } catch (Throwable ignore) {
            // ignore fallback issues
         }
      }
      return classes;
   }

   private void findAndAddClassesInPackageByFile(String packageName, String packagePath, List<Class<?>> classes, Set<String> collectedClassNames, ClassLoader primaryLoader, ClassLoader secondaryLoader) {
      File dir = new File(packagePath);
      if (!dir.exists() || !dir.isDirectory()) {
         return;
      }
      File[] files = dir.listFiles(file -> file.isDirectory() || file.getName().endsWith(".class"));
      if (files == null) {
         return;
      }
      for (File file : files) {
         if (file.isDirectory()) {
            String subPackage = packageName + "." + file.getName();
            findAndAddClassesInPackageByFile(subPackage, file.getAbsolutePath(), classes, collectedClassNames, primaryLoader, secondaryLoader);
         } else {
            String fileName = file.getName();
            String simpleClassName = fileName.substring(0, fileName.length() - 6);
            String fqcn = packageName + '.' + simpleClassName;
            tryLoadAndCollect(fqcn, classes, collectedClassNames, primaryLoader, secondaryLoader);
         }
      }
   }

   private boolean tryLoadAndCollect(String className, List<Class<?>> classes, Set<String> collectedClassNames, ClassLoader primary, ClassLoader secondary) {
      if (collectedClassNames.contains(className)) {
         return false;
      }
      if (primary != null) {
         try {
            Class<?> c = Class.forName(className, false, primary);
            classes.add(c);
            collectedClassNames.add(className);
            return true;
         } catch (Throwable t) {
         }
      }
      if (secondary != null) {
         try {
           Class<?> c = Class.forName(className, false, secondary);
           classes.add(c);
           collectedClassNames.add(className);
           return true;
         } catch (Throwable t) {
         }
      }
      return false;
   }

   private void scanBySingleLocation(URL location, String packageDirName, String packageName, List<Class<?>> classes, Set<String> collectedClassNames, ClassLoader primaryLoader, ClassLoader contextLoader) {
      try {
         String locProtocol = location.getProtocol();
         if ("file".equals(locProtocol)) {
            File locFile = new File(location.toURI());
            if (locFile.isFile() && locFile.getName().endsWith(".jar")) {
               try (JarFile jar = new JarFile(locFile)) {
                  Enumeration<JarEntry> entries = jar.entries();
                  while (entries.hasMoreElements()) {
                     JarEntry entry = entries.nextElement();
                     String name = entry.getName();
                     if (name.startsWith(packageDirName) && name.endsWith(".class") && !entry.isDirectory()) {
                        String className = name.substring(0, name.length() - 6).replace('/', '.');
                        tryLoadAndCollect(className, classes, collectedClassNames, primaryLoader, contextLoader);
                     }
                  }
               }
            } else if (locFile.isDirectory()) {
               String packagePath = new File(locFile, packageDirName).getAbsolutePath();
               findAndAddClassesInPackageByFile(packageName, packagePath, classes, collectedClassNames, primaryLoader, contextLoader);
            }
         }
      } catch (Throwable ignore) {
      }
   }
}
