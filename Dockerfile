FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /source

COPY ["src/EnergyEfficiency.Api/EnergyEfficiency.Api.csproj", "src/EnergyEfficiency.Api/"]
RUN dotnet restore "src/EnergyEfficiency.Api/EnergyEfficiency.Api.csproj"

COPY . .
RUN dotnet publish "src/EnergyEfficiency.Api/EnergyEfficiency.Api.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
COPY --from=build /app/publish .

ENV ASPNETCORE_HTTP_PORTS=8080
ENV ASPNETCORE_ENVIRONMENT=Production

EXPOSE 8080

# Executa a aplicacao sem privilegios de root
USER $APP_UID

ENTRYPOINT ["dotnet", "EnergyEfficiency.Api.dll"]
