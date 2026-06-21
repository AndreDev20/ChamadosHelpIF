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
    private static final String TIPO_PADRAO = "INFRAESTRUTURA";

    private final DataSource dataSource;

    public DatabaseMigrationRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        try (Connection conn = dataSource.getConnection()) {
            ensureObservacaoTecnica(conn);
            ensureTipoUsuarios(conn);
            ensureTipoIncidentes(conn);
        } catch (Exception e) {
            log.error("Erro ao executar migracao do banco: {}", e.getMessage(), e);
        }
    }

    private void ensureObservacaoTecnica(Connection conn) throws Exception {
        ResultSet rs = conn.getMetaData().getColumns(null, null, "incidentes", "observacao_tecnica");
        if (!rs.next()) {
            log.info("Coluna 'observacao_tecnica' nao encontrada. Criando como nullable...");
            conn.createStatement().execute(
                    "ALTER TABLE incidentes ADD COLUMN observacao_tecnica TEXT"
            );
            log.info("Coluna 'observacao_tecnica' criada com sucesso.");
            return;
        }

        log.info("Coluna 'observacao_tecnica' ja existe. Garantindo que e nullable...");
        try {
            conn.createStatement().execute(
                    "ALTER TABLE incidentes ALTER COLUMN observacao_tecnica DROP NOT NULL"
            );
            log.info("Constraint NOT NULL removida de 'observacao_tecnica'.");
        } catch (Exception ex) {
            log.info("Coluna ja era nullable, nenhuma alteracao necessaria.");
        }
        conn.createStatement().execute(
                "UPDATE incidentes SET observacao_tecnica = NULL WHERE observacao_tecnica = ''"
        );
    }

    private void ensureTipoUsuarios(Connection conn) throws Exception {
        ResultSet rs = conn.getMetaData().getColumns(null, null, "usuarios", "tipo");
        if (!rs.next()) {
            log.info("Coluna 'tipo' em usuarios nao encontrada. Criando como nullable...");
            conn.createStatement().execute("ALTER TABLE usuarios ADD COLUMN tipo VARCHAR(32)");
        }

        conn.createStatement().execute(
                "UPDATE usuarios SET tipo = '" + TIPO_PADRAO + "' WHERE role = 'MANUTENCAO' AND tipo IS NULL"
        );
        conn.createStatement().execute(
                "UPDATE usuarios SET tipo = NULL WHERE role <> 'MANUTENCAO'"
        );
    }

    private void ensureTipoIncidentes(Connection conn) throws Exception {
        ResultSet rs = conn.getMetaData().getColumns(null, null, "incidentes", "tipo");
        if (!rs.next()) {
            log.info("Coluna 'tipo' em incidentes nao encontrada. Criando com valor padrao...");
            conn.createStatement().execute(
                    "ALTER TABLE incidentes ADD COLUMN tipo VARCHAR(32) DEFAULT '" + TIPO_PADRAO + "'"
            );
        }

        conn.createStatement().execute(
                "UPDATE incidentes SET tipo = '" + TIPO_PADRAO + "' WHERE tipo IS NULL"
        );
    }
}
