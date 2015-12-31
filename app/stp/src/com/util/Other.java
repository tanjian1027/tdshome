package com.util;

import java.io.File;
import java.io.IOException;
import java.util.List;


import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class Other {

	/**
	 * 创建excel 2003文件
	 * 
	 * @param list
	 *            数据集合
	 * @param head
	 *            表格头
	 * @param date
	 * @param sheetName
	 * @param path 
	 */
	public String createExcel(List<List<String>> list, String[] head,
			String sheetName,String path) {
		File file = new File(path);
		
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		//file = new File(path);  
        WritableWorkbook book=null;
		int row = 65535;
		int size = list.size() / row + 1;
		try {
			//OutputStream os = super.getResponse().getOutputStream();// 取得输出流
			//getResponse().reset();// 清空输出流
			/*getResponse().setHeader("Content-disposition",
					"attachment; filename=fine.xls");// 设定输出文件头
*/			//getResponse().setContentType("application/msexcel");// 定义输出类型
			book = Workbook.createWorkbook(file);// 建立excel文件
			// 在同一个excel文件中，循环创建工作簿并写入数据
			for (int i = 0; i < size; i++) {
				int begin = row * i;
				int end = row * i + row;
				if (begin >= list.size())
					break;
				if (end > list.size())
					end = list.size();
				// 向excel文件工作簿中写入数据
				setExcelSheet(book, list, head, sheetName, begin, end, i);
			}
			if(size == 0 ){
				setExcelSheet(book, list, head, sheetName, 0, 1, 1);
			}
			book.write();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				book.close();// 关闭输出流
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return  file.getAbsolutePath();
	}

	
	
	/**
	 * 向excel文件工作簿中写入数据
	 * 
	 * @param book
	 *            写入对象
	 * @param list
	 *            数据集合
	 * @param head
	 *            表格头
	 * @param name
	 *            工作簿名称
	 * @param begin
	 *            循环集合开始index
	 * @param end
	 *            循环集合结束index
	 * @param page
	 *            工作的页数（第page页）
	 */
	protected static void setExcelSheet(WritableWorkbook book,
			List<List<String>> list, String[] head, String sheetName, int begin,
			int end, int page) {
		try {
			// 创建工作薄
			WritableSheet sheet = book.createSheet(sheetName + "_" + "第"
					+ (page + 1) + "页", page);
			
			// 循环写入表格头
			for (int i = 0; i < head.length; i++) {
				Label label = new Label(i, 0, head[i]);
				sheet.addCell(label);
				//设置列宽
				sheet.setColumnView(i, 20);
			}
			for (int i = begin; i < end; i++) {
				// 为row赋值
				List<String> column = list.get(i);
				for (int j = 0; j < column.size(); j++) {
					
					/*
					 * 为excel工作簿单元格依次赋值 Label(单元格的x坐标, 单元格的y坐标, 单元格中的值);
					 */
					Label label = new Label(j, i - begin + 1, column.get(j));
					
					sheet.addCell(label);
				}
			}
		} catch (Exception e) {
			//Log4j.log.error(e.getMessage());
		}
	}

}
