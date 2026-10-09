using EnergyEfficiency.Api.Models;

namespace EnergyEfficiency.Api.Repositories;

public interface IUsuarioRepository
{
    Task<Usuario?> GetByEmailAsync(string email, CancellationToken ct = default);
}
