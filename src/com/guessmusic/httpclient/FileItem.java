package com.guessmusic.httpclient;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 文件元数据。 dongchao
 */
public class FileItem {
	private String fieldName;
	private String fileName;
	private byte[] content;
	private File file;
	private String suffix;

	/**
	 * 基于本地文件的构造器。
	 * 
	 * @param file
	 *            本地文件
	 */
	public FileItem(String fieldName, File file) {
		this.fieldName = fieldName;
		this.file = file;
		this.fileName = file.getName();
		int i = fileName.lastIndexOf(".");
		if (i != -1) {
			this.suffix = fileName.substring(i);
		}
	}

	/**
	 * 基于文件绝对路径的构造器。
	 * 
	 * @param filePath
	 *            文件绝对路径
	 */
	public FileItem(String fieldName, String filePath) {
		this(fieldName, new File(filePath));
	}

	/**
	 * 基于文件名和字节流的构造器。
	 * 
	 * @param fileName
	 *            文件名
	 * @param content
	 *            文件字节流
	 */
	public FileItem(String fieldName, String fileName, String suffix, byte[] content) {
		this.fieldName = fieldName;
		this.fileName = fileName;
		this.suffix = suffix;
		this.content = content;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFileName() {
		return this.fileName;
	}

	public byte[] getContent() throws IOException {
		if (this.content == null && this.file != null && this.file.exists()) {
			BufferedInputStream in = null;
			ByteArrayOutputStream out = null;
			try {
				in = new BufferedInputStream(new FileInputStream(this.file));
				out = new ByteArrayOutputStream(8192);
				int ch;
				while ((ch = in.read()) != -1) {
					out.write(ch);
				}
				this.content = out.toByteArray();
			} finally {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			}
		}
		return this.content;
	}

	public String getSuffix() {
		return suffix;
	}

}
