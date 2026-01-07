FROM eclipse-temurin:25-jdk-alpine AS builder

# Metadados
LABEL stage=builder
LABEL description="Build stage for Sales Platform"

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Gradle
COPY gradle gradle
COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Copiar gradle.properties e remover configuração de JAVA_HOME local
COPY gradle.properties .
RUN sed -i '/org.gradle.java.home/d' gradle.properties

# Dar permissão de execução ao gradlew
RUN chmod +x gradlew

# Download de dependências (layer cacheável)
# Isso permite que o Docker cache as dependências se não houver mudanças
RUN ./gradlew dependencies --no-daemon || true

# Copiar código fonte
COPY src src

# Build da aplicação (sem executar testes para build mais rápido)
# Para incluir testes, remova o -x test
RUN ./gradlew bootJar -x test --no-daemon

# Verificar se o JAR foi criado
RUN ls -lh build/libs/

# ============================================================================
# STAGE 2: Runtime
# ============================================================================
FROM eclipse-temurin:25-jre-alpine AS runtime

# Instalar dependências necessárias
RUN apk add --no-cache \
    curl \
    tzdata \
    && rm -rf /var/cache/apk/*

# Criar usuário não-root para executar a aplicação (segurança)
RUN addgroup -S spring && adduser -S spring -G spring

# Definir diretório de trabalho
WORKDIR /app

# Copiar JAR da stage de build
COPY --from=builder /app/build/libs/*.jar app.jar

# Criar diretórios para logs e dados
RUN mkdir -p /app/logs /app/data && \
    chown -R spring:spring /app

# Mudar para usuário não-root
USER spring:spring

# Expor porta da aplicação
EXPOSE 8080

# Configurações de JVM otimizadas para containers
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 \
    -Duser.timezone=America/Sao_Paulo"

# Configurações da aplicação via variáveis de ambiente
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
#HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
#    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]