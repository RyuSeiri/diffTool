
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CompareTool {
	public static void main(String[] args) throws Exception {
		Properties readProperty = ReadProperty();
		String path1 = readProperty.getProperty("path1") == null ? null
				: readProperty.getProperty("path1").toString();
		String path2 = readProperty.getProperty("path2") == null ? null
				: readProperty.getProperty("path2").toString();
		if (isEmpty(path1) || isEmpty(path2)) {
			return;
		}
		String diffFolder = System.getProperty("user.dir") + "/diffForder/";
		File dest = new File(diffFolder);
		if (!dest.exists())
			dest.mkdirs();
		Map<String, FileModel> fileMap1 = getFiles(path1);
		Map<String, FileModel> fileMap2 = getFiles(path2);
		List<FileModel> result = new ArrayList<FileModel>();
		result.addAll(compareFile(fileMap1, fileMap2));
		result.addAll(compareFile(fileMap2, fileMap1));
		if (result.size() <= 0) {
			return;
		}
		for (FileModel fileModel : result) {
					+ fileModel.getFile().getAbsolutePath());
			File destFile = new File(diffFolder
					+ fileModel.getFile().getAbsolutePath()
							.replaceAll("\\\\", "/")
							.replaceAll(addSlantLine(path2), ""));
			copyFile(fileModel.getFile(), destFile);
		}
	}

	private static List<FileModel> compareFile(Map<String, FileModel> fileMap1,
			Map<String, FileModel> fileMap2) {
		List<FileModel> list = new ArrayList<FileModel>();
		for (String key : fileMap1.keySet()) {
			FileModel fileModel1 = fileMap1.get(key);
			FileModel fileModel2 = fileMap2.get(key);
			if (fileModel2 == null) {
				list.add(fileModel1);
				continue;
			}
			if (fileModel1.getFile().isFile()
					&& !fileModel1.getMd5().equals(fileModel2.getMd5()))
				list.add(fileModel1);
		}
		return list;
	}

	private static Map<String, FileModel> getFiles(String path)
			throws Exception {
		Map<String, FileModel> map = new HashMap<String, FileModel>();
		File folder = new File(path);
		String diffType = ReadProperty().getProperty("diffType");
		Object[] files = getFileList(folder, diffType).toArray();
		Arrays.sort(files);
		for (Object object : files) {
			File file = (File) object;
			String key = file.getAbsolutePath().replaceAll("\\\\", "/")
					.replaceAll(path, "");
			String md5 = "";
			if (file.isFile())
				md5 = MD5.getMD5(file);
			map.put(key, new FileModel(file, md5));
		}
		return map;
	}

	private static List<File> getFileList(File folder, String diffType) {
		List<File> list = new ArrayList<File>();
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory())
				list.addAll(getFileList(file, diffType));
			if (file.getAbsolutePath().contains(
					isEmpty(diffType) ? ".java" : diffType))
				list.add(file);
		}
		return list;
	}

	private static Properties ReadProperty() throws Exception {
		File pf = new File(System.getProperty("user.dir")
				+ "/config.properties");
		InputStream inpf = new FileInputStream(pf);
		Properties p = new Properties();
		p.load(new InputStreamReader(inpf, "UTF-8"));
		return p;
	}

	private static boolean isEmpty(String string) {
		return !(string != null && !"".equals(string));
	}

	private static void copyFile(File source, File dest) throws Exception {
		File tempFile = new File(dest.getPath().substring(0,
				dest.getPath().lastIndexOf("\\") + 1));
		if (!tempFile.exists())
			tempFile.mkdirs();
		dest.createNewFile();
		FileInputStream inputStream = new FileInputStream(source);
		FileOutputStream outStream = new FileOutputStream(dest);
		byte[] b = new byte[1024];
		int n = 0;
		while ((n = inputStream.read(b)) != -1) {
			outStream.write(b, 0, n);
		}
		inputStream.close();
		outStream.close();
	}

	private static String addSlantLine(String path) {
		String result = "";
		if (path.endsWith("/") || path.endsWith("\\"))
			result = path;
		else
			result = path + "/";
		return result;
	}
}
