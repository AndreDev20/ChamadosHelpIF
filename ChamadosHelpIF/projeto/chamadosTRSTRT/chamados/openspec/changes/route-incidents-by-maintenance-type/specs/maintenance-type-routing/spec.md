## ADDED Requirements

### Requirement: Maintenance types are defined
The system SHALL provide a controlled list of maintenance types for campus operations: technology, electrical, hydraulic, infrastructure, and furniture.

#### Scenario: Type options are shown in forms
- **WHEN** an administrator creates or edits a maintenance user, or a user opens a new incident
- **THEN** the system SHALL offer the same controlled maintenance type options in the form

### Requirement: Maintenance users have a specialty type
The system SHALL require users with the `MANUTENCAO` role to have exactly one maintenance type and SHALL not require a maintenance type for `ADMIN` or `COMUM` users.

#### Scenario: Admin creates a maintenance user with type
- **WHEN** an administrator submits a new user with role `MANUTENCAO` and a valid maintenance type
- **THEN** the system SHALL persist the user with that maintenance type

#### Scenario: Admin creates a maintenance user without type
- **WHEN** an administrator submits a new user with role `MANUTENCAO` without a maintenance type
- **THEN** the system SHALL reject the submission with a validation message

#### Scenario: Admin creates a common user without type
- **WHEN** an administrator submits a new user with role `COMUM` without a maintenance type
- **THEN** the system SHALL persist the user without requiring a maintenance type

### Requirement: Incidents have a required maintenance type
The system SHALL require each new incident to include one maintenance type that identifies the specialty needed to resolve the incident.

#### Scenario: User opens an incident with type
- **WHEN** an authenticated user submits a new incident with a valid maintenance type
- **THEN** the system SHALL persist the incident with that maintenance type

#### Scenario: User opens an incident without type
- **WHEN** an authenticated user submits a new incident without a maintenance type
- **THEN** the system SHALL reject the incident with a validation message

### Requirement: Automatic assignment matches incident type
The system SHALL automatically assign a new incident only to a maintenance user whose maintenance type matches the incident maintenance type.

#### Scenario: Matching technician exists
- **WHEN** a new incident is submitted with type `ELETRICA` and at least one maintenance user has type `ELETRICA`
- **THEN** the system SHALL assign the incident to one of the `ELETRICA` maintenance users

#### Scenario: Multiple matching technicians exist
- **WHEN** a new incident is submitted and multiple maintenance users match its maintenance type
- **THEN** the system SHALL assign the incident to the matching technician with the lowest number of open incidents

#### Scenario: No matching technician exists
- **WHEN** a new incident is submitted and no maintenance user matches its maintenance type
- **THEN** the system SHALL not create the incident and SHALL show that no technician is available for the selected type

### Requirement: Maintenance type is visible in operational tables
The system SHALL display the maintenance type for users and incidents in administrative and maintenance views where assignment decisions are reviewed.

#### Scenario: Admin reviews incident list
- **WHEN** an administrator views the admin panel
- **THEN** each listed incident SHALL show its maintenance type

#### Scenario: Maintenance user reviews incident list
- **WHEN** a maintenance user views the maintenance incident list or incident details
- **THEN** each incident SHALL show its maintenance type

#### Scenario: Admin reviews user list
- **WHEN** an administrator views the user list
- **THEN** maintenance users SHALL show their maintenance type
