using EnergyEfficiency.Api.ViewModels;

namespace EnergyEfficiency.Api.Services;

public interface ILeituraService
{
    Task<LeituraResponse> RegistrarAsync(LeituraCreateRequest request, CancellationToken ct = default);
    Task<PagedResult<LeituraResponse>> ListarAsync(LeituraFilter filtro, CancellationToken ct = default);
}
