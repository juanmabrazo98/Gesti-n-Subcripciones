# Gestión de Subcripciones

## Descripción

Este proyecto es una aplicación para la gestión de suscripciones utilizando FHIR, Kie Server y Spring Boot. La aplicación permite crear, visualizar, modificar y eliminar suscripciones y notificaciones a través de una interfaz web.

## Características

- Gestión de Kie Servers.
- Gestión de Notification EPs.
- Creación y eliminación de suscripciones.
- Visualización y filtrado de suscripciones.
- Integración con FHIR y Kie Server.
- Configuración a través de `application.properties`.

## Requisitos

- Java 8 o superior
- Maven 3.6.3 o superior
- PostgreSQL 12 o superior

## Instalación

1. Clonar el repositorio

   ```bash
   git clone https://github.com/juanmabrazo98/Gesti-n-Subcripciones
   cd broker-service

2. Configurar la base de datos PostgreSQL
Crear la base de datos

    sudo -u postgres createdb fkbroker

Otorgar todos los privilegios al usuario jbpm (O el que se configure como usuario en application.properties )

    GRANT ALL PRIVILEGES ON DATABASE fkbroker TO jbpm;

Configurar application.properties

    spring.datasource.username=jbpm
    spring.datasource.password=jbpm
    spring.datasource.url=jdbc:postgresql://localhost:5432/fkbroker
    spring.datasource.driver-class-name=org.postgresql.xa.PGXADataSource

3. Compilar y ejecutar la aplicación

Desde el directorio raiz de la aplicación ejecutar el siguiente comando:
   
    sudo ./launch.sh clean install -Ppostgres 



## Autor



Juanma Brazo 