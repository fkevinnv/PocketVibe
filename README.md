# PocketVibe 💸

Gestor de finanzas personales con Jetpack Compose, Material 3, MVVM y Room.
UI "delightful": contadores animados, FAB tipo dial, láser de escaneo,
gráficos Canvas animados, confeti al completar objetivos y swipe-to-delete.

## Stack

- Kotlin + Jetpack Compose (BOM 2024.10.00) + Material 3
- MVVM con StateFlow / ViewModel
- Room 2.6.1 (KSP) — transacciones e wishlist
- CameraX 1.4.0 — vista previa del escáner de tickets
- ML Kit Text Recognition (dependencia incluida; la extracción real del
  importe es el punto de integración marcado en `ScannerScreen.kt`)

## Estructura

```
app/src/main/java/com/pocketvibe/
├── MainActivity.kt
├── data/
│   ├── TransactionRepository.kt
│   └── local/ (Entities, Converters, Daos, AppDatabase)
├── vm/ (Dashboard, Transactions, Monthly, Wishlist)
└── ui/
    ├── theme/Theme.kt
    ├── navigation/AppNavHost.kt
    ├── components/ (AnimatedCounter, DialFab, Charts, WishCard,
    │                CelebrationOverlay, TransactionRow)
    └── screens/ (Dashboard, AddTransactionSheet, Scanner,
                  MonthlySummary, Wishlist, History)
```

## Cómo abrirlo

1. Android Studio Ladybug o superior → **Open** → selecciona esta carpeta.
   El IDE generará el Gradle Wrapper automáticamente (no se incluye el
   `gradle-wrapper.jar` por ser binario). Alternativa con Gradle local 8.7+:

   ```
   gradle wrapper && ./gradlew assembleDebug
   ```

2. Ejecuta en un dispositivo/emulador con API 26+.
3. Concede permiso de cámara para probar el escáner de tickets.

## Notas

- El escáner simula el OCR (1,8 s → 23,45 €). Para OCR real, procesa el
  `ImageProxy` con ML Kit y aplica `(?i)total.*?(\d+[.,]\d{2})`.
- Las contribuciones a wishlist son de 50 € fijos (botón "+ Aportar");
  el DAO ya tiene `contribute(id, amount)` atómico.
- La app usa tema oscuro por defecto (esquema `PocketVibeDarkColors`).
