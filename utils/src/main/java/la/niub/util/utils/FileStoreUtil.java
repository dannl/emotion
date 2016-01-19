package la.niub.util.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileStoreUtil {

    private static final String FILE_ENCODE = "utf-8";

    public static String loadFromFile(File storeFile) {

        if (storeFile == null) {
            return null;
        }

        String result = null;

        try {
            result = IOUtilities.loadContent(new FileInputStream(storeFile), FILE_ENCODE);
        } catch (FileNotFoundException e) {
            Log.d(e.getMessage());
        } catch (IOException e) {
            Log.d(e.getMessage());
        }

        return result;
    }

    public static void saveToFile(File storeFile, String store) {
        if (storeFile == null || store == null) {
            return;
        }
        try {
            IOUtilities.saveToFile(storeFile, store, FILE_ENCODE);
        } catch (IOException e) {
            Log.d(e.getMessage());
        }
    }

    public static String loadFromFile(String filename) {

        File storeFile = createFile(filename);
        return loadFromFile(storeFile);
    }

    public static void saveToFile(String filename, String store) {
        if (filename == null || store == null) {
            return;
        }

        File storeFile = createFile(filename);
        saveToFile(storeFile, store);
    }

    private static File createFile(String filename) {
        Context context = AppContext.getInstance();
        File file = new File(context.getFilesDir(), filename);
        return file;
    }
}
