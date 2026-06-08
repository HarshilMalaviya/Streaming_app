# VideoStreamingApp

A video streaming application built with Spring Boot. Supports video upload, HLS (HTTP Live Streaming) conversion, and adaptive bitrate streaming.

## Features

- Video upload with metadata (title, description)
- On-the-fly HLS conversion using FFmpeg
- HLS streaming with master playlist and TS segments
- Byte-range video streaming for direct playback
- RESTful API

## Prerequisites

- Java 21+
- Maven (or use the included Maven wrapper)
- MySQL 8.0+
- FFmpeg installed and available in PATH

## Configuration

All settings are externalized via environment variables (defaults shown):

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/video_streaming` | MySQL JDBC URL |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `your_db_password` | MySQL password |
| `APP_PORT` | `3030` | Application server port |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5174/**` | Allowed CORS origins |
| `VIDEO_DIR` | `videos/` | Directory for uploaded videos |
| `VIDEO_HLS_DIR` | `videos_hls/` | Directory for HLS output |

## Running Locally

### 1. Start MySQL

Ensure MySQL is running on `localhost:3306` with database `test3` (or change via env vars).

### 2. Build and run

```bash
cd VideoStreamingApp
./mvnw spring-boot:run
```

Or build and run the JAR:

```bash
./mvnw package -DskipTests
java -jar target/VideoStreamingApp-0.0.1-SNAPSHOT.jar
```

The server starts at `http://localhost:3030`.

## Running with Docker

From the project root:

```bash
docker-compose up --build
```

This starts both MySQL and the application. The app is available at `http://localhost:3030`.

## API Endpoints

### Upload a video

```
POST /api/v1/video
Content-Type: multipart/form-data

file: <video file>
title: "My Video"
description: "A description"
```

### Get all videos

```
GET /api/v1/video
```

### Stream video (full file)

```
GET /api/v1/video/stream/{id}
```

### Stream video (byte range / partial content)

```
GET /api/v1/video/stream/range/{id}
Range: bytes=0-1048575
```

### Serve HLS master playlist

```
GET /api/v1/video/{videoId}/master.m3u8
```

### Serve HLS segment

```
GET /api/v1/video/{videoId}/{segment}.ts
```

## Tech Stack

- **Spring Boot 3.3.3** — application framework
- **Spring Data JPA** — database access
- **MySQL** — relational database
- **FFmpeg** — HLS video processing
- **Lombok** — boilerplate reduction
- **Docker** — containerization
