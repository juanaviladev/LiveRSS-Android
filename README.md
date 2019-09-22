<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="https://stackedit.io/style.css" />
</head>

<body class="stackedit">
  <div class="stackedit__html"><h1 id="liverss"><img src="https://i.imgur.com/v1lLN3R.pngg" alt="Icono de la app"> LiveRSS</h1>
<p><strong>LiveRSS</strong> es una aplicación Android desarrollada con Kotlin que <strong>permite ver en vivo las noticias de portada de los principales diarios españoles</strong>.</p>
<p>Se trata de una POC (Prueba de concepto) para demostrar en una aplicación funcional el uso de distintas librerías del lenguaje y del ecosistema Android.</p>
<p>Su desarrollo queda abierto para añadir nuevas funcionalidades, corregir bugs y experimentar con nuevas técnicas o librerías.</p>
<h3 id="p-aligncenter📦-a-hrefliverss-debug.apkdescargar-apkap"><p align="center">📦 <a href="liverss-debug.apk">Descargar APK</a></p></h3>
<p align="center">
  <img src="demo.gif">
</p>
<h2 id="🎉-funcionalidades-disponibles">🎉 Funcionalidades disponibles</h2>
<ul>
<li>
<p><strong>Feed de noticias “en vivo”</strong> 📢<br>
Una lista de noticias que es actualizada cada vez que se encuentran nuevas noticias. A día de hoy el tiempo mínimo entre búsquedas de noticias es de 15 minutos, ampliable por el usuario hasta las 24h.</p>
</li>
<li>
<p><strong>Marcar una noticia como favorita</strong>  ❤️<br>
Puedes marcar cualquiera de las noticias del feed como favorita, para poder acceder a ella más tarde de forma más simple.</p>
</li>
<li>
<p><strong>Notificación de nuevas noticias</strong> 🔔<br>
Es posible habilitar una opción para que cada vez que se encuentran nuevas noticias y no se esté usando la aplicación, el usuario sea notificado</p>
</li>
<li>
<p><strong>Limpieza automática de noticias antiguas</strong> 🗑️<br>
Para evitar que se acumulen de forma indefinida las noticias, periódicamente, las noticias más antiguas, son eliminadas del feed, ahorrando espacio en el dispositivo. El criterio por defecto es de eliminación de aquellas publicadas hace 1 día o más, pero es posible modificarlo.</p>
</li>
<li>
<p><strong>Fuentes de noticias personalizables</strong> 📰<br>
La aplicación hace uso del servicio RSS de distintos diarios de tirada nacional:</p>
<ul>
<li>El Mundo</li>
<li>El País</li>
<li>El Diario</li>
<li>El Confidencial</li>
<li>Marca</li>
<li>AS</li>
<li>Europapress</li>
<li>20Minutos</li>
</ul>
<p>El usuario puede deshabilitar aquellas fuentes de las que no desee recibir noticias. Por defecto, se obtienen noticias de todas las fuentes disponibles.</p>
</li>
<li>
<p><strong>Leer una noticia en la app</strong> 👁️‍🗨️<br>
Cualquiera de las noticias disponibles en el feed o marcadas en favoritos pueden ser leídas pulsando sobre ella. La noticia será leída a través de la dirección web que provee la fuente, sin necesidad de salir de la aplicación.</p>
</li>
</ul>
<h2 id="❓-backlog">❓ Backlog</h2>
<ul>
<li>Soporte multilenguaje (español e inglés)</li>
<li>Tests unitarios y de integración</li>
<li>Lectura offline de noticias favoritas</li>
<li>Filtrado de noticias por palabras clave o tags</li>
<li>Adición de fuentes sin necesidad de actualizar la aplicación</li>
<li>Ordenación de las noticias: por fecha de publicación, por tipo, etc.</li>
<li>División del proyecto en módulos IntelliJ</li>
<li>…</li>
</ul>
<h2 id="📐👨‍💻-arquitectura-de-la-aplicación">📐👨‍💻 Arquitectura de la aplicación</h2>
<p>La arquitectura de la aplicación está basada en <a href="https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html">Clean Architecture</a>.</p>
<p><img src="https://blog.cleancoder.com/uncle-bob/images/2012-08-13-the-clean-architecture/CleanArchitecture.jpg" alt="enter image description here"></p>
<p>Se mantiene el DIP (Principio de inversión de dependencias), representado en la imagen mediante flechas negras. dividida en 4 capas bien diferenciadas entre sí:</p>
<ul>
<li>
<p>🖼️ <strong>Presentación</strong><br>
Junto con la de datos y framework, conforman la <strong>parte más externa de la aplicación</strong>. En esta capa se encuentran aquellas <strong>clases dedicadas a la interacción con el usuario</strong>, además de ser el punto de entrada a la capa más interna, la de dominio.</p>
<p>Respecto a los patrones aplicados en esta capa:</p>
<ul>
<li><em>MVVM (Model-View-View-Model)</em><br>
Este patrón junto con la librería perteneciente a Android Jetpack hace más sencillo el manejo de los cambios de configuración que se producen al rotar la pantalla o cambiar alguna configuración determinada del sistema. Como el MVP, hace posible el testing unitario en gran parte de la capa de presentación.<br>
<br></li>
</ul>
</li>
<li>
<p>🗂️ <strong>Datos</strong><br>
En esta capa pueden encontrarse todas aquellas clases relacionadas con la persistencia de los datos y la comunicación con terceros.<br>
En este caso, se ha implementado una <strong>base de datos local para almacenar las noticias</strong>, implementada con <strong>SQLite</strong>. Además, se encuentran todas las <strong>clases encargadas de la comunicación con los endpoints RSS</strong> que proporcionan cada uno de los diarios.</p>
<p>Respecto a los patrones aplicados en esta capa:</p>
<ul>
<li><em>Repository</em><br>
Implementa una interfaz de operaciones en forma de colección (add, remove, etc.) que cumple con las necesidades de la capa de dominio. En esta capa se encuentra la implementación de las operaciones definidas en la capa de dominio. Es una forma alternativa al patrón DAO, en vez de estar orientada y definida por la capa de datos, se rige estrictamente por las necesidades del dominio.<br>
<br></li>
</ul>
</li>
<li>
<p>🔨 <strong>Framework</strong><br>
La capa de dominio requiere de <strong>ciertos servicios que están implementados en el SDK de Android</strong> y que por tanto tienen dependencias que <strong>hacen el diseño muy rígido y dificultan el testing unitario</strong>, por ello en esta capa se encuentran las implementaciones de aquellos servicios que están relacionados con la plataforma y que la capa de dominio define para completar sus operaciones.</p>
<p>En ella podemos encontrar distintas clases:</p>
<ul>
<li>Para programar tareas diferidas (mediante WorkManager)</li>
<li>Notificación al usuario (mediante NotificationManager)</li>
<li>Acceso a I/O de ficheros (mediante métodos del SDK Android)</li>
<li>Acceso a preferencias de usuario (mediante SharedPreferences)</li>
<li>Para conocer si la aplicación está o no en primer plano (mediante LifecycleObserver)<br>
<br></li>
</ul>
</li>
<li>
<p>🧠 <strong>Dominio</strong><br>
Se trata del núcleo de la aplicación, en esta capa <strong>se encuentran todos los casos de uso de la aplicación</strong>, <strong>junto con la definición de los servicios utilizados</strong> por los casos de uso. Estos servicios son definidos desde la capa de dominio, pero por las razones explicadas anteriormente, son implementados en otra capa.</p>
<p>Respecto a los patrones aplicados en esta capa:</p>
<ul>
<li>
<p><em>Command</em><br>
Los casos de uso están <strong>divididos de forma unitaria en clases independientes</strong> que comparten una <strong>misma interfaz</strong>, de forma que se establece un ‘protocolo’ para la implementación de un caso de uso, consistente en: unos parámetros de entrada, unos parámetros de salida y un método de ejecución.</p>
<p>Al tener todos una interfaz común, es posible ejecutarlos a través de su método de ejecución a través de <strong>una clase invocadora, que es la que decide en última instancia cómo se ejecuta el comando</strong>. En este caso se hace uso de la librería de <strong>corrutinas de Kotlin</strong>.</p>
</li>
<li>
<p><em>Decorator</em><br>
Los casos de uso hacen un uso intensivo de la base de datos, realizando distintas operaciones que en ocasiones tienen que ejecutarse de forma atómica, para ello existe una <strong>clase decoradora de los casos de uso</strong> que permite envolver la <strong>ejecución del caso de uso en una transacción</strong>.</p>
</li>
</ul>
</li>
</ul>
<h2 id="🗺️-diagrama-de-clases">🗺️ Diagrama de clases</h2>
<p><em>En desarrollo</em></p>
<h2 id="🙏-librerías-y-agradecimientos">🙏 Librerías y agradecimientos</h2>
<ul>
<li>Parte de la iconografía y de las ilustraciones pertenecen a <a href="http://icons8.com">icons8.com</a></li>
<li><a href="https://developer.android.com/topic/libraries/architecture">Android Architecture Components</a></li>
<li><a href="https://jsoup.org/">JSoup</a></li>
<li><a href="https://github.com/bumptech/glide">Glide</a></li>
<li><a href="http://greenrobot.org/eventbus/">EventBus</a></li>
<li><a href="https://github.com/Kotlin/kotlinx.coroutines">Kotlin Coroutines</a></li>
<li><a href="https://www.joda.org/joda-time/">Joda Time</a></li>
</ul>
</div>
</body>

</html>
