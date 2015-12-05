package com.github.jneat.mybatis;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PostgresqlTest extends JsonHandlersTestApi {

    private static final String PG_URL = "jdbc:postgresql://jneat/jneat?user=jneat&password=jneat";

    private static final String PG_SQL = "/postgresql.sql";

    private static SqlSessionFactory sessionFactory;

    private static ArrayNode aNode;

    private static ObjectNode oNode;

    @BeforeClass
    public static void init() throws SQLException, IOException {
        PGSimpleDataSource pgDs = new PGSimpleDataSource();
        pgDs.setUrl(PG_URL);
        sessionFactory = JsonHandlersTestApi.setUpDb(pgDs, PG_SQL);
    }

    JsonNode readJson(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return mapper.readTree(json);
    }

    @Test
    public void test1InsertNulls() {
        try (SqlSession sess = sessionFactory.openSession()) {
            JsonMapper mapper = sess.getMapper(JsonMapper.class);
            mapper.insert(new JsonEntity(1, null, null, null, null));
            mapper.insertValues(2, null, null, null, null);
            sess.commit();
        }
    }

    @Test
    public void test2InsertValues() throws IOException {
        aNode = (ArrayNode)readJson("[1,2,3,7,8]");
        oNode = (ObjectNode)readJson("{a:12, b:12.12, c: \"some name\"}");
        try (SqlSession sess = sessionFactory.openSession()) {
            JsonMapper mapper = sess.getMapper(JsonMapper.class);

            mapper.insert(new JsonEntity(3, aNode, oNode, JsonNodeValue.from(aNode), JsonNodeValue.from(oNode)));
            mapper.insertValues(4, aNode, oNode, JsonNodeValue.from(aNode), JsonNodeValue.from(oNode));
            sess.commit();
        }
    }

    @Test
    public void test3ReadNulls() {
        try (SqlSession sess = sessionFactory.openSession()) {
            JsonMapper mapper = sess.getMapper(JsonMapper.class);
            JsonEntity e1 = mapper.get(1);
            assertThat(e1.getJsonArray()).isNotNull();
            assertThat(e1.getJsonArray().isMissingNode()).isTrue();
            assertThat(e1.getJsonObject()).isNotNull();
            assertThat(e1.getJsonObject().isMissingNode()).isTrue();
            assertThat(e1.getNodeArray().isPresent()).isFalse();
            assertThat(e1.getNodeArray().get().isMissingNode()).isTrue();
            assertThat(e1.getNodeObject().isPresent()).isFalse();
            assertThat(e1.getNodeObject().get().isMissingNode()).isTrue();

            JsonEntity e2 = mapper.get(2);
            assertThat(e2.getJsonArray()).isNotNull();
            assertThat(e2.getJsonArray().isMissingNode()).isTrue();
            assertThat(e2.getJsonObject()).isNotNull();
            assertThat(e2.getJsonObject().isMissingNode()).isTrue();
            assertThat(e2.getNodeArray().isPresent()).isFalse();
            assertThat(e2.getNodeArray().get().isMissingNode()).isTrue();
            assertThat(e2.getNodeObject().isPresent()).isFalse();
            assertThat(e2.getNodeObject().get().isMissingNode()).isTrue();
        }
    }

    @Test
    public void test4ReadValues() {
        try (SqlSession sess = sessionFactory.openSession()) {
            JsonMapper mapper = sess.getMapper(JsonMapper.class);
            JsonEntity e1 = mapper.get(3);

            compareArrays(aNode, e1.getJsonArray());
            compareObjects(oNode, e1.getJsonObject());
            compareArrays(aNode, e1.getNodeArray().get());
            compareObjects(oNode, e1.getNodeObject().get());

            JsonEntity e2 = mapper.get(4);
            compareArrays(aNode, e2.getJsonArray());
            compareObjects(oNode, e2.getJsonObject());
            compareArrays(aNode, e2.getNodeArray().get());
            compareObjects(oNode, e2.getNodeObject().get());
        }
    }

    protected void compareArrays(TreeNode a, TreeNode b) {
        assertThat(a.isArray()).isEqualTo(b.isArray());
        assertThat(a.size()).isEqualTo(b.size());
        for (int i = 0; i < a.size(); i++) {
            assertThat(a.get(i)).isEqualTo(b.get(i));
        }
    }

    protected void compareObjects(TreeNode a, TreeNode b) {
        assertThat(a.isObject()).isEqualTo(b.isObject());
        assertThat(a.size()).isEqualTo(b.size());

        Iterator<String> fnames = a.fieldNames();
        while (fnames.hasNext()) {
            String key = fnames.next();
            assertThat(a.get(key)).isEqualTo(b.get(key));
        }
    }

}
