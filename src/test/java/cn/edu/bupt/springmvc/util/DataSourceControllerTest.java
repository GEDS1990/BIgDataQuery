package cn.edu.bupt.springmvc.util;

import static org.junit.Assert.fail;

import cn.edu.bupt.springmvc.core.util.SSHClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

public class DataSourceControllerTest extends AbstractContextControllerTest {


//	@AfterClass
//	public void after(){
//		SSHClient.PERMSSH
//	}
	
	@Test
	public void testTestDatabaseAvaliable() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/DataSource/testDatabaseAvaliable")
//						.contentType(MediaType.APPLICATION_JSON)
						.param("IP","111.207.243.70")
//						.content(gson.toJson(config))
						.param("port","3606")
						.param("username","root")
						.param("password","cYz#P@ss%w0rd$868")
						.param("databaseName","knowledge_base")
						.param("type","mysql")
						)
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

	@Test
	public void testAddDatabaseDataSource() throws Exception {
		String[] tableNames = {"first_category","question_label"};
		String[] rowkeys = {"id","id"};
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/DataSource/addDatabaseDataSource")
//						.contentType(MediaType.APPLICATION_JSON)
						.param("IP","111.207.243.70")
////						.content(gson.toJson(config))
						.param("port","3606")
						.param("username","root")
						.param("password","cYz#P@ss%w0rd$868")
						.param("databaseName","knowledge_base")
						.param("type","mysql")
						.param("tables[]",tableNames)
								.param("rowkeys[]",rowkeys)
						.param("")
						.param("tags","category")
						.param("isSync","true")
						.param("hour","12")
						.param("minute","15")
								.param("creatorId","1")
						)
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

	@Test
	public void testGetDatabaseTableSchema() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/DataSource/getDatabaseTableSchema")
//						.contentType(MediaType.APPLICATION_JSON)
//						.param("IP","111.207.243.70")
////						.content(gson.toJson(config))
//						.param("port","3606")
//						.param("username","root")
//						.param("password","cYz#P@ss%w0rd$868")
						.param("tableName","first_category")
//						.param("type","mysql")
						)
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

	@Test
	public void testGetDatabaseTableDemo() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/DataSource/getDatabaseTableDemo")
//						.contentType(MediaType.APPLICATION_JSON)
//						.param("IP","111.207.243.70")
////						.content(gson.toJson(config))
//						.param("port","3606")
//						.param("username","root")
//						.param("password","cYz#P@ss%w0rd$868")
						.param("tableName","first_category")
//						.param("type","mysql")
						)
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}


	@Test
	public void testGetLoadFutureResult() throws Exception {
		MvcResult result = mockMvc
				.perform(
						MockMvcRequestBuilders.post("/DataSource/getLoadFutureResult")
//						.contentType(MediaType.APPLICATION_JSON)
//						.param("IP","111.207.243.70")
////						.content(gson.toJson(config))
//						.param("port","3606")
//						.param("username","root")
//						.param("password","cYz#P@ss%w0rd$868")
								.param("creatorId","1")
//						.param("type","mysql")
				)
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

//	@Test
//	public void testAddCsvDataSource() throws Exception {
//		MockMultipartHttpServletRequestBuilder mockMultipartHttpServletRequestBuilder
//				= MockMultipartHttpServletRequestBuilder("/DataSource/addCsvDataSource",null);
////		MvcResult result = mockMvc
////				.perform(
////						MockMultipartHttpServletRequestBuilder.
////								fileupload("/DataSource/addCsvDataSource")
//////						.contentType(MediaType.APPLICATION_JSON)
//////						.param("IP","111.207.243.70")
////////						.content(gson.toJson(config))
//////						.param("port","3606")
//////						.param("username","root")
//////						.param("password","cYz#P@ss%w0rd$868")
////								.param("creatorId","1")
//////						.param("type","mysql")
////				)
////				.andDo(MockMvcResultHandlers.print())
////				.andReturn();
//	}
}
