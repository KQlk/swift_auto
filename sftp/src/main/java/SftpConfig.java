import lombok.Data;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;


public class SftpConfig {
    private String hostname;
    private Integer port;
    private String username;
    private String password;
    private Integer timeout;
    private Resource privateKey;
    private String remoteRootPath;
    private String fileSuffix;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Resource getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(Resource privateKey) {
        this.privateKey = privateKey;
    }

    public String getRemoteRootPath() {
        return remoteRootPath;
    }

    public void setRemoteRootPath(String remoteRootPath) {
        this.remoteRootPath = remoteRootPath;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public SftpConfig(String hostname, Integer port, String username, String password, Integer timeout, Resource privateKey, String remoteRootPath, String fileSuffix) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.timeout = timeout;
        this.privateKey = privateKey;
        this.remoteRootPath = remoteRootPath;
        this.fileSuffix = fileSuffix;
    }
    public SftpConfig(String hostname, Integer port, String username, String password, Integer timeout, String remoteRootPath) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.timeout = timeout;
        this.remoteRootPath = remoteRootPath;
    }
    public SftpConfig() throws IOException {
//        Properties properties = new Properties();
//        properties.load(SftpConfig.class.getClassLoader().getResourceAsStream("sftp.properties"));
//        this.hostname = properties.getProperty("hostname");
//        this.port = (Integer) properties.get("port");
//        this.username = properties.getProperty("username");
//        this.password = properties.getProperty("password");
//        this.timeout = (Integer) properties.get("timeout");
//        this.privateKey = (Resource) properties.get("privateKey");
//        this.remoteRootPath = properties.getProperty("remoteRootPath");
//        this.fileSuffix = properties.getProperty("fileSuffix");
    }

}
