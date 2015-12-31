package com.tangdi.attachment.util;

import java.io.File;
import java.io.IOException;

public class FileUtil {
	private static String SHOWIMAGETYPE = "BMP,GIF,JPG,ICO,PNG,TIF"; //预览时候可以进行查看的图片类型
	private static String SHOWMEDIATYPE = "MP4"; //预览时候可以进行查看的多媒体类型
	
    //创建文件夹
    public static void createFolder(String sFolder) throws Exception{
        File fDir = new File(sFolder);
        if (!fDir.exists() || !fDir.isDirectory()) {
            if(!fDir.mkdirs()){
                throw new IOException("Can't create Folder"+sFolder);
            }
        }
    }

    //创建文件
    public static File createFile(String sFile) throws IOException {
        File f = new File(sFile);
        if (!f.exists()) {
            if(!f.createNewFile()){
                throw new IOException("Can't create file"+sFile);
            }
        }
        return f;

    }

    //删除文件
    public static boolean deleteFile(String sFile) throws IOException {
        File f = new File(sFile);
        String parentDir = f.getParent();

        if(f.exists() && f.isFile()){
            if(!f.delete()){
                throw new IOException("Can't delete file"+sFile);
            }
        }
        //若上级目录下没有文件，则依次删除上级目录，直至fj或temp目录
        File pf=new File(parentDir);
        while(pf.exists() && pf.isDirectory()){
        	String pd=pf.getParent();
        	File[] flist=pf.listFiles();
        	if(flist.length==0){
        		pf.delete();
        		pf=new File(pd);
        		if(pf.getName().equalsIgnoreCase("fj")||pf.getName().equalsIgnoreCase("temp")){
        			break;
        		}
        	}else{
        		break;
        	}

        }

        return true;

    }

    //从一个文件夹中获得第一个文件的名称
    public static String getFileName(String dirName){
        File dir = new File(dirName);
        String fileName = null;
        if(dir.isDirectory()){
            String[] fileList = dir.list();
            if(fileList != null && fileList.length>0){
                fileName = fileList[0];
            }
        }
        return fileName;
    }

    //删除文件的父目录(仅上一层)
    public static void deleteParentDir(String fileName){
        File file = new File(fileName);
        String parentDir = file.getParent();
        FileOperator.removeDir(parentDir);
    }

    //删除本文件夹及全部内容
    public static void deleteFolder(File folder)   {
        String childs[] = folder.list();
        if   (childs == null || childs.length <= 0){
            folder.delete();
        }else{
	        for(int i=0;i<childs.length;i++){
	            String childName = childs[i];
	            String childPath   = folder.getPath()   +   File.separator   +   childName;
	            File filePath = new File(childPath);
	            if   (filePath.exists()   &&   filePath.isFile())   {
	                  filePath.delete();
	            }else if(filePath.exists() && filePath.isDirectory())   {
	                  deleteFolder(filePath);
	            }
	        }
	        folder.delete();
        }
    }

    //删除本文件夹下全部内容
    public static void deleteChildFolder(File folder)   {
        String childs[] = folder.list();
        if   (childs == null || childs.length <= 0){
            return;
        }

        for(int i=0;i<childs.length;i++){
            String childName = childs[i];
            String childPath   = folder.getPath()   +   File.separator   +   childName;
            File filePath = new File(childPath);
            if   (filePath.exists()   &&   filePath.isFile())   {
                  filePath.delete();
            }else if(filePath.exists() && filePath.isDirectory())   {
                  deleteFolder(filePath);
            }
        }
    }
    
    //获得文件类型
    public static String getFileType(String sFile){
		String sRes = "";
		if(sFile.indexOf(".")!=-1){
			String[] arr = sFile.split("\\.");
			if(arr.length != 0){
				sRes = arr[arr.length-1];
			}
		}
		return sRes;
	}
    
    /**
     * 是否可预览的图片
     * @param sType
     * @return
     */
    public static boolean isImageShow(String sType){
    	boolean bRes = false;
    	if(null == sType){
    		return bRes;
    	}
    	sType = sType.toUpperCase();
    	String[] arr = SHOWIMAGETYPE.split(",");
    	for(String sTemp:arr){
    		if(sTemp.equalsIgnoreCase(sType)){
    			bRes = true;
    			break;
    		}
    	}
    	return bRes;
    }
    
    /**
     * 是否可预览的SWF
     * @param sType
     * @return
     */
    public static boolean isSwfShow(String sType){
    	boolean bRes = false;
    	if(null == sType){
    		return bRes;
    	}
    	if("SWF".equalsIgnoreCase(sType)){
    		bRes = true;
    	}
    	return bRes;
    }
    
    /**
     * 是否可预览的Media
     * @param sType
     * @return
     */
    public static boolean isMediaShow(String sType){
    	boolean bRes = false;
    	if(null == sType){
    		return bRes;
    	}
    	sType = sType.toUpperCase();
    	String[] arr = SHOWMEDIATYPE.split(",");
    	for(String sTemp:arr){
    		if(sTemp.equalsIgnoreCase(sType)){
    			bRes = true;
    			break;
    		}
    	}
    	return bRes;
    }
}
