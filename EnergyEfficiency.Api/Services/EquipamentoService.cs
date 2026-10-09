using EnergyEfficiency.Api.Exceptions;
using EnergyEfficiency.Api.Repositories;
using EnergyEfficiency.Api.ViewModels;

namespace EnergyEfficiency.Api.Services;

public class EquipamentoService : IEquipamentoService
{
    private readonly IEquipamentoRepository _equipamentos;

    public EquipamentoService(IEquipamentoRepository equipamentos) => _equipamentos = equipamentos;

    public async Task<EquipamentoResponse> AlterarEstadoAsync(int id, bool ativo, CancellationToken ct = default)
    {
        var equipamento = await _equipamentos.GetByIdAsync(id, ct)
            ?? throw new NotFoundException($"Equipamento {id} nao encontrado.");

        if (equipamento.Ativo == ativo)
        {
            throw new BusinessException($"Equipamento ja esta {(ativo ? "ligado" : "desligado")}.");
        }

        equipamento.Ativo = ativo;
        await _equipamentos.UpdateAsync(equipamento, ct);

        return Map(equipamento);
    }

    public async Task<EquipamentoResponse> ObterAsync(int id, CancellationToken ct = default)
    {
        var equipamento = await _equipamentos.GetByIdAsync(id, ct)
            ?? throw new NotFoundException($"Equipamento {id} nao encontrado.");

        return Map(equipamento);
    }

    private static EquipamentoResponse Map(Models.Equipamento e) => new()
    {
        Id = e.Id,
        Nome = e.Nome,
        NumeroSerie = e.NumeroSerie,
        SetorId = e.SetorId,
        SetorNome = e.Setor?.Nome ?? string.Empty,
        Ativo = e.Ativo,
        PotenciaNominalWatts = e.PotenciaNominalWatts
    };
}
