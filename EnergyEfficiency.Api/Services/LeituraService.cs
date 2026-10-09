using EnergyEfficiency.Api.Exceptions;
using EnergyEfficiency.Api.Helpers;
using EnergyEfficiency.Api.Models;
using EnergyEfficiency.Api.Repositories;
using EnergyEfficiency.Api.ViewModels;

namespace EnergyEfficiency.Api.Services;

public class LeituraService : ILeituraService
{
    private readonly ILeituraRepository _leituras;
    private readonly IAlertaRepository _alertas;
    private readonly IEquipamentoRepository _equipamentos;
    private readonly ISetorRepository _setores;

    public LeituraService(
        ILeituraRepository leituras,
        IAlertaRepository alertas,
        IEquipamentoRepository equipamentos,
        ISetorRepository setores)
    {
        _leituras = leituras;
        _alertas = alertas;
        _equipamentos = equipamentos;
        _setores = setores;
    }

    public async Task<LeituraResponse> RegistrarAsync(LeituraCreateRequest request, CancellationToken ct = default)
    {
        var equipamento = await _equipamentos.GetByIdAsync(request.EquipamentoId, ct)
            ?? throw new NotFoundException($"Equipamento {request.EquipamentoId} nao encontrado.");

        if (!equipamento.Ativo)
        {
            throw new BusinessException("Equipamento esta desligado e nao pode registrar leituras.");
        }

        var leitura = new LeituraConsumo
        {
            EquipamentoId = request.EquipamentoId,
            DataHora = request.DataHora,
            ConsumoKwh = request.ConsumoKwh
        };

        await _leituras.AddAsync(leitura, ct);

        var gerouAlerta = await AvaliarAlertaAsync(equipamento.SetorId, request.DataHora, ct);

        return new LeituraResponse
        {
            Id = leitura.Id,
            EquipamentoId = equipamento.Id,
            EquipamentoNome = equipamento.Nome,
            SetorNome = equipamento.Setor?.Nome ?? string.Empty,
            DataHora = leitura.DataHora,
            ConsumoKwh = leitura.ConsumoKwh,
            GerouAlerta = gerouAlerta
        };
    }

    private async Task<bool> AvaliarAlertaAsync(int setorId, DateTime referencia, CancellationToken ct)
    {
        var setor = await _setores.GetByIdAsync(setorId, ct);
        if (setor is null) return false;

        var inicioMes = new DateTime(referencia.Year, referencia.Month, 1, 0, 0, 0, DateTimeKind.Utc);
        var fimMes = inicioMes.AddMonths(1).AddTicks(-1);

        var consumoMes = await _leituras.SomaConsumoSetorPeriodoAsync(setorId, inicioMes, fimMes, ct);

        if (consumoMes <= setor.LimiteConsumoMensalKwh) return false;

        if (await _alertas.ExistsAbertoForSetorAsync(setorId, ct)) return false;

        await _alertas.AddAsync(new AlertaConsumo
        {
            SetorId = setorId,
            DataHora = DateTime.UtcNow,
            ConsumoAcumuladoKwh = consumoMes,
            LimiteKwh = setor.LimiteConsumoMensalKwh,
            Resolvido = false
        }, ct);

        return true;
    }

    public async Task<PagedResult<LeituraResponse>> ListarAsync(LeituraFilter filtro, CancellationToken ct = default)
    {
        var query = _leituras.Query();

        if (filtro.EquipamentoId.HasValue)
            query = query.Where(l => l.EquipamentoId == filtro.EquipamentoId.Value);

        if (filtro.SetorId.HasValue)
            query = query.Where(l => l.Equipamento!.SetorId == filtro.SetorId.Value);

        if (filtro.DataInicio.HasValue)
            query = query.Where(l => l.DataHora >= filtro.DataInicio.Value);

        if (filtro.DataFim.HasValue)
            query = query.Where(l => l.DataHora <= filtro.DataFim.Value);

        var projected = query
            .OrderByDescending(l => l.DataHora)
            .Select(l => new LeituraResponse
            {
                Id = l.Id,
                EquipamentoId = l.EquipamentoId,
                EquipamentoNome = l.Equipamento!.Nome,
                SetorNome = l.Equipamento.Setor!.Nome,
                DataHora = l.DataHora,
                ConsumoKwh = l.ConsumoKwh,
                GerouAlerta = false
            });

        return await projected.ToPagedResultAsync(filtro.Page, filtro.PageSize, ct);
    }
}
