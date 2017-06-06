package cn.edu.bupt.springmvc.web.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import cn.edu.bupt.springmvc.core.util.ExceptionUtil;
import org.apache.poi.ss.formula.functions.DateFunc;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lmk.database_schema.schema.TableSchema;

import cn.edu.bupt.springmvc.core.util.ReturnModel;
import cn.edu.bupt.springmvc.web.service.DataSourceService;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/DataSource")
public class DataSourceController {

	@Resource
	private DataSourceService dataSourceService;
	/**
	 * 
	 * @param IP
	 * @param port
	 * @param username
	 * @param password
	 * @param databaseName
	 * @param type
	 * @return 如果数据库可用，返回所有的表名
	 */
    @RequestMapping(value = "/testDatabaseAvaliable",method =RequestMethod.POST)
    public @ResponseBody ReturnModel testDatabaseAvaliable(
    		String IP,String port,  String username,String password,String databaseName,String type) {
    	
    	dataSourceService.init(IP,port,username,password,databaseName,type);
    	
        ReturnModel result = new ReturnModel();
        Set<String> tableNames = null;
        
    	try {
			tableNames = dataSourceService.testDataSourceAvaliable();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setResult(false);
			result.setReason(e.getMessage());
			return result;
		}
    	result.setResult(true);
    	result.setDatum(tableNames);
    	return result;
    }

    @RequestMapping("/addDatabaseDataSource")
    public  @ResponseBody ReturnModel addDatabaseDataSource(
    		String IP,String port,  String username,String password,String databaseName,String type,
    		@RequestParam(value="tables[]") String[] tables,@RequestParam(value="rowkeys[]") String[] rowkeys,
    		String dataSourceName,String tags,boolean isSync,int hour,int minute,
			int creatorId
    		){
    	List<String> sshResult = null;
    	ReturnModel result = new ReturnModel();
    	dataSourceService.init(IP,port,username,password,databaseName,type);
    	Set<String> tableNames = null;
    	try {
			tableNames = dataSourceService.testDataSourceAvaliable();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setResult(false);
			result.setReason(e.getMessage());
			return result;
		}
		try {
			sshResult = dataSourceService.addDatabaseDataSource(IP, port, username, password, databaseName, type, tables, rowkeys, dataSourceName, tags, isSync, hour, minute, creatorId);
		} catch (SQLException e) {
			e.printStackTrace();
			result.setResult(false);
			result.setReason(e.getMessage());
			return result;
		} catch (InterruptedException e) {
			e.printStackTrace();
			result.setResult(false);
			result.setReturnCode(3);
			result.setReason("load 任务被中断"+ExceptionUtil.printStackTraceToString(e));
			return result;
		} catch (ExecutionException e) {
			e.printStackTrace();
			result.setResult(false);
			result.setReturnCode(2);
			result.setReason("load 任务出错"+ExceptionUtil.printStackTraceToString(e.getCause()));
			return result;
		} catch (TimeoutException e) {
			e.printStackTrace();
			result.setResult(false);
			result.setReturnCode(1);
			result.setReason("load 任务超时未完成"+ExceptionUtil.printStackTraceToString(e));
			return result;
		}
		result.setResult(true);
    	result.setReturnCode(0);
    	result.setDatum(sshResult);
        return result;
    }

	@RequestMapping("/getLoadFutureResult")
	public  @ResponseBody ReturnModel getLoadFutureResult(
			int creatorId
	){
		ReturnModel result = new ReturnModel();
		List<String> sshResult = null;

		try {
			sshResult = dataSourceService.getLoadFutureResult();
		}  catch (InterruptedException e) {
			e.printStackTrace();
			result.setResult(false);
			result.setReturnCode(3);
			result.setReason("load 任务被中断"+ExceptionUtil.printStackTraceToString(e));
			return result;
		} catch (ExecutionException e) {
			e.printStackTrace();
			result.setResult(false);
			result.setReturnCode(2);
			result.setReason("load 任务出错"+ ExceptionUtil.printStackTraceToString(e.getCause()));
			return result;
		} catch (TimeoutException e) {
			e.printStackTrace();
			result.setResult(false);
			result.setReturnCode(1);
			result.setReason("load 任务超时未完成"+ ExceptionUtil.printStackTraceToString(e));
			return result;
		}
		if (sshResult == null){
			result.setResult(false);
			result.setReturnCode(4);
			result.setReason("load 任务未完成 或者 任务失败");
			return result;
		}
		result.setResult(true);
		result.setReturnCode(0);
		result.setDatum(sshResult);
		return result;
	}
//
//	private ReturnModel handle(Throwable cause) {
//		ReturnModel result = new ReturnModel();
//		result.setReturnCode(2);
//		result.setResult(false);
//		result.setReason(cause.toString());
//		return  result;
//	}

	@RequestMapping("/getDatabaseTableSchema")
    public  @ResponseBody ReturnModel getDatabaseTableSchema(
    		String tableName
    		){
    	ReturnModel result = new ReturnModel();
    	TableSchema tableSchema = null;
    	try {
    		tableSchema = dataSourceService.getTableSchema(tableName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setResult(false);
			result.setReason(e.getMessage());
			return result;
		}
    	result.setResult(true);
    	result.setDatum(tableSchema);
        return result;
    }
    
    @RequestMapping("/getDatabaseTableDemo")
    public  @ResponseBody ReturnModel getDatabaseTableDemo(
    		String tableName
    		){
    	ReturnModel result = new ReturnModel();
    	String  tableDemo= null;
    	try {
    		tableDemo = dataSourceService.getTableDataDemo(tableName,20,0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setResult(false);
			result.setReason(e.getMessage());
			return result;
		}
    	result.setResult(true);
    	result.setDatum(tableDemo);
        return result;
    }

	@RequestMapping("/addCsvDataSource")
	public  @ResponseBody ReturnModel addCsvDataSource(
			@RequestParam (value = "file",required = true) MultipartFile file,
			int creatorId,String dataSourceName,String separatorChar,String quoteChar,String[] columnNames
	){
		ReturnModel result = new ReturnModel();
		try {
			dataSourceService.addCsvDataSource(file,dataSourceName,separatorChar,quoteChar,creatorId,columnNames);
		} catch (IOException e) {
			e.printStackTrace();
			result.setResult(false);result.setReason(e.toString());
			return result;
		} catch (SQLException e) {
//执行hive建表或者数据load失败
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
//
			e.printStackTrace();
		}
		return result;
	}
}
