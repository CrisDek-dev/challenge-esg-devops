using EnergyEfficiency.Api.Exceptions;
using EnergyEfficiency.Api.Helpers;
using EnergyEfficiency.Api.Repositories;
using EnergyEfficiency.Api.ViewModels;

namespace EnergyEfficiency.Api.Services;

public class AlertaService : IAlertaService
{
    private readonly IAlertaRepository _alertas;

    public AlertaService(IAlertaRepository alertas) => _alertas = alertas;

    public async Task<PagedResult<AlertaResponse>> ListarAsync(AlertaFilter filtro, CancellationToken ct = default)
    {
        var query = _alertas.Query();

        if (filtro.SetorId.HasValue)
            query = query.Where(a => a.SetorId == filtro.SetorId.Value);

        if (filtro.Resolvido.HasValue)
            query = query.Where(a => a.Resolvido == filtro.Resolvido.Value);

        var projected = query
            .OrderByDescending(a => a.DataHora)
            .Select(a => new AlertaResponse
            {
                Id = a.Id,
                SetorId = a.SetorId,
                SetorNome = a.Setor!.Nome,
                DataHora = a.DataHora,
                ConsumoAcumuladoKwh = a.ConsumoAcumuladoKwh,
                LimiteKwh = a.LimiteKwh,
                PercentualAcimaLimite = a.LimiteKwh == 0
                    ? 0
                    : ((a.ConsumoAcumuladoKwh - a.LimiteKwh) / a.LimiteKwh) * 100,
                Resolvido = a.Resolvido,
                DataResolucao = a.DataResolucao
            });

        return await projected.ToPagedResultAsync(filtro.Page, filtro.PageSize, ct);
    }

    public async Task<AlertaResponse> ResolverAsync(long id, CancellationToken ct = default)
    {
        var alerta = await _alertas.GetByIdAsync(id, ct)
            ?? throw new NotFoundException($"Alerta {id} nao encontrado.");

        if (alerta.Resolvido)
            throw new BusinessException("Alerta ja esta resolvido.");

        alerta.Resolvido = true;
        alerta.DataResolucao = DateTime.UtcNow;
        await _alertas.UpdateAsync(alerta, ct);

        return new AlertaResponse
        {
            Id = alerta.Id,
            SetorId = alerta.SetorId,
            SetorNome = alerta.Setor?.Nome ?? string.Empty,
            DataHora = alerta.DataHora,
            ConsumoAcumuladoKwh = alerta.ConsumoAcumuladoKwh,
            LimiteKwh = alerta.LimiteKwh,
            PercentualAcimaLimite = alerta.LimiteKwh == 0
                ? 0
                : ((alerta.ConsumoAcumuladoKwh - alerta.LimiteKwh) / alerta.LimiteKwh) * 100,
            Resolvido = alerta.Resolvido,
            DataResolucao = alerta.DataResolucao
        };
    }
}
