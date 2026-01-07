plugins {
    java
    id("org.springframework.boot") version "3.5.9"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "br.com.fsj"
version = "1.0.0-SNAPSHOT"
description = "Plataforma de Vendas Unificada"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot 3.5.9 Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql")

    // H2 Database (para testes)
    testRuntimeOnly("com.h2database:h2")

    // Flyway - Compatível com Spring Boot 3.5.9
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // MapStruct - Compatível com Java 25
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    // Springdoc OpenAPI (Swagger) - Versão compatível com Spring Boot 3.5.9
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    
    // DevTools (opcional, para desenvolvimento)
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

