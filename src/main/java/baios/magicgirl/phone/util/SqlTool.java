package baios.magicgirl.phone.util;

import java.sql.*;
import java.util.*;


public class SqlTool {

    /**
     * 生成 SQLite 标准 JDBC 连接 URL
     * 适配 JS 字符串入参，路径支持相对/绝对路径
     *
     * @param dbPath 数据库文件路径（必填，示例："./config/magicgirl/chat.db"）
     * @return 格式化的 SQLite JDBC URL（格式：jdbc:sqlite:文件路径）
     * @throws IllegalArgumentException 路径为空/空白时抛出
     */
    public static String getSqliteUrl(String dbPath) {
        // 入参非空校验：避免空路径导致无效URL
        if (dbPath == null || dbPath.trim().isEmpty()) {
            throw new IllegalArgumentException("数据库文件路径不能为空！请传入有效路径（如 ./config/xxx.db）");
        }
        return "jdbc:sqlite:" + dbPath.trim();
    }

    /**
     * 核心方法：动态创建 SQLite 表（完全自定义表名、列名、列类型）
     * 入参全为 JS/Java 互转兼容类型，可直接被 KubeJS 调用
     *
     * @param dbPath            数据库文件路径（JS 传入字符串，示例："./config/magicgirl/chat.db"）
     * @param tableName         目标表名（JS 传入字符串，仅允许字母/数字/下划线，避免 SQL 注入）
     * @param columnDefinitions 列定义键值对（JS 传入对象 → 自动映射为 Java Map）
     *                          格式：Key=列名（字符串），Value=列类型+约束（字符串）
     *                          示例：{"id":"INTEGER PRIMARY KEY AUTOINCREMENT", "player_name":"TEXT NOT NULL"}
     * @throws IllegalArgumentException 入参为空/格式非法时抛出（表名/列名含非法字符、列定义为空等）
     * @throws RuntimeException         数据库连接超时/SQL执行失败时抛出（封装底层 SQLException）
     */
    public static void dbInit(String dbPath, String tableName, Map<String, String> columnDefinitions) {
        // ========== 1. 严格入参校验（避免非法参数导致SQL错误） ==========
        // 数据库路径校验
        if (dbPath == null || dbPath.trim().isEmpty()) {
            throw new IllegalArgumentException("参数错误：dbPath（数据库路径）不能为空！");
        }
        // 表名校验：非空 + 仅允许字母/数字/下划线（基础SQL注入防护）
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("参数错误：tableName（表名）不能为空！");
        }
        if (!tableName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException(
                    "参数错误：tableName（表名）仅允许字母、数字、下划线！当前值：" + tableName
            );
        }
        // 列定义校验：非空 + 至少包含1列
        if (columnDefinitions == null || columnDefinitions.isEmpty()) {
            throw new IllegalArgumentException("参数错误：columnDefinitions（列定义）不能为空！至少定义1列");
        }

        // ========== 2. 设置JDBC连接超时（避免无限等待） ==========
        DriverManager.setLoginTimeout(5); // 超时时间：5秒

