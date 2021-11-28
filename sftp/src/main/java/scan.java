import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class scan {

    private List<File> files;

    public static List<String> getACKs(String path){

        File file = new File(path);

        if(null == path)    return null;

        String[] file_names = file.list();

        if (null == file_names) return null;

        List<String> ack_files = (List<String>) Arrays.stream(file_names).filter(o -> StringUtils.endsWithIgnoreCase(o,".ack")).limit(50);

        return ack_files;
    }

    public List<String> getTXTs(String path,List<String> ack_files){

        File file = new File(path);

        if(null == path)    return null;

        String[] file_names = file.list();

        if (null == file_names) return null;

        List<String> txt_files = Arrays.asList(file_names).stream().filter(o->{
            if(ack_files.stream().map(p->p.split("_")[1]).equals(o.split("_")[1]))  return true;
            else return false;
        }).collect(Collectors.toList());

        return txt_files;
    }

    public void getTXTs(List<String> acks) {
    }
}
