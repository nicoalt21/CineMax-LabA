[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

<img src="doc/img/SigilloAteneoTestoColori.svg" width="250px;" alt="Insubria Logo">

# 🎬 CineMax

**Progetto universitario per l'esame di Laboratorio Interdisciplinare A – Università degli Studi dell'Insubria (2025/2026)**

Sistema di gestione per piccoli cinema monosala da 200 posti. Permette la gestione del palinsesto cinematografico e delle prenotazioni tramite interfaccia testuale (TUI), scritto in **Java 25** con persistenza su file CSV.

Manuale Tecnico, Manuale Utente e JavaDoc disponibili in `/doc`.

---

## 👥 Autori

- **Alt Niccolò Jacopo 762605 VA**
- **Soldo Mateo 760762 VA**
- **Vignati Davide 761134 VA**

---

## 📦 Dipendenze

Il progetto utilizza **Maven** per la compilazione. Non sono presenti dipendenze esterne: la build si basa esclusivamente sulle API standard di Java 25.

---

## ⚙️ Requisiti

- **Java 25** o superiore
- **Maven 3.8+**

---

## 🔨 Build

```bash
mvn clean install
```

Il JAR eseguibile viene generato in `bin/CineMax.jar`.

---

## ▶️ Avvio

```bash
java -jar bin/CineMax.jar
```

**Importante:** esegui il comando dalla root del progetto. I file CSV in `data/` vengono risolti relativamente alla working directory corrente.

---

## 📁 Struttura del Progetto


da fare

---

## 📌 Note

- Password memorizzate come hash SHA-256 (UTF-8)
- Formato data/ora nei CSV: `yyyy-MM-dd HH:mm:ss`
- Separatore campi CSV: punto e virgola (`;`)
- Capienza sala fissa: 200 posti
- I dati vengono salvati automaticamente all'uscita dall'applicazione

---

## 📄 Licenza

Questo progetto è rilasciato sotto licenza **MIT**. Vedi il file `LICENSE` per i dettagli.