        // ========== 3. 动态拼接建表SQL（核心逻辑） ==========
        StringBuilder createTableSQL = new StringBuilder();
        // SQL前缀：CREATE TABLE IF NOT EXISTS 表名 (
        createTableSQL.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName.trim())
                .append(" (");

        // 遍历列定义，拼接每列的「列名 类型+约束」
        int columnIndex = 0;
        for (Map.Entry<String, String> column : columnDefinitions.entrySet()) {
            String colName = column.getKey();
            String colType = column.getValue();

            // 列名校验：非空 + 合法字符
            if (colName == null || colName.trim().isEmpty()) {
                throw new IllegalArgumentException("参数错误：存在空列名！列定义索引：" + columnIndex);
            }
            if (!colName.matches("[a-zA-Z0-9_]+")) {
                throw new IllegalArgumentException(
                        "参数错误：列名含非法字符（仅允许字母/数字/下划线）！列名：" + colName
                );
            }
            // 列类型校验：非空
            if (colType == null || colType.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "参数错误：列[" + colName + "]的类型/约束不能为空！"
                );
            }

            // 拼接列定义（非第一列前加逗号分隔）
            if (columnIndex > 0) {
                createTableSQL.append(", ");
            }
            createTableSQL.append(colName.trim())
                    .append(" ")
                    .append(colType.trim());

            columnIndex++;
        }
        createTableSQL.append(")"); // 闭合SQL语句
        String finalSQL = createTableSQL.toString();
        System.out.println("[SQLite 工具类] 待执行建表SQL：" + finalSQL);

        // ========== 4. 执行SQL（try-with-resources 自动关闭资源） ==========
        try (Connection conn = DriverManager.getConnection(getSqliteUrl(dbPath));
             Statement stmt = conn.createStatement()) {

            // 执行建表操作（IF NOT EXISTS 保证重复调用不报错）
            stmt.execute(finalSQL);
            System.out.println(
                    "[SQLite 工具类] 表创建成功！" +
                            "\n- 数据库路径：" + dbPath +
                            "\n- 表名：" + tableName +
                            "\n- 列数：" + columnDefinitions.size()
            );

        } catch (SQLTimeoutException e) {
            // 连接超时异常：明确提示超时时间和路径
            String errorMsg = "[SQLite 工具类] 数据库连接超时！超时时间：5秒，数据库路径：" + dbPath;
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg, e); // 转为运行时异常，便于JS捕获

        } catch (SQLException e) {
            // SQL执行异常：携带完整上下文（表名/SQL/错误信息）
            String errorMsg = "[SQLite 工具类] 建表失败！" +
                    "\n- 表名：" + tableName +
                    "\n- 执行SQL：" + finalSQL +
                    "\n- 错误原因：" + e.getMessage();
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg, e); // 封装底层异常，保留堆栈

        } catch (Exception e) {
            // 兜底异常：捕获所有未知错误，保证错误信息可追溯
            String errorMsg = "[SQLite 工具类] 动态建表时发生未知错误！" +
                    "\n- 表名：" + tableName +
                    "\n- 错误信息：" + e.getMessage();
            System.err.println(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    public static void insertData(String dbPath, String tableName, Map<String, Object> data) {
        // 1. 入参校验
        if (dbPath == null || dbPath.trim().isEmpty()) {
            throw new IllegalArgumentException("参数错误：dbPath（数据库路径）不能为空！");
        }
        if (tableName == null || tableName.trim().isEmpty() || !tableName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("参数错误：tableName（表名）非法！仅允许字母、数字、下划线");
        }
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("参数错误：data（插入数据）不能为空！至少包含1个键值对");
        }

        // 2. 拼接插入 SQL（参数化查询，避免 SQL 注入）
        // 列名拼接：key1,key2,key3
        String columns = String.join(",", data.keySet().stream()
                .map(String::trim)
                .filter(col -> col.matches("[a-zA-Z0-9_]+")) // 过滤非法列名
                .toList());
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("插入数据中无合法列名！列名仅允许字母、数字、下划线");
        }

        // 参数占位符：?, ?, ?
        String placeholders = String.join(",", Collections.nCopies(data.size(), "?"));
        String insertSQL = String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName.trim(), columns, placeholders);

        // 3. 执行插入（try-with-resources 自动关闭资源）
        DriverManager.setLoginTimeout(5);
        try (Connection conn = DriverManager.getConnection(getSqliteUrl(dbPath));
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            // 4. 绑定参数（适配不同类型的值：字符串、数字、布尔、null 等）
            int paramIndex = 1;
            for (Object value : data.values()) {
                setPreparedStatementParam(pstmt, paramIndex, value);
                paramIndex++;
            }

            // 5. 执行插入
            int affectedRows = pstmt.executeUpdate();
            System.out.println("[SQLite] 单条数据插入成功！表：" + tableName + "，影响行数：" + affectedRows);

        } catch (SQLTimeoutException e) {
            throw new RuntimeException("数据库连接超时（5秒）！路径：" + dbPath, e);
        } catch (SQLException e) {
            throw new RuntimeException("插入数据失败！SQL：" + insertSQL + "，原因：" + e.getMessage(), e);
        }
    }

    /**
     * 给 PreparedStatement 绑定参数，适配 SQLite 支持的类型（字符串、数字、布尔、null 等）
     *
     * @param pstmt PreparedStatement 对象
     * @param index 参数索引（从 1 开始）
     * @param value 要绑定的值（JS 传入的任意类型）
     * @throws SQLException 参数绑定失败时抛出
     */
    private static void setPreparedStatementParam(PreparedStatement pstmt, int index, Object value) throws SQLException {
        if (value == null) {
            pstmt.setNull(index, Types.NULL); // null 值
        } else if (value instanceof String) {
            pstmt.setString(index, (String) value); // 字符串（自动转义单引号）
        } else if (value instanceof Integer) {
            pstmt.setInt(index, (Integer) value); // 整数
        } else if (value instanceof Long) {
            pstmt.setLong(index, (Long) value); // 长整数（时间戳常用）
        } else if (value instanceof Double) {
            pstmt.setDouble(index, (Double) value); // 浮点数
        } else if (value instanceof Boolean) {
            pstmt.setBoolean(index, (Boolean) value); // 布尔值（SQLite 存储为 0/1）
        } else {
            // 其他类型转为字符串存储（兼容 JS 传入的特殊类型）
            pstmt.setString(index, value.toString());
        }
    }

    // ===================== 新增：查询功能 =====================

    /**
     * 单条数据查询（适配 KJS，返回第一条匹配结果）
     *
     * @param dbPath          数据库文件路径（JS 字符串）
     * @param tableName       目标表名（JS 字符串）
     * @param columns         要查询的列（JS 数组/字符串，如 ["id","player_name"] 或 "*"）
     * @param whereConditions 查询条件（JS 对象，如 {player_name:"张三"}，空则查第一条）
     * @param whereLogic      条件逻辑（AND/OR，默认 AND）
     * @return 单条数据（Map → JS 对象），无结果返回 null
     */
    public static Map<String, Object> querySingle(String dbPath, String tableName, Object columns,
                                                  Map<String, Object> whereConditions, String whereLogic) {
        // 1. 入参预处理
        List<Map<String, Object>> resultList = queryList(dbPath, tableName, columns, whereConditions, whereLogic, 1, 0);
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    /**
     * 批量数据查询（核心查询方法，支持分页）
     *
     * @param dbPath          数据库文件路径
     * @param tableName       目标表名
     * @param columns         要查询的列（JS 数组/字符串：数组 → 指定列，字符串 "*" → 所有列）
     * @param whereConditions 查询条件（JS 对象，空则查全表）
     * @param whereLogic      条件逻辑（AND/OR，默认 AND）
     * @param limit           返回条数（JS 数字，0 → 无限制）
     * @param offset          偏移量（JS 数字，分页用，默认 0）
     * @return 查询结果列表（List<Map> → JS 数组），无结果返回空列表
     */
    public static List<Map<String, Object>> queryList(String dbPath, String tableName, Object columns,
                                                      Map<String, Object> whereConditions, String whereLogic,
                                                      int limit, int offset) {
        // 1. 入参校验
        if (dbPath == null || dbPath.trim().isEmpty()) {
            throw new IllegalArgumentException("参数错误：dbPath（数据库路径）不能为空！");
        }
        if (tableName == null || tableName.trim().isEmpty() || !tableName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("参数错误：tableName（表名）非法！");
        }

        // 2. 处理查询列（适配 JS 传入的数组/字符串）
        String queryColumns = "*";
        if (columns != null) {
            if (columns instanceof List<?>) {
                // JS 数组 → 拼接列名
                List<String> colList = ((List<?>) columns).stream()
                        .map(Object::toString)
                        .map(String::trim)
                        .filter(col -> col.matches("[a-zA-Z0-9_*]+"))
                        .toList();
                if (!colList.isEmpty()) {
                    queryColumns = String.join(",", colList);
                }
            } else if (columns instanceof String) {
                // JS 字符串 → 直接使用（如 "*"）
                String colStr = ((String) columns).trim();
                if (!colStr.isEmpty()) {
                    queryColumns = colStr;
                }
            }
        }

        // 3. 拼接 WHERE 条件（参数化，避免注入）
        StringBuilder whereClause = new StringBuilder();
        List<Object> whereParams = new ArrayList<>();
        if (whereConditions != null && !whereConditions.isEmpty()) {
            whereClause.append(" WHERE ");
            String logic = (whereLogic == null || !whereLogic.trim().toUpperCase().matches("AND|OR")) ? "AND" : whereLogic.trim().toUpperCase();
            int condIndex = 0;
            for (Map.Entry<String, Object> cond : whereConditions.entrySet()) {
                String colName = cond.getKey().trim();
                if (!colName.matches("[a-zA-Z0-9_]+")) {
                    throw new IllegalArgumentException("非法条件列名：" + colName);
                }
                if (condIndex > 0) {
                    whereClause.append(" ").append(logic).append(" ");
                }
                whereClause.append(colName).append(" = ?");
                whereParams.add(cond.getValue());
                condIndex++;
            }
        }

        // 4. 拼接 LIMIT/OFFSET（分页）
        StringBuilder limitOffsetClause = new StringBuilder();
        if (limit > 0) {
            limitOffsetClause.append(" LIMIT ").append(limit);
        }
        if (offset >= 0) {
            limitOffsetClause.append(" OFFSET ").append(offset);
        }

        // 5. 完整 SQL
        String querySQL = String.format("SELECT %s FROM %s %s %s",
                queryColumns, tableName.trim(), whereClause, limitOffsetClause);
        System.out.println("[SQLite] 执行查询 SQL：" + querySQL);

        // 6. 执行查询
        List<Map<String, Object>> result = new ArrayList<>();
        DriverManager.setLoginTimeout(5);
        try (Connection conn = DriverManager.getConnection(getSqliteUrl(dbPath));
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {

            // 绑定 WHERE 参数
            for (int i = 0; i < whereParams.size(); i++) {
                setPreparedStatementParam(pstmt, i + 1, whereParams.get(i));
            }

            // 解析结果集（转为 Map，适配 JS）
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String colName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        // 类型适配：SQLite 布尔值转 Boolean，数字转对应类型
                        if (value instanceof Number && metaData.getColumnType(i) == Types.BOOLEAN) {
                            row.put(colName, ((Number) value).intValue() == 1);
                        } else {
                            row.put(colName, value);
                        }
                    }
                    result.add(row);
                }
            }

        } catch (SQLTimeoutException e) {
            throw new RuntimeException("数据库连接超时（5秒）！路径：" + dbPath, e);
        } catch (SQLException e) {
            throw new RuntimeException("查询失败！SQL：" + querySQL + "，原因：" + e.getMessage(), e);
        }

        System.out.println("[SQLite] 查询完成！表：" + tableName + "，返回条数：" + result.size());
        return result;
    }

    // ===================== 新增：删除功能 =====================

    /**
     * 条件删除数据（禁止无条件删除，避免误删全表）
     *
     * @param dbPath          数据库文件路径（JS 字符串）
     * @param tableName       目标表名（JS 字符串）
     * @param whereConditions 删除条件（JS 对象，必填！如 {player_name:"张三"}）
     * @param whereLogic      条件逻辑（AND/OR，默认 AND）
     * @return 影响行数（JS 数字）
     */
    public static int deleteData(String dbPath, String tableName, Map<String, Object> whereConditions, String whereLogic) {
        // 1. 入参校验（强制条件删除，禁止 WHERE 为空）
        if (dbPath == null || dbPath.trim().isEmpty()) {
            throw new IllegalArgumentException("参数错误：dbPath（数据库路径）不能为空！");
        }
        if (tableName == null || tableName.trim().isEmpty() || !tableName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("参数错误：tableName（表名）非法！");
        }
        if (whereConditions == null || whereConditions.isEmpty()) {
            throw new IllegalArgumentException("禁止无条件删除！请传入 whereConditions（删除条件）");
        }

        // 2. 拼接删除 SQL（参数化）
        StringBuilder whereClause = new StringBuilder(" WHERE ");
        List<Object> whereParams = new ArrayList<>();
        String logic = (whereLogic == null || !whereLogic.trim().toUpperCase().matches("AND|OR")) ? "AND" : whereLogic.trim().toUpperCase();

        int condIndex = 0;
        for (Map.Entry<String, Object> cond : whereConditions.entrySet()) {
            String colName = cond.getKey().trim();
            if (!colName.matches("[a-zA-Z0-9_]+")) {
                throw new IllegalArgumentException("非法条件列名：" + colName);
            }
            if (condIndex > 0) {
                whereClause.append(" ").append(logic).append(" ");
            }
            whereClause.append(colName).append(" = ?");
            whereParams.add(cond.getValue());
            condIndex++;
        }

        String deleteSQL = String.format("DELETE FROM %s %s", tableName.trim(), whereClause);
        System.out.println("[SQLite] 执行删除 SQL：" + deleteSQL);

        // 3. 执行删除
        DriverManager.setLoginTimeout(5);
        try (Connection conn = DriverManager.getConnection(getSqliteUrl(dbPath));
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            // 绑定条件参数
            for (int i = 0; i < whereParams.size(); i++) {
                setPreparedStatementParam(pstmt, i + 1, whereParams.get(i));
            }

            // 执行删除，返回影响行数
            int affectedRows = pstmt.executeUpdate();
            System.out.println("[SQLite] 删除完成！表：" + tableName + "，影响行数：" + affectedRows);
            return affectedRows;

        } catch (SQLTimeoutException e) {
            throw new RuntimeException("数据库连接超时（5秒）！路径：" + dbPath, e);
        } catch (SQLException e) {
            throw new RuntimeException("删除失败！SQL：" + deleteSQL + "，原因：" + e.getMessage(), e);
        }
    }

    // ===================== 便捷重载方法（简化 KJS 调用） =====================
    // 查询单条：默认 AND 逻辑
    public static Map<String, Object> querySingle(String dbPath, String tableName, Object columns, Map<String, Object> whereConditions) {
        return querySingle(dbPath, tableName, columns, whereConditions, "AND");
    }

    // 查询单条：默认查所有列 + AND 逻辑
    public static Map<String, Object> querySingle(String dbPath, String tableName, Map<String, Object> whereConditions) {
        return querySingle(dbPath, tableName, "*", whereConditions, "AND");
    }

    // 查询列表：默认无分页 + AND 逻辑
    public static List<Map<String, Object>> queryList(String dbPath, String tableName, Object columns, Map<String, Object> whereConditions) {
        return queryList(dbPath, tableName, columns, whereConditions, "AND", 0, 0);
    }

    // 查询列表：默认查所有列 + 无分页 + AND 逻辑
    public static List<Map<String, Object>> queryList(String dbPath, String tableName, Map<String, Object> whereConditions) {
        return queryList(dbPath, tableName, "*", whereConditions, "AND", 0, 0);
    }

    // 删除：默认 AND 逻辑
    public static int deleteData(String dbPath, String tableName, Map<String, Object> whereConditions) {
        return deleteData(dbPath, tableName, whereConditions, "AND");
    }
}