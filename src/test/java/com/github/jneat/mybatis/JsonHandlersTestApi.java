package com.github.jneat.mybatis;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

public abstract class JsonHandlersTestApi {

    protected static String getResourceAsString(String resource) throws IOException {

//        System.out.println(JsonHandlersTestApi.class.getResource(resource).getFile());
//
//        Assertions.assertThat(JsonHandlersTestApi.class.getResource(resource).getFile())
//            .isEqualTo("");
        String query;
        try (final InputStream is = JsonHandlersTestApi.class.getResourceAsStream(resource)) {
//        try (final InputStream is = JsonHandlersTestApi.class.getResourceAsStream(resource)) {

            Assertions.assertThat(is).isNotNull();

            StringWriter stringWriter = new StringWriter();
            int b;
            while ((b = is.read()) != -1) {
                stringWriter.write(b);
            }
            query = stringWriter.toString();
        }
        return query;
    }

    protected static SqlSessionFactory setUpDb(DataSource ds, String initSql) throws SQLException, IOException {
        try (final Connection cnx = ds.getConnection(); final Statement st = cnx.createStatement()) {
            st.execute(getResourceAsString(initSql));
        }

        // Init mybatis
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("jneat", transactionFactory, ds);
        Configuration configuration = new Configuration(environment);
        configuration.getTypeHandlerRegistry().register("com.github.jneat.mybatis");
        configuration.addMapper(JsonMapper.class);

        return new SqlSessionFactoryBuilder().build(configuration);
    }
}
