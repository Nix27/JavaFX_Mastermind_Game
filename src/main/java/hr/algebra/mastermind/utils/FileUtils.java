package hr.algebra.mastermind.utils;

import java.io.*;

public final class FileUtils {
    private FileUtils() {}

    public static<T extends Serializable> void save(T t, String filename) throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))){
            oos.writeObject(t);
        }
    }

    public static<T extends Serializable> T read(String filename) throws IOException, ClassNotFoundException {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))){
            return (T)ois.readObject();
        }
    }
}
