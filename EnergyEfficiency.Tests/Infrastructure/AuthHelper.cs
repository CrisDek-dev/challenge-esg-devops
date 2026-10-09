using System.Net.Http.Headers;
using System.Net.Http.Json;
using EnergyEfficiency.Api.ViewModels;

namespace EnergyEfficiency.Tests.Infrastructure;

public static class AuthHelper
{
    public static async Task AutenticarAsync(HttpClient client)
    {
        var response = await client.PostAsJsonAsync("/api/auth/login", new LoginRequest
        {
            Email = "admin@energia.com",
            Senha = "admin@123"
        });
        response.EnsureSuccessStatusCode();

        var body = await response.Content.ReadFromJsonAsync<LoginResponse>();
        client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", body!.Token);
    }
}
