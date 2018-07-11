package com.clearcrane.musicplayer.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileUtils {

    public static String readFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeToFile(String filePath, String content) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter pw = new PrintWriter(filePath);
            pw.write(content);

            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(File file, InputStream is) throws IOException {
        OutputStream os = new FileOutputStream(file);
        byte[] buffer = new byte[2048];
        int size;
        while ((size = is.read(buffer)) > 0) {
            byte[] read = new byte[size];
            System.arraycopy(buffer, 0, read, 0, size);
            os.write(read);
        }

        os.flush();
        os.close();
    }

    public static void appendToFile(String filePath, String content) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            PrintWriter pw = new PrintWriter(new FileWriter(filePath, true));
            pw.println(content);

            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object readObjectFromFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object o = ois.readObject();

            ois.close();
            fis.close();
            return o;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeObjectToFile(Object o, String file) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(o);

            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
