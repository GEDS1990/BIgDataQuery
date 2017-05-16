package cn.edu.bupt.springmvc.core.task;

import cn.edu.bupt.springmvc.core.util.SSHClient;
import cn.edu.bupt.springmvc.core.util.TableUtil;
import cn.edu.bupt.springmvc.web.dao.*;
import cn.edu.bupt.springmvc.web.model.*;
import com.lmk.database_schema.DatabaseSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by limingkun on 2017/5/6.
 * 做两件事情
 * 1 将关系数据库中的数据导入到Hbase中   生成的表名由表名+时间组成
 * 2 在hive中建立相应的外部表
 */


public class RelationalDBLoadTask implements Callable<List<String>>{

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private DataSourceMapper dataSourceDao;
    private FolderTableMapper folderTableDao;
    private RelationalDatabaseInfoMapper relationalDatabaseInfoDao;
    private WorkTableMapper workTableDao;
    private WorkTableLogMapper workTableLogDao;
    private SyncTaskMapper syncTaskDao;

    private RelationalDatabaseInfo dbInfo;
    private DataSource dataSource;
    private SyncTask syncTask;
    private FolderTable folderTable;
    private String[] rowkeys;

    private String importType;

    private Date dNow;
    private SimpleDateFormat ft;

    private DatabaseSchema dbSchema;

    private List<String> hbaseTablenames;

    public RelationalDBLoadTask(RelationalDatabaseInfo dbInfo, DataSource ds,
                                SyncTask syncTask, FolderTable folderTable, String[] rowkeys,
                                DataSourceMapper dataSourceDao, FolderTableMapper folderTableDao,
                                RelationalDatabaseInfoMapper relationalDatabaseInfoDao, SyncTaskMapper syncTaskDao,
                                WorkTableMapper workTableDao, WorkTableLogMapper workTableLogDao, DatabaseSchema dbschema,
                                String importType) {
        this.dbInfo = dbInfo;
        this.dataSource = ds;
        this.syncTask = syncTask;
        this.folderTable = folderTable;
        this.rowkeys = rowkeys;
        this.dataSourceDao = dataSourceDao;
        this.folderTableDao = folderTableDao;
        this.relationalDatabaseInfoDao = relationalDatabaseInfoDao;
        this.syncTaskDao = syncTaskDao;
        this.workTableDao = workTableDao;
        this.workTableLogDao = workTableLogDao;
        this.dbSchema = dbschema;
        this.importType = importType;
    }

    @Override
    public List<String> call() throws IOException, SQLException {

        List<String> sshSqoopResultList = new ArrayList<String>();
//        List<String> sshHiveResultList = new ArrayList<String>();
        hbaseTablenames = new ArrayList<String>();
        List<String> tables = TableUtil.convertStrToTableList(dataSource.getTableNames());
        //可能有多个表同时载入
        for (int i = 0;i<rowkeys.length;i++
             ) {
            //获得数据载入执行命令
            String execStr = "";
            if (importType.toLowerCase().trim().equals("import_to_hbase_overwrite")){
                execStr = generateImportToHbaseExecStr(tables.get(i).trim(),rowkeys[i].trim());
            }else if(importType.toLowerCase().trim().equals("import_to_hive_overwrite")){
                execStr = generateImportToHiveOverwriteExecuStr(tables.get(i).trim());
            }else if (importType.toLowerCase().trim().equals("import_to_hive_append")){
                execStr = generateImportToHiveAppendExecuStr(tables.get(i).trim());
            }

            //执行数据载入命令，这是同步的
            String runResult = null;
            try {
                runResult = SSHClient.PERMSSH.shell.exec(execStr);
                System.out.println(runResult);
                logger.info(runResult);
                //将该条命令的载入结果加入到结果集中
                sshSqoopResultList.add(runResult);
            } catch (IOException e) {
                //如果命令执行失败
                e.printStackTrace();
                //采取的策略是只要有一张表加载失败，后面的表都不加载了
                throw new IOException("表"+tables.get(i).trim()+"载入失败",e);
            }
            //如果命令执行成功//

            //执行web工程中数据源 的 添加工作
            generateRelatedSystemInfo(tables.get(i).trim());
            /**    放弃将数据导入hbase然后再导入hive的方案，采用直接导入hive的方案    **/
            //在载入的hbase表的基础上构建hive外表
//            generateHiveTable(tables.get(i).trim(),hbaseTablenames.get(i).trim());
        }

        return sshSqoopResultList;
    }

    private String generateImportToHiveAppendExecuStr(String trim) {
        //TODO
        return null;
    }

