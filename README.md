# My Diary
An Android app to learn a new language through daily practice — built in Kotlin, powered by Firebase.

# Purpose of the project
I built this app to help myself learn Korean — adding words from daily discoveries and study sessions, then writing about my day to practice using them in context. The project also gave me a hands-on way to strengthen my skills in Kotlin, Android development, and Firebase.

# Main Features:

## Personal Daily Diary
Write about your day in the language you're studying. Using words in the context of real experiences is one of the most effective methods to consolidate them in long-term memory. In this section you can directly search a word already in your dictionary or adding a new one.

## Dictionary
Build your personal dictionary by adding words as you learn them. Each entry can be organized into sets to focus the study on specific words in the memory game and enriched with personal notes to capture meanings, usage tips, or any concept worth remembering. Your vocabulary grows with you and is always within reach.

## Exercises
Practice with the words in your vocabulary through interactive exercises. Select a specific set to focus your training, and save each session when you're done so you can track which words still need work. The more vocabulary you build, the richer and more challenging your practice becomes.


# Tech Stack

| Technology | Usage |
|---|---|
| Kotlin | Main language |
| XML | UI layouts and resources |
| Firebase | Authentication and realtime database |
| Gradle | Build system |

---

## Getting Started

### Prerequisites
- Android Studio
- JDK 11+
- A Firebase account with a configured project

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Vhyra/MyDiary_Re.git
   cd MyDiary_Re
   ```

2. **Configure Firebase**
- Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project
- Enable **Authentication** (Email/Password provider)
- Enable **Realtime Database** and set up the rules
- Download the `google-services.json` file and place it in the `app/` folder

> The `google-services.json` file is not included in this repository.
> To run the app locally, create a free Firebase project and add your own configuration file.

3. **Open the project in Android Studio** and let Gradle sync the dependencies

4. **Run the app** on an emulator or physical device (Android 7.0+)

---

# Project Structure

```
app/
└── src/
    └── main/
        ├── java/                   # Kotlin source code
        |    ├── adapters           # Bridge between data and views
        |    ├── authentication     # Code to manage registration and authentication of users
        |    ├── data               # ViewModel and Firebase helper to manage data
        |    ├── interfaces         # Defines contract between MainActivity and Fragments for navigation and data management
        |    ├── pages              # Contains the Fragments for each section of the app
        |    ├── services           # Audio service for button sound effects
        |    └── MainActivity.kt    # App entry point, implements the interface to handle navigation and data operations requested by Fragments
        └── res/                    # Layouts, drawables, strings
gradle/                             # Gradle configuration
build.gradle.kts                    # Dependencies and plugins
settings.gradle.kts                 # Project settings
```

# License

Distributed under the MIT License. See the `LICENSE` file for details.
