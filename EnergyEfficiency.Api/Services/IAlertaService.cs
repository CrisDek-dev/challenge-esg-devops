using EnergyEfficiency.Api.ViewModels;

namespace EnergyEfficiency.Api.Services;

public interface IAlertaService
{
    Task<PagedResult<AlertaResponse>> ListarAsync(AlertaFilter filtro, CancellationToken ct = default);
    Task<AlertaResponse> ResolverAsync(long id, CancellationToken ct = default);
}
