namespace EnergyEfficiency.Api.Models;

public class AlertaConsumo
{
    public long Id { get; set; }

    public int SetorId { get; set; }
    public Setor? Setor { get; set; }

    public DateTime DataHora { get; set; }

    public decimal ConsumoAcumuladoKwh { get; set; }
    public decimal LimiteKwh { get; set; }

    public bool Resolvido { get; set; }
    public DateTime? DataResolucao { get; set; }
}
