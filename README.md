# Projeto Integrado - P2

## Instruções

Sistema FatecRoom – Reserva de Salas, Laboratórios e Recursos

## Objetivo do Projeto

Desenvolver um sistema **modular** em **Java** que permita gerenciar reservas de
salas, laboratórios e recursos da Fatec.

O sistema deve utilizar arquivos CSV/TXT como banco de dados, empregar
estruturas condicionais e de repetição, Arrays/ArrayList, tratamento de
exceções, manipulação de Strings, e interfaces gráficas com Swing, conforme os
conteúdos trabalhados no semestre. 

Cada grupo ficará responsável pela implementação de um módulo, e ao final todos
serão integrados em um sistema único. 

## Funcionalidades do Sistema (módulos)

Cada módulo é independente e representará uma parte do sistema final.

1. Cadastro de Salas e Laboratórios  - GRUPO 9
- Inserir novas salas
- Listar salas
- Salvar no arquivo salas.csv (id_sala;nome_sala;tipo_sala;capacidade;bloco;observacao)
1. Cadastro de Recursos (projetores, notebooks etc.)  - GRUPO 8
- Inserir, listar e editar recursos
- Salvar em recursos.csv (id_recurso;nome_recurso;tipo_recurso;patrimonio;local_padrao;observacao)
1. Cadastro de Usuários (professores, turmas etc.) - GRUPO 6
- Inserir, listar e validar login
- Salvar em usuarios.csv (id_usuario;nome_usuario;tipo_usuario;email;senha;ativo)
2. Reserva de Sala/Laboratório - GRUPO 7
- Validar disponibilidade
- Registrar a reserva em reservas_salas.csv (id_reserva;id_sala;id_usuario;data_reserva;hora_inicio;hora_fim;status;observacao)
3. Reserva de Recursos - GRUPO 3
- Validar disponibilidade
- Registrar em reservas_recursos.csv (id_reserva_recurso;id_recurso;id_usuario;data_reserva;hora_inicio;hora_fim;status;observacao)
4. Consulta Avançada de Reservas - GRUPO 7
- Buscar por sala, usuário, data
- Exibir dados filtrados
5. Cancelamento de Reservas - GRUPO 5
- Localizar registro
- Reescrever o arquivo sem o registro cancelado
6. Relatórios  - GRUPO 2
- Contagem de reservas
- Sala mais utilizada
- Usuário que mais reserva
- Exportar relatório .txt
7.  Dashboard Inicial (Swing) - GRUPO 1
- Mostrar resumo do sistema
- Botões para abrir módulos (telas externas)
8.  Tela de Login (Swing) - GRUPO 4
- Validar com usuarios.csv
- Redirecionar para o menu principal

Todos geram Arquivo de Log - logs_operacoes.csv (data_hora;id_usuario;acao;detalhe)