## 1. Domain Model and Persistence

- [x] 1.1 Create `TipoManutencao` enum with `TECNOLOGIA`, `ELETRICA`, `HIDRAULICA`, `INFRAESTRUTURA`, and `MOBILIARIO`, including display labels for Thymeleaf views.
- [x] 1.2 Add nullable `tipo` field to `Usuario` using `@Enumerated(EnumType.STRING)` and validation rules that require it only when `role` is `MANUTENCAO`.
- [x] 1.3 Add required `tipo` field to `Incidente` using `@Enumerated(EnumType.STRING)`.
- [x] 1.4 Update database migration logic to add `tipo` to `usuarios` and `incidentes`, populate legacy maintenance users and incidents with a safe default, and keep non-maintenance users nullable.

## 2. Repository and Service Rules

- [x] 2.1 Add repository support for finding maintenance users by `Role.MANUTENCAO` and `TipoManutencao`.
- [x] 2.2 Update `AtribuicaoService` to accept an incident type, filter technicians by matching type, and then choose the lowest open workload.
- [x] 2.3 Update `IncidenteService.criar` and related update methods to require and persist the incident type.
- [x] 2.4 Update `UsuarioService.criar` and `UsuarioService.atualizar` to persist type for maintenance users and clear or ignore type for non-maintenance users.
- [x] 2.5 Return a clear validation error when no technician exists for the selected incident type.

## 3. Controllers and Forms

- [x] 3.1 Update admin user creation and editing endpoints to receive `TipoManutencao` and expose type options to templates.
- [x] 3.2 Update incident creation and admin incident editing endpoints to receive and expose `TipoManutencao`.
- [x] 3.3 Update `incidente/novo.html` to require a type selection when opening a new incident.
- [x] 3.4 Update admin user forms to show type selection when role is `MANUTENCAO` and avoid requiring it for other roles.
- [x] 3.5 Update admin and maintenance incident tables/details to display the incident type.
- [x] 3.6 Update admin user list/table to display the maintenance type for maintenance users.

## 4. Verification

- [x] 4.1 Add service-level tests for assigning incidents only to technicians with matching type.
- [x] 4.2 Add tests for missing type on maintenance users and incidents.
- [x] 4.3 Add a test or manual verification path for the no-compatible-technician error.
- [x] 4.4 Run the project test suite or document why it could not be run in the current environment.

Note: tests were run with the Maven bundled in IntelliJ IDEA. Result: 5 tests run, 0 failures, 0 errors.
