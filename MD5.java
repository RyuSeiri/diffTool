
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5 {

	public static String getMD5(File file) throws Exception {
		BigInteger bigInteger = null;
		FileInputStream fil = new FileInputStream(file);
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] b = new byte[1024];
		int n;
		while ((n = fil.read(b)) != -1) {
			messageDigest.update(b, 0, n);
		}
		bigInteger = new BigInteger(1, messageDigest.digest());
		fil.close();
		return bigInteger.toString(16);
	}

}
