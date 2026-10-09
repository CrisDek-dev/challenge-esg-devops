using EnergyEfficiency.Tests.Infrastructure;

namespace EnergyEfficiency.Tests.Controllers;

public class DashboardControllerTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly HttpClient _client;

    public DashboardControllerTests(CustomWebApplicationFactory factory) => _client = factory.CreateClient();

    [Fact]
    public async Task Get_ConsumoPorSetor_ReturnsHttpStatusCode200()
    {
        // Arrange
        var inicio = DateTime.UtcNow.AddDays(-30).ToString("o");
        var fim = DateTime.UtcNow.ToString("o");
        var request = $"/api/dashboard/consumo-por-setor?dataInicio={inicio}&dataFim={fim}";

        // Act
        var response = await _client.GetAsync(request);

        // Assert
        response.EnsureSuccessStatusCode();
    }
}
