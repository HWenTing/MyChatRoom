

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
/**
 * ������
 * @author HP
 *
 */
public class CatUtil {
	@SuppressWarnings("unused")
	public static String getTimer() {
		//��ȡʱ���ʽ
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
}
