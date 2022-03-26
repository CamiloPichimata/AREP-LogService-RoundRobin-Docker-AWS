# Modularización con virtaulización (Docker - AWS)
### Arquitecturas Empresariales
#### Camilo Andrés Pichimata cárdenas
##### Marzo del 2022

## Descripción
En el presente laboratorio se desarrollará una aplicación con la siguiente arquitectura propuesta:

![](img/Arquitectura_Propuesta.png)

En donde:
- El servicio MongoDB es una instancia de MongoDB corriendo en un container de docker en una máquina virtual de EC2

- LogService es un servicio REST que recibe una cadena, la almacena en la base de datos y responde en un objeto JSON con las 10 ultimas cadenas almacenadas en la base de datos y la fecha en que fueron almacenadas.

- La aplicación web APP-LB-RoundRobin está compuesta por un cliente web y al menos un servicio REST. El cliente web tiene un campo y un botón y cada vez que el usuario envía un mensaje, este se lo envía al servicio REST y actualiza la pantalla con la información que este le regresa en formato JSON. El servicio REST recibe la cadena e implementa un algoritmo de balanceo de cargas de Round Robin, delegando el procesamiento del mensaje y el retorno de la respuesta a cada una de las tres instancias del servicio LogService.

## Desarrollo
Para complir con los requerimientos de la arquitectura propuesta se implementaron las siguientes clases:

- **Balancer.java:** Esta clase está compuesta por un cliente web y servicios REST. Desde el cliente web se envían cadenas al servicio REST y recibe de este información en formato JSON. El servicio REST recibe la cadena e implementa un algoritmo de balanceo de cargas de Round Robin que se encarga de distribuir las peticiones recibidas entre las tres instancias del servicio LogService.

- **LogService.java** LogService es un servicio REST que recibe las cadenas enviadas por el balanceador, las almacena en la base de datos y retorna un objeto JSON con las 10 ultimas cadenas almacenadas en la base de datos y la fecha en que fueron almacenadas.

## Despliege Docker
Para realizar el despliegue se generan las imágenes del LogService y el balanceador de carga, para esto se hace uso de los siguientes comandos:
    
```bash
# Imagen del balanceador de carga
docker build --tag balancer .

# Imagen de LogService
docker build --tag logservice .
```

La salida en consola al ejecutar los comandos es la siguiente:

- Balanceador de carga

![](img/docker_build_balancer.png)

- LogService

![](img/docker_build_logservice.png)

Con el comando `docker images` se puede revisar que las imagenes fueron contruidas:

![](img/docker_images.png)

Luego de creadas las imágenes mediante el archivo [docker-compose.yml]() se genera una configuración auntomática para docker al ejecutar el siguiente comando: 

```bash
# Configuración automática con archivo docker-compose.yml
docker compose up -d
```

Se ejecuta el comando:

![](img/docker-compose_up_1.png)

Al finar la ejecución indica que los servicios especificados en el archivo docker-compose.yml se crearon correctamente:

![](img/docker-compose_up_2.png)

Se verifica que se crearon los servicios al ejecutar el comando `docker ps`:

![](img/docker_ps.png)

Del mismo modo se puede ver en Docker Desktop el estado de los contenedores en "Running" como se ve en la siguiente imágen:

![](img/docker_desktop_running.png)

### Carga de imagenes a Docker Hub
Se ingresa a la dirección `https://hub.docker.com/` y se ingresa con el usuaro y contraseña, luego de esto se procede a crear un nuevo repositorio dando click en el boton **Create Repository**.

Se ingresa el nombre, una descripción corta y en este caso la visibilidad como pública, como se puede ver a continuación:

![](img/docker_repositorio_1.png)

Se crea una referencia a las imágenes de manera local con el nombre del repositorio usando los siguientes comandos:

```bash
# Balanceador de carga
docker tag balancer camilopichimata/arep-logservice_roundrobin_aws

# LogService
docker tag logservice camilopichimata/arep-logservice_roundrobin_aws
```

La ejecución de los comandos se puede visualizar en la siguiente imagen:

![](img/docker_reflocal_repo.png)

Se comprueba que se haya creado la referencia local al repositorio con el comando `docker images`:

![](img/docker_reflocal_repo_2.png)

Se realiza la autenticación de la cuenta de Docker Hub en caso de ser necesario con el comando `docker login`, se debe ingresar el nombre de ususario y la contraseña:

![](img/docker_login.png)

Luego de esto, se realiza en envío de la imagen al repositorio utilizando el siguiente comando:

```bash
docker push camilopichimata/arep-logservice_roundrobin_aws:latest
```

La salida mostrada en la consola es la siguiente:

![](img/docker_push.png)

Verificamos que se visualice el push realizado en el repositorio:

![](img/docker_repositorio_2.png)

## Despliegue AWS

