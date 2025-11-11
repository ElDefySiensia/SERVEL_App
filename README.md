README
📱 APP SERVEL

Versión: 0.3

Esta aplicación fue creada con el propósito de modelar un prototipo de app móvil de Servel. ⚠️ Importante: No está destinada para el mercado. El logo usado es temporal y propiedad de SERVEL, únicamente con fines de práctica/estudio.

🎯 Objetivo de la App

- Permitir a los usuarios registrarse utilizando: Rut y clave única (con validación de aceptación de términos).
- Aplicar métodos de seguridad simples para evitar bypass del login.
- Integrar funcionalidades importantes de la página web oficial de SERVEL para mejorar la eficiencia.
- Presentar una posibilidad de voto en línea mediante Rut y clave única, evitando accesos automatizados.

🆕 Novedades en esta versión

- Se añadieron las Activities principales:
  - Mi Portal
  - Datos Electorales
  - Trámites
  - Registro de usuario con validación de aceptación de condiciones.

- Integración con SQLite:
  - Base de datos `servel.db` creada mediante `AdminSQLiteOpenHelper`.
  - Tablas `usuarios` y `datos_usuarios` para almacenar RUT, clave encriptada y datos electorales.
  - Validación de login real contra la BD en vez de datos de prueba.

- Seguridad:
  - Claves encriptadas con SHA-256 al momento del registro.
  - Login solo permite comparar la clave ingresada con la encriptada en la BD.
  - Cierre de conexiones SQLite y cursors tras cada operación para evitar fugas.

- Experiencia de usuario:
  - No se permite crear cuenta sin aceptar los términos (aparece mensaje de alerta).
  - Navegación optimizada entre pantallas y manejo correcto del estado de la app.
  - Ajuste de márgenes y EdgeToEdge para compatibilidad con distintos dispositivos Android.

⚡ Características

- Interfaz clara y scrollable para dispositivos móviles.
- Botones con efecto ripple y contraste adecuado para mejor interacción.
- Manejo de datos desde Login → Registro → Mi Portal → Datos Electorales, dejando preparado el uso completo de SQLite.
- Uso de Intents y Extras para pasar información entre actividades de forma segura.
- Validación de campos de texto y mensajes de error claros en cada paso.

📌 Notas

- La app ahora funciona con base de datos real para registro y login.
- El registro encripta la clave automáticamente y la guarda en SQLite.
- Login verifica contra la clave encriptada, comparando hashes.
- Futuras versiones implementarán más consultas a la BD para mostrar información electoral completa.
