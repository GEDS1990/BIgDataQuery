package cn.edu.bupt.springmvc.web.service.impl;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import javax.annotation.Resource;

import cn.edu.bupt.springmvc.core.task.RelationalDBLoadTask;
import cn.edu.bupt.springmvc.core.util.FileUtil;
import cn.edu.bupt.springmvc.web.dao.*;
import cn.edu.bupt.springmvc.web.model.FileInfo;
import cn.edu.bupt.springmvc.web.model.WorkTable;
import cn.edu.bupt.springmvc.web.service.FolderService;
import com.lmk.csv_schema.CSVFile;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.lmk.database_schema.DatabaseSchema;
import com.lmk.database_schema.schema.TableSchema;

import cn.edu.bupt.springmvc.core.util.TableUtil;
import cn.edu.bupt.springmvc.web.model.DataSource;
import cn.edu.bupt.springmvc.web.model.FolderTable;
import cn.edu.bupt.springmvc.web.model.RelationalDatabaseInfo;
import cn.edu.bupt.springmvc.web.model.SyncTask;
import cn.edu.bupt.springmvc.web.service.DataSourceService;
import org.springframework.web.multipart.MultipartFile;


@Service
public class DataSourceServiceImpl implements DataSourceService {

	private final String pathPerfix = "/var/tmp/bigquery";

	private DateFormat df = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

	@Autowired
	private DataSourceMapper dataSourceDao;
	@Autowired
	private FolderTableMapper folderTableDao;
	@Autowired
	private RelationalDatabaseInfoMapper relationalDatabaseInfoDao;
	@Autowired
	private WorkTableMapper workTableDao;
	@Autowired
	private WorkTableLogMapper workTableLogDao;
	@Autowired
	private SyncTaskMapper syncTaskDao;
	@Autowired
	private FileInfoMapper fileInfoDao;

	@Resource
	private FolderService folderService;

	private DatabaseSchema schema = null;

	@Resource(name = "taskExecutor")
	private ThreadPoolTaskExecutor taskExecutor;

	Future<List<String>> loadFuture = null;

	@Override
	public void init(String IP,String port,  String username,String password,String databaseName,String type) {
		schema = new DatabaseSchema(type, IP,port, databaseName,username , password);
	}
	
	
	@Override
	public Set<String> testDataSourceAvaliable() throws SQLException {
		// TODO Auto-generated method stub
		
		return schema.getTableNames();
	}

	@Override
	public TableSchema getTableSchema(String tableName) throws SQLException{
		return schema.getTableSchema(tableName);
	}
	
	@Override
	public String getTableDataDemo(String tableName,int limit ,int offset) throws SQLException{
		return schema.getTableDataDemo(tableName, limit, offset);
	}
	
	@Override
	public List<String> addDatabaseDataSource(String IP, String port, String username, String password, String databaseName, String type,
											  @RequestParam(value = "tables[]") String[] tables, String[] rowkeys,
											  String dataSourceName, String tags, boolean isSync, int hour, int minute, int creatorId) throws SQLException, InterruptedException, ExecutionException, TimeoutException {
		List<String> sshResultList;
		//
		//开一个新的线程来用sqoop把数据从Mysql同步到 hive
		RelationalDatabaseInfo dbInfo = new RelationalDatabaseInfo();
		dbInfo.setIpHost(IP);dbInfo.setPort(port);dbInfo.setUsername(username);dbInfo.setPassword(password);
		dbInfo.setDbName(databaseName);dbInfo.setType(type);


		DataSource ds = new DataSource();
		ds.setCreateorId(creatorId);
		ds.setIsDeleted(true);//当数据从数据库中所有表都加载到Hbase之后才会为false
		ds.setName(dataSourceName);ds.setBasedTableType(TableUtil.getBasedTableType(type));
		ds.setCreateTime(new Date());ds.setTableNames(TableUtil.convertTablesToStr(tables));
		ds.setTags(tags);


		//注意，是以整个数据名字作为文件名
		FolderTable folder = folderService.ifNotFindThenCreate(folderService.getWorkTableFolder().getId(),dataSourceName,"WORKTABLE",creatorId);


		SyncTask syncTask = new SyncTask();
		syncTask.setHour(hour);syncTask.setMinute(minute);


		RelationalDBLoadTask task = new RelationalDBLoadTask(dbInfo,ds,syncTask,folder,rowkeys,dataSourceDao,folderTableDao,
															relationalDatabaseInfoDao,syncTaskDao,workTableDao,workTableLogDao
															,schema,"import_to_hive_overwrite");
		loadFuture = taskExecutor.submit(task);

		sshResultList =  loadFuture.get();

		return sshResultList;
	}

