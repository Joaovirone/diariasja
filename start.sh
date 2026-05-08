#!/bin/bash

# Script para iniciar o desenvolvimento da aplicação completa

echo "🚀 Iniciando Diárias JÁ..."
echo ""

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não está instalado. Por favor, instale o Docker."
    exit 1
fi

# Verificar se Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose não está instalado. Por favor, instale o Docker Compose."
    exit 1
fi

# Verificar se Node.js está instalado
if ! command -v node &> /dev/null; then
    echo "⚠️  Node.js não está instalado. Será necessário para desenvolvimento do frontend."
fi

echo "📦 Configurando ambiente..."

# Criar arquivo .env.local se não existir
if [ ! -f "frontend/.env.local" ]; then
    echo "Criando frontend/.env.local..."
    cp frontend/.env.example frontend/.env.local
fi

echo ""
echo "🐳 Iniciando serviços Docker..."
docker-compose up -d

echo ""
echo "⏳ Aguardando serviços estarem prontos..."
sleep 10

echo ""
echo "✅ Serviços inicializados com sucesso!"
echo ""
echo "📍 URLs da aplicação:"
echo "   Frontend:  http://localhost:5173"
echo "   Backend:   http://localhost:8080"
echo "   Banco de Dados: localhost:3300"
echo "   Redis:     localhost:6379"
echo ""
echo "📝 Documentação API: http://localhost:8080/swagger-ui.html"
echo ""
echo "🛑 Para parar os serviços, execute: docker-compose down"
echo "🔄 Para visualizar logs: docker-compose logs -f"
