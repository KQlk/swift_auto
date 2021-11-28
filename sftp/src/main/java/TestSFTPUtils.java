import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TestSFTPUtils {
    private static final Logger logger = LoggerFactory.getLogger(TestSFTPUtils.class);

    public static void main(String[] args) {
        SFTP ftp = new SFTP(3, 6000);
        SftpConfig sftpConfig = new SftpConfig("10.0.155.55", 22, "test", "test", 1000, "/opt/bdepfile/bdp/tset/20190425");
        try {
            List<String> list = ftp.listFiles("/opt/bdepfile/bdp/pucms/20190108", sftpConfig);
            logger.info("文件上传下载详情"  ,new Object[]{list});
        } catch (SftpException e) {
            logger.error("文件上传下载异常:[{}]" ,new Object[]{e});
        }
    }
}
