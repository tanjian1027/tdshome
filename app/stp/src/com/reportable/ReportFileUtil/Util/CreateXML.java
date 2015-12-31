package com.reportable.ReportFileUtil.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import tangdi.atc.Atc;
import tangdi.engine.context.Etf;
import tangdi.expr.functions.TdExpBasicFunctions;

public class CreateXML {

	// 无参数构造方�?
	public CreateXML() {
	}

	/**
	 * 支付机构可疑交易报文生成
	 * 
	 * @param rbif
	 *            父级�?
	 * @param filename
	 * @param type
	 *            生成报文类型 3.2.1.1可疑交易（行为）报告报文类型分为3种： 普�?报文=NPS�?重发报文=RPS�?
	 *            纠错报文=CPS�?3.2.1.2补充交易报告报文类型分为3种： 普�?报文=NPC
	 *            补报报文=APC�?纠错报文=CPC�?
	 * @return
	 * @throws Exception
	 */
	public String createNPS(RBIF rbif, String type, String FileId)
			throws Exception {
		// 使用DocumentHelper类创建一个文档实�?
		Document document = DocumentHelper.createDocument();

		// 创建根元�?
		Element ElementPSTR = document.addElement("PSTR");

		// 创建基本信息节点
		Element ElementRBIF = ElementPSTR.addElement(getClassName(rbif
				.getClass().getName()));
		Field[] fields = getField(rbif);
		// 获取基本信息下的�?��属�?
		String fieldName = null; // 属�?名称
		String fieldType = null; // 属�?类型
		Object fieldValue = null; // 属�?�?
		for (Field f : fields) {

			fieldName = f.getName();
			fieldType = f.getType().getName();
			fieldValue = getFieldValue(rbif, fieldName, fieldType);
			// 属�?只有两种类型�?�?��是String �?��是List
			if ("java.lang.String".equals(fieldType)) {
				// 创建普�?基本信息属�?节点
				Element ElementString = ElementRBIF.addElement(fieldName);
				// 设置普�?属�?�?
				ElementString.setText(fieldValue.toString());
			} else if ("java.util.List".equals(fieldType)) {
				// 获取 可疑主体和可疑交易信�?列表
				List<Object> list = (List) fieldValue;
				// 添加父节�?
				Element Elements = ElementPSTR.addElement(fieldName);
				// 添加节点
				for (int i = 0; i < list.size(); i++) {
					Object objClass = list.get(i);
					Element element = Elements.addElement(getClassName(objClass
							.getClass().getName()));
					element.addAttribute("seqno", String.valueOf(i + 1));
					Field[] fieldList = getField(objClass);
					for (Field fl : fieldList) {
						fieldName = fl.getName();
						fieldType = fl.getType().getName();
						fieldValue = getFieldValue(objClass, fieldName,
								fieldType);
						if ("java.lang.String".equals(fieldType)) {
							// 创建普�?基本信息属�?节点
							Element ElementString = element
									.addElement(fieldName);
							// 设置普�?属�?�?
							ElementString.setText(fieldValue.toString());
						} else {
							Object objClass2 = fieldValue;
							Element element2 = element
									.addElement(getClassName(objClass2
											.getClass().getName()));
							Field[] fieldList2 = getField(objClass2);
							for (Field fl2 : fieldList2) {
								fieldName = fl2.getName();
								fieldType = fl2.getType().getName();
								fieldValue = getFieldValue(objClass2,
										fieldName, fieldType);
								if ("java.lang.String".equals(fieldType)) {
									// 创建普�?基本信息属�?节点
									Element ElementString2 = element2
											.addElement(fieldName);
									// 设置普�?属�?�?
									ElementString2.setText(fieldValue
											.toString());
								}
							}
						}
					}
				}
			}
		}
		String filename = getFileName(rbif.getRFSG(),type, FileId);
		// 将创建的XML文档存盘
		try {
			XMLWriter output = null;
			// 创建�?��格式化对�?
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 使用TAB缩进
			format.setIndent("\t");
			format.setEncoding("gb18030");
			// 创建�?��XMLWriter对象
			output = new XMLWriter(new FileOutputStream(new File(filename)),
					format);
			// 将XML文档输出
			output.write(document);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		// 返回XML文档对象
		return filename;
	}

	/**
	 * 获取文件名称
	 * 
	 * @return
	 */
	public String getFileName(String pc,String type, String FileId) {

		// 报告机构 机构代码
		String organCode = "";
		// 报�?批次�?
		String sendCode = "";
		// xml存放路径
		String XmlAddress = "";
		ResourceBundle resb = ResourceBundle.getBundle("ReportFile");
		XmlAddress = resb.getString("XmlAddress");
		String basepath = TdExpBasicFunctions.GETCURAPPHOME();
		organCode = resb.getString("organCode");
		String createFileTime = new SimpleDateFormat("yyyyMMdd")
				.format(new Date());
		if ("added".equals(type)) {
			String filename = "";
			String sql = "select file_name from stp_reportfile_log where id = "
					+ FileId.replaceAll(";", "");
			int req = Atc.ReadRecord(sql);
			if (0 == req) {
				String fname = Etf.getChildValue("file_name");
				String[] fnames = fname.split("\\.");
				String sql2 = "select count(id) reportFileNames from stp_reportfile_log where file_name like '%"
						+ fnames[0].substring(3) + "%'";
				int req2 = Atc.ReadRecord(sql2);
				if (0 == req2) {
					String sendCode2 = "0000"
							+ Etf.getChildValue("reportFileNames");
					filename = fnames[0].substring(3) + "-"
							+ sendCode2.substring(sendCode2.length() - 4);
				}
			}
			type = "NPC";
			System.out
					.println(basepath + XmlAddress + type + filename + ".xml");
			return basepath + XmlAddress + type + filename + ".xml";
		} else {
			String sql = "select count(id)+1 reportFileNum from stp_reportfile_log where file_type != 'added' and create_file_time between '"
					+ createFileTime
					+ "000000' and '"
					+ createFileTime
					+ "235959'";
			int req = Atc.ReadRecord(sql);
			if (req == 0) {
				sendCode = "0000" + Etf.getChildValue("reportFileNum");
				sendCode = sendCode.substring(sendCode.length() - 4);
			} else {
				sendCode = "0001";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String date = sdf.format(new Date());
			type = "NPS";
			pc = "0000"+pc; // 当日报送批次 默认为第一次
			return basepath + XmlAddress + type + organCode + "-" + date + "-"
					+ pc.substring(pc.length()-4) + "-" + sendCode + ".XML";
		}
	}

	/**
	 * 根据对象获取对象内的属�?集合
	 * 
	 * @param obj
	 * @return
	 */
	public static Field[] getField(Object obj) {
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		return fields;
	}

	/**
	 * 
	 * @param 获取类名
	 * @return
	 */

	public String getClassName(String className) {
		return className.substring(className.lastIndexOf(".") + 1);
	}

	/**
	 * 获取属�?�?
	 * 
	 * @param obj
	 *            �?
	 * @param fieldName
	 *            属�?�?
	 * @return 属�?�?
	 * @throws Exception
	 */
	public static Object getFieldValue(Object obj, String fieldName, String fieldType)
			throws Exception {
		Object object = null;
		Method m = obj.getClass().getMethod("get" + fieldName);
		object = m.invoke(obj); // 调用getter方法获取属�?�?
		object = object == null || object.equals("") ? "@I" : object;
		return object;
	}

	// 将文本串转换成XML文档并存�?
	public Document create(String filename, String text) throws Exception {
		// 使用DocumentHelper类将文本串转换为XML文档
		Document document = DocumentHelper.parseText(text);

		// 将创建的XML文档存盘
		try {
			XMLWriter output = null;
			// 创建�?��格式化对�?
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 使用TAB缩进
			format.setIndent("\t");
			// 创建�?��XMLWriter对象
			output = new XMLWriter(new FileOutputStream(new File(filename)),
					format);
			// 将XML文档输出
			output.write(document);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		// 返回XML文档对象
		return document;
	}

	// 在指定的文件中，将指定Xpath下的指定元素的文本�?进行替换
	public Document update(String filename, String xpath, String element,
			String srcText, String tagText) throws Exception {
		// 读取指定的XML文件，返回内存XML文档对象
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new File(filename));

		// 将指定的Xpath的元素文本�?进行替换
		List list = doc.selectNodes(xpath);
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element el = (Element) it.next();
			Iterator it1 = el.elementIterator(element);
			while (it1.hasNext()) {
				Element titleElement = (Element) it1.next();
				if (titleElement.getText().equals(srcText))
					titleElement.setText(tagText);
			}
		}
		// 将创建的XML文档存盘
		try {
			XMLWriter output = null;
			// 创建�?��格式化对�?
			OutputFormat format = OutputFormat.createPrettyPrint();
			// 使用TAB缩进
			format.setIndent("\t");
			// 创建�?��XMLWriter对象
			output = new XMLWriter(new FileOutputStream(new File(filename)),
					format);
			// 将XML文档输出
			output.write(doc);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		// 返回XML文档对象
		return doc;
	}

}