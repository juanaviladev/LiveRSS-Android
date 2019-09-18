
# ![Icono](icon.png) LiveRSS 
**LiveRSS** es una aplicación Android desarrollada con Kotlin que **permite ver en vivo las noticias de portada de los principales diarios españoles**.

Se trata de una POC (Prueba de concepto) para demostrar en una aplicación funcional el uso de distintas librerías del lenguaje y del ecosistema Android.

Su desarrollo queda abierto para añadir nuevas funcionalidades, corregir bugs y experimentar con nuevas técnicas o librerías.

<p align="center">
  <img src="demo.gif">
</p>

## 🎉 Funcionalidades disponibles
 - **Feed de noticias "en vivo"** 📢
Una lista de noticias que es actualizada cada vez que se encuentran nuevas noticias. A día de hoy el tiempo mínimo entre búsquedas de noticias es de 15 minutos, ampliable por el usuario hasta las 24h.

 - **Marcar una noticia como favorita**  ❤️
Puedes marcar cualquiera de las noticias del feed como favorita, para poder acceder a ella más tarde de forma más simple.

 - **Notificación de nuevas noticias** 🔔
 Es posible habilitar una opción para que cada vez que se encuentran nuevas noticias y no se esté usando la aplicación, el usuario sea notificado
 
 - **Limpieza automática de noticias antiguas** 🗑️
 Para evitar que se acumulen de forma indefinida las noticias, periódicamente, las noticias más antiguas, son eliminadas del feed, ahorrando espacio en el dispositivo. El criterio por defecto es de eliminación de aquellas publicadas hace 1 día o más, pero es posible modificarlo.

 - **Fuentes de noticias personalizables** 📰
La aplicación hace uso del servicio RSS de distintos diarios de tirada nacional:
	 - El Mundo
	 - El País
	 - El Diario
	 - El Confidencial
	 - Marca
	 - AS
	 - Europapress
	 - 20Minutos

	El usuario puede deshabilitar aquellas fuentes de las que no desee recibir noticias. Por defecto, se obtienen noticias de todas las fuentes disponibles.

 - **Leer una noticia en la app** 👁️‍🗨️
 Cualquiera de las noticias disponibles en el feed o marcadas en favoritos pueden ser leídas pulsando sobre ella. La noticia será leída a través de la dirección web que provee la fuente, sin necesidad de salir de la aplicación.
 
## ❓ Backlog
 - Soporte multilenguaje (español e inglés)
 - Tests unitarios y de integración
 - Lectura offline de noticias favoritas
 - Filtrado de noticias por palabras clave o tags
 - Adición de fuentes sin necesidad de actualizar la aplicación
 - Ordenación de las noticias: por fecha de publicación, por tipo, etc.
 - División del proyecto en módulos IntelliJ
 - ...

## 📐👨‍💻 Arquitectura de la aplicación

La arquitectura de la aplicación está basada en [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html). 

![enter image description here](https://blog.cleancoder.com/uncle-bob/images/2012-08-13-the-clean-architecture/CleanArchitecture.jpg)

Se mantiene el DIP (Principio de inversión de dependencias), representado en la imagen mediante flechas negras. dividida en 4 capas bien diferenciadas entre sí:

 - 🖼️ **Presentación**
Junto con la de datos y framework, conforman la **parte más externa de la aplicación**. En esta capa se encuentran aquellas **clases dedicadas a la interacción con el usuario**, además de ser el punto de entrada a la capa más interna, la de dominio.

	Respecto a los patrones aplicados en esta capa:
	
	 - *MVVM (Model-View-View-Model)*
	 Este patrón junto con la librería perteneciente a Android Jetpack hace más sencillo el manejo de los cambios de configuración que se producen al rotar la pantalla o cambiar alguna configuración determinada del sistema. Como el MVP, hace posible el testing unitario en gran parte de la capa de aplicación.
	 
 - 🗂️ **Datos**
 En esta capa pueden encontrarse todas aquellas clases relacionadas con la persistencia de los datos y la comunicación con terceros.
En este caso, se ha implementado una **base de datos local para almacenar las noticias**, implementada con **SQLite**. Además, se encuentran todas las **clases encargadas de la comunicación con los endpoints RSS** que proporcionan cada uno de los diarios.

	Respecto a los patrones aplicados en esta capa:
	
	- *Repository*
	 Implementa una interfaz de operaciones en forma de colección (add, remove, etc.) que cumple con las necesidades de la capa de dominio. En esta capa se encuentra la implementación de las operaciones definidas en la capa de dominio. Es una forma alternativa al patrón DAO, en vez de estar orientada y definida por la capa de datos, se rige estrictamente por las necesidades del dominio.

 - 🔨 **Framework**
La capa de dominio requiere de **ciertos servicios que están implementados en el SDK de Android** y que por tanto tienen dependencias que **hacen el diseño muy rígido y dificultan el testing unitario**, por ello en esta capa se encuentran las implementaciones de aquellos servicios que están relacionados con la plataforma y que la capa de dominio define para completar sus operaciones.

	En ella podemos encontrar distintas clases:
	- Para programar tareas diferidas (mediante WorkManager)
	- Notificación al usuario (mediante NotificationManager)
	- Acceso a I/O de ficheros (mediante métodos del SDK Android)
	- Acceso a preferencias de usuario (mediante SharedPreferences)
	- Para conocer si la aplicación está o no en primer plano (mediante LifecycleObserver)
	
 - 🧠 Dominio
Se trata del núcleo de la aplicación, en esta capa **se encuentran todos los casos de uso de la aplicación**, **junto con la definición de los servicios utilizados** por los casos de uso. Estos servicios son definidos desde la capa de dominio, pero por las razones explicadas anteriormente, son implementados en otra capa.

	Respecto a los patrones aplicados en esta capa:
	
	 - *Command*
	 Los casos de uso están **divididos de forma unitaria en clases independientes** que comparten una **misma interfaz**, de forma que se establece un 'protocolo' para la implementación de un caso de uso, consistente en: unos parámetros de entrada, unos parámetros de salida y un método de ejecución.
	 
		 Al tener todos una interfaz común, es posible ejecutarlos a través de su método de ejecución a través de **una clase invocadora, que es la que decide en última instancia cómo se ejecuta el comando**. En este caso se hace uso de la librería de **corrutinas de Kotlin**.
	 
	 - *Decorator*
	 Los casos de uso hacen un uso intensivo de la base de datos, realizando distintas operaciones que en ocasiones tienen que ejecutarse de forma atómica, para ello existe una **clase decoradora de los casos de uso** que permite envolver la **ejecución del caso de uso en una transacción**.

## 🗺️ Diagrama de clases

*En desarrollo*

##  🙏 Librerías y agradecimientos

 - Parte de la iconografía y de las ilustraciones pertenecen a icons8.com
 - [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
 - [JSoup](https://jsoup.org/)
 - [Glide](https://github.com/bumptech/glide)
 - [EventBus](http://greenrobot.org/eventbus/)
 - [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
 - [Joda Time](https://www.joda.org/joda-time/)
