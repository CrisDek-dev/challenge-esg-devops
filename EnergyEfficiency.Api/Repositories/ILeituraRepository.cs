using EnergyEfficiency.Api.Models;

namespace EnergyEfficiency.Api.Repositories;

public interface ILeituraRepository
{
    Task<LeituraConsumo> AddAsync(LeituraConsumo leitura, CancellationToken ct = default);
    IQueryable<LeituraConsumo> Query();
    Task<decimal> SomaConsumoSetorPeriodoAsync(int setorId, DateTime inicio, DateTime fim, CancellationToken ct = default);
}
