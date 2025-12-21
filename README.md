# Progress Tracker App

A smart learning companion designed to make studying more effective and boost long-term retention. The application is built with an Android frontend and a Spring Boot backend, demonstrating a complete client-server architecture.

## 🌟 Impact

This application solves the common problem of inconsistent learning and forgetting what you've studied. By providing a structured way to track daily study topics and implementing a smart, spaced-repetition revision system, it aims to make learning more effective, reduce procrastination, and ensure that knowledge is retained for the long term.

## 🚀 Features

*   **Daily Study & Goal Tracking:** Log daily study topics or general goals with titles and descriptions.
*   **Smart Revision System:** Automatically schedules revision reminders for topics based on a spaced repetition formula (e.g., after 1, 3, 7, and 15 days).
*   **Revision Dashboard:** A dedicated screen to keep track of all topics that are due for revision, so nothing is ever missed.
*   **Notes Section:** A separate tab for taking and organizing study notes related to specific topics.
*   **Progress Management:** Mark goals/tasks as complete and delete them when they are no longer needed.
*   **Centralized Architecture:** All data is stored on a central server, making the app scalable and ready for future multi-device support.

## 🏛️ Architecture

This application follows a modern client-server architecture:

*   **Frontend (Android Client):** A native Android application responsible for the user interface and user interaction. It communicates with the backend via RESTful API calls.
*   **Backend (Spring Boot Server):** A robust server that exposes a set of RESTful APIs to handle all business logic (Create, Read, Update, Delete) and data persistence.
*   **Communication:** The client and server communicate over HTTP using JSON as the data exchange format. The Android client uses **Retrofit** for efficient and clean network requests.

## 🛠️ Tech Stack

### Frontend (Android)

*   **Language:** Java
*   **UI:** XML, RecyclerView, Material Components
*   **Networking:** Retrofit & Gson
*   **Architecture:** Client-Server

### Backend (Spring Boot)

*   **Language:** Java
*   **Framework:** Spring Boot
*   **APIs:** Spring Web (RESTful)
*   **Data Persistence:** Spring Data JPA, Hibernate
*   **Database:** H2 (for local development)
*   **Build Tool:** Maven

## 🏁 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

*   Java 17 or higher
*   Android Studio (latest version)
*   An IDE for Java/Spring (like IntelliJ IDEA or VS Code)
*   An Android Emulator or a physical device

### 1. Backend Setup

1.  Open the **Spring Boot backend project** in your IDE (e.g., IntelliJ IDEA).
2.  Let the IDE resolve the Maven dependencies.
3.  Run the `ProgressTrackerBackendApplication.java` file to start the server.
4.  The server will start on `http://localhost:8080`.

### 2. Android App Setup

1.  Open the **Android project** in Android Studio.
2.  Let Gradle sync the project dependencies.
3.  **Important:** Ensure your `ApiClient.java` is pointing to the correct local address for the emulator:
    ```java
    // in app/src/main/java/com/example/progresstracker/network/ApiClient.java
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    ```
4.  Run the app on an emulator or a physical device connected to the same WiFi network as your computer.

    *   **Note:** If using a physical device, replace `10.0.2.2` in `BASE_URL` with your computer's local network IP address (e.g., `http://192.168.1.10:8080/`).
