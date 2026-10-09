using EnergyEfficiency.Tests.Infrastructure;

namespace EnergyEfficiency.Tests.Controllers;

public class LeiturasControllerTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly HttpClient _client;

    public LeiturasControllerTests(CustomWebApplicationFactory factory) => _client = factory.CreateClient();

    [Fact]
    public async Task Get_Leituras_ReturnsHttpStatusCode200()
    {
        // Arrange
        var request = "/api/leituras?page=1&pageSize=10";

        // Act
        var response = await _client.GetAsync(request);

        // Assert
        response.EnsureSuccessStatusCode();
    }
}
