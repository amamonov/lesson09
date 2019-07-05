package part1.lesson09;

import org.apache.commons.jci.ReloadingClassLoader;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Properties;

/**
 * Дан интерфейс
 *
 * public interface Worker {
 *     void doWork();
 * }
 *
 * Необходимо написать программу, выполняющую следующее:
 *
 *     Программа с консоли построчно считывает код метода doWork. Код не должен требовать импорта дополнительных классов.
 *     После ввода пустой строки считывание прекращается и считанные строки добавляются в тело метода public void doWork() в файле
 * SomeClass.java.
 *     Файл SomeClass.java компилируется программой (в рантайме) в файл SomeClass.class.
 *     Полученный файл подгружается в программу с помощью кастомного загрузчика
 *     Метод, введенный с консоли, исполняется в рантайме (вызывается у экземпляра объекта подгруженного класса)
 *
 * @author Alexander.Mamonov@protonmail.ch
 * @version 2.0
 */
public class task01 {
    public static void main(String[] args) {
        Properties property = new Properties();

        try {
            String fileprop = "F:\\JavaProjects\\Innopolis\\src\\part1\\lesson09\\resources\\config.properties";
            FileInputStream fis = new FileInputStream(fileprop);
            property.load(fis);
            String path = property.getProperty("path");
            String file = property.getProperty("file");
            String importedClass = property.getProperty("importedClass");

            writeFile(path + file, changeFile(readFile(path + file)));
            compileFile(path, file, importedClass);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static List readFile(String readPath) {
        File file = new File(readPath);
        List arrayOfLines = new ArrayList();

        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                arrayOfLines.add(scanner.nextLine());
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Read array: " + arrayOfLines);
        return arrayOfLines;
    }

    private static List changeFile(List arrayOfLines) {
        List inputArrayOfLines = readConsole();
        List finalArrayOfLines = new ArrayList();
        String line = "";
        Iterator iter = arrayOfLines.iterator();

        for (int i = 0; i < arrayOfLines.size(); i++) {
            line = iter.next().toString() ;
//            System.out.println("Line = " + line);

            if (line.endsWith("public void doWork() {")) {
                finalArrayOfLines.add(line);
                finalArrayOfLines.addAll(inputArrayOfLines);
            }
            else {
                finalArrayOfLines.add(line);
            }
        }

//        System.out.println("Changed array: " + finalArrayOfLines);
        return finalArrayOfLines;
    }

    private static String writeFile(String writePath, List arrayOfLines) {
        try {
            FileOutputStream fos = new FileOutputStream(writePath);
            Iterator iter = arrayOfLines.iterator();
            String line = "";

            for (int i = 0; i < arrayOfLines.size(); i++) {
                line = iter.next().toString() + "\n";
                fos.write(line.getBytes());
//                System.out.println("Line to write: " + line);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return writePath;
    }

    private static void compileFile(String path, String file, String importedClass) {
        Path rootPath = Paths.get(path);
        Path javaFilePath = rootPath.resolve(file);

        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        jc.run(null, null, null, javaFilePath.toAbsolutePath().toString());

        URLClassLoader classLoader = null;
        try {
            classLoader = URLClassLoader.newInstance(new URL[] { rootPath.toUri().toURL() });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        ReloadingClassLoader rlc = new ReloadingClassLoader(classLoader); /* !!! */
        try {
            rlc.loadClass(importedClass);
            Someclass scl = new Someclass();
            scl.doWork();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static List readConsole() {
        List arrayOfLines = new ArrayList();
        Scanner input = new Scanner(System.in);
        String line = " ";

        while (!line.equals("")) {
            System.out.println("Put an emplty line to exit. Please input a new line: ");
            line = input.nextLine();
            arrayOfLines.add(line);

//            System.out.println("Your inpuе line is " + line);
        }

        System.out.println("Your input array: " + arrayOfLines);
        return arrayOfLines;
    }
}