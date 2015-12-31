package com.tangdi.attachment.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
/**
 * <p>Title: FileOperator.java</p>
 * <p>Description: this is opertator to deal with action related with File I/O.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p> </p>
 * @author Jason Meng
 * @version 1.0
 */
public class FileOperator {

	public static void writeToFile(String sFilePathName, String sText) throws IOException {
		File f;
		FileOutputStream fos = null;
		f = new File(sFilePathName);
		if (!f.exists())
			f.createNewFile();
		fos = new FileOutputStream(f);
		fos.write(sText.getBytes());
		fos.close();
	}

    /**
     * copy files recursively...
     * 
     * @param sSource
     * @param sDest
     * @param sFilter
     * @param bRecur
     */
    public static void copyFiles(String sSource, String sDest, String sFilter, boolean bRecur) {
        copyFiles (sSource, sDest, new FileFilter(sFilter),  bRecur);
    }
    
    public  static void copyFiles(String sSource, String sDest, FilenameFilter fileFilter, boolean bRecur) {
    	try {
            File fSource = new File(sSource);
            File fDest = new File(sDest);

            if (!fDest.exists() || !fDest.isDirectory()) {
                fDest.mkdirs();
            }

            String sSubs[] = fSource.list(fileFilter);
            int len = sSubs.length;

            for (int i = 0; i < len; i++) {

                File fS = new File(fSource, sSubs[i]);
                File fD = new File(fDest, sSubs[i]);

                if (fS.isDirectory()) {
                    if (bRecur) {
                        if (sSubs[i].equalsIgnoreCase("CVS"))
                            continue;
                        fD.mkdir();
                        copyFiles(fS.getAbsolutePath(), fD.getAbsolutePath(), fileFilter, true);
                    }
                } else {
                        fD.createNewFile();
                        copyFile(fS, fD);
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * @param sSource
     * @param sDest
     * @param sFilter
     * @param bRecur
     */
    public static void moveFiles(String sSource, String sDest, String sFilter, boolean bRecur) {
        try {

            File fSource = new File(sSource);
            File fDest = new File(sDest);

            if (!fDest.exists() || !fDest.isDirectory()) {
                fDest.mkdirs();
            }

            String[] sSubs;
            if (sFilter != null) {
                sSubs = fSource.list(new FileFilter(sFilter));
            }
            else {
                sSubs = fSource.list();
            }
            
            int len = sSubs.length;

            for (int i = 0; i < len; i++) {
                File fS = new File(fSource, sSubs[i]);
                File fD = new File(fDest, sSubs[i]);

                if (fS.isDirectory()) {
                    if (bRecur) {
                        if (sSubs[i].equalsIgnoreCase("CVS")) {
                            continue;
                        }
                            
                        fD.mkdir();
                        copyFiles(fS.getAbsolutePath(), fD.getAbsolutePath(), sFilter, true);
                    }
                } 
                else {
                    fD.createNewFile();
                    copyFile(fS, fD);
                    fS.delete();
                }
            }

        } 
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
	
    public static void MoveFiles(String sSource, String sDest, String sFilter, boolean bRecur) {
        moveFiles(sSource, sDest, sFilter, bRecur);
    }

	public static void renameFile(File fS,File fD){
		if(fS.exists() && !fD.exists()){
			fS.renameTo(fD);
		}
	}
	
	public static void createFile(String sD) throws IOException{
		File fD = new File(sD);
		if(!fD.exists()){
			fD.createNewFile();
		}
	}
	public static void deleteFile(String sD){
		File fD = new File(sD);
		if(fD.exists() && fD.isFile()){
			fD.delete();
		}
	}
	
	public static void moveFile(String sS, String sD)  throws Exception{
		File fS = new File(sS);
		File fD = new File(sD);
		if (fS.exists() && fS.isFile()) {
			if (fD.exists()) {
				if(fD.isFile()){
					fD.delete();
				}else{
					fD = new File(sD,fS.getName());
					if(fD.exists() && fD.isFile()){
						fD.delete();
					}
				}
			}
			copyFile(fS, fD);
			fS.delete();
		}else{
			System.out.println("[ERROR] Process of copyFile wrong: "+ sS +" cannot be found!!");
		}
	}
	public static void copyFile(String sS, String sD, String sF) throws Exception{
		File fS = new File(sS);
		File fD = new File(sD);
		if (fS.exists() && fS.isFile()) {
			if(!fD.exists() || !fD.isDirectory()) {
				fD.mkdirs();
			}
			fD = new File(sD,sF);
			if(fD.exists() && fD.isFile()){
				fD.delete();
			}
			copyFile(fS, fD);
		}else{
			System.out.println("[ERROR] Process of copyFile wrong: "+ sS +" cannot be found!!");
		}
	}
	
	public static void copyFile(String sS, String sD) throws Exception{
		File fS = new File(sS);
		if (fS.exists() && fS.isFile()) {
			copyFile(sS, sD, fS.getName());
		}else{
			System.out.println("[ERROR] Process of copyFile wrong: "+ sS +" cannot be found!!");
		}
	}
	/**
	 * @param fS
	 * @param fD
	 */
	public static void copyFile(File fS, File fD) {
		try {
			FileInputStream is = new FileInputStream(fS);
			FileOutputStream os = new FileOutputStream(fD);

			byte buffer[] = new byte[2048];

			int len = is.read(buffer);
			while (len != -1) {
				os.write(buffer, 0, len);
				len = is.read(buffer);
			}

			is.close();
			os.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void removeFiles(String sFolder,String sFilter){
			File fFolder = new File(sFolder);
			if (fFolder.exists() && fFolder.isDirectory()) {
				String sSubs[] = fFolder.list(new FileFilter(sFilter));
				int len = sSubs.length;
				for (int i = 0; i < len; i++) {
					File fT = new File(sFolder, sSubs[i]);
					if (fT.exists() && fT.isFile()) {
						fT.delete();
					}	
				}
			}
	}
	
	/**
	 * @param sFolder
	 */
	public static void removeDir(String sFolder) {
		File fFolder = new File(sFolder);
		if (fFolder.exists() && fFolder.isDirectory()) {
			String sSubs[] = fFolder.list();
			int len = sSubs.length;
			for (int i = 0; i < len; i++) {
				File fT = new File(sFolder, sSubs[i]);
				if (fT.isDirectory()) {
					removeDir(fT.getPath());						
				}
				fT.delete();
			}
			fFolder.delete();
		}
	}
	
    /**
     * @param sFolder
     */
    public static void removeDirWhenEmpty(String sFolder) {
        File fFolder = new File(sFolder);
        if (fFolder.exists() && fFolder.isDirectory()) {
            String sSubs[] = fFolder.list();
            if (sSubs.length == 0) {
                fFolder.delete();
            }
        }
    }
    
}
