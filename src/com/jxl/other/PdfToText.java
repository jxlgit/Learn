package com.jxl.other;

import java.io.FileWriter;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;

/**
 * 
 * @Description:   PDF转txt
 * @author: jxl     
 * @date:   2019年11月26日 下午4:57:31   
 * @version V1.0
 */
public class PdfToText {
	

	public static void main(String[] args) {

		// 创建PdfDocument实例
		PdfDocument doc = new PdfDocument();

		// 加载PDF文件
		doc.loadFromFile("C:\\log\\111.pdf");

		StringBuilder sb = new StringBuilder();

		PdfPageBase page;

		// 遍历PDF页面，获取文本
		for (int i = 0; i < doc.getPages().getCount(); i++) {
			page = doc.getPages().get(i);
			sb.append(page.extractText(false));
		}
		
		FileWriter writer;

		try {
			// 将文本写入文本文件
			writer = new FileWriter("C:\\log\\222.txt");
			writer.write(sb.toString());
			writer.flush();
			System.out.println(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		doc.close();
	}
	
	
	
}
