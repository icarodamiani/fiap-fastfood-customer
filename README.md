# fiap-fastfood-customer

# Recursos e Bibliotecas

- [x] Java 17
- [x] PostgreSQL
- [x] Spring Boot
- [x] MapStruct

# Detalhes da Implementação MVP 2

## [Gerenciamento de Clientes]([CustomerController.java](fastfood-api%2Fsrc%2Fmain%2Fjava%2Fio%2Ffiap%2Ffastfood%2Fdriver%2Fcontroller%2Fcustomer%2FCustomerController.java))

A inserção do cliente ocorre de forma opcional e em conjunto ao registro do pedido. No entanto, conforme requisito,
a aplicação dispõe de um endpoint para remoção total dos dados do cliente.

### Mapeamento da entidade

    id : Identificador do produto, gerado automaticamente pela base de dados
    nome : Identificador da categoria do produto
    cpf : Value Added Tax Identifier (CPF/CNPJ/VAT)
    email : Email do cliente
    telefone : Telefone do Cliente

# Início rápido

```shell 
docker-compose up
```

A aplicação será disponibilizada em [localhost:8080](http://localhost:8080).

# Deploy

O deploy das aplicações é feito e gerenciado através de Helm charts, estes localizados na pasta [charts](charts). Todos
os charts apontam para imagens públicas e podem ser deployados em qualquer ordem. No entanto, em razão das dependências
entre si, para que as aplicações estabilizem todos devem estar deployados.