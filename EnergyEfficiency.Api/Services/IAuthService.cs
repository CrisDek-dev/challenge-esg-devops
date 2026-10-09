using EnergyEfficiency.Api.ViewModels;

namespace EnergyEfficiency.Api.Services;

public interface IAuthService
{
    Task<LoginResponse> LoginAsync(LoginRequest request, CancellationToken ct = default);
}
