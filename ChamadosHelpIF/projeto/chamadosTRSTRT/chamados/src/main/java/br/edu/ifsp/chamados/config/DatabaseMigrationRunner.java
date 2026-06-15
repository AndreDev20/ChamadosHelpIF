package br.edu.ifsp.chamados.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

@Component
public class DatabaseMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationRunner.class);
    private final DataSource dataSource;

    public DatabaseMigrationRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getColumns(null, null, "incidentes", "observacao_tecnica");
            if (!rs.next()) {
                // Coluna não existe — cria como nullable
                log.info("Coluna 'observacao_tecnica' nao encontrada. Criando como nullable...");
                conn.createStatement().execute(
                    "ALTER TABLE incidentes ADD COLUMN observacao_tecnica TEXT"
                );
                log.info("Coluna 'observacao_tecnica' criada com sucesso.");
            } else {
                // Coluna existe — garante que é nullable (remove NOT NULL se houver)
                log.info("Coluna 'observacao_tecnica' ja existe. Garantindo que e nullable...");
                try {
                    conn.createStatement().execute(
                        "ALTER TABLE incidentes ALTER COLUMN observacao_tecnica DROP NOT NULL"
                    );
                    log.info("Constraint NOT NULL removida de 'observacao_tecnica'.");
                } catch (Exception ex) {
                    log.info("Coluna ja era nullable, nenhuma alteracao necessaria.");
                }
                // Zera strings vazias que possam ter sido salvas
                conn.createStatement().execute(
                    "UPDATE incidentes SET observacao_tecnica = NULL WHERE observacao_tecnica = ''"
                );
            }
        } catch (Exception e) {
            log.error("Erro ao executar migracao do banco: {}", e.getMessage(), e);
        }
    }
}
