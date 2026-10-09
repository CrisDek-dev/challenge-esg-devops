using EnergyEfficiency.Tests.Infrastructure;

namespace EnergyEfficiency.Tests.Controllers;

public class HealthTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly HttpClient _client;

    public HealthTests(CustomWebApplicationFactory factory) => _client = factory.CreateClient();

    [Fact]
    public async Task Get_Health_ReturnsHttpStatusCode200()
    {
        // Arrange
        var request = "/health";

        // Act
        var response = await _client.GetAsync(request);

        // Assert
        response.EnsureSuccessStatusCode(); // verifica StatusCode 200
        Assert.Equal("Healthy", await response.Content.ReadAsStringAsync());
    }
}
