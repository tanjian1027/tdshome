package com.reportable.ReportFileUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class fileUtil {
	
    public static BufferedReader bufread;
    private static String readStr ="";
    private static String filename = "";
	
    public static void main(String args[]) {  
        String file = "D://700000000000001_20150119.zip";  
        String saveRootDirectory = "D://ceshi/"; 
        new fileUtil().zipFileRead(file, saveRootDirectory);  
    }  
    
    public static void write2file(String cbuf) {   
        File file = null;//首先定义文件类   
        OutputStream os = null;//定义字节流   
        OutputStreamWriter osw = null;//OutputStreamWriter是字节流通向字符流的桥梁。   
        BufferedWriter bw = null;//定义缓冲区   
           
        try {   
            file = new File("d:\\700000000000001_20150119.zip"); //新建文件对象   
            //从文件系统中的某个文件中获取字节   
            os = new FileOutputStream(file, true); //true是append设为允许，即可以在原文件末端追加。   
            //将字节流转换成字符流   
            osw = new OutputStreamWriter(os);   
            //把接收到的字符流放入缓冲区，提高读写速度。   
            bw = new BufferedWriter(osw);   
               
            //将字符串以流的形式写入文件    
        } catch (FileNotFoundException e) {   
            System.out.println("找不到指定文件");   
        } catch (IOException e) {   
            System.out.println("写入文件错误");   
        } finally {   
            try {   
                //关闭文件放到finally里，无论读取是否成功，都要把流关闭。   
                //关闭的顺序：最后开的先关闭，栈的先进后出原理。   
                bw.close();   
                osw.close();   
                os.close();   
            } catch (IOException e) {   
                System.out.println("文件流无法关闭");   
            }   
        }   
    }   
  
    /** 
     *  
     * @Description: TODO(读取Zip信息，获得zip中所有的目录文件信息) 
     * @param设定文件 
     * @return void 返回类型 
     * @throws 
     */  
    public void zipFileRead(String file, String saveRootDirectory) {  
        try {  
            // 获得zip信息  
            ZipFile zipFile = new ZipFile(file);  
            @SuppressWarnings("unchecked")  
            Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile  
                    .entries();  
            while (enu.hasMoreElements()) {  
                ZipEntry zipElement = (ZipEntry) enu.nextElement();  
                InputStream read = zipFile.getInputStream(zipElement);  
                String fileName = zipElement.getName();  
                if (!zipElement.isDirectory() && fileName.indexOf("INN")> -1) {// 是否为文件  
                    unZipFile(zipElement, read, saveRootDirectory);  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     *  
     * @Description: TODO(找到文件并读取解压到指定目录) 
     * @param 设定文件 
     * @return void 返回类型 
     * @throws 
     */  
    public void unZipFile(ZipEntry ze, InputStream read,  
            String saveRootDirectory) throws FileNotFoundException, IOException {  
        // 如果只读取图片，自行判断就OK.  
        String fileName = ze.getName();  
        // 判断文件是否符合要求或者是指定的某一类型  
//      if (fileName.equals("WebRoot/WEB-INF/web.xml")) {  
            // 指定要解压出来的文件格式（这些格式可抽取放置在集合或String数组通过参数传递进来，方法更通用）  
        	filename = saveRootDirectory + fileName;
            File file = new File(filename);  
            if (!file.exists()) {  
                File rootDirectoryFile = new File(file.getParent());  
                // 创建目录  
                if (!rootDirectoryFile.exists()) {  
                    boolean ifSuccess = rootDirectoryFile.mkdirs();  
                    if (ifSuccess) {  
                        System.out.println("文件夹创建成功!");  
                    } else {  
                        System.out.println("文件创建失败!");  
                    }  
                }  
                // 创建文件  
                try {  
                    file.createNewFile();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            
            // 写入文件  
            BufferedOutputStream write = new BufferedOutputStream(  
                    new FileOutputStream(file));  
            int cha = 0;
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((cha = read.read()) != -1) {
                write.write(cha);  
            }  
            // 要注意IO流关闭的先后顺序  
            write.flush();  
            write.close();  
            read.close(); 
            readTxtFile();
            writeTxtFile();
            // }  
//      }  
    }  
    
    
    /**
     * 读取文本文件.
     * 
     */
    public static String readTxtFile(){
        String read;
        FileReader fileread;
        try {
            fileread = new FileReader(filename);
            bufread = new BufferedReader(fileread);
            try {
                while ((read = bufread.readLine()) != null) {
                    readStr = readStr + read+ "\r\n";
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return readStr;
    }
    
    /**
     * 写文件.
     * 
     */
    public static void writeTxtFile() throws IOException{
        //先读取原有文件内容，然后进行写入操作
        StringBuffer sb = new StringBuffer();
        String[] read1 = readStr.split("\n");
        for (int i = 0; i < read1.length; i++) {
        	String[] read2 = read1[i].split(" ");
    		for (int j = 0; j < read2.length; j++) {
    			if (read2[j] != null && !read2[j].trim().equals("")) {
    				sb.append(read2[j]);
    				if(j<read2.length-1){
    					sb.append("|");
    				}
    			}
    		}
    		if(i<read1.length-1){
    			sb.append("\n");
    		}
		}
        File f = new File(filename);
        FileWriter fw =  new FileWriter(f);
        fw.write("");
        fw.close();
        RandomAccessFile mm = null;
        try {
            mm = new RandomAccessFile(filename, "rw");
            mm.writeBytes(sb.toString());
        } catch (IOException e1) {
            // TODO 自动生成 catch 块
            e1.printStackTrace();
        } finally {
            if (mm != null) {
                try {
                    mm.close();
                } catch (IOException e2) { 
                    // TODO 自动生成 catch 块
                    e2.printStackTrace();
                }
            }
        }
    }

}
