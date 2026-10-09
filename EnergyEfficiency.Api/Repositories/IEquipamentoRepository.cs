using EnergyEfficiency.Api.Models;

namespace EnergyEfficiency.Api.Repositories;

public interface IEquipamentoRepository
{
    Task<Equipamento?> GetByIdAsync(int id, CancellationToken ct = default);
    Task UpdateAsync(Equipamento equipamento, CancellationToken ct = default);
    IQueryable<Equipamento> Query();
}
