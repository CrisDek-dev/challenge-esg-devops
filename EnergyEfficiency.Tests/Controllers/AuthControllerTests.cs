using System.Net.Http.Json;
using EnergyEfficiency.Api.ViewModels;
using EnergyEfficiency.Tests.Infrastructure;

namespace EnergyEfficiency.Tests.Controllers;

public class AuthControllerTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly HttpClient _client;

    public AuthControllerTests(CustomWebApplicationFactory factory) => _client = factory.CreateClient();

    [Fact]
    public async Task Post_Login_ReturnsHttpStatusCode200()
    {
        // Arrange
        var request = "/api/auth/login";
        var body = new LoginRequest { Email = "admin@energia.com", Senha = "admin@123" };

        // Act
        var response = await _client.PostAsJsonAsync(request, body);

        // Assert
        response.EnsureSuccessStatusCode(); // verifica StatusCode 200
    }
}
