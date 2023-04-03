package cn.ybzy.dao;

import java.util.List;


import cn.ybzy.model.UploadFile;

public class UploadFileDaoImpl extends BaseDao<UploadFile> implements UploadFileDao{

	@Override
	public void addFileInfo(UploadFile uploadFile) {
		//id是自增，不用插入
		String sql ="INSERT INTO `uploadfiles`(`old_file_name`,`file_type`,`file_size`,`save_path`,"
		+"`save_time`,`desc`,`save_name` ) VALUES(?,?,?,?,?,?,?)";
		super.update(sql, uploadFile.getOldFileName(),uploadFile.getFileType(),
				uploadFile.getFileSize(),uploadFile.getSavePath(),uploadFile.getSaveTime(),uploadFile.getDesc()
				,uploadFile.getSaveName());
	}

	@Override
	public List<UploadFile> getUploadFiles() {
		//查询时，要用到别名，我们要用到字段值，上面的插入不用别名，那自系统自动完成的。
		String sql = "SELECT `id` id,`old_file_name` oldFileName,`file_type` fileType,`file_size` fileSize,`save_path` savePath,`save_time` saveTime,`desc` `desc`,`save_name` saveName FROM `uploadfiles`";
		return super.getList(sql);
	}

	@Override
	public void deletUploadFile(int id) {
		String sql = "DELETE FROM `uploadfiles` WHERE `id`=? ";
		super.update(sql, id);
	}
	
	@Override
	public UploadFile get(int id) {
		String sql = "SELECT `id` id,`old_file_name` oldFileName,`file_type` fileType,`file_size` fileSize,`save_path` savePath,`save_time` saveTime,`desc` `desc`,`save_name` saveName FROM `uploadfiles` WHERE id=?";
		return super.get(sql, id);
	}

}
