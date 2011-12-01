package cazcade.common;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author neilellis@cazcade.com
 */
public class DirZip {


    public static void zipDir(final String dir2zip, @Nonnull final ZipOutputStream zos, final boolean recursive) {
        try {
            //create a new File object based on the directory we have to zip
            final File
                    zipDir = new File(dir2zip);
            //get a listing of the directory content
            final String[] dirList = zipDir.list();
            final byte[] readBuffer = new byte[2156];
            int bytesIn = 0;
            //loop through dirList, and zip the files
            for (int i = 0; i < dirList.length; i++) {
                final File f = new File(zipDir, dirList[i]);
                if (f.isDirectory() && recursive) {
                    //if the File object is a directory, call this
                    //function again to add its content recursively
                    final String filePath = f.getPath();
                    zipDir(filePath, zos, false);
                    //loop again
                    continue;
                }
                //if we reached here, the File object f was not a directory
                //create a FileInputStream on top of f
                final FileInputStream fis = new FileInputStream(f);
                //create a new zip entry
                final ZipEntry anEntry = new ZipEntry(f.getPath());
                //place the zip entry in the ZipOutputStream object
                zos.putNextEntry(anEntry);
                //now write the content of the file to the ZipOutputStream
                while ((bytesIn = fis.read(readBuffer)) != -1) {
                    zos.write(readBuffer, 0, bytesIn);
                }
                //close the Stream
                fis.close();
            }
        } catch (Exception e) {
            //handle exception
        }
    }
}
