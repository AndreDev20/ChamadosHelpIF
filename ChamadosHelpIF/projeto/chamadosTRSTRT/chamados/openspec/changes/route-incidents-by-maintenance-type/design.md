## Context

O HelpIF hoje modela usuarios com `Role` (`ADMIN`, `MANUTENCAO`, `COMUM`) e incidentes com categoria, bloco, local, observacao, anexos, solicitante, responsavel, data e status. A atribuicao automatica escolhe o tecnico de manutencao com menor quantidade de chamados abertos, mas nao considera a natureza do problema.

A nova regra precisa conectar tres pontos do fluxo:

- O administrador cadastra ou edita usuarios de manutencao com uma especialidade.
- O usuario informa o tipo/especialidade do chamado ao abrir a solicitacao.
- A atribuicao automatica escolhe um tecnico de manutencao com a mesma especialidade do chamado.

## Goals / Non-Goals

**Goals:**

- Criar uma taxonomia inicial de 5 tipos de manutencao: `TECNOLOGIA`, `ELETRICA`, `HIDRAULICA`, `INFRAESTRUTURA`, `MOBILIARIO`.
- Persistir o tipo do tecnico em `usuarios` quando `role = MANUTENCAO`.
- Persistir o tipo requerido pelo chamado em `incidentes`.
- Garantir que a atribuicao automatica considere o tipo do chamado antes de balancear carga.
- Atualizar formularios e tabelas para capturar, editar e exibir o tipo.
- Manter compatibilidade com chamados e usuarios ja existentes durante migracao.

**Non-Goals:**

- Criar roteamento multi-especialidade para um unico chamado.
- Permitir que um tecnico tenha varias especialidades.
- Adicionar workflow de reatribuicao manual avancada ou fila sem responsavel.
- Alterar os papeis de usuario existentes.
- Substituir o armazenamento atual de anexos.

## Decisions

### Decision: Representar tipos com enum dedicado

Criar um enum `TipoManutencao` com os valores iniciais `TECNOLOGIA`, `ELETRICA`, `HIDRAULICA`, `INFRAESTRUTURA` e `MOBILIARIO`.

Rationale: o dominio precisa de uma lista pequena, controlada e exibivel nos formularios. Um enum segue o padrao atual de `Role`, `StatusIncidente`, `CategoriaIncidente`, `BlocoLocal` e `LocalEspecifico`.

Alternative considered: tabela relacional `tipos_manutencao`. Isso facilitaria configuracao dinamica, mas adicionaria telas, CRUD e migracoes extras antes de haver necessidade real.

### Decision: Usar o mesmo enum em usuarios e incidentes

Adicionar `tipo` em `Usuario` e `Incidente` com `@Enumerated(EnumType.STRING)`.

Rationale: o tipo do chamado e o tipo do tecnico fazem parte da mesma taxonomia. Usar o mesmo enum evita divergencia entre listas.

Alternative considered: campos separados (`tipoUsuario`, `tipoIncidente`) com enums distintos. Isso reduziria acoplamento conceitual, mas duplicaria valores e validacoes sem ganho pratico.

### Decision: Tornar tipo obrigatorio para manutencao e chamados novos

Usuarios com papel `MANUTENCAO` MUST ter tipo. Usuarios `ADMIN` e `COMUM` MUST NOT depender de tipo. Chamados novos MUST ter tipo.

Rationale: sem tipo no chamado nao existe roteamento especializado; sem tipo no tecnico nao existe correspondencia confiavel.

Alternative considered: permitir tipo vazio e cair para qualquer tecnico. Isso preserva o comportamento antigo, mas enfraquece a feature e pode esconder cadastro incompleto.

### Decision: Atribuir por tipo e depois por menor carga aberta

`AtribuicaoService` deve receber o tipo do chamado, buscar tecnicos `MANUTENCAO` do mesmo tipo e escolher entre eles o tecnico com menor quantidade de chamados nao concluidos.

Rationale: preserva a ideia atual de balanceamento, mas aplica primeiro a regra de especialidade.

Alternative considered: round-robin puro por tipo. Seria simples, mas perderia a nocao de carga real que o sistema ja usa.

### Decision: Falha explicita quando nao houver tecnico compativel

Se nao existir tecnico de manutencao cadastrado com o tipo do chamado, o sistema deve impedir a criacao do chamado e informar que nao ha tecnico disponivel para aquele tipo.

Rationale: criar um chamado sem responsavel em um fluxo que promete atribuicao automatica pode gerar tickets invisiveis operacionalmente. A falha explicita forca o cadastro correto de tecnicos.

Alternative considered: salvar o chamado sem responsavel. Isso exigiria uma fila de triagem sem dono e mudaria o processo de atendimento.

## Risks / Trade-offs

- Dados existentes sem tipo -> aplicar migracao com valor padrao para permitir que o app continue subindo; depois administradores podem ajustar os tipos corretos.
- Tecnicos cadastrados com tipo errado -> chamados vao para a especialidade errada; mitigar exibindo tipo em tabelas administrativas e telas de edicao.
- Tipo obrigatorio em formularios antigos ou requests incompletos -> validar no controller/service e retornar mensagem clara.
- Enum fixo pode exigir deploy para adicionar novos tipos -> aceitavel agora, pois a lista inicial e pequena e controlada.
- A query de carga por responsavel continua sendo chamada por tecnico candidato -> aceitavel para volume pequeno; se crescer, agregar em query unica por tipo.

## Migration Plan

1. Adicionar enum `TipoManutencao`.
2. Adicionar coluna `tipo` em `usuarios`, nullable no banco para preservar `ADMIN` e `COMUM`.
3. Adicionar coluna `tipo` em `incidentes`, com valor padrao para registros existentes.
4. Preencher usuarios `MANUTENCAO` existentes com um tipo padrao inicial, preferencialmente `INFRAESTRUTURA`, para evitar falha em producao.
5. Atualizar formularios para exigir tipo apenas quando o usuario for manutencao e sempre para novos chamados.
6. Atualizar a atribuicao automatica para filtrar por tipo.
7. Revisar dados em producao e ajustar manualmente as especialidades dos tecnicos.

Rollback: manter as colunas adicionadas sem uso e reverter o codigo para a atribuicao anterior. Como os campos sao aditivos, rollback nao deve exigir remover colunas imediatamente.

## Open Questions

- O tipo padrao para dados legados deve ser `INFRAESTRUTURA` ou outro valor escolhido pela equipe do campus?
- O administrador deve poder editar o tipo de chamados ja concluidos ou apenas chamados abertos?
