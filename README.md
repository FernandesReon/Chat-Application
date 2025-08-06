# ChatSphere - Real Time Chat Application

## Overview
A full-stack real-time chat application built using Spring Boot and React.js. This application supports 
private and group messaging, user authentication with admin control, real-time updates via WebSockets, and is 
containerized using Docker for easy deployment.

## Features

- User authentication and authorization
- Role-based access (User and Admin)
- Dedicated admin panel
- Real-time messaging using WebSockets
- Private and group chat functionality
- Online and offline user status
- Typing indication in real time
- Message history with pagination
- Emoji reactions to messages
- User presence tracking
- Responsive UI with React.js

## Tech Stack

### Backend
- Java
- Spring Boot
- Spring Security
- JWT
- WebSocket (STOMP)
- Redis (for caching and online status)
- MySQL (relational database)
- JPA

### Frontend
- React.js
- Axios (for HTTP requests)
- SockJS and STOMP.js (for WebSocket communication)

### DevOps & Deployment
- Docker (containerization)
- Render (cloud deployment)
- Git & GitHub (version control)
