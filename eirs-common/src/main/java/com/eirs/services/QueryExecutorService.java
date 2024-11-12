package com.eirs.services;

import com.eirs.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueryExecutorService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private AppConfig appConfig;

    @Autowired
    ModuleAlertService moduleAlertService;

    public Integer execute(String query) {
        long start = System.currentTimeMillis();
        log.info("DB[{}] Going to Execute Query:{}", appConfig.getDbType(), query);
        Integer result = jdbcTemplate.update(query);
        log.info("DB[{}] Executed TimeTaken:{} Result:{} Query:{}", appConfig.getDbType(), (System.currentTimeMillis() - start), result, query);
        return result;
    }

    public Integer executeCreate(String query) {
        long start = System.currentTimeMillis();
        try {
            log.info("DB[{}] Going to Execute Query:{}", appConfig.getDbType(), query);
            Integer result = jdbcTemplate.update(query);
            log.info("DB[{}] Executed TimeTaken:{} Result:{} Query:{}", appConfig.getDbType(), (System.currentTimeMillis() - start), result, query);
            return result;
        } catch (Exception e) {
            log.error("Error while executing TimeTaken:{} Query:{} Error:{}", (System.currentTimeMillis() - start), query, e.getMessage());
            throw e;
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
