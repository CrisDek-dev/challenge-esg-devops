using EnergyEfficiency.Api.Repositories;
using EnergyEfficiency.Api.ViewModels;
using Microsoft.EntityFrameworkCore;

namespace EnergyEfficiency.Api.Services;

public class DashboardService : IDashboardService
{
    private readonly ISetorRepository _setores;
    private readonly ILeituraRepository _leituras;
    private readonly IAlertaRepository _alertas;
    private readonly IEquipamentoRepository _equipamentos;

    public DashboardService(
        ISetorRepository setores,
        ILeituraRepository leituras,
        IAlertaRepository alertas,
        IEquipamentoRepository equipamentos)
    {
        _setores = setores;
        _leituras = leituras;
        _alertas = alertas;
        _equipamentos = equipamentos;
    }

    public async Task<IReadOnlyList<ConsumoSetorResponse>> ConsumoPorSetorAsync(RelatorioConsumoFilter filtro, CancellationToken ct = default)
    {
        var setores = await _setores.Query().ToListAsync(ct);
        var result = new List<ConsumoSetorResponse>(setores.Count);

        foreach (var setor in setores)
        {
            var consumo = await _leituras.SomaConsumoSetorPeriodoAsync(setor.Id, filtro.DataInicio, filtro.DataFim, ct);
            var qtdEquip = await _equipamentos.Query().CountAsync(e => e.SetorId == setor.Id, ct);
            var alertasAbertos = await _alertas.Query().CountAsync(a => a.SetorId == setor.Id && !a.Resolvido, ct);

            result.Add(new ConsumoSetorResponse
            {
                SetorId = setor.Id,
                SetorNome = setor.Nome,
                LimiteMensalKwh = setor.LimiteConsumoMensalKwh,
                ConsumoTotalKwh = consumo,
                PercentualUtilizado = setor.LimiteConsumoMensalKwh == 0
                    ? 0
                    : (consumo / setor.LimiteConsumoMensalKwh) * 100,
                QuantidadeEquipamentos = qtdEquip,
                AlertasAbertos = alertasAbertos
            });
        }

        return result;
    }
}
