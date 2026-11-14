# Dónde van las imágenes

Todo lugar de imagen está marcado en el código con `★★★ IMAGEN AQUÍ #n ★★★`.
Usa **Ctrl+Shift+F** en Android Studio y busca `IMAGEN AQUÍ` para encontrarlos todos.

| # | Qué es | Dónde está marcado | Archivo que debes poner en `app/src/main/res/drawable/` |
|---|--------|--------------------|-----------------------------------------------------|
| 0 | Icono de la app | `AndroidManifest.xml` | `ic_launcher_marcador.xml` (o Image Asset y cambiar `android:icon` en el manifest) |
| 1 | Logo (login) | `res/layout/activity_login.xml` | `img_logo.png` |
| 2 | Mapa / ubicación | `res/layout/activity_direccion.xml` | `img_mapa.png` |
| 3 | Imagen del pedido | `res/layout/activity_pedido.xml` | `img_pedido.png` |
| 4 | Fotos de los platos | `ImagenUtil.kt` (y los 3 `item_*.xml`, `dialog_producto.xml`, `MenuAdminActivity.kt`) | `img_producto_1.png` … `img_producto_4.png` y `img_producto_default.png` |

## Cómo reemplazar un marcador

Cada imagen viene con un marcador (cuadro gris con una X, como en tu boceto) en un archivo `.xml` con el mismo nombre.

1. Copia tu imagen a `res/drawable/` con el nombre de la tabla (ej. `img_logo.png`).
2. **Borra** el marcador `img_logo.xml` de esa carpeta (si no, Android Studio da "recurso duplicado").
3. Listo, no hay que tocar código.

Reglas de nombres: solo minúsculas, números y guion bajo; sin espacios ni tildes.

## Productos nuevos con foto

Copia la imagen a `res/drawable/` (ej. `img_pollo_frito.png`) y, en el panel de admin, escribe en el campo
"Imagen" el nombre **sin extensión**: `img_pollo_frito`.
