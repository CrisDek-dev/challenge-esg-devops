FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

COPY ["EnergyEfficiency.Api/EnergyEfficiency.Api.csproj", "EnergyEfficiency.Api/"]
RUN dotnet restore "EnergyEfficiency.Api/EnergyEfficiency.Api.csproj"

COPY . .
WORKDIR "/src/EnergyEfficiency.Api"
RUN dotnet publish "EnergyEfficiency.Api.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
COPY --from=build /app/publish .

ENV ASPNETCORE_URLS=http://+:8080
ENV ASPNETCORE_ENVIRONMENT=Production

EXPOSE 8080

# Executa a aplicacao sem privilegios de root
USER $APP_UID

ENTRYPOINT ["dotnet", "EnergyEfficiency.Api.dll"]
