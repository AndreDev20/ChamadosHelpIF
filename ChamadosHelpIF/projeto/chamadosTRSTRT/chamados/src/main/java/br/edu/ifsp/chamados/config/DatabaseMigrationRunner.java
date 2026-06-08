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
            // Verifica se a coluna observacao_tecnica já existe na tabela incidentes
            ResultSet rs = conn.getMetaData().getColumns(null, null, "incidentes", "observacao_tecnica");
            if (!rs.next()) {
                log.info("Coluna 'observacao_tecnica' não encontrada. Criando...");
                conn.createStatement().execute(
                    "ALTER TABLE incidentes ADD COLUMN observacao_tecnica TEXT"
                );
                log.info("Coluna 'observacao_tecnica' criada com sucesso.");
            } else {
                log.info("Coluna 'observacao_tecnica' já existe. Nenhuma migração necessária.");
            }
        } catch (Exception e) {
            log.error("Erro ao executar migração do banco: {}", e.getMessage(), e);
        }
    }
}
