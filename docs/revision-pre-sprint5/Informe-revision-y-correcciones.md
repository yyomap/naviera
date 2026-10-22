# Registro Marítimo Digital — revisión y correcciones antes del Sprint 5

## Entrega local

Se aplicaron las cuatro correcciones acordadas sobre yyomap/naviera, revisión base 9211a7daa0ac9a3830a534d6950caaf9c18e016b. La entrega se publica en una rama independiente, revision/correcciones-pre-sprint5. La rama main permanece sin cambios.

## Cambios realizados

| Área | Antes | Ahora |
|---|---|---|
| Bandeja y revocación | Las aprobadas abrían directamente el PDF y no permitían llegar al detalle desde la bandeja | La bandeja ofrece Ver detalle y Certificado PDF. Desde el detalle se accede a la revocación |
| Documento PDF | Bastaba con comenzar por los cuatro caracteres %PDF | Se comprueban estructura, páginas, contenido legible y cierre completo mediante OpenPDF; se rechazan archivos falsos, truncados y protegidos con contraseña. Se conserva el límite de tamaño |
| Certificado | Afirmaba estar firmado electrónicamente por la AMP | Incluye el aviso: Prototipo académico: documento de demostración, sin firma digital ni validez oficial |
| Mensajes de error | Faltaba respuesta general y podían devolverse mensajes técnicos de IllegalArgumentException | Las validaciones de negocio conservan mensajes claros; los errores inesperados reciben respuesta genérica y se registran internamente. JSON inválido devuelve 400; se conservan estados y cabeceras del framework |

Para leer y rechazar de forma controlada PDFs cifrados se añadió la dependencia Bouncy Castle bcprov-jdk18on 1.86. No se añadió firma digital al proyecto.

Se ajustaron los archivos PDF de prueba de las cuatro suites Java heredadas: ahora generan PDFs reales en lugar de texto que simula una cabecera. Esto no cambia la herramienta acordada con la profesora: Katalon sigue siendo la herramienta para las pruebas de interfaz del equipo.

## Validación realizada

- 12 pruebas automatizadas aprobadas: seis de validación PDF, cinco de manejo de errores y una de contexto Spring. La última ejecución específica repitió las 11 pruebas nuevas, también aprobadas.
- 41 comprobaciones del flujo corregido aprobadas, incluidos registro con PDF real, rechazo, reenvío, aprobación, certificado, vencimiento, revocación y controles por rol.
- Navegador real: un funcionario abrió una solicitud aprobada desde la bandeja; se verificaron las acciones Ver detalle y Certificado PDF y la sección de revocación visible.
- Texto extraído del PDF confirma el aviso académico; el PDF posterior a la revocación contiene REVOCADO.
- Límite >10 MiB comprobado por separado: HTTP 413 y mensaje en español.
- Compilación Maven correcta. Los datos temporales creados para las comprobaciones fueron eliminados, sin borrar registros anteriores. La secuencia de IDs puede haber avanzado.

Estas verificaciones son pruebas Java, HTTP y navegador. No se ejecutó Katalon porque el proyecto Katalon no está disponible en este entorno. Tampoco se ejecutaron completas las suites Selenium heredadas tras el cambio; únicamente se actualizaron sus PDFs de prueba y se verificó su compilación.

## Pendientes que siguen fuera de estas cuatro correcciones

- Ejecutar los casos de interfaz en Katalon y adjuntar el reporte del equipo.
- Revisar CSRF, mensajes que permiten identificar correos registrados y configuración de cuentas de desarrollo.
- Probar concurrencia en decisiones y registros duplicados antes de afirmar que está protegida.
- Decidir si los reenvíos deben vincularse explícitamente con su solicitud anterior y si se registrará la descarga del certificado como evento independiente.
- Completar horas reales, observaciones de la profesora, métricas y facturas con datos del equipo.

## Cómo usar los archivos

El ZIP contiene una copia del código del proyecto con las correcciones, sin historial Git, bases de datos, archivos subidos ni compilados. Extraer en una carpeta nueva para conservar intacta cualquier copia anterior. Dentro está registronaves, el proyecto Maven. Requiere JDK 17 o superior y MySQL funcionando con la base amp_registro_db; utilizar la configuración local correspondiente.

En Windows, desde registronaves:

```
.\mvnw.cmd "-Dtest=ValidacionPdfTest,ManejadorErroresTest,RegistronavesApplicationTests" test
.\mvnw.cmd -DskipTests package
java -jar target/registronaves-0.0.1-SNAPSHOT.jar
```

En Linux/macOS se usa bash mvnw en lugar de .\mvnw.cmd. Para ejecutar en este entorno ya configurado, el helper /workspace/naviera-setup/bin/mvn-naviera activa el JDK y la configuración Maven locales.

