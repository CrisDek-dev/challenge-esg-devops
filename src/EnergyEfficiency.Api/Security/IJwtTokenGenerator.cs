using EnergyEfficiency.Api.Models;

namespace EnergyEfficiency.Api.Security;

public interface IJwtTokenGenerator
{
    (string Token, DateTime ExpiresAt) GenerateToken(Usuario usuario);
}
