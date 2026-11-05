# ☕ JavaBeans IA

MVP de um simulador interativo de cafeteria desenvolvido em Java e Spring Boot, com integração de Inteligência Artificial para tornar a experiência mais dinâmica e personalizada.

O projeto simula o atendimento em uma cafeteria, permitindo que o jogador interaja com clientes, prepare bebidas de acordo com receitas e acumule pontos conforme seu desempenho.

## Funcionalidades

- **Gerenciamento de sessões:** inicialização e acompanhamento do progresso dos jogadores.
- **Cenários interativos:** clientes e receitas com diferentes combinações de ingredientes.
- **Preparação de bebidas:** validação dos ingredientes selecionados conforme a receita esperada.
- **Sistema de pontuação:** cálculo e acúmulo de XP conforme o desempenho.
- **Feedback com IA:** geração de respostas contextualizadas a partir das interações do jogador.

## Tecnologias

| Tecnologia | Aplicação |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 3.3.4 | Framework backend |
| Spring Data JPA | Persistência de dados |
| H2 Database | Banco de dados |
| Maven | Gerenciamento de dependências |
| Inteligência Artificial | Geração de feedback e interações |

## Estrutura do projeto

```text
src/main/java/com/animahub/javabeans/ia/
├── dto/
├── engine/
├── model/
├── repository/
├── service/
└── ...
