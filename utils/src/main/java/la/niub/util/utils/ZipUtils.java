
package la.niub.util.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static boolean zipFile(File file, File outFile) {
        if (file == null || !file.exists() || outFile == null) {
            return false;
        }
        ZipOutputStream out = null;
        FileInputStream in = null;
        ZipEntry entry = new ZipEntry(file.getName());
        try {
            out = new ZipOutputStream(new FileOutputStream(outFile));
            in = new FileInputStream(file);
            out.putNextEntry(entry);
            IoUtils.copyStream(in, out);
            out.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
            FileUtil.deleteFileIfExist(outFile);
            return false;
        } finally {
            IoUtils.closeQuietly(in);
            IoUtils.closeQuietly(out);
        }
        return true;
    }

    public static final String EXT = ".zip";
    private static final String BASE_DIR = "";

    // 符号"/"用来作为目录标识判断符
    private static final String PATH = "/";
    private static final int BUFFER = 1024;

    public static void test() throws Exception {
        ZipUtils.compress("d:\\f.txt");
        ZipUtils.compress("d:\\fd");
        ZipUtils.decompress("d:\\f.txt.zip", "d:\\ff");
        ZipUtils.decompress("d:\\fd.zip", "d:\\fdf");
    }

    /**
     * 压缩
     * 
     * @param srcFile
     * @throws Exception
     */
    public static void compress(File srcFile) throws Exception {
        String name = srcFile.getName();
        String basePath = srcFile.getParent();
        String destPath = basePath + name + EXT;
        compress(srcFile, destPath);
    }

    /**
     * 压缩
     * 
     * @param srcFile 源路径
     * @param destPath 目标路径
     * @throws Exception
     */
    public static void compress(File srcFile, File destFile) throws Exception {
        // 对输出文件做CRC32校验
        CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(
                destFile), new CRC32());
        ZipOutputStream zos = new ZipOutputStream(cos);
        compress(srcFile, zos, BASE_DIR);
        zos.flush();
        zos.close();
    }

    /**
     * 压缩文件
     * 
     * @param srcFile
     * @param destPath
     * @throws Exception
     */
    public static void compress(File srcFile, String destPath) throws Exception {
        compress(srcFile, new File(destPath));
    }

    /**
     * 压缩
     * 
     * @param srcFile 源路径
     * @param zos ZipOutputStream
     * @param basePath 压缩包内相对路径
     * @throws Exception
     */
    private static void compress(File srcFile, ZipOutputStream zos,
            String basePath) throws Exception {
        if (srcFile.isDirectory()) {
            compressDir(srcFile, zos, basePath);
        } else {
            compressFile(srcFile, zos, basePath);
        }
    }

    /**
     * 压缩
     * 
     * @param srcPath
     * @throws Exception
     */
    public static void compress(String srcPath) throws Exception {
        File srcFile = new File(srcPath);
        compress(srcFile);
    }

    /**
     * 文件压缩
     * 
     * @param srcPath 源文件路径
     * @param destPath 目标文件路径
     */
    public static void compress(String srcPath, String destPath)
            throws Exception {
        File srcFile = new File(srcPath);
        compress(srcFile, destPath);
    }

    /**
     * 压缩目录
     * 
     * @param dir
     * @param zos
     * @param basePath
     * @throws Exception
     */
    private static void compressDir(File dir, ZipOutputStream zos,
            String basePath) throws Exception {
        File[] files = dir.listFiles();
        // 构建空目录
        if (files.length < 1) {
            ZipEntry entry = new ZipEntry(basePath + dir.getName() + PATH);
            zos.putNextEntry(entry);
            zos.closeEntry();
        }
        for (File file : files) {
            // 递归压缩
            compress(file, zos, basePath + dir.getName() + PATH);
        }
    }

    /**
     * 文件压缩
     * 
     * @param file 待压缩文件
     * @param zos ZipOutputStream
     * @param dir 压缩文件中的当前路径
     * @throws Exception
     */
    private static void compressFile(File file, ZipOutputStream zos, String dir)
            throws Exception {

        /**
         * 压缩包内文件名定义
         * 
         * <pre>
         * 如果有多级目录，那么这里就需要给出包含目录的文件名
         * 如果用WinRAR打开压缩包，中文名将显示为乱码
         * </pre>
         */
        ZipEntry entry = new ZipEntry(dir + file.getName());

        zos.putNextEntry(entry);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                file));

        int count;
        byte data[] = new byte[BUFFER];
        while ((count = bis.read(data, 0, BUFFER)) != -1) {
            zos.write(data, 0, count);
        }
        bis.close();

        zos.closeEntry();
    }

    /**
     * 解压缩
     * 
     * @param srcFile
     */
    public static void decompress(File srcFile) {
        String basePath = srcFile.getParent();
        decompress(srcFile, basePath);
    }

    /**
     * 解压缩
     * 
     * @param srcFile
     * @param destPath
     * @throws Exception
     */
    public static void decompress(File srcFile, String destPath) {
        decompress(srcFile, new File(destPath));
    }

    /**
     * 解压缩
     * 
     * @param srcFile
     * @param destFile
     * @throws Exception
     */
    public static void decompress(File srcFile, File destFile) {
        try {
            decompress(new FileInputStream(srcFile), destFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void decompress(InputStream in, File destFile) {
        ZipInputStream zis = null;
        try {
            CheckedInputStream cis = new CheckedInputStream(in, new CRC32());
            zis = new ZipInputStream(cis);
            decompress(zis, destFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(zis);
        }
    }

    /**
     * 文件 解压缩
     * 
     * @param destFile 目标文件
     * @param zis ZipInputStream
     * @throws IOException
     * @throws Exception
     */
    private static void decompress(ZipInputStream zis, File destFile) throws IOException {
        ZipEntry entry = null;
        while ((entry = zis.getNextEntry()) != null) {
            // 文件
            File childFile = new File(destFile, entry.getName());
            // 文件检查
            FileUtil.ensureParent(childFile);
            if (entry.isDirectory()) {
                childFile.mkdirs();
            } else {
                decompressFile(zis, childFile);
            }
            zis.closeEntry();
        }
    }

    /**
     * 文件 解压缩
     * 
     * @param srcPath 源文件路径
     * @throws Exception
     */
    public static void decompress(String srcPath) throws Exception {
        File srcFile = new File(srcPath);
        decompress(srcFile);
    }

    /**
     * 文件 解压缩
     * 
     * @param srcPath 源文件路径
     * @param destPath 目标文件路径
     * @throws Exception
     */
    public static void decompress(String srcPath, String destPath) {
        File srcFile = new File(srcPath);
        decompress(srcFile, destPath);
    }

    /**
     * 文件解压缩
     * 
     * @param destFile 目标文件
     * @param zis ZipInputStream
     * @throws Exception
     */
    private static void decompressFile(ZipInputStream zis, File destFile) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(destFile);
            IoUtils.copyStream(zis, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(fos);
        }
    }

}
