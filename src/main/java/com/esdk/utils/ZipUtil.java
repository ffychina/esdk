/**
 *
 */
package com.esdk.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author Frank
 *
 */
public class ZipUtil {
    /** Constants for mode listing or mode extracting. */
    public static final int LIST = 0, EXTRACT = 1;

    /** Whether we are extracting or just printing TOC */
    protected int mode = LIST;

    /** The ZipFile that is used to read an archive */
    protected ZipFile zippy;

    /** The buffer for reading/writing the ZipFile data */
    protected byte[] b;

    private String unzipFileTargetLocation;

    public ZipUtil() {
        b = new byte[8092];
    }

    /** Set the Mode (list, extract). */
    public void setMode(int m) {
        if (m == LIST || m == EXTRACT)
            mode = m;
    }

    /** Cache of paths we've mkdir()ed. */
    protected SortedSet dirsMade;

    /**
     * For a given Zip file, process each entry.
     *
     * @param fileName
     *            unzip file name
     * @param unZipTarget
     *            location for unzipped file
     */
    public void unZip(String fileName, String unZipTarget) throws IOException {
        this.unzipFileTargetLocation = unZipTarget;
        dirsMade = new TreeSet();
        zippy = new ZipFile(fileName);
        Enumeration all = zippy.entries();
        while (all.hasMoreElements()) {
            getFile((ZipEntry) all.nextElement());
        }
    }

    protected boolean warnedMkDir = false;

    /**
     * Process one file from the zip, given its name. Either print the name, or
     * create the file on disk.
     */
    protected void getFile(ZipEntry e) throws IOException {
        String zipName = e.getName();
        switch (mode) {
            case EXTRACT:
                if (zipName.startsWith("/")) {
                    if (!warnedMkDir)
                        System.out.println("Ignoring absolute paths");
                    warnedMkDir = true;
                    zipName = zipName.substring(1);
                }
                // if a directory, just return. We mkdir for every file,
                // since some widely-used Zip creators don't put out
                // any directory entries, or put them in the wrong place.
                if (zipName.endsWith("/")) {
                    return;
                }
                // Else must be a file; open the file for output
                // Get the directory part.
                int ix = zipName.lastIndexOf('/');
                if (ix > 0) {
                    //String dirName = zipName.substring(0, ix);
                    String fileName = zipName.substring(ix + 1, zipName.length());
                    zipName = fileName;

                }
                String targetFile = this.unzipFileTargetLocation;
                File file = new File(targetFile);
                if (!file.exists())
                    file.createNewFile();
                FileOutputStream os = null;
                InputStream is = null;
                try {
                    os = new FileOutputStream(targetFile);
                    is = zippy.getInputStream(e);
                    int n = 0;
                    while ((n = is.read(b)) > 0)
                        os.write(b, 0, n);

                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }
                }
                break;
            case LIST:
                // Not extracting, just list
                if (e.isDirectory()) {
                    System.out.println("Directory " + zipName);
                } else {
                    System.out.println("File " + zipName);
                }
                break;
            default:
                throw new IllegalStateException("mode value (" + mode + ") bad");
        }
    }

    /**
     * unzip zip file with
     *
     * @param unZipFile
     *            ,and save unzipped file to
     * @param saveFilePath
     * @param unZipFile
     *            full file path,like 'd:\temp\test.zip'
     * @param saveFilePath
     *            full file path,like 'd:\temp\test.kml'
     * @return
     */
    public static boolean unzip(String unZipFile, String saveFilePath) {
        boolean succeed = true;
        ZipInputStream zin = null;
        ZipEntry entry;
        try {
            // zip file path
            File olddirec = new File(unZipFile);
            zin = new ZipInputStream(new FileInputStream(unZipFile));
            // iterate ZipEntry in zip
            while ((entry = zin.getNextEntry()) != null) {
                // if folder,create it
                if (entry.isDirectory()) {
                    File directory = new File(olddirec.getParent(),
                            entry.getName());
                    if (!directory.exists()) {
                        if (!directory.mkdirs()) {
                            System.out.println("Create foler in "
                                    + directory.getAbsoluteFile() + " failed");
                        }
                    }
                    zin.closeEntry();
                }
                // if file,unzip it
                if (!entry.isDirectory()) {
                    File myFile = new File(saveFilePath);
                    FileOutputStream fout = null;
                    DataOutputStream dout = null;
                    try {
                        fout = new FileOutputStream(myFile);
                        dout = new DataOutputStream(fout);
                        byte[] b = new byte[1024];
                        int len = 0;
                        while ((len = zin.read(b)) != -1) {
                            dout.write(b, 0, len);
                        }
                    } finally {
                        if (dout != null) {
                            try {
                                dout.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (fout != null) {
                            try {
                                fout.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    zin.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            succeed = false;
            System.out.println(e);
        } finally {
            if (null != zin) {
                try {
                    zin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (succeed)
            System.out.println("File unzipped successfully!");
        else
            System.out.println("File unzipped with failure!");
        return succeed;
    }

}
