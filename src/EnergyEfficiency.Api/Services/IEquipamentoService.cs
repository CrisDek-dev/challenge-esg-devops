using EnergyEfficiency.Api.ViewModels;

namespace EnergyEfficiency.Api.Services;

public interface IEquipamentoService
{
    Task<EquipamentoResponse> AlterarEstadoAsync(int id, bool ativo, CancellationToken ct = default);
    Task<EquipamentoResponse> ObterAsync(int id, CancellationToken ct = default);
}
