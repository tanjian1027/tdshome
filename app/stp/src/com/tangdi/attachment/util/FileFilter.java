package com.tangdi.attachment.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilter implements FilenameFilter {
	String afn;
	boolean bFilterDir;
	public FileFilter(String afn) {
		this.afn = afn;
		this.bFilterDir = true;
	}
	
	public FileFilter(String afn, boolean bFilterDirectory) {
		this.afn = afn;
		this.bFilterDir = bFilterDirectory;
	}
	
	public boolean accept(File dir, String name) {
		if(afn == null) {
			return true;
		}
		
		if (this.bFilterDir) {
			return name.endsWith(afn) && (new File(dir,name)).isFile();
		} else {
			return name.endsWith(afn);
		}
	}
}
