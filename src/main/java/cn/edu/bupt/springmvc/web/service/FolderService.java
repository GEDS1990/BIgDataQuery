package cn.edu.bupt.springmvc.web.service;

import cn.edu.bupt.springmvc.web.model.FolderTable;

import java.sql.SQLException;

public interface FolderService {
    FolderTable getDashBoardFolder();

    public FolderTable ifNotFindThenCreate(Integer parentFolderId, String folderName, String type, int creatorId) throws SQLException;
    public FolderTable getWorkTableFolder();


}
