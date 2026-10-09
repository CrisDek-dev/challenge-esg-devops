using System.ComponentModel.DataAnnotations;

namespace EnergyEfficiency.Api.Models;

public class LeituraConsumo
{
    public long Id { get; set; }

    public int EquipamentoId { get; set; }
    public Equipamento? Equipamento { get; set; }

    public DateTime DataHora { get; set; }

    [Range(0.0, double.MaxValue)]
    public decimal ConsumoKwh { get; set; }
}
