---
title: java 文件处理
date: 2017-06-30 11:41:43
tags: ["java"]
categories: ["java"]
top: 1
---
# `Path`
`Path` 是java7中`java.nio.file`包中的类，它是一个抽象构造。创建和处理`Path`不会马上绑定到对应的物理位置，如果试图读取一个未创建的文件会抛出IOException.

使用 `Paths.get(String filePath, String more ... )` 来创建一个 `Path`。

可以通过`resolve`方法来合并两个`Path`: 
```java
// 最终是文件是 /usr/local/xml.conf 
Paths.get("/usr").resolve("local/xml.conf")
```

还可以通过`startsWith(Path prefix)`, `equals(Path path)` 和 `endsWith(Path suffix)` 来比较路径

还可以在`Path`和`File`之间进行转换: `file.toPath()`, `path.toFile()`

## 遍历目录
可以通过`Files.walkFileTree(Path start, FileVisitor<? super Path> visitor)` 来遍历目录树，其中实现`FileVisitor`接口，需要实现如下几个方法：
```java
// 在进入一个目录之前被调用
FileVisitResult preVisitDirectory(T dir, BasicFileAttributes attrs)
// 处理当前文件
FileVisitResult visitFile(T file, BasicFileAttributes attrs) throws IOException
// 访问文件失败时被调动，文件属性不能读取、目录不能被打开等等
FileVisitResult visitFileFailed(T file, IOException exc) throws IOException
// 访问完目录时被调用
FileVisitResult postVisitDirectory(T dir, IOException exc)
```
可以如下使用
```java
public static class HandleFile implements FileVisitor<Path> {
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        System.out.println("preVisitDirectory: " + dir.toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
            throws IOException {
        System.out.println("visitFile: " +path.getFileName());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.println("visitFile: " + file.getFileName());
        return  FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        System.out.println("postVisitDirectory: " + dir.getFileName());

        return  FileVisitResult.CONTINUE;
    }
    public static void main(String[] args) throws IOException {
        Path startingDir = Paths.get("E:\\golang");
        Files.walkFileTree(startingDir, new HandleFile());
    }
}
```
执行上述程序产生结果为：
```
preVisitDirectory: E:\golang
preVisitDirectory: E:\golang\pkg
postVisitDirectory: pkg
preVisitDirectory: E:\golang\src
visitFile: test.go
postVisitDirectory: src
postVisitDirectory: golang
```

> 注意： 路径中如果有中文，可能会有问题
 
java中提供了实现`FileVisitor`的接口的类`SimpleFileVisitor`，可以按需覆盖默认方法，简化编写代码。

## 文件的创建和删除
可以调用`Files`工具类的`createFile(Path path, FileAttribute<?>... attrs)`方法来创建文件，还可以通过`FileAttribute`来指定文件属性。
```java
Path path = Paths.get("D://test.txt");
Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrw-r--");
FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(perms);
Files.createFile(path, attrs);
```

文件的删除就比较简单了：
```java
Path path = Paths.get("D://test.txt");
Files.delete(path);
```

## 文件的复制和移动
借助`Files`工具类可以很简单地完成文件的复制和移动
```java
// 将一个流拷贝到文件中
Files.copy(InputStream in, Path target, CopyOption... options) throws IOException
// 例子
URI u = URI.create("http://java.sun.com/");
try (InputStream in = u.toURL().openStream()) {
   Files.copy(in, Paths.get("D:/test"), StandardCopyOption.REPLACE_EXISTING);
}
// 将一个输入流拷贝到输出流中
Files.copy(InputStream source, OutputStream sink) throws IOException
// 将文件拷贝到输出流中
Files.copy(Path source, OutputStream out) throws IOException
// 将文件从一个路径拷贝到另一个路径
Files.copy(Path source, Path target, CopyOption... options) throws IOException
```

可以通过`Files.move(Path source, Path taget, CopyOption... options)` 来移动文件：
```java
Files.move(Path.get("source"), Path.get("target"), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
```

## 快速读写数据
可以通过`Files`工具类中的`newBufferedReader(Path path, Charset cs) throws IOException` 将一个文件快速读入到`BufferedReader`实例中:
```java
Path path = Paths.get("D://test.txt");
try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}
```

`Files`工具类中的`BufferedWriter newBufferedWriter(Path path, Charset cs, OpenOption... options)` 来快速获取`BufferedWriter`.

还有一些简化的读取和写入，不过对于大文件的处理并不适用：
```java
List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
byte[] bytes = Files.readAllBytes(path);
```

## 监控文件变化
java7中可以用`java.nio.file.WatchService`类检测文件或者目录的变化，并在文件发生变化时触发相应的方法
```java
try {
    WatchService watcher = FileSystems.getDefault().newWatchService();
    Path path = Paths.get("D:/test");
    WatchKey key = path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
    while (!isShutdown) {
        key = watcher.take();
        key.pollEvents().stream()
                .filter(event -> event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
                .forEach(event -> System.out.println(path.toAbsolutePath().toString() + " has been modified"));
    }
} catch (InterruptedException e) {
    e.printStackTrace();
}
```