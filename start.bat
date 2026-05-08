@echo off

REM Script para iniciar o desenvolvimento da aplicação completa (Windows)

echo.
echo 🚀 Iniciando Diarias JA...
echo.

REM Verificar se Docker está instalado
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker nao esta instalado. Por favor, instale o Docker.
    exit /b 1
)

REM Verificar se Docker Compose está instalado
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker Compose nao esta instalado. Por favor, instale o Docker Compose.
    exit /b 1
)

echo 📦 Configurando ambiente...

REM Criar arquivo .env.local se não existir
if not exist "frontend\.env.local" (
    echo Criando frontend\.env.local...
    copy frontend\.env.example frontend\.env.local
)

echo.
echo 🐳 Iniciando servicos Docker...
docker-compose up -d

echo.
echo ⏳ Aguardando servicos estarem prontos...
timeout /t 10

echo.
echo ✅ Servicos inicializados com sucesso!
echo.
echo 📍 URLs da aplicacao:
echo    Frontend:  http://localhost:5173
echo    Backend:   http://localhost:8080
echo    Banco de Dados: localhost:3300
echo    Redis:     localhost:6379
echo.
echo 📝 Documentacao API: http://localhost:8080/swagger-ui.html
echo.
echo 🛑 Para parar os servicos, execute: docker-compose down
echo 🔄 Para visualizar logs: docker-compose logs -f
