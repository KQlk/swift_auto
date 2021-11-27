import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public class SftpConfig {
    private String hostname;
    private Integer port;
    private String username;
    private String password;
    private Integer timeout;
    private Resource privateKey;
    private String remoteRootPath;
    private String fileSuffix;

}
