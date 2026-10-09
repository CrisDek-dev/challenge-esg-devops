using EnergyEfficiency.Api.ViewModels;

namespace EnergyEfficiency.Api.Services;

public interface IDashboardService
{
    Task<IReadOnlyList<ConsumoSetorResponse>> ConsumoPorSetorAsync(RelatorioConsumoFilter filtro, CancellationToken ct = default);
}
