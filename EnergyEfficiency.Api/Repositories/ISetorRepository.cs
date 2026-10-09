using EnergyEfficiency.Api.Models;

namespace EnergyEfficiency.Api.Repositories;

public interface ISetorRepository
{
    Task<Setor?> GetByIdAsync(int id, CancellationToken ct = default);
    IQueryable<Setor> Query();
}
