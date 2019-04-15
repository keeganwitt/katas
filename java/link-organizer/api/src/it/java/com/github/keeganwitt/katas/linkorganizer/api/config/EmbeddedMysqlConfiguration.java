package com.github.keeganwitt.katas.linkorganizer.api.config;

import com.google.common.io.Files;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.ScriptResolver;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class EmbeddedMysqlConfiguration {
    @Bean(destroyMethod = "stop")
    EmbeddedMysql embeddedMysql() {
        MysqldConfig config = MysqldConfig.aMysqldConfig(Version.v5_7_latest).withPort(3310)
                .withUser("linksUser", "linksUser")
                .withTempDir(Files.createTempDir().getAbsolutePath())
                .build();
        return EmbeddedMysql.anEmbeddedMysql(config).addSchema("linksdb",
                ScriptResolver.classPathScript("create-db.sql")).start();
    }

    @Value("${spring.datasource.driver-class-name}")
    String driverName;

    @Value("${spring.datasource.url}")
    String connectionUrl;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String password;

    @Bean
    @DependsOn("embeddedMysql")
    DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(connectionUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
