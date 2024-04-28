# Servidor Multicast e TCP

O diretório [servidor](servidor/) contém a implementação da parte servidor deste projeto. Ele possui duas partes: servidor de descoberta via multicast e um servidor TCP que retorna a mensagem enviada pelo ciente toda em letras maiúsculas.

Primeiramente, para realizar as instruções a seguir, clone este repositório e entre no diretório do mesmo:

```bash
git clone https://github.com/STD29006-classroom/2024-01-lista-01-jpmsb
cd 2024-01-lista-01-jpmsb
```

## Construção da imagem do servidor

Para criar a imagem de contêiner do servidor, execute o comando abaixo:

```bash
docker build -t jpmsb/servidor servidor
```

Verifique a criação com o comando:

```bash
docker images

REPOSITORY      TAG      IMAGE ID      CREATED         SIZE
jpmsb/servidor  latest   38b895dde15d  2 minutes ago  167 MB
```

## Criação da rede personalizada

Para criar a rede personalizada, execute o comando abaixo:

```bash
docker network create jpmsb-std
```

## Instanciação do servidor

Para instanciar o servidor, basta executar o comando abaixo:

```bash
docker run -it --rm --name servidor --net jpmsb-std jpmsb/servidor
```

A saída na tela deverá ser semelhante ao mostrado abaixo:

```
2024/04/28 17:23:01 - INFO - Identificando as interfaces de rede...
2024/04/28 17:23:01 - INFO - Nome: eth0         Endereço: 10.89.0.2

2024/04/28 17:23:01 - INFO - ░▒▒ Servidor de descoberta Multicast iniciado! ▒▒░
2024/04/28 17:23:01 - INFO - Endereço do grupo multicast: 231.0.0.0     Porta: 8888

2024/04/28 17:23:01 - INFO - ░▒▒ Iniciando o servidor TCP ▒▒░
2024/04/28 17:23:01 - INFO - Servidor aguardando conexões em 0.0.0.0/0.0.0.0:51000

2024/04/28 17:23:01 - INFO - Pressione CTRL+C para encerrar o servidor.
```

É possível utilizar variáveis de ambiente para mudar o comportamento do servidor. As seguintes variáveis estão disponíveis:

 - `ENDERECO_MULTICAST`: Endereço do grupo multicast. Padrão: `231.0.0.0`;
 - `PORTA_MULTICAST`: Porta do grupo multicast. Padrão: `8888`;
 - `PORTA_TCP`: Porta do servidor TCP. Padrão: `51000`.

Por exemplo:

```bash
docker run -it --rm --name servidor --net jpmsb-std -e ENDERECO_MULTICAST="234.0.0.1" -e PORTA_MULTICAST="12000" -e PORTA_TCP="12345" jpmsb/servidor
```

O comando acima produzirá a saída abaixo:

```
2024/04/28 17:26:42 - INFO - Identificando as interfaces de rede...
2024/04/28 17:26:42 - INFO - Nome: eth0         Endereço: 10.89.0.3

2024/04/28 17:26:42 - INFO - ░▒▒ Servidor de descoberta Multicast iniciado! ▒▒░
2024/04/28 17:26:42 - INFO - Endereço do grupo multicast: 234.0.0.1     Porta: 12000

2024/04/28 17:26:42 - INFO - ░▒▒ Iniciando o servidor TCP ▒▒░
2024/04/28 17:26:42 - INFO - Servidor aguardando conexões em 0.0.0.0/0.0.0.0:12345

2024/04/28 17:26:42 - INFO - Pressione CTRL+C para encerrar o servidor.
```

**Note que os comandos utilizados destroem o contêiner quando o mesmo for encerrado.**

Caso esteja utilizando o Podman, basta substituir o comando `docker` por `podman`.