	@Override
	public List<String> getLoadFutureResult() throws InterruptedException, ExecutionException, TimeoutException {
		List<String> result = null;
		if (loadFuture.isDone()){
			return loadFuture.get(2*1000, TimeUnit.MILLISECONDS);
		}
		return result;
	}

	@Override
	public void addCsvDataSource(MultipartFile file, String dataSourceName,
								 String separatorChar,String quoteChar, int creatorId,
								String[] columnNames) throws IOException {
		Date d = new Date();

		String fullName = file.getOriginalFilename();
		int index = fullName.lastIndexOf('.');
		String namePerfix = fullName.substring(0,index);
		String nameSuffix = fullName.substring(index+1);
		String savedFileFullNamePrefix = pathPerfix+namePerfix+df.format(d);
		String savedFileFullNameSuffix = nameSuffix;
		String savedFileFullName = pathPerfix+namePerfix+df.format(d)+"."+nameSuffix;

		File tmpFile = new File(pathPerfix+namePerfix+df.format(d)+"."+nameSuffix);
		//先将文件拷贝到web服务器上
		FileUtils.copyInputStreamToFile(file.getInputStream(),tmpFile);

		//将web服务器上的文件传到集群上？ 好像不需要

		//根据csv的内容确定每个字段的类型，根据csv的表头构建hive表

		String createHiveTableSql = generateHiveTableCreateSql(savedFileFullNamePrefix,
				savedFileFullNameSuffix,separatorChar,quoteChar,columnNames);
		//将文件的数据导入到hive表中

		//web项目的一些管理信息


		FileInfo fileInfo = new FileInfo();
		fileInfo.setName(file.getOriginalFilename()+df.format(d));fileInfo.setSavedFolder(pathPerfix);
		fileInfo.setUpdateTime(d);fileInfo.setType(FileUtil.getFileType(fullName));
		fileInfoDao.insertSelective(fileInfo);

		DataSource ds = new DataSource();
		ds.setCreateorId(creatorId);ds.setIsDeleted(false);ds.setCreateTime(d);ds.setName(namePerfix);
		ds.setBasedTableType(TableUtil.getBasedTableType(nameSuffix));ds.setDataId(fileInfo.getId());
		ds.setTableNames(namePerfix);
		dataSourceDao.insertSelective(ds);

		WorkTable wt = new WorkTable();
		wt.setDatasourceId(ds.getId());wt.setIsDeleted(false);
		wt.setFolderId(folderService.getWorkTableFolder().getId());
		wt.setLastChangeTime(d);wt.setName(namePerfix+df.format(d));
//        wt.setRemark(remark);wt.setTags(tags);wt.setVersion(1);
		wt.setSolidTableName(namePerfix);
		workTableDao.insertSelective(wt);

	}

	private String generateHiveTableCreateSql(String savedFileFullNamePrefix,
											  String savedFileFullNameSuffix,String separatorChar,String quoteChar,
											  String[] columnNames) throws IOException {
		/**
		 * CREATE TABLE csv_table(a string, b string)
		 * ROW FORMAT SERDE 'org.apache.Hadoop.hive.serde2.OpenCSVSerde'
		 * WITH SERDEPROPERTIES (   "separatorChar" = "\t",   "quoteChar"     = "'",   "escapeChar"    = "\\")
		 * STORED AS TEXTFILE;
		 */
		String filePath = pathPerfix + "/"+savedFileFullNamePrefix+"."+savedFileFullNameSuffix;
		CSVFile csvFile = new CSVFile(filePath,separatorChar,quoteChar,"\\",-1);
		StringBuffer sql = null;

		if (columnNames == null){
			columnNames = csvFile.getHeadList(null);
		}

		if (columnNames== null){
			ArrayList<String> tmp = new ArrayList<String>();
			for(int i =1;i<=csvFile.getColumnNum();i++){
				tmp.add("column"+i);
			}
			columnNames = (String[]) tmp.toArray();

		}

		Map<Integer,String> types = csvFile.getTypes();

		sql.append("CREATE TABLE `").append(savedFileFullNamePrefix).append("` ").append("( ");

		for (Integer index: types.keySet()
			 ) {
			sql.append("`").append(columnNames[index]).append("` ").append(types.get(index)).append(" ");
		}
		sql.append(")\n");
		sql.append(" ROW FORMAT SERDE 'org.apache.Hadoop.hive.serde2.OpenCSVSerde' ");
		sql.append(" WITH SERDEPROPERTIES (  \"separatorChar\" = \" ").append(separatorChar.trim().charAt(0)).append("\", ")
				.append("\"quoteChar\"     = \"").append(quoteChar.trim().charAt(0)).append("\", ")
				.append(" \"escapeChar\"    = \"\\\\\"").append(") ").append("\n");
		sql.append("STORED AS TEXTFILE");

		return sql.toString();
	}
}
