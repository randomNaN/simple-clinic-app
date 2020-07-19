## Simple clinic app

Simple CRUD app written in [Kotlin](https://kotlinlang.org/)
### Libraries used:

 - [Ktor](https://github.com/ktorio/ktor) - Kotlin async web framework
 - [Netty](https://github.com/netty/netty) - Async web server
 - [Exposed](https://github.com/JetBrains/Exposed) - Kotlin SQL framework
 - [Postgres](https://github.com/postgres/postgres) - Embeddable database
 - [HikariCP](https://github.com/brettwooldridge/HikariCP) - High performance JDBC connection pooling
 - [Gson](https://github.com/google/gson) - JSON serialization/deserialization

### Getting Started

1. Clone the repo.
2. In the root directory execute `docker-compose up`

### Routing

`GET /clinics` or `GET /diagnostics` - get all clinics or diagnotics

`GET /clinics/{id}` or `GET /diagnostics/{id}` - get all clinics or diagnotics instance by id

`POST /diagnostics/new` add a new diagnostic to the database by providing a JSON object
`{ "name": "name", "category": "category" }`

`POST /clinics/new` add a new diagnostic to the database by providing a JSON object  
`{ "name": "name", "addr": "addr", "phone":"phone", services:[ {"diagnostic_id":1, "price":100} ] }`

Note: existing diagnostic_id is expected for creating/updating a clinic.

`PUT /diagnostics/{id}` update the diagnostic with id by providing a JSON object (same as in POST).

`PUT /clinics/{id}` update the clinic with id by providing a JSON object (same as in POST).

`DELETE /diagnostics/{id}` and `DELETE /clinics/{id}` deletes an existing clinic or diagnostic


