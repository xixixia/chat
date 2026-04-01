package com.mallchat.controller;

import com.mallchat.common.Result;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口。
 */
@RestController
@RequestMapping("/health")
public class HealthController {
    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 服务健康检查。
     *
     * @return 状态
     */
    @GetMapping
    public Result<String> health() {
        return Result.ok("OK");
    }

    /**
     * 数据库健康检查。
     *
     * @return 状态
     * @throws Exception 数据库异常
     */
    @GetMapping("/db")
    public Result<String> db() throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT 1");
            return Result.ok("DB_OK");
        }
    }
}
