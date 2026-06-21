@echo off
setlocal

set "PORT=8080"
set "SPRING_DATASOURCE_URL=jdbc:h2:mem:chamados_db;MODE=MariaDB;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
set "SPRING_DATASOURCE_USERNAME=sa"
set "SPRING_DATASOURCE_PASSWORD="
set "SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver"
set "SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop"

if not exist target mkdir target

echo Starting HelpIF on http://localhost:%PORT% with H2 in-memory database...
java -jar target\chamados-0.0.1-SNAPSHOT.jar
