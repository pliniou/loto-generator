# CLI / Ambiente (Windows) — checklist

## Requisitos
- Android Studio (stable)
- JDK 17 (embedded do Android Studio é suficiente)
- SDK instalado (API 34+ recomendado)
- Emulador configurado ou device físico

## Checklist
1) Abrir projeto no Android Studio
2) Sync Gradle
3) Rodar `./gradlew :app:assembleDebug`
4) Rodar `./gradlew testDebugUnitTest`
5) Rodar `./gradlew lintDebug`

## Observações
- `local.properties` é gerado automaticamente (não versionar).
- Gradle Wrapper (gradlew) deve existir no repo.