    private void generateHiveTable(String dbTableName, String hbaseTableName) throws SQLException {


//        dbSchema.getTableSchema(tableName);
    }

    private void generateRelatedSystemInfo(String tableName) {

        relationalDatabaseInfoDao.insertSelective(dbInfo);
        dataSource.setDataId(dbInfo.getId());
        dataSource.setIsDeleted(false);
        dataSourceDao.insertSelective(dataSource);
        syncTask.setDatasourceId(dataSource.getId());
        syncTaskDao.insertSelective(syncTask);

        WorkTable wt = new WorkTable();
        wt.setDatasourceId(dataSource.getId());wt.setIsDeleted(false);
        wt.setFolderId(folderTable.getId());
        wt.setLastChangeTime(new Date());wt.setName(tableName + ft.format(dNow));
//        wt.setRemark(remark);wt.setTags(tags);wt.setVersion(1);
        wt.setSolidTableName(tableName);
        workTableDao.insertSelective(wt);

        WorkTableLog log = new WorkTableLog();
        log.setOperateTime(new Date());log.setType("CREATE");log.setWorkTableId(wt.getId());
        workTableLogDao.insertSelective(log);

    }

    private String generateImportToHbaseExecStr(String tableName, String rowkey) {
        /**
         * import
         --connect
         jdbc:mysql://111.207.243.70:3606/video_information_mining
         --username
         root
         --password
         'cYz#P@ss%w0rd$868'
         --null-string
         ''
         --table
         iqiyi_movie_info
         --columns
         "id,url,video_name,video_channel,video_area,video_type,language,main_actor,video_director,brief_intro,play_counts"
         --hbase-table
         iqiyi_movie_info
         --hbase-create-table
         --column-family
         o
         --hbase-row-key
         id
         */
        StringBuilder sb = new StringBuilder("sqoop import \"-Dorg.apache.sqoop.splitter.allow_text_splitter=true\" ");

        dNow = new Date( );
        ft = new SimpleDateFormat ("yyyy_MM_dd_hh_mm_ss");

        hbaseTablenames.add(tableName + ft.format(dNow));
//        System.out.println("Current Date: " + ft.format(dNow));
        sb.append("--connect").append(" ")
                .append("jdbc:mysql://"+dbInfo.getIpHost()+":"+dbInfo.getPort()+"/"+dbInfo.getDbName()).append(" ")
                .append("--username ").append(dbInfo.getUsername()).append(" ")
                .append("--password ").append("\'"+dbInfo.getPassword()+"\'").append(" ")
                .append("--null-string ").append("\' \'").append(" ")
                .append("--table ").append(tableName).append(" ")
//                .append("--columns ").append(generateColumns(columns)).append(" ")
                .append("--hbase-table ").append("bigquery:"+tableName + ft.format(dNow)).append(" ")
                .append("--hbase-create-table").append(" ")
                .append("--column-family ").append("info").append(" ")
                .append("--hbase-row-key ").append(rowkey);

        return sb.toString();
    }

    private String generateImportToHiveOverwriteExecuStr(String tableName){
        /**
         * import
         --connect
         jdbc:mysql://10.108.209.86:3306/video_information_mining
         --username
         root
         --password
         'lmk@mysql'
         --null-string
         ''
         --table
         video_page_info
         --hive-import
         --hive-database
         bigquery
         --hive-overwrite
         --create-hive-table
         --hive-table
         video_page_info
         #--fields-terminated-by '\t'
         --delete-target-dir
         */
        StringBuilder sb = new StringBuilder("sqoop import  ");

        dNow = new Date( );
        ft = new SimpleDateFormat ("yyyy_MM_dd_hh_mm_ss");

        hbaseTablenames.add(tableName + ft.format(dNow));
//        System.out.println("Current Date: " + ft.format(dNow));
        sb.append("--connect").append(" ")
                .append("jdbc:mysql://"+dbInfo.getIpHost()+":"+dbInfo.getPort()+"/"+dbInfo.getDbName()).append(" ")
                .append("--username ").append(dbInfo.getUsername()).append(" ")
                .append("--password ").append("\'"+dbInfo.getPassword()+"\'").append(" ")
                .append("--null-string ").append("\' \'").append(" ")
                .append("--table ").append(tableName).append(" ")
                .append("--hive-import ")
                .append("--hive-database ").append("bigquery ")
                .append("--hive-overwrite ")
                .append("--create-hive-table ")
                .append("--hive-table ").append(tableName+ ft.format(dNow)).append(" ")
                .append("--delete-target-dir ");

        return sb.toString();
    }
}
