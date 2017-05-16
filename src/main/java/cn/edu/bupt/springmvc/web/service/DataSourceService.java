package cn.edu.bupt.springmvc.web.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.lmk.database_schema.schema.TableSchema;
import org.springframework.web.multipart.MultipartFile;

public interface DataSourceService {

	Set<String> testDataSourceAvaliable() throws SQLException;
	public TableSchema getTableSchema(String tableName) throws SQLException;
	String getTableDataDemo(String tableName, int limit, int offset) throws SQLException;
	List<String> addDatabaseDataSource(String IP, String port, String username, String password, String databaseName,
									   String type, String[] tables, String[] rowkeys, String dataSourceName, String tags, boolean isSync, int hour, int minute, int createId) throws SQLException, InterruptedException, ExecutionException, TimeoutException;
	void init(String IP, String port, String username, String password, String databaseName, String type);

	List<String> getLoadFutureResult() throws InterruptedException, ExecutionException, TimeoutException;

    void addCsvDataSource(MultipartFile file, String dataSourceName, String separatorChar,
						  String quoteChar,int creatorId,String[] columnNames) throws IOException;


//	boolean getLoadFutureResult() throws InterruptedException, ExecutionException, TimeoutException;
}
