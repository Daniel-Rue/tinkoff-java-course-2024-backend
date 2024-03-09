package edu.java.scrapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
public class IntegrationTest extends IntegrationEnvironment {

    @Test
    public void testTablesCreation() throws Exception {
        try (Connection connection = POSTGRES.createConnection("");
             Statement statement = connection.createStatement()) {
            assertTrue(tableExists(statement, "link"), "Table 'link' was not created.");
            assertTrue(tableExists(statement, "chat"), "Table 'chat' was not created.");
            assertTrue(tableExists(statement, "chat_link"), "Table 'chat_link' was not created.");
        }
    }

    private boolean tableExists(Statement statement, String tableName) throws Exception {
        ResultSet rs = statement.executeQuery("SELECT to_regclass('public." + tableName + "');");
        if (rs.next()) {
            return rs.getString(1) != null;
        }
        return false;
    }
}
