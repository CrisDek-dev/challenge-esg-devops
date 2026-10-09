using EnergyEfficiency.Api.Models;

namespace EnergyEfficiency.Api.Repositories;

public interface IAlertaRepository
{
    Task<AlertaConsumo> AddAsync(AlertaConsumo alerta, CancellationToken ct = default);
    IQueryable<AlertaConsumo> Query();
    Task<AlertaConsumo?> GetByIdAsync(long id, CancellationToken ct = default);
    Task UpdateAsync(AlertaConsumo alerta, CancellationToken ct = default);
    Task<bool> ExistsAbertoForSetorAsync(int setorId, CancellationToken ct = default);
}
