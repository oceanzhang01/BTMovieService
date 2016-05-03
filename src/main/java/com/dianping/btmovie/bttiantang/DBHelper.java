package com.dianping.btmovie.bttiantang;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DBHelper
{
	private static ConnectionSource connectionSource = null;
	private static ConnectionSource getConnectionSource() throws Exception{
		if(connectionSource == null || !connectionSource.isOpen()) {
			String databaseUrl = "jdbc:mysql://r2fn84951d.mysql.rds.aliyuncs.com:3306/btmovie";
			connectionSource =
					new JdbcConnectionSource(databaseUrl, "rpl9e91762", "rapsn035");
		}
		return connectionSource;
	}

	public static synchronized <D extends Dao<T, ?>, T> D createDao(Class<T> clazz) throws Exception{
		return DaoManager.createDao(getConnectionSource(),clazz);
	}
	public static synchronized void createTable(Class<?> cls)throws Exception{
		TableUtils.createTableIfNotExists(getConnectionSource(), cls);
	}

}
