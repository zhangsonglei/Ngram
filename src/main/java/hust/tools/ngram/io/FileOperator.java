package hust.tools.ngram.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * <ul>
 *<li>Description: 读写文件
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年6月20日
 *</ul>
 */
public class FileOperator {

	/**
     * 全角转半角
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {
    	char c[] = input.toCharArray();
    	for (int i = 0; i < c.length; i++) {
    		if (c[i] == '\u3000') 
    			c[i] = ' ';
    		else if (c[i] > '\uFF00' && c[i] < '\uFF5F') 
    			c[i] = (char) (c[i] - 65248);
    	}
         
        return new String(c);
    }
	
	/**
	 * 按行读取文件存放在List中（一次读完）
	 * @param path	文件路径
	 * @return		存放行的List
	 * @throws IOException
	 */
	public static List<String> readFileToList(String path) throws IOException {
		List<String> lines = new LinkedList<>();
		File file = new File(path);
		
		if(file.isFile() && file.exists()) {
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(inputStreamReader);
			
			String line = "";
			while((line = reader.readLine()) != null) {
				line = ToDBC(line).replaceAll("\\s", "").trim();
				if(!line.equals(""))
					lines.add(line);
			}
			reader.close();
		}else {
			System.err.println("File:\""+path+"\" read failed!");
		}
		
		return lines;	
	}
	
	/**
	 * 将数据写入文件中
	 * @param strings	待写入的数据
	 * @param path		写入路径
	 * @throws IOException
	 */
	public static void writeFile(List<String> strings, String path) throws IOException {
		File file = new File(path);
		
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file),"utf-8");
		BufferedWriter writer = new BufferedWriter(outputStreamWriter);
		
		for(String string : strings) {
			writer.write(string);
			writer.newLine();
		}
		writer.close();
	}
	
	/**
	 * 将数据写入文件中
	 * @param string	待写入的数据
	 * @param path		写入路径
	 * @throws IOException
	 */
	public static void writeFile(String string, String path) throws IOException {
		File file = new File(path);
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file),"utf-8");
		BufferedWriter writer = new BufferedWriter(outputStreamWriter);
		writer.write(string);
		writer.close();
	}
}
