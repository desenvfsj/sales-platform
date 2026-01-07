#!/bin/bash
# ============================================================================
# Script de Build Docker - Sales Platform
# ============================================================================
#
# Este script automatiza o build da imagem Docker da aplicação.
#
# Uso:
#   ./docker-build.sh [opcoes]
#
# Opções:
#   --no-cache    Build sem usar cache
#   --push        Push para registry após build
#   --tag TAG     Tag customizada (padrão: latest)
#
# ============================================================================

set -e  # Exit on error

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configurações
IMAGE_NAME="sales-platform"
IMAGE_TAG="latest"
REGISTRY=""  # Adicione seu registry aqui, ex: "docker.io/username"
NO_CACHE=""
PUSH=false

# Funções auxiliares
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Parse argumentos
while [[ $# -gt 0 ]]; do
    case $1 in
        --no-cache)
            NO_CACHE="--no-cache"
            shift
            ;;
        --push)
            PUSH=true
            shift
            ;;
        --tag)
            IMAGE_TAG="$2"
            shift 2
            ;;
        --help)
            echo "Uso: $0 [opcoes]"
            echo ""
            echo "Opções:"
            echo "  --no-cache    Build sem usar cache"
            echo "  --push        Push para registry após build"
            echo "  --tag TAG     Tag customizada (padrão: latest)"
            echo "  --help        Mostra esta mensagem"
            exit 0
            ;;
        *)
            log_error "Opção desconhecida: $1"
            exit 1
            ;;
    esac
done

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    log_error "Docker não está instalado!"
    exit 1
fi

# Verificar se Docker está rodando
if ! docker info &> /dev/null; then
    log_error "Docker daemon não está rodando!"
    exit 1
fi

# Montar nome completo da imagem
FULL_IMAGE_NAME="${IMAGE_NAME}:${IMAGE_TAG}"
if [ -n "$REGISTRY" ]; then
    FULL_IMAGE_NAME="${REGISTRY}/${FULL_IMAGE_NAME}"
fi

log_info "Iniciando build da imagem Docker..."
log_info "Imagem: ${FULL_IMAGE_NAME}"

# Limpar build anterior (opcional)
log_info "Limpando build anterior do Gradle..."
./gradlew clean --no-daemon || log_warning "Falha ao limpar build anterior"

# Build da imagem
log_info "Executando docker build..."
if docker build ${NO_CACHE} -t "${FULL_IMAGE_NAME}" .; then
    log_success "Build concluído com sucesso!"
else
    log_error "Falha no build da imagem!"
    exit 1
fi

# Mostrar informações da imagem
log_info "Informações da imagem:"
docker images "${FULL_IMAGE_NAME}" --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"

# Push para registry (se solicitado)
if [ "$PUSH" = true ]; then
    if [ -z "$REGISTRY" ]; then
        log_error "Registry não configurado! Defina REGISTRY no script."
        exit 1
    fi
    
    log_info "Fazendo push para registry..."
    if docker push "${FULL_IMAGE_NAME}"; then
        log_success "Push concluído com sucesso!"
    else
        log_error "Falha no push da imagem!"
        exit 1
    fi
fi

# Instruções finais
echo ""
log_success "Build finalizado!"
echo ""
echo "Para executar a aplicação:"
echo "  docker run -p 8080:8080 ${FULL_IMAGE_NAME}"
echo ""
echo "Para executar com docker-compose:"
echo "  docker-compose up -d"
echo ""
echo "Para ver logs:"
echo "  docker logs -f <container-id>"
echo ""

