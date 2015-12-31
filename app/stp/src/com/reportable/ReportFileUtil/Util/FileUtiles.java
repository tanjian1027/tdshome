package com.reportable.ReportFileUtil.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;

public class FileUtiles {
	
		/**
	 * 获取系统对应的行业类别
	 * 
	 * @param code
	 * @return
	 */
	public static String getCTVC(String code) {
		String ctvc = "";
		if ("A1".equals(code)) {
			ctvc = "2R";
		} else if ("B0".equals(code) || "B2".equals(code)) {
			ctvc = "2R";
		} else if ("A2".equals(code)) {
			ctvc = "2G";
		} else if ("00".equals(code) || "A0".equals(code) || "A7".equals(code)) {
			ctvc = "1A";
		} else if ("A8".equals(code) || "A9".equals(code) || "A5".equals(code)
				|| "A6".equals(code)) {
			ctvc = "2G";
		} else if ("A3".equals(code)) {
			ctvc = "2H";
		} else if ("A4".equals(code) || "B1".equals(code) || "B5".equals(code)) {
			ctvc = "1E";
		} else if ("B3".equals(code)) {
			ctvc = "2O";
		} else {
			ctvc = "1H";
		}
		return ctvc;
	}

	/**
	 * 获取系统对应的 身份类型
	 * 
	 * @param code
	 * @return
	 */
	public static String getCITP(char code) {
		String CITP = "";
		switch (code) {
		case '0':
			CITP = "11";
			break;
		case '1':
			CITP = "14";
			break;
		case '2':
			CITP = "12";
		case 'I':
			CITP = "13";
			break;
		case 'C':
			CITP = "13";
			break;
		case 'F':
			CITP = "11";
			break;
		case 'G':
			CITP = "12";
		default:
			CITP = "19";
			break;
		}
		return CITP;
	}

	/**
	 * 
	 * 使用文件通道的方式复制文件
	 * 
	 * 
	 * 
	 * @param s
	 * 
	 *            源文件
	 * 
	 * @param t
	 * 
	 *            复制到的新文件
	 */

	public static void fileChannelCopy(String s, String t) {

		FileInputStream fi = null;

		FileOutputStream fo = null;

		FileChannel in = null;

		FileChannel out = null;

		try {

			fi = new FileInputStream(new File(s));

			fo = new FileOutputStream(new File(t));

			in = fi.getChannel();// 得到对应的文件通道

			out = fo.getChannel();// 得到对应的文件通道

			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				fi.close();

				in.close();

				fo.close();

				out.close();

			} catch (IOException e) {

				e.printStackTrace();

			}

		}

	}
	/**
	 * 获取本机ip
	 * @return
	 */
	public static String getIp()
	{
		String ip;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			ip = "127.0.0.1";
		}
		return ip;
	}
}
