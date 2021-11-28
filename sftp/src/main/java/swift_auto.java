import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class swift_auto {

    /*
    private String hostname;
    private Integer port;
    private String username;
    private String password;
    private Integer timeout;
    private Resource privateKey;
    private String remoteRootPath;
    private String fileSuffix;
     */
    public static void main(String args[]) throws IOException, InterruptedException {

        String scan_path = "";
        String bak_path = "";
        String remote_path = "";

        List<String> acks = null;
        List<String> txts = null;
        //启动扫描
        scan scan = new scan();
        //启动移除
        move move = new move();
        while(true){
            acks = scan.getACKs(scan_path);
            if(acks!=null){
                txts = scan.getTXTs(scan_path,acks);
                if (txts!=null){
                    SFTP sftp = new SFTP();
                    SftpConfig sftpConfig = new SftpConfig();//填参数
                    txts.forEach(txt->{
                        sftp.upload(remote_path,txt,sftpConfig,bak_path);
                    });
                    acks.forEach(ack->{
                        sftp.upload(remote_path,ack,sftpConfig,bak_path);
                    });

                }
            }
            Thread.sleep(5000);
        }


    }
}