El ZIP descargado desde GitHub contiene la rama corregida completa. No es necesario aplicar un parche adicional.

## Matriz de revisión inicial

La siguiente matriz documenta el diagnóstico anterior a las correcciones. Para HU-04, HU-11, HU-15 y HU-18 prevalecen los resultados corregidos descritos arriba. El resto mantiene los límites de validación indicados.

| HU | Función | Resultado y evidencia | Pendiente / mejora |
|---|---|---|---|
| 01 | Acceso por roles | Comprobada API y redirecciones; BCrypt inspeccionado en código | Katalon: interacción y rol visible; endurecimiento transversal de sesión/CSRF |
| 02 | Bloqueo de cuenta | Tres fallos: 401, 401, 423; contraseña correcta bloqueada; duración cercana a 900 s; reinicio de contador y acceso tras vencimiento comprobados | Mensajes distintos permiten distinguir correo registrado; concurrencia no probada |
| 03 | Registro de nave | Creación En revisión, nombre obligatorio, formato IMO y duplicado secuencial comprobados | IMO sin dígito de control por decisión actual; unicidad concurrente sin protección observada, no ensayada |
| 04 | Carga PDF | Archivo ausente/falso y >10MiB rechazados; PDF generado por la app admitido | Parcial: `%PDF basura` es aceptado. Validar estructura PDF, no solo cuatro bytes |
| 05 | Mis solicitudes | Propia incluida y excluida para otro armador | Katalon: filtros, presentación y detalle |
| 06 | Bandeja funcionario | Solicitud nueva incluida y acceso por rol comprobado; buscador/filtros inspeccionados | Katalon: filtros y navegación; fallo de acceso a revocación en aprobadas (HU-18) |
| 07 | Abrir documento | Funcionario obtiene documento; otro armador recibe 404 | Katalon: apertura del visor y controles restantes |
| 08 | Aprobar solicitud | Aprueba y rechaza segunda aprobación secuencial | No hay @Version ni bloqueo explícito observado: investigar decisiones simultáneas antes de afirmar que están protegidas |
| 09 | Rechazar con observación | Sin observación 400; con observación cambia estado y propietario la consulta | Katalon: mensaje y observación visible |
| 10 | Consulta aseguradora | Consulta disponible; intento de decisión 403 | Katalon: filtros y ausencia de controles de edición |
| 11 | Certificado electrónico | Antes de aprobar 404; después PDF descargable con folio | Corregir texto que afirma firma electrónica de la AMP: el servicio genera un PDF sin implementar esa firma |
| 12 | Código y QR | Formato de código comprobado; SecureRandom y generación ZXing inspeccionados | No se decodificó QR en esta revisión; Katalon/manual: escanear y comprobar URL. APP_BASE_URL debe coincidir con destino accesible |
| 13 | Vigencia | Emisión +6 meses comprobada; fechas presentes en PDF | Katalon: presentación; casos fin de mes pendientes |
| 14 | Verificación pública | Sin sesión: Vigente, Vencido, Revocado e inexistente comprobados | Limitar consultas es endurecimiento futuro; QR y presentación pendientes |
| 15 | Mensajes claros | 404 inexistente y 413 español comprobados; manejadores específicos inspeccionados | Parcial: falta manejo general seguro; IllegalArgumentException devuelve mensaje original. No se indujeron fallos internos |
| 16 | Seguimiento por etapas | Fecha de decisión guardada; cinco etapas inspeccionadas en mis-solicitudes.js | Fechas de algunas etapas son derivadas de creación/decisión: no es una bitácora de eventos independientes. “Entregado” significa disponible, no descarga registrada. Katalon pendiente |
| 17 | Corregir y reenviar | Reenvío de IMO rechazado crea otra solicitud En revisión y conserva rechazo original; precarga y exigencia de nuevo PDF inspeccionadas | Falta vínculo persistente entre solicitud nueva y antecedente; recomendable para trazabilidad, adicional al criterio actual. Precarga visual pendiente |
| 18 | Revocar certificado | Rol, motivo, cambio público y PDF REVOCADO comprobados | Parcial en navegación: bandeja de aprobadas solo abre PDF; no ofrece acceso al detalle donde está Revocar |
| 19 | Copiar enlace | Código accesible en detalle solo a propietario/funcionario; botón y alternativa prompt inspeccionados | Portapapeles real no ejecutado: comprobar en Katalon, incluidos permisos y contexto seguro |
| 20 | Estado/vencimiento aseguradora | API devuelve Vencido y luego Revocado, con vencimiento correcto | Katalon: columnas y formato de fecha |
