package cn.edu.bupt.springmvc.web.service.impl;

import java.sql.SQLException;

import javax.annotation.Resource;

import cn.edu.bupt.springmvc.web.dao.FolderTableMapper;
import org.springframework.stereotype.Service;

import cn.edu.bupt.springmvc.web.model.FolderTable;
import cn.edu.bupt.springmvc.web.service.FolderService;

@Service
public class FolderServiceImpl implements FolderService {

	@Resource
	private FolderTableMapper folderTableDao;
	@Override
	public FolderTable getWorkTableFolder(){
		return folderTableDao.selectByPrimaryKey(2);
	}

	@Override
	public FolderTable getDashBoardFolder(){
		return folderTableDao.selectByPrimaryKey(3);
	}

	@Override
	public FolderTable ifNotFindThenCreate(Integer parentFolderId, String folderName, String type, int creatorId) throws SQLException{
		FolderTable newFolder = null;
		if(parentFolderId == null && type.toLowerCase().trim().equals("root")){
			parentFolderId = -1;
			newFolder = new FolderTable();
			newFolder.setName(folderName);newFolder.setParentFolderId(parentFolderId);
			newFolder.setType(type);newFolder.setOwnerId(creatorId);
			folderTableDao.insert(newFolder);
			return newFolder;
		}
		if(parentFolderId == null){
			throw new SQLException("父文件夹未指定");
		}
		FolderTable parent = folderTableDao.selectByPrimaryKey(parentFolderId);
		
		if(parent == null){
			throw new SQLException("父文件夹不存在");
		}else{
			if (isContains(parentFolderId,folderName)){
				throw new SQLException("该文件夹已经存在");
			}else{
				newFolder = new FolderTable();
				newFolder.setName(folderName);newFolder.setParentFolderId(parentFolderId);newFolder.setType(type);
//				newFolder.setIsRoot(isRoot);
			}
		}
		
		folderTableDao.insert(newFolder);
		
		return newFolder;	
	}

	private boolean isContains(Integer parentFolderId, String folderName) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
