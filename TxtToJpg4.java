package export;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

//备注：请说明情况
public class TxtToJpg4 {
	private String filePath = ".";// 源文件位置
	private String[] pattern;// 文件查找的正则
	private Pattern[] regPattern;
	private String enc = "gbk";

	private String saveFolder = ".";// 图片保存目录

	public TxtToJpg4(String path, String[] pattern) {
		this.filePath = path;
		this.pattern = pattern;
		init();
	}

	public TxtToJpg4(String[] pattern) {
		this.pattern = pattern;
		init();
	}
	//初始化
	public void init() {
		this.regPattern = getPatterns(pattern);
	}

	protected Pattern[] getPatterns(String[] p) {
		Pattern[] pa = new Pattern[p.length];
		for (int i = 0; i < p.length; i++) {
			pa[i] = Pattern.compile(toReg(p[i]));
		}
		return pa;
	}

	public void start() {
		listFile(new File(this.filePath));
	}

	public void start(String fileName) {
		listFile(new File(fileName));
	}

	public void listFile(File file) {
		if (file.isDirectory()) {
			// System.out.println(file.toString());
			File[] fa = file.listFiles();
			if (fa.length > 0) {
				for (int i = 0; i < fa.length; i++) {
					listFile(fa[i]); // 递归调用
				}
			}
		} else {
			String name = file.getName();
			for (Pattern p : this.regPattern) {
				Matcher match = p.matcher(name);
				if (match.matches()) {
					exportToJpg(file);
					// break;
					return;
				}
			}

		}
	}

	public void exportToJpg(File file) {
		int f_width = 19;// 行宽
		final int MAX_LINES = 200;
		int height = 0;
		final int MAX_HEIGHT = f_width * MAX_LINES + 15;
		int width = 800;

		int before_font = 11;// 每行前 空白像素

		BufferedImage image = null;

		// 输出数字
		BufferedReader br = null;

		try {
			int lines = 0;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), enc));

			String str = null;
			br.mark(1024 * 8);
			 
			while (br.readLine() != null)
				lines++;

			height = f_width * lines + 15;
			try {
				br.reset();
			} catch (Exception e) {
				System.out
						.println("文件 " + file.getAbsolutePath() + "出现reset问题,重新打开");
				// e.printStackTrace();
				br.close();
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), enc));
			}
			if (height > MAX_HEIGHT) {
				height = MAX_HEIGHT;
			}

			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			Graphics gra = image.getGraphics();
			// 设置背景色
			gra.setColor(Color.WHITE);

			gra.fillRect(1, 1, width, height);
			// 设置字体色
			gra.setColor(Color.red);

			gra.setFont(new Font("宋体", Font.PLAIN, 16));

			int i = 0;
			int k = 1;
			String jpgFile = file.getName();
			while ((str = br.readLine()) != null) {
				// System.out.println(str);
				gra.drawString(str, before_font, f_width * ((i++) + 1));
				if (i >= MAX_LINES) {

					OutputStream toClient = new FileOutputStream(new File(
							this.saveFolder, jpgFile + (k++) + ".jpg"));
					JPEGImageEncoder encoder = JPEGCodec
							.createJPEGEncoder(toClient);

					encoder.encode(image);

					toClient.close();

					image.flush();

					gra.clearRect(1, 1, width, height);
					// gra.setColor(Color.WHITE);
					gra.setColor(Color.WHITE);

					gra.fillRect(1, 1, width, height);
					// 设置字体色
					gra.setColor(Color.red);

					i = 0;
				}
			}

			if (i < MAX_LINES) {
				OutputStream toClient = new FileOutputStream(jpgFile + (k++)
						+ ".jpg");
				JPEGImageEncoder encoder = JPEGCodec
						.createJPEGEncoder(toClient);

				encoder.encode(image);

				toClient.close();

			}
			gra.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static String toReg(String s) {
		// s = s.replace('.', '#');
		// s = s.replaceAll("#", "\\\\.");
		// s = s.replace('*', '#');
		// s = s.replaceAll("#", ".*");
		// s = s.replace('?', '#');
		// s = s.replaceAll("#", ".?");
		// s = "^" + s + "$";
		// System.out.println(s);
		// return s;
		String s2 = "";
		char c = 0;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			switch (c) {
			case '.':
				s2 += "\\.";
				break;
			case '*':
				s2 += ".*";
				break;
			case '?':
				s2 += ".?";
				break;
			default:
				s2 += c;
			}
		}
		s2 = "^" + s2 + "$";
		// System.out.println(s2);
		return s2;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {// \\src\\cn\\Computer.java
		TxtToJpg4 jpg = new TxtToJpg4(
				"I:\\haiworksp\\java10_u1_3\\src\\u1proj", new String[] {
						"*.java", "*.jsp", "*.xml" });
		jpg.start();
	}

	public void setEnc(String enc) {
		this.enc = enc;
	}

}
