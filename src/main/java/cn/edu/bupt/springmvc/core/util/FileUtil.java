package cn.edu.bupt.springmvc.core.util;

/**
 * @author YaoZhidong
 * @version 1.0
 * @created 2017/5/14
 */
public class FileUtil {

    public static String getFileType(String fileName){
        String[] splits = fileName.split(".");
        if (splits[splits.length-1].trim().equals("xls")||splits[splits.length-1].trim().equals("xlsx")){
            return "excel";
        }
        if (splits[splits.length-1].trim().equals("csv")){
            return "csv";
        }
        return "csv";
    }

    public static String getFileNamePrefix(String fullName){
        int index = fullName.lastIndexOf('.');
        String namePerfix = fullName.substring(0,index);
        String nameSuffix = fullName.substring(index+1);
        return namePerfix;
    }

    public static String getFileNameSuffix(String fullName){
        int index = fullName.lastIndexOf('.');
        String namePerfix = fullName.substring(0,index);
        String nameSuffix = fullName.substring(index+1);
        return nameSuffix;
    }
}
