# OpenAPI Documentation

## 📋 Overview

This document describes the OpenAPI specification for the Bank Cards Management System.

## 📁 Files

- **`openapi.yaml`** - Main OpenAPI 3.0 specification file
- **`swagger-ui/`** - Auto-generated Swagger UI documentation (available after build)

## 🔗 Access Points

### Development Environment
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Production Environment
- **Swagger UI**: https://api.yourdomain.com/swagger-ui.html
- **OpenAPI JSON**: https://api.yourdomain.com/api-docs

## 🛠️ Generation

The OpenAPI specification is automatically generated from Spring Boot annotations using:

- **springdoc-openapi** library
- **@Operation**, **@ApiResponse**, **@Parameter** annotations
- **@Tag** for endpoint grouping

## 📊 Specification Structure

### Components
- **schemas** - Data models (DTOs, entities)
- **securitySchemes** - Authentication methods
- **responses** - Common HTTP responses
- **parameters** - Reusable parameters

### Paths
- **/auth/** - Authentication endpoints
- **/users/** - User management endpoints
- **/cards/** - Card management endpoints

### Tags
- **Authentication** - Login and token management
- **User Management** - User CRUD operations
- **Card Management** - Bank card operations

## 🔧 Maintenance

### Updating the Specification
1. Add OpenAPI annotations to controllers
2. Update DTO classes with proper validation annotations
3. Run application to regenerate documentation
4. Test endpoints in Swagger UI

### Validation
- Use OpenAPI validators to check specification compliance
- Test all endpoints through Swagger UI
- Verify response schemas match actual API responses

## 📝 Notes

- The OpenAPI file is located at `src/main/resources/docs/openapi.yaml`
- Auto-generated during application startup
- Includes examples for all request/response models
- Contains error response schemas for all endpoints