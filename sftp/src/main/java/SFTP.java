import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

@Component
public class SFTP {

    private long count;

    private long relink_count = 0;

    private long sleepTime;

    private static final Logger logger = LoggerFactory.getLogger(SFTP.class);

    public SFTP(SftpConfig sftpConfig) {
    }

    public ChannelSftp connect(SftpConfig sftpConfig){
        ChannelSftp sftp = null;
        try{
            JSch jsch = new JSch();
            jsch.getSession(sftpConfig.getUsername(),sftpConfig.getHostname(), sftpConfig.getPort());
            Session sshSession = jsch.getSession(sftpConfig.getUsername(),sftpConfig.getHostname(), sftpConfig.getPort());
            logger.info("Session created ... UserName=" + sftpConfig.getUsername() + ";host=" + sftpConfig.getHostname() + ";port=" + sftpConfig.getPort());
            sshSession.setPassword(sftpConfig.getPassword());
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking","no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            logger.info("Session connected ...");
            logger.info("Opening Channel ...");
            Channel channel = sshSession.openChannel("sftp");
            sftp = (ChannelSftp) channel;
            logger.info("登录成功");
        } catch (Exception e) {
            try{
                relink_count += 1;
                if (count == relink_count){
                    throw new RuntimeException(e);
                }
                Thread.sleep(sleepTime);
                logger.info("重新连接...");
                connect(sftpConfig);
            } catch (InterruptedException interruptedException) {
                throw new RuntimeException(interruptedException);
            }
            e.printStackTrace();
        }
        return sftp;
    }

    public void upload(String directory,String uploadFile,SftpConfig sftpConfig,String bak_path){
        ChannelSftp sftp = connect(sftpConfig);
        try{
            sftp.cd(directory);
        }catch (SftpException e){
            try{
                sftp.mkdir(directory);
                sftp.cd(directory);

            } catch (SftpException sftpException) {
                throw new RuntimeException("ftp创建文件路径失败:" + directory);
            }
        }
        File file = new File(uploadFile);
        InputStream inputStream = null;
        try{
            inputStream = new FileInputStream(file);
            sftp.put(inputStream,file.getName());
            if (bak_path != null)    new move().moveFile(bak_path,uploadFile);
        } catch (Exception e) {
            throw new RuntimeException("sftp异常"+e);
        }finally {
            disConnect(sftp);
            closeStream(inputStream,null);
        }
    }


    public void download(String diretory,String downloadFile,String saveFile,SftpConfig sftpConfig){
        OutputStream output = null;
        try{
            File localDirFile = new File(saveFile);
            if (!localDirFile.exists()){
                localDirFile.mkdirs();
            }
            if (logger.isInfoEnabled()) {
                logger.info("开始获取远程文件:[{}]---->[{}]",new Object[]{diretory,saveFile});
            }
            ChannelSftp sftp = connect(sftpConfig);
            sftp.cd(diretory);

        }catch(Exception e){
            if (logger.isInfoEnabled()) {
                logger.info("文件下载出现异常，[{}]",e);
            }
            throw new RuntimeException("文件下载出现异常，[{}]",e);
        }finally {
            closeStream(null,output);
        }
    }

    public void getFileDir(String remoteFilePath,String localDirPath,SftpConfig sftpConfig) throws Exception{
        File localDirFile = new File(localDirPath);
        if (!localDirFile.exists()) {
            localDirFile.mkdirs();
        }
        if (logger.isInfoEnabled()) {
            logger.info("sftp文件服务器文件夹[{}],下载到本地目录[{}]",new Object[]{remoteFilePath,localDirFile});
        }
        ChannelSftp channelSftp = connect(sftpConfig);
        Vector<ChannelSftp.LsEntry> lsEntries = channelSftp.ls(remoteFilePath);
        if (logger.isInfoEnabled()) {
            logger.info("远程目录下的文件为[{}]",lsEntries);
        }
        for(ChannelSftp.LsEntry entry : lsEntries){
            String fileName = entry.getFilename();
            if (checkFileName(fileName)){
                continue;
            }
            String remoteFileName = getRemoteFilePath(remoteFilePath,fileName);
            channelSftp.get(remoteFileName,localDirPath);
        }
        disConnect(channelSftp);
    }

    private void closeStream(InputStream inputStream,OutputStream outputStream){
        if (outputStream != null) {
            try{
                outputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        if (inputStream != null){
            try{
                inputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private boolean checkFileName(String fileName){
        if (".".equals(fileName) || "..".equals(fileName)){
            return true;
        }
        return false;
    }

    private String getRemoteFilePath(String remoteFilePath,String fileName){
        if (remoteFilePath.endsWith("/")){
            return remoteFilePath.concat(fileName);
        }else{
            return remoteFilePath.concat("/").concat(fileName);
        }
    }

    public void delete(String directory,String deleteFile, ChannelSftp sftp){
        try{
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> listFiles(String directory, SftpConfig sftpConfig) throws SftpException {
        ChannelSftp sftp = connect(sftpConfig);
        List fileNameList = new ArrayList();
        try {
            sftp.cd(directory);
        } catch (SftpException e) {
            return fileNameList;
        }
        Vector vector = sftp.ls(directory);
        for (int i = 0; i < vector.size(); i++) {
            if (vector.get(i) instanceof ChannelSftp.LsEntry) {
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) vector.get(i);
                String fileName = lsEntry.getFilename();
                if (".".equals(fileName) || "..".equals(fileName)) {
                    continue;
                }
                fileNameList.add(fileName);
            }
        }
        disConnect(sftp);
        return fileNameList;
    }

    /**
     * 断掉连接
     */
    public void disConnect(ChannelSftp sftp) {
        try {
            sftp.disconnect();
            sftp.getSession().disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SFTP(long count, long sleepTime) {
        this.count = count;
        this.sleepTime = sleepTime;
    }

    public SFTP() {

    }


}
