package com.github.jneat.mybatis;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.ibatis.annotations.Param;

public interface JsonMapper {

    JsonEntity get(@Param("id") long id);

    int insert(JsonEntity entity);

    int insertValues(
        @Param("id") long id,
        @Param("jsonArray") ArrayNode jArray,
        @Param("jsonObject") ObjectNode jObj,
        @Param("nodeArray") JsonNodeValue nArr,
        @Param("nodeObject") JsonNodeValue nObj
    );
}
