📱 APP SERVEL

Versión: 0.2

Esta aplicación fue creada con el propósito de modelar un prototipo de app móvil de Servel.
⚠️ Importante: No está destinada para el mercado. El logo usado es temporal y propiedad de SERVEL, únicamente con fines de práctica/estudio.

🎯 Objetivo de la App
- Permitir a los usuarios registrarse utilizando: Foto de su cara, Rut y clave única.
- Aplicar métodos de seguridad simples para evitar bypass del login.
- Integrar funcionalidades importantes de la página web oficial de SERVEL para mejorar la eficiencia.
- Presentar una posibilidad de voto en línea mediante Rut y clave única, evitando accesos automatizados.

🆕 Novedades en esta versión
- Se añadieron las Activities principales:
  - Mi Portal
  - Datos Electorales
  - Trámites
- Implementación de medidas básicas de seguridad: validación de datos y cierre de vistas que no están en uso.
- Mejora en la experiencia del usuario mediante navegación optimizada entre pantallas y manejo correcto del estado de la app.

⚡ Características
- Interfaz clara y scrollable para dispositivos móviles.
- Botones con efecto ripple y contraste adecuado para mejor interacción.
- Manejo de datos desde Login → Mi Portal → Datos Electorales, dejando preparado el uso de SQLite para consultas futuras.
- Uso de Intents y Extras para pasar información entre actividades de forma segura.

📌 Notas
- La app funciona actualmente con datos de prueba para login (Rut y Clave) antes de integrar la base de datos real.
- Futuras versiones integrarán SQLite para almacenar usuarios y datos electorales.
- Se implementó el manejo de márgenes y EdgeToEdge para compatibilidad con distintos dispositivos Android.
