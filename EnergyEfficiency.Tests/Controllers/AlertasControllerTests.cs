using EnergyEfficiency.Tests.Infrastructure;

namespace EnergyEfficiency.Tests.Controllers;

public class AlertasControllerTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly HttpClient _client;

    public AlertasControllerTests(CustomWebApplicationFactory factory) => _client = factory.CreateClient();

    [Fact]
    public async Task Get_Alertas_ReturnsHttpStatusCode200()
    {
        // Arrange
        await AuthHelper.AutenticarAsync(_client);
        var request = "/api/alertas?page=1&pageSize=10";

        // Act
        var response = await _client.GetAsync(request);

        // Assert
        response.EnsureSuccessStatusCode();
    }
}
