# Movies Battle

## Descrição
API REST para uma aplicação ao estilo card game, onde serão informados dois filmes e o jogador deve acertar aquele que possui melhor avaliação no IMDB.

## Requisitos
1. O jogador deve fazer login para iniciar uma nova partida. Portanto, cada partida sempre será identificada pela autenticação do usuário.
2. Cada rodada do jogo consiste em informar um par de filmes, observando para não repetir o mesmo par nem formar um par com um único filme.
a. São sequências não-válidas: [A-A] o mesmo filme repetido; [A-B, A-B] pares repetidos – considere iguais os pares do tipo A-B e B-A.
b. Os seguintes pares são válidos: [A-B, B-C] o filme B é usado em pares diferentes.
3. O jogador deve tentar acertar qual filme possui maior pontuação, composta pela nota (0.0-10.0) multiplicado pelo total de votos.
4. Se escolher o vencedor correto, conta 1 ponto. São permitidos até três erros. Após responder, terá acesso a novo par de filmes quando acessar o endpoint do quiz.
5. Forneça endpoints específicos para iniciar e encerrar a partida a qualquer momento. Valide o momento em que cada funcionalidade pode ser acionada.
6. Não deve ser possível avançar para o próximo par sem responder o atual.
7. Deve existir uma funcionalidade de ranking, exibindo os melhores jogadores e suas pontuações.
8. A pontuação é obtida multiplicando a quantidade de quizzes respondidos pela porcentagem de acerto.

## Solução 
Foi desenvolvido uma API em arquitetura em microservices com Spring (frameworks Spring: Web, Boot, Data, Security, Cloud) e Netflix Eureka e Gateway.
A autenticação foi desenvolvida com Spring Security e JWT.
Os dados foram recuperados da API http://www.omdbapi.com e armazenados no banco de dados H2 em memória.
 
## Microservices: 
- Userauth: responsável pela criação e autenticação dos usuários.
- Movies: disponibiliza e recupera os dados dos filmes da API omdapi. Também realiza a validação da pontuação entre os filmes.
- Game: controla a execução do jogo. Cria, valida, finaliza e fornece relatório final e ranking geral do game.

## Players iniciais
Na inicialização são criados dois usuários jogadores. Mas também é possível registrar outros.
 
- Username: player1
- Password: 1234
 
- Username: player2
- Password: 1234
 
