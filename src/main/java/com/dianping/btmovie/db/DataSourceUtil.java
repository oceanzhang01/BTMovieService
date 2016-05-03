package com.dianping.btmovie.db;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.*;
import java.util.Properties;

/**
 * The Class DataSourceUtil.
 */
public class DataSourceUtil {

    private static String confile = "druid.properties";
    private static Properties p = null;
    private static  DataSource dataSource = null;
    static {
        p = new Properties();
        InputStream inputStream = null;
        try {
            //java应用
            confile = DataSourceUtil.class.getClassLoader().getResource("").getPath()
                    + confile;
            System.out.println(confile);
            File file = new File(confile);
            inputStream = new BufferedInputStream(new FileInputStream(file));
            p.load(inputStream);
            dataSource = DruidDataSourceFactory.createDataSource(p);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static final DataSource getDataSource() {
        return dataSource;
    }
}

