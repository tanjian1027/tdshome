package com.tangdi.attachment.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

import javax.imageio.ImageIO;

import sun.misc.BASE64Encoder;

public class ImageUtil {
	
	/**
	 * 给定范围获得随机颜色
	 * @param fc
	 * @param bc
	 * @return
	 */
	public static Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
	
	/**
	 * 生成验证码图片
	 * 使用 ImageIO.write(BufferedImage, "JPEG", response.getOutputStream()) 写入页面img标签中
	 * img中使用 onclick="this.src='/tdweb/auth/genCaptcha.do'" 生成图片
	 * @param captcha 验证码
	 * @param width 图片宽
	 * @param height 图片高
	 * @param mFont 字体（可null）
	 * @param bgColor 背景色（可null）
	 * @return
	 */
	public static BufferedImage generateCaptchaImg (String captcha, int width, int height, Font mFont, Color bgColor) {
		if(mFont == null) {
			mFont = new Font("Times New Roman", Font.BOLD, 18);
		}
		if(bgColor == null) {
			bgColor = ImageUtil.getRandColor(200, 250);
		}
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// 设定背景色
		g.setColor(bgColor);
		g.fillRect(0, 0, width, height);
		g.setFont(mFont);
		// 画边框
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width - 1, height - 1);
		// 随机产生干扰线，使图象中的认证码不易被其它程序探测到
		g.setColor(getRandColor(160, 200));
		// 生成随机类
		Random random = new Random();
		for (int i = 0; i < 155; i++) {
			int x2 = random.nextInt(width);
			int y2 = random.nextInt(height);
			int x3 = random.nextInt(12);
			int y3 = random.nextInt(12);
			g.drawLine(x2, y2, x2 + x3, y2 + y3);
		}
		// 将认证码显示到图象中
		g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
		g.drawString(captcha, 2, 16);
		// 图象生效
		g.dispose();
		return image;
	}
	
	/**
	 * 生成图片base64编码
	 * html中的使用方式：<img src=“data:image/png;base64,xxxxxxxxx”/> 
	 * css中的使用方式：{background:url(data:image/gif;base64,xxxxxxx)}
	 * @param imgPath
	 * @return
	 * @throws Exception 
	 */
	public static String generateImgBase64(String imgPath) throws Exception {
		String content = "";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(imgPath);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			content = new BASE64Encoder().encode(bytes);
		} finally {
			fis.close();
		}
		return content;
	}
	
	/**
	 * 获得图片的宽度
	 * @param imgPath
	 * @return
	 * @throws Exception
	 */
	public static int getImgWidth(String imgPath) {
		int iRes = 0;
		File file = new File(imgPath);
		try {
			BufferedImage bi = ImageIO.read(file);
			int iH = bi.getHeight();
			iRes = bi.getWidth();
		} catch (Exception e) {
			
		}
		return iRes;
	}
	
	/**
	 * 获得图片的高度
	 * @param imgPath
	 * @return
	 * @throws Exception
	 */
	public static int getImgHeight(String imgPath) {
		int iRes = 0;
		File file = new File(imgPath);
		try {
			BufferedImage bi = ImageIO.read(file);
			iRes = bi.getHeight();
		} catch (Exception e) {
			
		}
		return iRes;
	}
}
