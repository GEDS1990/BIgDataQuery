package cn.edu.bupt.springmvc.web.service.impl;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.junit.Test;

import cn.edu.bupt.springmvc.util.AbstractContextControllerTest;
import cn.edu.bupt.springmvc.web.model.FolderTable;

public class FolderServiceImplTest extends AbstractContextControllerTest{

	@Resource
	private FolderServiceImpl folderService;
	
	
	@Test
	public void testIfNotFindThenCreate() throws SQLException {
//		FolderTable folder = folderService.ifNotFindThenCreate(null, "root", "ROOT");
//		logger.info(folder.getId().toString());
		int creatorId =1;
		FolderTable folder2 = folderService.ifNotFindThenCreate(1, "workTable", "WORKTABLE", creatorId);
		FolderTable folder3 = folderService.ifNotFindThenCreate(1, "dashBoard", "DASHBOARD", creatorId);
	}

}
