# JSON support for Mybatis 3.x using Jackson 2.6.x

Provide support for JSON like field types in any Database.
Note, that I developed this handler with PostgreSql in mind, 
but it looks like it can be used with any other database even without JSON support.

You should be able to use any Jackson version compatible with API version >= 2.6.0.

[![Release](https://jitpack.io/v/jneat/mybatis-jackson.svg)](https://jitpack.io/#jneat/mybatis-jackson)  
[API javadoc](https://jitpack.io/com/github/jneat/mybatis-jackson/-SNAPSHOT/javadoc/)

## How does it work
Because JDBC does not support JSON types, it transfer JSON to/from database as a string.
It serialize JSON to string on save and deserialize from string on read.
This feature means that we are really do not care if our DB can support JSON or not.

There are 2 handlers available:

* __TreeNodeTypeHandler__ - work only with ArrayNode and ObjectNode only via TreeNode interface
* __JsonNodeValueTypeHandler__ - value wrapper around JsonNode

### Lazy reading
Type handlers return lazy wrapper instead of already parsed Node representation.
It is waiting for you to call any of its methods - only then it will read JSON into structure.
But this approach may lead to **unexpected runtime exceptions** in a case if your database will return
invalid JSON string.

### Result is always not null despite what stored into DB
I just want to avoid some complexity by relying on MissingNode and not nullable results.

Just look here.
```java
class Dto {
    JsonNodeValue value1;
    TreeNode value2;
    // ... Some getters and setters
}

// Note that in DB value1 and value2 columns are null or empty strings
Dto row = dtoMapper.get();

// But actually you have JsonNodeValue wrapper and it is not null 
assert row.getValue1().isNotPresent();

// Eventually you will find that you are dealing with MissingNode
assert row.getValue1().get().isMissingNode();
assert row.getValue2().isMissingNode();

```

## Add to your project
You can add this artifact to your project using [JitPack](https://jitpack.io/#jneat/mybatis-jackson).  
All versions list, instructions for gradle, maven, ivy etc. can be found by link above.

To get latest commit use -SNAPSHOT instead version number.

## Configure
In result map configuration you should use: 

* `javaType="com.fasterxml.jackson.core.TreeNode"`
* `javaType="com.fasterxml.jackson.databind.JsonNode"`

You should not configure anything if you want to use TreeNode types as arguments in your mapper
functions, but keep in mind that handler only expect objects of type ArrayNode or ObjectNode
for TreeNodeTypeHandler. And JsonNode for JsonNodeValueTypeHandler.


### Mybatis config
```xml
<!-- mybatis-config.xml -->
<typeHandlers>
  <typeHandler handler="com.github.jneat.mybatis.TreeNodeTypeHandler"/>
  <typeHandler handler="com.github.jneat.mybatis.JsonNodeValueTypeHandler"/>
</typeHandlers>
```

Or you can use package search

```xml
<!-- mybatis-config.xml -->
<typeHandlers>
  <package name="com.github.jneat.mybatis"/>
</typeHandlers>
```

### Mybatis via Spring
```xml
<bean id="SomeId" class="org.mybatis.spring.SqlSessionFactoryBean">
    <!-- your configuration -->
    <property name="typeHandlersPackage" value="com.github.jneat.mybatis" />
</bean>
```