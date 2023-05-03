
<h2 align="center"> 
  🍀 Api Trevo 🍀
</h1>

<p align="center"> API que gerencia os pulverizadores de fertilizantes da indústria trevo. </p>
<p align="center">
  <img height="350" src="https://user-images.githubusercontent.com/61324956/223739972-1cc74f2c-3fb9-46f7-bdae-4079324af744.png" />
</p>
- Objetivo é divulgar um novo portfólio de produtos para os clientes da Indústria Trevo, bem como captar as propostas de interesse nesses produtos.

### Diagrama de E/R

<p align="center">
  <img height="400" src="https://user-images.githubusercontent.com/61324956/230724211-c3635149-5d8e-404e-9365-0c0917a80cc5.png" />
</p>

### Docker


Build da imagem

```bash
  docker build -t <NOME_IMAGEM>:<VERSAO> /path/to/dockerfile/directory
```

Run docker

```bash
  docker run -d -p <PORTA>:8081 -e DATASOURCE_URL=<VARIAVEL_AMBIENTE> -e DATASOURCE_PASSWORD=<VARIAVEL_AMBIENTE> -e JWT_SECRET=<VARIAVEL_AMBIENTE> -e DATASOURCE_USERNAME=<VARIAVEL_AMBIENTE> <IMAGEM>
```

Execultar docker

```bash
  docker exec -it <IMAGEM> bash
```


### Swagger


URL da documentação 

```bash
  http://18.212.22.68:8081/swagger-ui.html
```



## Stack utilizada
  - <img src="https://img.icons8.com/color/48/000000/java-coffee-cup-logo--v1.png"/> 11.0.17 
  - <img src="https://img.icons8.com/color/48/000000/spring-logo.png"/> 2.6.3
  - <img src="https://img.icons8.com/color/48/000000/jenkins.png"/> 2.303.2
 
 
 

