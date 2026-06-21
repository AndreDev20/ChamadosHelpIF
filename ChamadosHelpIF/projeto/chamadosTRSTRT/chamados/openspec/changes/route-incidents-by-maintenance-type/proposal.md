## Why

Hoje todos os chamados sao distribuidos entre usuarios de manutencao sem considerar a especialidade do tecnico. Isso pode encaminhar problemas de tecnologia, eletrica, infraestrutura ou mobiliario para pessoas sem o perfil adequado, atrasando o atendimento e exigindo redistribuicao manual.

## What Changes

- Adicionar um campo `tipo` aos usuarios com perfil `MANUTENCAO`, representando sua especialidade operacional.
- Definir de 4 a 5 tipos de manutencao iniciais para o campus: tecnologia, eletrica, hidraulica, infraestrutura e mobiliario.
- Adicionar um campo `tipo` aos chamados para indicar qual especialidade deve atender a demanda.
- Atualizar o cadastro/edicao de usuarios para permitir selecionar o tipo quando o papel for `MANUTENCAO`.
- Atualizar o cadastro/edicao de chamados para capturar e exibir o tipo do chamado.
- Alterar a atribuicao automatica para escolher apenas tecnicos de manutencao com tipo correspondente ao tipo do chamado.
- Atualizar as tabelas de banco de dados necessarias para persistir o tipo em usuarios e incidentes.

## Capabilities

### New Capabilities

- `maintenance-type-routing`: Define tipos de manutencao, associa tecnicos e chamados a esses tipos, e direciona chamados automaticamente para tecnicos compativeis.

### Modified Capabilities

- None.

## Impact

- Entidades JPA: `Usuario` e `Incidente`.
- Enum novo ou equivalente para representar os tipos de manutencao.
- Repositorios e consultas usadas para listar tecnicos e contar chamados abertos por responsavel.
- Servicos: `UsuarioService`, `IncidenteService`, `AtribuicaoService` e fluxos de registro/admin quando criarem usuarios de manutencao.
- Controllers e templates Thymeleaf de admin, manutencao e abertura de chamado.
- Banco de dados: novas colunas em `usuarios` e `incidentes`, com migracao ou ajuste controlado para dados existentes.
- Testes de regra de negocio para atribuicao por tipo e validacao de formularios.
