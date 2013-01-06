package cn.edaijia.android.client.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.edaijia.android.client.AppInfo;

public class FileUtils {

    private static int FILESIZE = 4 * 1024;

    private static Logger sLogger = Logger.createLogger("FileUtils");

    /**
     * 在SD卡上创建文件
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    private static File createSDFile(String fileName) throws IOException {
        File dir = new File(AppInfo.BASE_DATA_DIR + File.separator
                + Utils.BASE_FOLDER_NAME);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     * 
     * @param fileName
     * @return
     */
    public static String getFilePath(String fileName) {
        File file = new File(AppInfo.BASE_DATA_DIR + File.separator
                + Utils.BASE_FOLDER_NAME + File.separator + fileName);
        if (file.exists()){
            return file.getPath();
        }
        return null;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     * 
     * @param path
     * @param fileName
     * @param input
     * @return
     */
    public static File write2SDFromInput(String path, String fileName,
            InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            // createSDDir(path);
            file = createSDFile(path + fileName);
            output = new FileOutputStream(file);
            sLogger.d("----write-file---");
            byte[] buffer = new byte[FILESIZE];
            while ((input.read(buffer)) != -1) {
                output.write(buffer);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
