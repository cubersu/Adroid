# Adroid Signal

Kripto para "alım-satım sinyal" uygulaması. **Gerçek para ile işlem yapmaz** — yalnızca teknik
indikatörlere dayalı AL/SAT sinyalleri üretir ve kullanıcıya bildirim gönderir.

> ⚠️ Bu uygulama bir finansal tavsiye aracı değildir, yalnızca teknik analiz sinyali üreticisidir.

## Veri Kaynağı

[BtcTurk Public API](https://docs.btcturk.com/) — API key gerekmez.

- REST: ticker, exchange info, kline/mum geçmişi (`graph-api.btcturk.com`)
- WebSocket (öncelikli, canlı veri): `wss://ws-feed-pro.btcturk.com`
  - `tradeview` kanalı → `PAIR\1` formatında subscribe (1 dakikalık mum)
  - `ticker` kanalı → anlık fiyat
  - Bağlantı koptuğunda otomatik reconnect + yeniden subscribe

## Mimari

MVVM + Clean Architecture, tek Gradle modülü (`:app`) içinde katman bazlı paketleme:

```
com.adroid.cryptosignal/
├── data/
│   ├── remote/          BtcTurk REST (Retrofit) + WebSocket (OkHttp) istemcileri, DTO'lar
│   ├── local/            Room (sinyal geçmişi, takip listesi) + DataStore (ayarlar)
│   ├── mapper/           DTO/Entity <-> domain model dönüşümleri
│   └── repository/       domain.repository arayüzlerinin implementasyonları
├── domain/
│   ├── model/             Framework'ten bağımsız domain modelleri
│   ├── indicator/         Saf Kotlin gösterge fonksiyonları (EMA, RSI, MACD, BB, ATR, VWAP)
│   ├── strategy/          Strategy arayüzü + varsayılan AL/SAT kural seti
│   ├── repository/       Repository arayüzleri
│   └── usecase/           Use case'ler
├── presentation/          Jetpack Compose ekranları + ViewModel'ler
├── service/               Foreground Service (WebSocket yaşam döngüsü) + bildirimler
└── di/                    Hilt modülleri
```

`domain/indicator` ve `domain/strategy` paketleri kasıtlı olarak Android SDK'sına bağımlı
değildir; saf Kotlin olarak yazılmıştır ve JVM unit testleri ile doğrulanır.

## Teknoloji

Kotlin · Jetpack Compose · Coroutines/Flow · Hilt · Retrofit + OkHttp WebSocket ·
Room · DataStore · WorkManager · Foreground Service

Min SDK 26, Target/Compile SDK 34.

## Derleme

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

> Not: Bu proje bulut tabanlı bir ajan oturumunda, Android SDK'sı ve `dl.google.com`
> erişimi olmayan bir ortamda yazılmıştır. Kod elle ve dikkatle standart Android/Kotlin
> pratiklerine göre oluşturulmuştur ancak `./gradlew` ile gerçek bir derleme bu ortamda
> doğrulanamamıştır. Lütfen Android Studio'da açıp derleyerek doğrulayın.

## Geliştirme Aşamaları

1. Proje iskeleti (bu adım)
2. BtcTurk veri katmanı (REST + WebSocket)
3. İndikatör motoru
4. Sinyal mantığı (Strategy)
5. Jetpack Compose UI
6. Bildirim sistemi (Foreground Service)
